package com.lcsk42.frameworks.starter.web.converter;

import com.lcsk42.frameworks.starter.convention.enums.BaseEnum;
import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * BaseEnum 参数转换器
 */
public class BaseEnumConverter<T extends BaseEnum<T>> implements Converter<String, T> {

    private final Map<String, T> enumMap = new HashMap<>();

    public BaseEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(String.valueOf(e.getValue()), e);
        }
    }

    @Override
    public T convert(@Nonnull String source) {
        return enumMap.get(source);
    }
}
