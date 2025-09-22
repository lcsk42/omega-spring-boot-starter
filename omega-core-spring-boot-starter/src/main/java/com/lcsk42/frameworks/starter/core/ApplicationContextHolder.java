package com.lcsk42.frameworks.starter.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 应用上下文持有器
 * <p>
 * 一个用于全局持有和访问 Spring {@link ApplicationContext} 的工具类。 适用于在非托管组件中访问 Spring 管理的 Bean。
 * </p>
 */
public class ApplicationContextHolder implements ApplicationContextAware {

  // Spring 应用上下文的静态引用
  private static ApplicationContext context;

  /**
   * 根据类型获取 Spring 管理的 Bean
   *
   * @param clazz Bean 的类类型
   * @param <T> Bean 的类型
   * @return Bean实例
   */
  public static <T> T getBean(Class<T> clazz) {
    return context.getBean(clazz);
  }

  /**
   * 根据名称获取 Spring 管理的 Bean
   *
   * @param name Bean 的名称
   * @return Bean 实例
   */
  public static Object getBean(String name) {
    return context.getBean(name);
  }

  /**
   * 根据名称和类型获取 Spring 管理的 Bean
   *
   * @param name Bean 的名称
   * @param clazz Bean 的期望类型
   * @param <T> Bean 的类型
   * @return Bean实例
   */
  public static <T> T getBean(String name, Class<T> clazz) {
    return context.getBean(name, clazz);
  }

  /**
   * 从 Spring 上下文中获取指定类型的所有 Bean
   *
   * @param clazz 要获取的 Bean 类型
   * @param <T> Bean 的类型
   * @return 包含 Bean 名称和实例的 Map
   */
  public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
    return context.getBeansOfType(clazz);
  }

  /**
   * 在指定 Bean 上查找特定注解
   *
   * @param beanName Bean 名称
   * @param annotationType 要查找的注解类型
   * @param <A> 注解类型
   * @return 注解实例（如果存在），否则返回 null
   */
  public static <A extends Annotation> A findAnnotationOnBean(String beanName,
      Class<A> annotationType) {
    return context.findAnnotationOnBean(beanName, annotationType);
  }

  /**
   * 获取当前的 Spring {@link ApplicationContext} 实例
   *
   * @return 应用上下文
   */
  public static ApplicationContext getInstance() {
    return context;
  }

  /**
   * 设置 Spring 应用上下文 此方法会在 Spring 上下文初始化时自动调用
   *
   * @param applicationContext 要设置的应用上下文
   * @throws BeansException 如果设置上下文失败
   */
  @Override
  @SuppressWarnings("squid:S2696")
  public void setApplicationContext(@NonNull ApplicationContext applicationContext)
      throws BeansException {
    ApplicationContextHolder.context = applicationContext;
  }
}
