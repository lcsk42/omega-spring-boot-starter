package com.lcsk42.frameworks.starter.log.core.wrapper;

import jakarta.annotation.Nonnull;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 可重复读取响应内容的包装器
 * 支持缓存响应内容，便于日志记录和后续处理 (不缓存SSE)
 */
public class RepeatReadResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream cachedOutputStream = new ByteArrayOutputStream();
    private final PrintWriter writer = new PrintWriter(cachedOutputStream, true);
    /**
     * 是否为流式响应
     */
    private boolean isStreamingResponse = false;

    public RepeatReadResponseWrapper(HttpServletResponse response) {
        super(response);
        checkStreamingResponse();
    }

    public static RepeatReadResponseWrapper of(HttpServletResponse response) {
        if (response instanceof RepeatReadResponseWrapper) {
            return (RepeatReadResponseWrapper) response;
        } else {
            return new RepeatReadResponseWrapper(response);
        }
    }

    @Override
    public void setContentType(String type) {
        super.setContentType(type);
        // 根据 Content-Type 判断是否为流式响应
        if (type != null) {
            String lowerType = type.toLowerCase();
            isStreamingResponse = lowerType.contains(MediaType.TEXT_EVENT_STREAM_VALUE);
        }
    }

    private void checkStreamingResponse() {
        String contentType = getContentType();
        if (contentType != null) {
            String lowerType = contentType.toLowerCase();
            isStreamingResponse = lowerType.contains(MediaType.TEXT_EVENT_STREAM_VALUE);
        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        checkStreamingResponse();
        // 对于 SSE 流式响应，直接返回原始响应流，不做额外处理
        if (isStreamingResponse) {
            return super.getOutputStream();
        }
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {}

            @Override
            public void write(int b) throws IOException {
                cachedOutputStream.write(b);
            }

            @Override
            public void write(@Nonnull byte[] b) throws IOException {
                cachedOutputStream.write(b);
            }

            @Override
            public void write(@Nonnull byte[] b, int off, int len) throws IOException {
                cachedOutputStream.write(b, off, len);
            }
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        checkStreamingResponse();
        if (isStreamingResponse) {
            // 对于 SSE 流式响应，直接返回原始响应写入器，不做额外处理
            return super.getWriter();
        }
        return writer;
    }

    /**
     * 获取缓存的响应内容
     *
     * @return 缓存的响应内容
     */
    public String getResponseContent() {
        if (!isStreamingResponse) {
            writer.flush();
            return cachedOutputStream.toString(StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * 将缓存的响应内容复制到原始响应中
     *
     * @throws IOException IO 异常
     */
    public void copyBodyToResponse() throws IOException {
        if (!isStreamingResponse && cachedOutputStream.size() > 0) {
            getResponse().getOutputStream().write(cachedOutputStream.toByteArray());
        }
    }

    /**
     * 是否为流式响应
     *
     * @return 是否为流式响应
     */
    public boolean isStreamingResponse() {
        return isStreamingResponse;
    }
}
