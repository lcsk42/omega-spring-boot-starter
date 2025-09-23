package com.lcsk42.frameworks.starter.mq.rocket.model;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public final class MessageWrapper<T> {

    /**
     * key
     */
    @NonNull
    private String key;

    /**
     * 具体的消息体
     */
    @NonNull
    private T message;

    /**
     * 唯一标识
     */
    private String uuid = UUID.randomUUID().toString();

    /**
     * 时间戳
     */
    private Long timestamp = System.currentTimeMillis();

    public static <T> MessageWrapper<T> of(@Nonnull String key, @Nonnull T message) {
        return new MessageWrapper<>(key, message);
    }
}
