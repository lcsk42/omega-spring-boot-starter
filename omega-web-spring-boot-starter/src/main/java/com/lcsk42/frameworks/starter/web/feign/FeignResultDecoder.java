package com.lcsk42.frameworks.starter.web.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcsk42.frameworks.starter.convention.errorcode.BaseErrorCode;
import com.lcsk42.frameworks.starter.convention.exception.ServiceException;
import com.lcsk42.frameworks.starter.convention.model.Result;
import feign.Response;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.core.ResolvableType;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 自定义 Feign 响应解码器，用于解包 Result<T> 响应包装器，
 * 并在 API 响应指示失败时抛出异常。
 */
@RequiredArgsConstructor
public class FeignResultDecoder implements Decoder {

    // Feign 使用的原始解码器（通常是 SpringDecoder）
    private final Decoder decoder;

    private final ObjectMapper objectMapper;

    @Override
    public Object decode(Response response, Type type)
            throws IOException {

        // 从 Feign 请求模板中获取方法元数据
        final Method method = response.request().requestTemplate().methodMetadata().method();

        // 检查声明的返回类型是否为非 Result 类型，即需要从 Result<T> 中解包
        final boolean isResult = method.getReturnType() != Result.class;

        if (isResult) {
            // 首先将响应解码为 Result 对象
            // 获取方法上的参数
            ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
            Type resolvedType = resolvableType.getType();
            // 解析为 Result 包装类型
            Result<?> result = (Result<?>) this.decoder.decode(response, Result.class);

            // 如果响应指示成功，则返回实际数据
            if (BooleanUtils.isTrue(result.isSucceed())) {
                return objectMapper.convertValue(result.getData(),
                        objectMapper.constructType(resolvedType));
            } else {
                // 否则，抛出自定义异常以表示失败
                throw new ServiceException(result.getMessage(), BaseErrorCode.SERVICE_FEIGN_ERROR);
            }
        }

        // 如果方法本身返回的不是 Result 对象，则按原样解码
        return this.decoder.decode(response, type);
    }
}
