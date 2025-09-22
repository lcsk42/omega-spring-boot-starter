package com.lcsk42.frameworks.starter.web.converter;

import com.lcsk42.frameworks.starter.convention.enums.BaseEnum;
import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BaseEnum 参数转换器工厂
 */
@SuppressWarnings("rawtypes,unchecked")
public class BaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {

    private static final Map<Class, Converter> CONVERTER_CACHE = new ConcurrentHashMap<>();

    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(@Nonnull Class<T> targetType) {
        return CONVERTER_CACHE.computeIfAbsent(targetType, key -> new BaseEnumConverter<>(targetType));
    }
}
