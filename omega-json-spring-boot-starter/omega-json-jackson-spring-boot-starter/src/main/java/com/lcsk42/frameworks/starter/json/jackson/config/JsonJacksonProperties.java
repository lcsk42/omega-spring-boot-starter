package com.lcsk42.frameworks.starter.json.jackson.config;


import com.lcsk42.frameworks.starter.json.core.enums.BigNumberSerializeMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = JsonJacksonProperties.PREFIX)
public class JsonJacksonProperties {
  public static final String PREFIX = "framework.json.jackson";

  /**
   * 大数值序列化模式
   */
  private BigNumberSerializeMode bigNumberSerializeMode = BigNumberSerializeMode.FLEXIBLE;
}
