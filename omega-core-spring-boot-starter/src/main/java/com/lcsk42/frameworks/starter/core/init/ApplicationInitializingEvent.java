package com.lcsk42.frameworks.starter.core.init;

import org.springframework.context.ApplicationEvent;

/**
 * 应用初始化事件
 * <p>
 * 规约事件，通过此事件可以查看业务系统所有初始化行为
 * </p>
 */
public class ApplicationInitializingEvent extends ApplicationEvent {

  /**
   * 构造一个新的 {@code ApplicationEvent}
   *
   * @param source 事件源对象或关联对象 (never {@code null})
   */
  public ApplicationInitializingEvent(Object source) {
    super(source);
  }
}
