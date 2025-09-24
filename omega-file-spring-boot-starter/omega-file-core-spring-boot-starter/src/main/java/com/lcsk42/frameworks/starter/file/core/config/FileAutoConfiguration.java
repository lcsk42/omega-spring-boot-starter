package com.lcsk42.frameworks.starter.file.core.config;

import com.lcsk42.frameworks.starter.file.core.enums.FileUploadType;
import com.lcsk42.frameworks.starter.file.core.service.FileService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Objects;
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
            List<FileService> fileServices = ServiceLoader.load(FileService.class).stream()
                    .map(ServiceLoader.Provider::get)
                    .toList();
            // 如果一个都没有加载到，抛出异常
            if (fileServices.isEmpty()) {
                throw new IllegalArgumentException("No FileService implementation available");
            }
            // 如果只有一个实现，直接返回该实现
            if (fileServices.size() == 1) {
                return fileServices.getFirst().of(properties);
            }
            // 如果有多个实现，根据配置的 type 选择对应的实现
            FileUploadType fileUploadType = properties.getFileUploadType();
            Objects.requireNonNull(fileUploadType, "File service type cannot be null");

            return fileServices.stream()
                    .filter(service -> fileUploadType.equals(service.getFileUploadType()))
                    .findFirst()
                    .map(service -> service.of(properties))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No FileService implementation available for type: " + fileUploadType));
        } catch (ServiceConfigurationError e) {
            throw new IllegalStateException("Failed to load FileService implementations", e);
        }
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'File' completed initialization.");
    }
}
