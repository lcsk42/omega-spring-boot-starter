package com.lcsk42.frameworks.starter.database.mybatisflex.config;

import com.lcsk42.frameworks.starter.core.YamlPropertySourceFactory;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@AllArgsConstructor
@AutoConfiguration
@MapperScan("${" + MybatisFlexProperties.MAPPER_PACKAGE + "}")
@EnableConfigurationProperties(MybatisFlexProperties.class)
@PropertySource(value = "classpath:default-data-mybatis-flex.yml",
        factory = YamlPropertySourceFactory.class)
public class MyBatisFlexAutoConfiguration {



    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'MyBatis Flex' completed initialization.");
    }
}
