package com.lcsk42.frameworks.starter.apidoc.config;

import com.lcsk42.frameworks.starter.apidoc.handler.BaseEnumParameterHandler;
import com.lcsk42.frameworks.starter.apidoc.handler.OpenApiHandler;
import com.lcsk42.frameworks.starter.core.YamlPropertySourceFactory;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * API 文档自动配置
 */
@Slf4j
@EnableWebMvc
@AutoConfiguration(before = SpringDocConfiguration.class)
@EnableConfigurationProperties({ApiDocProperties.class})
@PropertySource(value = "classpath:default-api-doc.yml", factory = YamlPropertySourceFactory.class)
public class ApiDocAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/");
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());
    }

    /**
     * Open API 配置
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openApi(ApiDocProperties properties) {
        Info info = new Info().title("%s %s".formatted(properties.getName(), "API 文档"))
                .version(properties.getVersion())
                .description(properties.getDescription());
        ApiDocProperties.Contact contact = properties.getContact();
        if (contact != null) {
            info.contact(new Contact().name(contact.getName()).email(contact.getEmail())
                    .url(contact.getUrl()));
        }
        ApiDocProperties.License license = properties.getLicense();
        if (license != null) {
            info.license(new License().name(license.getName()).url(license.getUrl()));
        }
        OpenAPI openApi = new OpenAPI();
        openApi.info(info);
        Components components = properties.getComponents();
        if (components != null) {
            openApi.components(components);
            // 鉴权配置
            Map<String, SecurityScheme> securitySchemeMap = components.getSecuritySchemes();
            if (MapUtils.isNotEmpty(securitySchemeMap)) {
                SecurityRequirement securityRequirement = new SecurityRequirement();
                securitySchemeMap.values().stream().map(SecurityScheme::getName)
                        .forEach(securityRequirement::addList);
                openApi.addSecurityItem(securityRequirement);
            }
        }
        return openApi;
    }

    /**
     * 全局自定义配置（全局添加鉴权参数）
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer(ApiDocProperties properties) {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((s, pathItem) -> {
                    // 为所有接口添加鉴权
                    Components components = properties.getComponents();
                    if (components != null
                            && MapUtils.isNotEmpty(components.getSecuritySchemes())) {
                        Map<String, SecurityScheme> securitySchemeMap =
                                components.getSecuritySchemes();
                        pathItem.readOperations().forEach(operation -> {
                            SecurityRequirement securityRequirement = new SecurityRequirement();
                            securitySchemeMap.values().stream().map(SecurityScheme::getName)
                                    .forEach(securityRequirement::addList);
                            operation.addSecurityItem(securityRequirement);
                        });
                    }
                });
            }
        };
    }

    /**
     * 自定义 OpenApi 处理器
     */
    @Bean
    public OpenAPIService openApiBuilder(Optional<OpenAPI> openAPI, SecurityService securityParser,
            SpringDocConfigProperties springDocConfigProperties,
            PropertyResolverUtils propertyResolverUtils,
            Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomisers,
            Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomisers,
            Optional<JavadocProvider> javadocProvider) {
        return new OpenApiHandler(openAPI, securityParser, springDocConfigProperties,
                propertyResolverUtils,
                openApiBuilderCustomisers, serverBaseUrlCustomisers, javadocProvider);
    }

    /**
     * 自定义 BaseEnum 枚举参数配置（针对实现了 BaseEnum 的枚举，优化其枚举值和描述展示）
     *
     * @return {@link BaseEnumParameterHandler }
     */
    @Bean
    public BaseEnumParameterHandler customParameterCustomizer() {
        return new BaseEnumParameterHandler();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'ApiDoc' completed initialization.");
    }
}
