package com.lcsk42.frameworks.starter.apidoc.config;

import io.swagger.v3.oas.models.Components;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * API 文档扩展配置属性
 */
@Setter
@Getter
@NoArgsConstructor
@ConfigurationProperties(prefix = ApiDocProperties.PREFIX)
public class ApiDocProperties {

    public static final String PREFIX = "framework.api-doc";

    /**
     * 组件配置（包括鉴权配置等）
     */
    @NestedConfigurationProperty
    private Components components;

    /**
     * ID
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 版本
     */
    private String version;

    /**
     * URL
     */
    private String url;

    /**
     * 基本包
     */
    private String basePackage;

    /**
     * 联系人
     */
    private Contact contact;

    /**
     * 许可协议
     */
    private License license;

    /**
     * 是否为生产环境
     */
    private boolean production = false;

    /**
     * 联系人配置属性
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Contact {
        /**
         * 名称
         */
        private String name;

        /**
         * 邮箱
         */
        private String email;

        /**
         * URL
         */
        private String url;
    }

    /**
     * 许可协议配置属性
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class License {
        /**
         * 名称
         */
        private String name;

        /**
         * URL
         */
        private String url;
    }
}
