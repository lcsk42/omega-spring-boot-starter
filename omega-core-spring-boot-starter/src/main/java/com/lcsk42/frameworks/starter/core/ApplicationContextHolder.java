package com.lcsk42.frameworks.starter.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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
public class ApplicationContextHolder implements BeanFactoryPostProcessor, ApplicationContextAware {

    /**
     * "@PostConstruct" 注解标记的类中，由于 ApplicationContext 还未加载，导致空指针<br>
     * 因此实现 BeanFactoryPostProcessor 注入 ConfigurableListableBeanFactory 实现 bean 的操作
     */
    private static ConfigurableListableBeanFactory beanFactory;

    // Spring 应用上下文的静态引用
    private static ApplicationContext applicationContext;

    /**
     * 根据类型获取 Spring 管理的 Bean
     *
     * @param clazz Bean 的类类型
     * @param <T> Bean 的类型
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return getBeanFactory().getBean(clazz);
    }

    /**
     * 根据名称获取 Spring 管理的 Bean
     *
     * @param name Bean 的名称
     * @return Bean 实例
     */
    public static Object getBean(String name) {
        return getBeanFactory().getBean(name);
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
        return getBeanFactory().getBean(name, clazz);
    }

    /**
     * 从 Spring 上下文中获取指定类型的所有 Bean
     *
     * @param clazz 要获取的 Bean 类型
     * @param <T> Bean 的类型
     * @return 包含 Bean 名称和实例的 Map
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return getBeanFactory().getBeansOfType(clazz);
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
        return getBeanFactory().findAnnotationOnBean(beanName, annotationType);
    }

    /**
     * 获取配置文件配置项的值
     *
     * @param key 配置项key
     * @return 属性值
     */
    public static String getProperty(String key) {
        if (null == applicationContext) {
            return null;
        }
        return applicationContext.getEnvironment().getProperty(key);
    }

    /**
     * 获取配置文件配置项的值
     *
     * @param key 配置项key
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static String getProperty(String key, String defaultValue) {
        if (null == applicationContext) {
            return null;
        }
        return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    /**
     * 获取配置文件配置项的值
     *
     * @param <T> 属性值类型
     * @param key 配置项key
     * @param targetType 配置项类型
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        if (null == applicationContext) {
            return null;
        }
        return applicationContext.getEnvironment().getProperty(key, targetType, defaultValue);
    }

    /**
     * 获取当前的 Spring {@link ApplicationContext} 实例
     *
     * @return 应用上下文
     */
    public static ApplicationContext getInstance() {
        return applicationContext;
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
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    /**
     * "@PostConstruct" 注解标记的类中，由于 ApplicationContext 还未加载，导致空指针<br>
     * 因此实现 BeanFactoryPostProcessor 注入 ConfigurableListableBeanFactory实现 bean 的操作
     */
    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
        ApplicationContextHolder.beanFactory = beanFactory;
    }

    /**
     * 获取{@link ListableBeanFactory}，可能为{@link ConfigurableListableBeanFactory} 或 {@link ApplicationContextAware}
     *
     * @return {@link ListableBeanFactory}
     */
    public static ListableBeanFactory getBeanFactory() {
        final ListableBeanFactory factory = null == beanFactory ? applicationContext : beanFactory;
        if (null == factory) {
            throw new RuntimeException(
                    "No ConfigurableListableBeanFactory or ApplicationContext injected, maybe not in the Spring environment?");
        }
        return factory;
    }
}
