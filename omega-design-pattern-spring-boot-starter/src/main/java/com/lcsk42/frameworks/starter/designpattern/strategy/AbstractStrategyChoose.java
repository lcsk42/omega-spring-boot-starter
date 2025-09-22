package com.lcsk42.frameworks.starter.designpattern.strategy;

import com.lcsk42.frameworks.starter.core.ApplicationContextHolder;
import com.lcsk42.frameworks.starter.core.init.ApplicationInitializingEvent;
import com.lcsk42.frameworks.starter.designpattern.exception.DesignPatternException;
import jakarta.annotation.Nonnull;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 策略选择器 - 根据给定的策略键选择并应用策略 支持直接匹配和基于正则模式匹配两种方式
 */
@SuppressWarnings("rawtypes,unchecked")
public class AbstractStrategyChoose implements ApplicationListener<ApplicationInitializingEvent> {

  // 存储策略名称到策略实例的映射表
  private final Map<String, AbstractExecuteStrategy> strategyMap = new HashMap<>();

  /**
   * 根据策略键选择策略 根据参数支持直接匹配或正则模式匹配
   *
   * @param strategyKey 标识策略的唯一键
   * @param usePatternMatch 为 true 时使用正则模式匹配进行策略选择
   * @return 选中的策略实例
   */
  public AbstractExecuteStrategy<?, ?> chooseStrategy(String strategyKey, Boolean usePatternMatch) {
    // 启用模式匹配时，通过正则表达式查找策略
    if (usePatternMatch != null && usePatternMatch) {
      return strategyMap.values().stream()
          // 检查策略是否支持模式匹配
          .filter(each -> StringUtils.hasText(each.getPatternMatchKey()))
          // 用正则表达式匹配策略键
          .filter(each -> Pattern.compile(each.getPatternMatchKey()).matcher(strategyKey).matches())
          .findFirst().orElseThrow(() -> new DesignPatternException("Strategy Undefined"));
    }
    // 否则进行精确匹配查找
    return Optional.ofNullable(strategyMap.get(strategyKey)).orElseThrow(
        () -> new DesignPatternException(String.format("[%s] Strategy Undefined", strategyKey)));
  }

  /**
   * 选择策略并执行其 accept 方法（无返回值） 适用于不需要返回值的策略场景
   *
   * @param strategyKey 标识目标策略的键
   * @param request 策略执行的输入数据
   */
  public <T> void chooseAndAccept(String strategyKey, T request) {
    // 选择策略并执行 accept 方法
    AbstractExecuteStrategy strategy = chooseStrategy(strategyKey, null);
    strategy.accept(request);
  }

  /**
   * 选择策略并执行其 accept 方法（无返回值） 当 usePatternMatch 为 true 时支持正则模式匹配
   *
   * @param strategyKey 标识目标策略的键
   * @param request 策略执行的输入数据
   * @param usePatternMatch 是否启用正则模式匹配的标志
   */
  public <T> void chooseAndAccept(String strategyKey, T request, Boolean usePatternMatch) {
    // 选择策略（支持模式匹配）并执行 accept 方法
    AbstractExecuteStrategy strategy = chooseStrategy(strategyKey, usePatternMatch);
    strategy.accept(request);
  }

  /**
   * 选择策略并执行其 apply 方法获取返回结果 适用于需要返回执行结果的策略场景
   *
   * @param strategyKey 标识目标策略的键
   * @param request 策略执行的输入数据
   * @param <T> 输入参数类型
   * @param <R> 返回值类型
   * @return 策略执行结果
   */
  public <T, R> R chooseAndApply(String strategyKey, T request) {
    // 选择策略并执行 apply 方法获取结果
    AbstractExecuteStrategy strategy = chooseStrategy(strategyKey, null);
    return (R) strategy.apply(request);
  }

  /**
   * 应用上下文初始化时触发的回调方法 扫描并注册所有可用策略到策略映射表
   *
   * @param event 应用初始化事件对象
   */
  @Override
  public void onApplicationEvent(@Nonnull ApplicationInitializingEvent event) {
    // 从应用上下文中获取所有 AbstractExecuteStrategy 类型的 bean
    Map<String, AbstractExecuteStrategy> strategies =
        ApplicationContextHolder.getBeansOfType(AbstractExecuteStrategy.class);
    // 按策略唯一名称注册每个策略
    strategies.forEach((beanName, strategy) -> {
      // 检查是否已存在同名策略
      AbstractExecuteStrategy<?, ?> existing = strategyMap.get(strategy.getStrategyName());
      if (existing != null) {
        throw new DesignPatternException(
            String.format("[%s] Duplicate Execution Strategy", strategy.getStrategyName()));
      }
      // 注册策略到映射表
      strategyMap.put(strategy.getStrategyName(), strategy);
    });
  }
}
