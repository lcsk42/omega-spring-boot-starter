package com.lcsk42.frameworks.starter.database.mybatisplus.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.lcsk42.frameworks.starter.core.ApplicationContextHolder;
import com.lcsk42.frameworks.starter.core.YamlPropertySourceFactory;
import com.lcsk42.frameworks.starter.database.mybatisplus.handler.BasePOMetaObjectHandler;
import com.lcsk42.frameworks.starter.database.mybatisplus.handler.CompositeBaseEnumTypeHandler;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@AutoConfiguration
@MapperScan("${" + MybatisPlusProperties.MAPPER_PACKAGE + "}")
@EnableConfigurationProperties(MybatisPlusProperties.class)
@PropertySource(value = "classpath:default-data-mybatis-plus.yml",
        factory = YamlPropertySourceFactory.class)
public class MybatisPlusAutoProperties {

    /**
     * MyBatis Plus 配置
     */
    @Bean
    public MybatisPlusPropertiesCustomizer mybatisPlusPropertiesCustomizer() {
        return properties -> properties.getConfiguration()
                .setDefaultEnumTypeHandler(CompositeBaseEnumTypeHandler.class);
    }

    /**
     * MyBatis Plus 插件配置
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor(MybatisPlusProperties properties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 其他拦截器
        Map<String, InnerInterceptor> innerInterceptors =
                ApplicationContextHolder.getBeansOfType(InnerInterceptor.class);
        if (!innerInterceptors.isEmpty()) {
            innerInterceptors.values().forEach(interceptor::addInnerInterceptor);
        }
        // 分页插件
        MybatisPlusProperties.Pagination pagination = properties.getPagination();
        if (pagination != null) {
            interceptor.addInnerInterceptor(this.paginationInnerInterceptor(pagination));
        }
        // 乐观锁插件
        if (properties.isOptimisticLockerEnabled()) {
            interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        }
        // 防全表更新与删除插件
        if (properties.isBlockAttackPluginEnabled()) {
            interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        }
        return interceptor;
    }

    /**
     * ID 生成器配置
     */
    @Configuration
    @Import({MyBatisPlusIdGeneratorConfiguration.Default.class,
            MyBatisPlusIdGeneratorConfiguration.Custom.class})
    protected static class MyBatisPlusIdGeneratorAutoConfiguration {
    }

    /**
     * 分页插件配置（<a href=
     * "https://baomidou.com/pages/97710a/#paginationinnerinterceptor">PaginationInnerInterceptor</a>）
     */
    private PaginationInnerInterceptor paginationInnerInterceptor(
            MybatisPlusProperties.Pagination pagination) {
        // 对于单一数据库类型来说，都建议配置该值，避免每次分页都去抓取数据库类型
        PaginationInnerInterceptor paginationInnerInterceptor =
                new PaginationInnerInterceptor(pagination.getDbType());
        paginationInnerInterceptor.setOverflow(pagination.isOverflow());
        paginationInnerInterceptor.setMaxLimit(pagination.getMaxLimit());
        return paginationInnerInterceptor;
    }

    /**
     * 字段自动补全
     */
    @Bean
    public BasePOMetaObjectHandler myMetaObjectHandler() {
        return new BasePOMetaObjectHandler();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'MyBatis Plus' completed initialization.");
    }
}
