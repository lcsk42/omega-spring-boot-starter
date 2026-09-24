package com.lcsk42.frameworks.starter.crypto.api.filter;


import com.lcsk42.frameworks.starter.crypto.core.util.CryptoUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 请求体解密包装类
 */
public class RequestBodyDecryptWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public RequestBodyDecryptWrapper(HttpServletRequest request,
            String privateKey,
            String secretKeyHeader) throws IOException {
        super(request);
        this.body = getDecryptContent(request, privateKey, secretKeyHeader);
    }

    /**
     * 获取解密后的请求体
     *
     * @param request 请求对象
     * @param privateKey RSA私钥
     * @param secretKeyHeader 密钥头
     * @return 解密后的请求体
     * @throws IOException /
     */
    public byte[] getDecryptContent(HttpServletRequest request,
            String privateKey,
            String secretKeyHeader) throws IOException {
        // 通过 请求头 获取 AES 密钥，密钥内容经过 RSA 加密
        String secretKeyByRsa = request.getHeader(secretKeyHeader);
        // 通过 RSA 解密，获取 AES 密钥，密钥内容经过 Base64 编码
        String secretKeyByBase64 = CryptoUtil.decryptRsa(secretKeyByRsa, privateKey);
        // 通过 Base64 解码，获取 AES 密钥
        String aesSecretKey = CryptoUtil.decodeBase64(secretKeyByBase64);
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Charset charset = StandardCharsets.UTF_8;
        String encoding = request.getCharacterEncoding();
        if (StringUtils.isNotBlank(encoding)) {
            charset = Charset.forName(encoding);
        }
        String requestBody = IOUtils.toString(request.getInputStream(), charset);
        // 通过 AES 密钥，解密 请求体
        return CryptoUtil.decryptAes(requestBody, aesSecretKey).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public int getContentLength() {
        return body.length;
    }

    @Override
    public long getContentLengthLong() {
        return body.length;
    }

    @Override
    public String getContentType() {
        return MediaType.APPLICATION_JSON_VALUE;
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream stream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return stream.read();
            }

            @Override
            public int available() {
                return body.length;
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}
