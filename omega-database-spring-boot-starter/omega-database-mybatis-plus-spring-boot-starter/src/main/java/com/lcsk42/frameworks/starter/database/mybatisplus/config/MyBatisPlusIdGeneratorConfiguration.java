package com.lcsk42.frameworks.starter.database.mybatisplus.config;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.lcsk42.frameworks.starter.common.util.NetworkUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;

/**
 * MyBatis ID 生成器配置
 */
@Slf4j
@NoArgsConstructor
public class MyBatisPlusIdGeneratorConfiguration {

    /**
     * 自定义 ID 生成器-默认（雪花算法，使用网卡信息绑定雪花生成器，防止集群雪花 ID 重复）
     */
    @ConditionalOnMissingBean(IdentifierGenerator.class)
    @ConditionalOnProperty(name = MybatisPlusProperties.ID_GENERATOR_TYPE, havingValue = "default", matchIfMissing = true)
    public static class Default {
        static {
            log.debug("[Omega] - Auto Configuration 'MyBatis Plus-IdGenerator-Default' completed initialization.");
        }

        @Bean
        public IdentifierGenerator identifierGenerator() {
            return new DefaultIdentifierGenerator(NetworkUtil.getLocalhost());
        }
    }

    /**
     * 自定义 ID 生成器
     */
    @ConditionalOnProperty(name = MybatisPlusProperties.ID_GENERATOR_TYPE, havingValue = "custom")
    public static class Custom {
        @Bean
        @ConditionalOnMissingBean
        public IdentifierGenerator identifierGenerator() {
            if (log.isErrorEnabled()) {
                log.error("Consider defining a bean of type '{}' in your configuration.",
                        ResolvableType.forClass(IdentifierGenerator.class)
                );
            }
            throw new NoSuchBeanDefinitionException(IdentifierGenerator.class);
        }
    }
}