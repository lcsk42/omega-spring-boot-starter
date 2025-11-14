package com.lcsk42.frameworks.starter.security.xss.filter;

import com.lcsk42.frameworks.starter.security.xss.configuration.XssProperties;
import com.lcsk42.frameworks.starter.security.xss.enums.XssMode;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.http.HttpMethod;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 针对 XssServletRequest 进行过滤的包装类
 */
public class XssServletRequestWrapper extends HttpServletRequestWrapper {

    private final XssProperties xssProperties;

    private String body = StringUtils.EMPTY;

    private static final String HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";

    public XssServletRequestWrapper(HttpServletRequest request, XssProperties xssProperties)
            throws IOException {
        super(request);
        this.xssProperties = xssProperties;

        if (StringUtils.equalsAnyIgnoreCase(
                request.getMethod().toUpperCase(),
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.PUT.name())) {
            body = IOUtils.toString(request.getReader());
            if (StringUtils.isBlank(body)) {
                return;
            }
            body = this.handleTag(body);
        }
    }

    @Override
    public BufferedReader getReader() {
        return IOUtils.toBufferedReader(new StringReader(body));
    }

    @Override
    public ServletInputStream getInputStream() {
        return getServletInputStream(body);
    }

    @Override
    public String getQueryString() {
        return this.handleTag(super.getQueryString());
    }

    @Override
    public String getParameter(String name) {
        return this.handleTag(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (ArrayUtils.isEmpty(values)) {
            return values;
        }
        int length = values.length;
        String[] resultValues = new String[length];
        for (int i = 0; i < length; i++) {
            resultValues[i] = this.handleTag(values[i]);
        }
        return resultValues;
    }

    /**
     * 对文本内容进行 XSS 处理
     *
     * @param content 文本内容
     * @return 返回处理过后内容
     */
    private String handleTag(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        XssMode mode = xssProperties.getMode();
        // 转义
        if (XssMode.ESCAPE.equals(mode)) {

            List<String> htmlTags = new ArrayList<>();
            Matcher matcher = Pattern.compile(HTML_MARK).matcher(content);
            while (matcher.find()) {
                htmlTags.add(matcher.group());
            }
            if (htmlTags.isEmpty()) {
                return content;
            }
            for (String tag : htmlTags) {
                String escapedTag = StringEscapeUtils.escapeHtml4(tag);
                escapedTag = StringUtils.replace(escapedTag, "\\", "");
                content = StringUtils.replace(content, tag, escapedTag);
            }
            return content;
        }
        // 清理
        return content.replaceAll(HTML_MARK, StringUtils.EMPTY);
    }

    static ServletInputStream getServletInputStream(String body) {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 设置监听器
            }
        };
    }

}
