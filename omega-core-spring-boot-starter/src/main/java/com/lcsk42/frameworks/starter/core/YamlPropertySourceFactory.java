package com.lcsk42.frameworks.starter.core;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * 用于加载 YAML 格式的配置文件
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

  @SuppressWarnings("NullableProblems")
  @Override
  public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource)
      throws IOException {
    YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
    List<PropertySource<?>> propertySources =
        loader.load(resource.getResource().getFilename(), resource.getResource());

    if (!propertySources.isEmpty()) {
      // 如果有多个文档块，只返回第一个
      return propertySources.getFirst();
    }

    throw new IllegalStateException(
        "No YAML property sources loaded from " + resource.getResource().getFilename());
  }
}
