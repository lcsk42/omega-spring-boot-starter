package com.lcsk42.frameworks.starter.json.jackson.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.lcsk42.frameworks.starter.convention.enums.BaseEnum;
import com.lcsk42.frameworks.starter.core.YamlPropertySourceFactory;
import com.lcsk42.frameworks.starter.json.jackson.serializer.BaseEnumDeserializer;
import com.lcsk42.frameworks.starter.json.jackson.serializer.BaseEnumSerializer;
import com.lcsk42.frameworks.starter.json.jackson.serializer.BigNumberSerializer;
import com.lcsk42.frameworks.starter.json.jackson.serializer.SimpleDeserializersWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Slf4j
@AllArgsConstructor
@AutoConfiguration
@EnableConfigurationProperties(JsonJacksonProperties.class)
@PropertySource(value = "classpath:default-json-jackson.yml",
        factory = YamlPropertySourceFactory.class)
public class JacksonAutoConfiguration {
    private final JsonJacksonProperties properties;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            JavaTimeModule javaTimeModule = this.javaTimeModule();
            SimpleModule baseEnumModule = this.baseEnumModule();
            SimpleModule bigNumberModule = this.bigNumberModule();

            builder.timeZone(TimeZone.getDefault());
            builder.modules(javaTimeModule, baseEnumModule, bigNumberModule);
            log.debug("[Omega] - Auto Configuration 'Jackson' completed initialization.");
        };
    }

    /**
     * 日期时间序列化及反序列化配置
     *
     * @return {@link JavaTimeModule}
     */
    private JavaTimeModule javaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 针对时间类型：LocalDateTime 的序列化和反序列化处理
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(dateTimeFormatter));
        // 针对时间类型：LocalDate 的序列化和反序列化处理
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        // 针对时间类型：LocalTime 的序列化和反序列化处理
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        // 针对时间类型：Instant 的序列化和反序列化处理
        javaTimeModule.addSerializer(Instant.class, InstantSerializer.INSTANCE);
        javaTimeModule.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
        // 针对时间类型：Duration 的序列化和反序列化处理
        javaTimeModule.addSerializer(Duration.class, DurationSerializer.INSTANCE);
        javaTimeModule.addDeserializer(Duration.class, DurationDeserializer.INSTANCE);
        return javaTimeModule;
    }

    /**
     * 枚举序列化及反序列化配置
     *
     * @return SimpleModule
     */
    private SimpleModule baseEnumModule() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BaseEnum.class, BaseEnumSerializer.INSTANCE);
        SimpleDeserializersWrapper deserializers = new SimpleDeserializersWrapper();
        deserializers.addDeserializer(BaseEnum.class, BaseEnumDeserializer.INSTANCE);
        simpleModule.setDeserializers(deserializers);
        return simpleModule;
    }

    /**
     * 大数值序列化及反序列化配置
     *
     * @return SimpleModule
     */
    private SimpleModule bigNumberModule() {
        SimpleModule bigNumberModule = new SimpleModule();
        switch (properties.getBigNumberSerializeMode()) {
            case FLEXIBLE -> {
                bigNumberModule.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
                bigNumberModule.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
                bigNumberModule.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
            }
            case TO_STRING -> {
                bigNumberModule.addSerializer(Long.class, ToStringSerializer.instance);
                bigNumberModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
                bigNumberModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
            }
            default -> log
                .warn("[Omega] - Jackson big number serialization mode: NO_OPERATION - values exceeding JavaScript range may lose precision.");
        }
        return bigNumberModule;
    }
}
