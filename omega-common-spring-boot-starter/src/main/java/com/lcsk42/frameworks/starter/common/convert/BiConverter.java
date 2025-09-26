package com.lcsk42.frameworks.starter.common.convert;

import org.mapstruct.InheritInverseConfiguration;

import java.util.List;

/**
 * 两种类型 (S 和 T) 之间双向转换的接口。
 * 提供常见的日期/时间转换的默认方法。
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 */
public interface BiConverter<S, T> extends BaseConverter {
    /**
     * 将目标类型 (T) 转换为源类型 (S)。
     *
     * @param t 目标类型的实例
     * @return 转换后的源类型实例
     */
    S toS(T t);

    /**
     * 将源类型 (S) 转换为目标类型 (T)。
     *
     * @param s 源类型的实例
     * @return 转换后的目标类型实例
     */
    @InheritInverseConfiguration
    T toT(S s);

    /**
     * 将目标类型 (T) 的列表转换为源类型 (S)。
     *
     * @param tList 目标类型实例的列表
     * @return 转换后的源类型实例的列表
     */
    List<S> toS(List<T> tList);

    /**
     * 将源类型 (S) 的列表转换为目标类型 (T)。
     *
     * @param sList 源类型实例的列表
     * @return 转换后的目标类型实例的列表
     */
    List<T> toT(List<S> sList);
}
