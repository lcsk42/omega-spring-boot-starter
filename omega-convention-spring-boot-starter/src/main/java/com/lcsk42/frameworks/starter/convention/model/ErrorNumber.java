package com.lcsk42.frameworks.starter.convention.model;

/**
 * 表示一个 3 位数字（0-999）的错误编号。 这是一个不可变的 record 类，会对输入进行验证。
 *
 * @param value 错误编号的整数值（闭区间 0-999）
 */
public record ErrorNumber(int value){
// 用于缓存频繁使用的错误编号（0-999）
private static final ErrorNumber[]CACHE=new ErrorNumber[1000];

/**
 * 紧凑型构造函数，用于验证输入值。 在标准构造函数之前运行。
 *
 * @throws IllegalArgumentException 如果数值不在 0-999 范围内
 */
public ErrorNumber{if(value<0||value>999){throw new IllegalArgumentException("Error number must be 3-digit (0-999)");}}

/**
 * 静态工厂方法，提供对象缓存功能。 对 0-999 的值返回缓存实例以减少对象创建。
 *
 * @param value 错误编号的整数值
 * @return 如果可用则返回缓存的 ErrorNumber 实例，否则返回新实例
 * @throws IllegalArgumentException 如果数值不在 0-999 范围内
 */
public static ErrorNumber of(int value){
// 首先检查缓存边界以提高性能
if(value>=0&&value<CACHE.length){ErrorNumber cached=CACHE[value];if(cached==null){
// 线程安全的延迟初始化
synchronized(CACHE){cached=CACHE[value];if(cached==null){cached=new ErrorNumber(value);CACHE[value]=cached;}}}return cached;}
// 对于超出缓存范围的值的回退方案（尽管构造函数会拒绝这些值）
return new ErrorNumber(value);}

/**
 * 返回错误编号的 3 位字符串表示。 必要时会填充前导零（例如 5 变为 "005"）。
 *
 * @return 格式化后的 3 位字符串
 */
@Override public String toString(){return String.format("%03d",value);}}
