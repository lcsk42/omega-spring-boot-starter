package com.lcsk42.frameworks.starter.json.jackson.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.lcsk42.frameworks.starter.convention.enums.BaseEnum;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 枚举接口 BaseEnum 反序列化器
 */
@SuppressWarnings("rawtypes")
@JacksonStdImpl
public class BaseEnumDeserializer extends JsonDeserializer<BaseEnum> {

    /**
     * 静态实例
     */
    public static final BaseEnumDeserializer INSTANCE = new BaseEnumDeserializer();

    @Override
    public BaseEnum deserialize(JsonParser jsonParser,
            DeserializationContext deserializationContext)
            throws IOException {
        Class<?> targetClass = jsonParser.getCurrentValue().getClass();
        String fieldName = jsonParser.getCurrentName();
        String value = jsonParser.getText();
        return this.getEnum(targetClass, value, fieldName);
    }

    /**
     * 通过某字段对应值获取枚举实例，获取不到时为 {@code null}
     *
     * @param targetClass 目标类型
     * @param value 字段值
     * @param fieldName 字段名
     * @return 对应枚举实例 ，获取不到时为 {@code null}
     */
    private BaseEnum getEnum(Class<?> targetClass, String value, String fieldName) {
        Field field = FieldUtils.getField(targetClass, fieldName);
        Class<?> fieldTypeClass = field.getType();
        Object[] enumConstants = fieldTypeClass.getEnumConstants();
        for (Object enumConstant : enumConstants) {
            if (ClassUtils.isAssignable(BaseEnum.class, fieldTypeClass)) {
                BaseEnum baseEnum = (BaseEnum) enumConstant;
                if (Objects.equals(String.valueOf(baseEnum.getValue()), value)) {
                    return baseEnum;
                }
            }
        }
        return null;
    }
}
