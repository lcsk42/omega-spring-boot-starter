package com.lcsk42.frameworks.starter.cache.redisson.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = RedisDistributedProperties.PREFIX)
public class RedisDistributedProperties {

  public static final String PREFIX = "framework.cache.redis";

  /**
   * 键前缀
   */
  private String prefix = "";

  /**
   * 键前缀使用的字符集
   */
  private String prefixCharset = StandardCharsets.UTF_8.name();

  /**
   * 值的默认超时时间 (单位: 毫秒)
   */
  private Long valueTimeout = 30 * 1_000L;

  /**
   * 值超时的时间单位
   */
  private TimeUnit valueTimeUnit = TimeUnit.MILLISECONDS;
}
