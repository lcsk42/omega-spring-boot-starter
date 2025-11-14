package com.lcsk42.frameworks.starter.security.xss.configuration;

import com.lcsk42.frameworks.starter.security.xss.enums.XssMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = XssProperties.PREFIX)
public class XssProperties {
    public static final String PREFIX = "framework.security.xss";
    public static final String XSS_ENABLE = PREFIX + ".enabled";

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 拦截路由（默认为空）
     *
     * <p>
     * 当拦截的路由配置不为空，则根据该配置执行过滤
     * </p>
     */
    private List<String> includePatterns = new ArrayList<>();

    /**
     * 放行路由（默认为空）
     */
    private List<String> excludePatterns = new ArrayList<>();

    /**
     * XSS 模式
     */
    private XssMode mode = XssMode.CLEAN;
}
