package com.lcsk42.frameworks.starter.log.core.wrapper;

import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import jakarta.annotation.Nonnull;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.MediaType;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 可重复读取请求体的包装器 支持文件流直接透传，非文件流可重复读取
 * <p>
 * 虽然这里可以多次读取流里面的数据， 但是建议还是调用getContentAsString()/getCachedContent() 方法， 已经把内容缓存在内存中了。
 * </p>
 */
public class RepeatReadRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 缓存内容
     */
    private final FastByteArrayOutputStream cachedContent;

    /*** 用于缓存输入流 */
    private ContentCachingInputStream contentCachingInputStream;

    /**
     * 字符编码
     */
    private final String characterEncoding;

    // private BufferedReader reader;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request the {@link HttpServletRequest} to be wrapped.
     * @throws IllegalArgumentException if the request is null
     */
    public RepeatReadRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.characterEncoding = request.getCharacterEncoding() != null
                ? request.getCharacterEncoding()
                : StandardCharsets.UTF_8.name();
        int contentLength = super.getRequest().getContentLength();
        cachedContent = (contentLength > 0)
                ? new FastByteArrayOutputStream(contentLength)
                : new FastByteArrayOutputStream();
        // 判断是否为文件上传请求
        if (!isMultipartContent(request)) {
            if (isFormRequest()) {
                writeRequestParametersToCachedContent();
            } else {
                StreamUtils.copy(request.getInputStream(), cachedContent);
            }
            contentCachingInputStream = new ContentCachingInputStream(cachedContent.toByteArray());
        }
    }

    public static RepeatReadRequestWrapper of(HttpServletRequest request) throws IOException {
        if (request instanceof RepeatReadRequestWrapper) {
            return (RepeatReadRequestWrapper) request;
        } else {
            return new RepeatReadRequestWrapper(request);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 如果是文件上传，直接返回原始输入流
        if (isMultipartContent(super.getRequest())) {
            return super.getRequest().getInputStream();
        }
        synchronized (this) {
            contentCachingInputStream.reset();
            return contentCachingInputStream;
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        // 如果是文件上传，直接返回原始Reader
        if (isMultipartContent(super.getRequest())) {
            return super.getRequest().getReader();
        }

        // BufferedReader不支持多次reset()（除非手动调用 mark() 并控制其生命周期），最安全的方式是每次调用getReader()时基于缓存内容重新创建一个新的BufferedReader实例。
        synchronized (this) {
            return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
        }
    }

    private void writeRequestParametersToCachedContent() {
        try {
            if (this.cachedContent.size() == 0) {
                Map<String, String[]> form = super.getParameterMap();
                for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext(); ) {
                    String name = nameIterator.next();
                    List<String> values = Arrays.asList(form.get(name));
                    for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext(); ) {
                        String value = valueIterator.next();
                        this.cachedContent.write(URLEncoder.encode(name, characterEncoding).getBytes());
                        if (value != null) {
                            this.cachedContent.write(StringConstant.EQUALS.getBytes());
                            this.cachedContent.write(URLEncoder.encode(value, characterEncoding).getBytes());
                            if (valueIterator.hasNext()) {
                                this.cachedContent.write(StringConstant.AMP.getBytes());
                            }
                        }
                    }
                    if (nameIterator.hasNext()) {
                        this.cachedContent.write(StringConstant.AMP.getBytes());
                    }
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write request parameters to cached content", ex);
        }
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public String getContentAsString() {
        return this.cachedContent.toString(Charset.forName(getCharacterEncoding()));
    }

    public FastByteArrayOutputStream getCachedContent() {
        return cachedContent;
    }

    /**
     * 判断当前请求是否为 multipart/form-data 类型的文件上传请求。 该类型一般用于表单上传文件的场景，例如 enctype="multipart/form-data"。
     *
     * @param request 当前 HTTP 请求对象
     * @return true 表示为 multipart 文件上传请求；否则为 false
     */
    public boolean isMultipartContent(ServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }

    private boolean isFormRequest() {
        String contentType = getContentType();
        return (contentType != null && contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
    }

    /**
     * Body 缓存的ServletInputStream实现 DefaultServerRequestBuilder.BodyInputStream
     */
    private static class ContentCachingInputStream extends ServletInputStream {

        private final InputStream delegate;

        public ContentCachingInputStream(byte[] body) {
            this.delegate = new ByteArrayInputStream(body);
        }

        public boolean isFinished() {
            return false;
        }

        public boolean isReady() {
            return true;
        }

        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        public int read() throws IOException {
            return this.delegate.read();
        }

        public int read(@Nonnull byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        public int read(@Nonnull byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        public int available() throws IOException {
            return this.delegate.available();
        }

        public void close() throws IOException {
            this.delegate.close();
        }

        public synchronized void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        public synchronized void reset() throws IOException {
            this.delegate.reset();
        }

        public boolean markSupported() {
            return this.delegate.markSupported();
        }
    }
}
