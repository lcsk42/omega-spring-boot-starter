package com.lcsk42.frameworks.starter.log.core.config;

import com.lcsk42.frameworks.starter.log.core.enums.Include;
import com.lcsk42.frameworks.starter.log.core.util.LogUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;
import java.util.Set;

/**
 * 日志配置属性
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(LogProperties.PREFIX)
public class LogProperties {

    public static final String PREFIX = "framework.log";

    /**
     * 访问日志配置
     */
    @NestedConfigurationProperty
    private AccessLogProperties accessLog = new AccessLogProperties();

    /**
     * 包含信息
     */
    private Set<Include> includes = Include.defaultIncludes();

    /**
     * 放行路由
     */
    private List<String> excludePatterns = List.of();

    /**
     * 是否匹配放行路由
     *
     * @param uri 请求 URI
     * @return 是否匹配
     */
    public boolean isMatch(String uri) {
        return this.getExcludePatterns().stream()
                .anyMatch(pattern -> LogUtil.isMatch(uri, pattern));
    }
}
