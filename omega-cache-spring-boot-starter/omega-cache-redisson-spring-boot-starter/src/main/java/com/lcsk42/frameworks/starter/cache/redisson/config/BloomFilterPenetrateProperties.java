package com.lcsk42.frameworks.starter.cache.redisson.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = BloomFilterPenetrateProperties.PREFIX)
public class BloomFilterPenetrateProperties {

  public static final String PREFIX = "framework.cache.redis.bloom-filter.default";

  /**
   * 是否开启 boolean 过滤器
   */
  private Boolean enable = false;

  /**
   * Bloom 过滤器实例的默认名称
   */
  private String name = "cache_penetration_bloom_filter";

  /**
   * 每个元素的预期插入次数
   */
  private Long expectedInsertions = 64L;

  /**
   * 预期误判概率
   */
  private Double falseProbability = 0.03D;
}
