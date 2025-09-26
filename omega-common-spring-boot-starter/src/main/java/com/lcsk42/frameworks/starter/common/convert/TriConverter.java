package com.lcsk42.frameworks.starter.common.convert;

import java.util.List;

/**
 * 三种类型 (P, T, V) 之间三向转换的接口。
 * 提供常见的日期/时间转换的默认方法。
 *
 * @param <P> PO 类型（持久化对象）
 * @param <T> DTO 类型（数据传输对象）
 * @param <V> VO 类型（视图对象）
 */
public interface TriConverter<P, T, V> extends BaseConverter {
    /**
     * 将 DTO 类型 (T) 转换为 PO 类型 (P)。
     *
     * @param t DTO 类型的实例
     * @return 转换后的 PO 类型实例
     */
    P toP(T t);

    /**
     * 将 PO 类型 (P) 转换为 VO 类型 (V)。
     *
     * @param p PO 类型的实例
     * @return 转换后的 VO 类型实例
     */
    V toV(P p);

    /**
     * 将 DTO 类型 (T) 的列表转换为 PO 类型 (P)。
     *
     * @param tList DTO 类型实例的列表
     * @return 转换后的 PO 类型实例的列表
     */
    List<P> toP(List<T> tList);

    /**
     * 将 PO 类型 (P) 的列表转换为 VO 类型 (V)。
     *
     * @param pList PO 类型实例的列表
     * @return 转换后的 VO 类型实例的列表
     */
    List<V> toV(List<P> pList);
}
