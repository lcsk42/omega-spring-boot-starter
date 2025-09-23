package com.lcsk42.frameworks.starter.web.config;

import com.lcsk42.frameworks.starter.web.converter.BaseEnumConverterFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
public class WebConfiguration implements WebMvcConfigurer {
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverterFactory(new BaseEnumConverterFactory());
  }

  @PostConstruct
  public void postConstruct() {
    log.debug("[Omega] - Auto Configuration 'Web MVC' completed initialization.");
  }
}
