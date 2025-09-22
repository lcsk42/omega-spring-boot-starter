package com.lcsk42.frameworks.starter.convention.enums;

import java.util.Objects;

/**
 * 一个通用接口，用于具有值和描述信息的枚举类型
 *
 * @param <T> 枚举值的类型
 */
public interface BaseEnum<T> {

  /**
   * 获取枚举的底层值
   *
   * @return 与此枚举常量关联的值
   */
  T getValue();

  /**
   * 获取枚举的人类可读描述
   *
   * @return 此枚举常量的描述性文本
   */
  String getDescription();

  /**
   * 根据值查找枚举常量。
   *
   * @param <E> 实现 BaseEnum 的枚举类型
   * @param <T> 值的类型
   * @param value 要查找的值
   * @param enumClass 要搜索的枚举类
   * @return 匹配的枚举常量，如果未找到则返回 null
   */
  static <E extends Enum<E> & BaseEnum<T>, T> E fromValue(T value, Class<E> enumClass) {
    Objects.requireNonNull(enumClass, "Enum class cannot be null");

    for (E enumConstant : enumClass.getEnumConstants()) {
      if (Objects.equals(enumConstant.getValue(), value)) {
        return enumConstant;
      }
    }
    return null;
  }

  /**
   * 根据描述信息查找枚举常量
   *
   * @param <E> 实现 BaseEnum 的枚举类型
   * @param description 要查找的描述信息
   * @param enumClass 要搜索的枚举类
   * @return 匹配的枚举常量，如果未找到则返回 null
   */
  @SuppressWarnings("rawtypes,unchecked")
  static <E extends Enum<E> & BaseEnum> E fromDescription(String description, Class<?> enumClass) {
    Objects.requireNonNull(enumClass, "Enum class cannot be null");

    for (Object enumConstant : enumClass.getEnumConstants()) {
      if (enumConstant instanceof BaseEnum<?> baseEnum
          && Objects.equals(baseEnum.getDescription(), description)) {
        return (E) enumConstant;
      }
    }
    return null;
  }

  /**
   * 检查给定值在指定枚举类中是否有效
   *
   * @param <E> 实现BaseEnum的枚举类型
   * @param value 要检查的值
   * @param enumClass 用于验证的枚举类
   * @return 如果值存在于枚举中则返回 true，否则返回 false
   */
  default <E extends Enum<E> & BaseEnum<T>> boolean isValidValue(T value, Class<E> enumClass) {
    return fromValue(value, enumClass) != null;
  }
}
