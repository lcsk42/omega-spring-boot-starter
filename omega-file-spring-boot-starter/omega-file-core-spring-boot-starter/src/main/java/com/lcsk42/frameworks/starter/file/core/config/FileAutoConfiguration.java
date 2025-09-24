package com.lcsk42.frameworks.starter.file.core.config;

import com.lcsk42.frameworks.starter.file.core.service.FileService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties({FileUploadProperties.class})
public class FileAutoConfiguration {

    private final FileUploadProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public FileService fileService() {
        try {
            return ServiceLoader.load(FileService.class).stream().map(ServiceLoader.Provider::get)
                    .findFirst()
                    .map(service -> service.of(properties))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No FileService implementation available"));
        } catch (ServiceConfigurationError e) {
            throw new IllegalStateException("Failed to load FileService implementations", e);
        }
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'File' completed initialization.");
    }
}
