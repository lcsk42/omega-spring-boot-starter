package com.lcsk42.frameworks.starter.mq.rocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DelayLevel {
    LEVEL_0S(0, "0s"),
    LEVEL_1S(1, "1s"),
    LEVEL_5S(2, "5s"),
    LEVEL_10S(3, "10s"),
    LEVEL_30S(4, "30s"),
    LEVEL_1M(5, "1m"),
    LEVEL_2M(6, "2m"),
    LEVEL_3M(7, "3m"),
    LEVEL_4M(8, "4m"),
    LEVEL_5M(9, "5m"),
    LEVEL_6M(10, "6m"),
    LEVEL_7M(11, "7m"),
    LEVEL_8M(12, "8m"),
    LEVEL_9M(13, "9m"),
    LEVEL_10M(14, "10m"),
    LEVEL_20M(15, "20m"),
    LEVEL_30M(16, "30m"),
    LEVEL_1H(17, "1h"),
    LEVEL_2H(18, "2h"),
    ;

    private final int level;
    private final String description;
}
