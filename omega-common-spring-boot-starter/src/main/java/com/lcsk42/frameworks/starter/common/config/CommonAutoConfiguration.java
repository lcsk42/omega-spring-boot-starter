package com.lcsk42.frameworks.starter.common.config;

import com.lcsk42.frameworks.starter.common.threadpool.build.ThreadPoolBuilder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.Executor;

@Slf4j
@AutoConfigureBefore(TaskExecutionAutoConfiguration.class)
public class CommonAutoConfiguration {
    /**
     * 创建主任务执行器 bean。 配置默认线程池包含以下特性： - 线程名前缀 "default-pool-" - 非守护线程
     *
     * @return 配置好的 ThreadPoolExecutor 实例
     */
    @Bean
    @Primary
    public Executor taskExecutor() {
        return ThreadPoolBuilder.builder().threadFactory("default-pool-", false).build();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'Common' completed initialization.");
    }
}
