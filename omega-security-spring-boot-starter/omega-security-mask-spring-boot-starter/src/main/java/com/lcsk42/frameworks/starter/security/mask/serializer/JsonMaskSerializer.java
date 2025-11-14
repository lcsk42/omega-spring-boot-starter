package com.lcsk42.frameworks.starter.security.mask.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.lcsk42.frameworks.starter.core.ApplicationContextHolder;
import com.lcsk42.frameworks.starter.security.mask.annotation.JsonMask;
import com.lcsk42.frameworks.starter.security.mask.strategy.MaskStrategy;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class JsonMaskSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private JsonMask jsonMask;

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
        if (StringUtils.isBlank(s)) {
            jsonGenerator.writeString(StringUtils.EMPTY);
            return;
        }
        // 使用自定义脱敏策略
        Class<? extends MaskStrategy> strategyClass = jsonMask.strategy();
        MaskStrategy maskStrategy = strategyClass != MaskStrategy.class
                ? ApplicationContextHolder.getBean(strategyClass)
                : jsonMask.value();
        jsonGenerator.writeString(
                maskStrategy.mask(s, jsonMask.character(), jsonMask.left(), jsonMask.right()));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider,
            BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty == null) {
            return serializerProvider.findNullValueSerializer(null);
        }
        if (!Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        JsonMask jsonMaskAnnotation = ObjectUtils.defaultIfNull(
                beanProperty.getAnnotation(JsonMask.class),
                beanProperty.getContextAnnotation(JsonMask.class));
        if (jsonMaskAnnotation == null) {
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return new JsonMaskSerializer(jsonMaskAnnotation);
    }
}
