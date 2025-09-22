package com.lcsk42.frameworks.starter.web.util;

import com.lcsk42.frameworks.starter.convention.errorcode.BaseErrorCode;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServletUtil {
    /**
     * 从请求中获取所有参数作为不可修改的映射
     *
     * @param request servlet 请求对象
     * @return 参数名到其值(作为 String 数组)的映射
     */
    public static Map<String, String[]> getParams(ServletRequest request) {
        return Collections.unmodifiableMap(request.getParameterMap());
    }

    /**
     * 以逗号分隔值的映射形式获取请求中的所有参数
     *
     * @param request servlet 请求对象
     * @return 参数名到其逗号分隔值的映射
     */
    public static Map<String, String> getParamMap(ServletRequest request) {
        return getParams(request).entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> String.join(StringConstant.COMMA, entry.getValue())
                ));
    }

    /**
     * 将请求体读取为字符串
     *
     * @param request servlet 请求对象
     * @return 请求体作为字符串返回, 如果读取失败则返回空字符串
     */
    public static String getBody(ServletRequest request) {
        try (Reader reader = request.getReader()) {
            return IOUtils.toString(reader);
        } catch (IOException e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 将请求体读取为字节数组
     *
     * @param request servlet 请求对象
     * @return 请求体作为字节数组返回, 如果读取失败则返回空数组
     */
    public static byte[] getBodyBytes(ServletRequest request) {
        try (InputStream input = request.getInputStream()) {
            return IOUtils.toByteArray(input);
        } catch (IOException e) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
    }

    /**
     * 获取请求中的所有头部信息作为映射(每个头部只取第一个值)
     *
     * @param request HTTP servlet 请求对象
     * @return 头部名称到其值的映射
     */
    public static Map<String, String> getHeaderMap(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        request::getHeader,
                        (existing, replacement) -> existing,
                        HashMap::new
                ));
    }

    /**
     * 获取请求中的所有头部信息作为映射(包含每个头部的所有值)
     *
     * @param request HTTP servlet 请求对象
     * @return 头部名称到其值列表的映射
     */
    public static Map<String, List<String>> getHeadersMap(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        name -> Collections.list(request.getHeaders(name)),
                        (existing, replacement) -> existing
                ));
    }

    /**
     * 获取响应中的所有头部信息作为映射
     *
     * @param response HTTP servlet 响应对象
     * @return 头部名称到其值列表的映射
     */
    public static Map<String, List<String>> getHeadersMap(HttpServletResponse response) {
        return response.getHeaderNames()
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        name -> response.getHeaders(name).stream().toList(),
                        (existing, replacement) -> existing,
                        HashMap::new
                ));
    }

    /**
     * 从请求中获取特定头部值(使用 UTF-8 编码)
     *
     * @param request HTTP servlet 请求对象
     * @param name    头部名称
     * @return 头部值, 如果未找到则返回空字符串
     */
    public static String getHeader(HttpServletRequest request, String name) {
        return getHeader(request, name, StandardCharsets.UTF_8);
    }

    /**
     * 使用指定字符集从请求中获取特定头部值
     *
     * @param request HTTP servlet 请求对象
     * @param name    头部名称
     * @param charset 用于解码的字符集
     * @return 头部值, 如果未找到则返回空字符串
     */
    public static String getHeader(HttpServletRequest request, String name, Charset charset) {
        String headerValue = request.getHeader(name);
        if (StringUtils.isBlank(headerValue)) {
            return StringUtils.EMPTY;
        }
        try {
            byte[] bytes = headerValue.getBytes(StandardCharsets.ISO_8859_1);
            return new String(bytes, charset);
        } catch (Exception e) {
            return headerValue;
        }
    }

    /**
     * 检查请求方法是否为 GET
     *
     * @param request HTTP servlet 请求对象
     * @return 如果是 GET 方法返回 true, 否则返回 false
     */
    public static boolean isGet(HttpServletRequest request) {
        return HttpMethod.GET.matches(request.getMethod());
    }

    /**
     * 检查请求方法是否为 POST
     *
     * @param request HTTP servlet 请求对象
     * @return 如果是 POST 方法返回 true, 否则返回 false
     */
    public static boolean isPost(HttpServletRequest request) {
        return HttpMethod.POST.matches(request.getMethod());
    }

    /**
     * 检查请求方法是否为 PUT
     *
     * @param request HTTP servlet 请求对象
     * @return 如果是 PUT 方法返回 true, 否则返回 false
     */
    public static boolean isPut(HttpServletRequest request) {
        return HttpMethod.PUT.matches(request.getMethod());
    }

    /**
     * 检查请求方法是否为 DELETE
     *
     * @param request HTTP servlet 请求对象
     * @return 如果是 DELETE 方法返回 true, 否则返回 false
     */
    public static boolean isDelete(HttpServletRequest request) {
        return HttpMethod.DELETE.matches(request.getMethod());
    }

    /**
     * 检查请求方法是否为 OPTIONS
     *
     * @param request HTTP servlet 请求对象
     * @return 如果是 OPTIONS 方法返回 true, 否则返回 false
     */
    public static boolean isOptions(HttpServletRequest request) {
        return HttpMethod.OPTIONS.matches(request.getMethod());
    }

    /**
     * 检查请求是否为 multipart 请求
     *
     * @param request HTTP servlet 请求对象
     * @return 如果是 multipart POST 请求则返回 true, 否则返回 false
     */
    public static boolean isMultipart(HttpServletRequest request) {
        if (!isPost(request)) {
            return false;
        } else {
            String contentType = request.getContentType();
            return StringUtils.isNoneBlank(contentType) && StringUtils.startsWithIgnoreCase("multipart/", contentType);
        }
    }

    /**
     * 从请求中获取特定 cookie
     *
     * @param httpServletRequest HTTP servlet 请求对象
     * @param name               cookie 名称
     * @return Cookie 对象, 如果未找到则返回 null
     */
    public static Cookie getCookie(HttpServletRequest httpServletRequest, String name) {
        return getCookieMap(httpServletRequest).get(name);
    }

    /**
     * 将请求中的所有 cookie 作为映射获取
     *
     * @param httpServletRequest HTTP servlet 请求对象
     * @return cookie 名称到 Cookie 对象的映射
     */
    public static Map<String, Cookie> getCookieMap(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (ArrayUtils.isEmpty(cookies)) {
            return Map.of();
        } else {
            return Stream.of(cookies)
                    .collect(Collectors.toMap(
                            Cookie::getName,
                            Function.identity(),
                            (existing, replacement) -> replacement
                    ));
        }
    }

    /**
     * 向响应中添加 cookie
     *
     * @param response HTTP servlet 响应对象
     * @param cookie   要添加的 cookie
     */
    public static void addCookie(HttpServletResponse response, Cookie cookie) {
        response.addCookie(cookie);
    }

    /**
     * 向响应中添加简单的 cookie
     *
     * @param response HTTP servlet 响应对象
     * @param name     cookie 名称
     * @param value    cookie 值
     */
    public static void addCookie(HttpServletResponse response, String name, String value) {
        addCookie(response, new Cookie(name, value));
    }

    /**
     * 向响应中添加上存活时间的 cookie
     *
     * @param response        HTTP servlet 响应对象
     * @param name            cookie 名称
     * @param value           cookie 值
     * @param maxAgeInSeconds cookie 的最大存活时间(秒)
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        addCookie(response, name, value, maxAgeInSeconds, StringConstant.SLASH, null);
    }

    /**
     * 向响应中添加完全可配置的 cookie
     *
     * @param response        HTTP servlet 响应对象
     * @param name            cookie 名称
     * @param value           cookie 值
     * @param maxAgeInSeconds cookie 的最大存活时间(秒)
     * @param path            cookie 的路径
     * @param domain          cookie 的域
     */
    public static void addCookie(HttpServletResponse response,
                                 String name,
                                 String value,
                                 int maxAgeInSeconds,
                                 String path,
                                 String domain) {
        Cookie cookie = new Cookie(name, value);
        if (StringUtils.isNoneBlank(domain)) {
            cookie.setDomain(domain);
        }

        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setPath(path);
        addCookie(response, cookie);
    }

    /**
     * 将文件作为附件写入响应
     *
     * @param response    HTTP servlet 响应对象
     * @param inputStream 文件的输入流
     * @param fileName    下载对话框中显示的文件名
     * @param contentType 文件的 MIME 类型
     */
    public static void write(HttpServletResponse response,
                             InputStream inputStream,
                             String fileName,
                             String contentType) {
        write(response, inputStream, fileName, contentType, StandardCharsets.UTF_8);
    }

    /**
     * 使用指定字符集将文件作为附件写入响应
     *
     * @param response    HTTP servlet 响应对象
     * @param inputStream 文件的输入流
     * @param fileName    下载对话框中显示的文件名
     * @param contentType 文件的 MIME 类型
     * @param charset     用于编码文件名的字符集
     */
    public static void write(HttpServletResponse response,
                             InputStream inputStream,
                             String fileName,
                             String contentType,
                             Charset charset) {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition
                        .attachment()
                        .filename(fileName, charset)
                        .build()
                        .toString()
        );
        response.setCharacterEncoding(charset.toString());
        response.setContentType(contentType);
        write(response, inputStream);
    }

    /**
     * 将文件以内联方式写入响应
     *
     * @param response    HTTP servlet 响应对象
     * @param inputStream 文件的输入流
     * @param fileName    文件名
     * @param contentType 文件的 MIME 类型
     */
    public static void writeToInline(HttpServletResponse response,
                                     InputStream inputStream,
                                     String fileName,
                                     String contentType) {
        writeToInline(response, inputStream, fileName, contentType, StandardCharsets.UTF_8);
    }

    /**
     * 使用指定字符集将文件以内联方式写入响应
     *
     * @param response    HTTP servlet 响应对象
     * @param inputStream 文件的输入流
     * @param fileName    文件名
     * @param contentType 文件的 MIME 类型
     * @param charset     用于编码文件名的字符集
     */
    public static void writeToInline(HttpServletResponse response,
                                     InputStream inputStream,
                                     String fileName,
                                     String contentType,
                                     Charset charset) {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition
                        .inline()
                        .filename(fileName, charset)
                        .build()
                        .toString()
        );
        response.setCharacterEncoding(charset.toString());
        response.setContentType(contentType);
        write(response, inputStream);
    }

    /**
     * 使用默认缓冲区大小将输入流写入响应
     *
     * @param response    HTTP servlet 响应对象
     * @param inputStream 要写入的输入流
     */
    public static void write(HttpServletResponse response, InputStream inputStream) {
        write(response, inputStream, 8192);
    }

    /**
     * 使用指定缓冲区大小将输入流写入响应
     *
     * @param response    HTTP servlet 响应对象
     * @param inputStream 要写入的输入流
     * @param bufferSize  用于复制的缓冲区大小
     */
    public static void write(HttpServletResponse response, InputStream inputStream, int bufferSize) {
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            IOUtils.copy(inputStream, outputStream, bufferSize);
            outputStream.flush();
        } catch (IOException ignored) {
            throw BaseErrorCode.SERVICE_ERROR.toException();
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
        }
    }

}