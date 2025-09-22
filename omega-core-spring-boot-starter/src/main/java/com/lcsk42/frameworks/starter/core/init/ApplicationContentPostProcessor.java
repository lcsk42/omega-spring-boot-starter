package com.lcsk42.frameworks.starter.core.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 应用初始化后置处理器，防止Spring事件被多次执行
 */
@RequiredArgsConstructor
public class ApplicationContentPostProcessor implements ApplicationListener<ApplicationReadyEvent> {

  private final ApplicationContext applicationContext;

  /**
   * 执行标识，确保 Spring 事件 {@link ApplicationReadyEvent} 有且执行一次
   */
  private final AtomicBoolean executeOnlyOnce = new AtomicBoolean(false);

  @Override
  public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
    if (!executeOnlyOnce.compareAndSet(false, true)) {
      return;
    }
    applicationContext.publishEvent(new ApplicationInitializingEvent(this));
  }
}
