package com.lcsk42.frameworks.starter.designpattern.chain;

import com.lcsk42.frameworks.starter.core.ApplicationContextHolder;
import com.lcsk42.frameworks.starter.designpattern.exception.DesignPatternException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象责任链上下文，负责管理和执行责任链模式。
 *
 * <p>
 * 本类负责管理责任链的各个组件，并根据提供的 "标记" 执行它们。同时会在应用启动时初始化并排序责任链处理器。
 * </p>
 *
 * @param <T> 责任链处理器将会处理的请求参数类型
 */

@SuppressWarnings("rawtypes")
public final class AbstractChainContext<T> implements CommandLineRunner {

    // 用于存储每个链标识符 (mark) 对应的责任链处理器列表的容器
    private final Map<String, List<AbstractChainHandler>> abstractChainHandlerContainer =
            new HashMap<>();

    /**
     * 执行指定标记对应责任链组件的处理流程。
     *
     * <p>
     * 此方法会根据给定的 "标记" 查找对应的处理器，并按照 {@link Ordered} 接口定义的顺序执行它们。 如果找不到对应标记的处理器，则会抛出
     * {@link DesignPatternException} 异常。
     * </p>
     *
     * @param handlerName 责任链组件的标识符
     * @param input 需要在责任链中传递的参数
     * @throws DesignPatternException 当找不到对应标记的处理器时抛出
     */
    @SuppressWarnings("unchecked")
    public void accept(String handlerName, T input) {
        List<AbstractChainHandler> abstractChainHandlers =
                abstractChainHandlerContainer.get(handlerName);
        if (CollectionUtils.isEmpty(abstractChainHandlers)) {
            throw new DesignPatternException(
                    String.format("[%s] Chain of Responsibility ID is undefined.", handlerName));
        }
        // 依次执行链中的每个 accept 方法
        abstractChainHandlers.forEach(each -> each.accept(input));
    }

    /**
     * 在应用启动时初始化责任链处理器。
     *
     * <p>
     * 此方法在应用上下文初始化后运行，会根据处理器的 {@link Ordered} 值进行排序， 确保处理器能够按照正确的顺序执行。
     * </p>
     *
     * @param args 传递给应用的命令行参数
     * @throws Exception 初始化过程中发生错误时抛出
     */
    @Override
    public void run(String... args) throws Exception {
        // 从应用上下文中获取所有 AbstractChainHandler 类型的 bean
        Map<String, AbstractChainHandler> chainFilterMap =
                ApplicationContextHolder.getBeansOfType(AbstractChainHandler.class);

        chainFilterMap.forEach((beanName, bean) -> {
            // 获取或初始化指定处理器名称对应的处理器列表
            List<AbstractChainHandler> abstractChainHandlers =
                    abstractChainHandlerContainer.get(bean.getHandlerName());
            if (CollectionUtils.isEmpty(abstractChainHandlers)) {
                abstractChainHandlers = new ArrayList<>();
            }
            abstractChainHandlers.add(bean);

            // 根据 order 值 (Ordered 接口) 对处理器进行排序
            List<AbstractChainHandler> actualAbstractChainHandlers =
                    abstractChainHandlers.stream().sorted(Comparator.comparing(Ordered::getOrder))
                            .toList();

            // 用排序后的处理器更新容器
            abstractChainHandlerContainer.put(bean.getHandlerName(), actualAbstractChainHandlers);
        });
    }
}
