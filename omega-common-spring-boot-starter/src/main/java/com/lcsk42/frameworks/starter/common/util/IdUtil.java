package com.lcsk42.frameworks.starter.common.util;

import com.lcsk42.frameworks.starter.common.snowflake.Snowflake;
import com.lcsk42.frameworks.starter.core.Singleton;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用于生成各类唯一标识符的工具类。 提供 UUID 生成、Snowflake ID 生成以及 worker/datacenter ID 计算方法。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdUtil {

  /**
   * 生成标准带连字符的 UUID (通用唯一标识符)。 示例: "550e8400-e29b-41d4-a716-446655440000"
   *
   * @return 带连字符的随机生成 UUID 字符串
   */
  public static String generateStandardUuid() {
    return UUID.randomUUID().toString();
  }

  /**
   * 生成不带连字符的紧凑型 UUID。 示例: "550e8400e29b41d4a716446655440000"
   *
   * @return 不带连字符的随机生成 UUID 字符串
   */
  public static String generateCompactUuid() {
    return UUID.randomUUID().toString().replace(StringConstant.DASHED, StringUtils.EMPTY);
  }

  /**
   * 基于主机信息生成 worker ID，若主机信息不可用则生成随机值。 尽可能使用主机名和 MAC 地址创建确定性 ID。 若无法获取主机信息，则在指定比特范围内回退为随机值。
   *
   * @param workerBits 分配给 worker ID 的比特数（通常为 5-10 bits）
   * @return 指定比特范围内的 worker ID（0 至 2^workerBits - 1）
   * @throws RuntimeException 若无法访问网络信息（已在内部处理）
   */
  public static long generateWorkerId(long workerBits) {
    try {
      String hostname = InetAddress.getLocalHost().getHostName();
      NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
      byte[] mac = network.getHardwareAddress();
      int hashCode = hostname.hashCode();
      if (mac != null) {
        for (byte b : mac) {
          hashCode += b;
        }
      }
      long maxWorkerId = ~(-1L << workerBits);
      return (hashCode & maxWorkerId);
    } catch (Exception e) {
      long maxWorkerId = ~(-1L << workerBits);
      return ThreadLocalRandom.current().nextLong(0, maxWorkerId + 1);
    }
  }

  /**
   * 基于 IP 地址生成 datacenter ID，若 IP 信息不可用则生成随机值。 尽可能使用主机 IP 地址创建确定性 ID。 若无法获取 IP 信息，则在指定比特范围内回退为随机值。
   *
   * @param datacenterBits 分配给 datacenter ID 的比特数（通常为 5 bits）
   * @return 指定比特范围内的 datacenter ID（0 至 2^datacenterBits - 1）
   * @throws RuntimeException 若无法访问网络信息（已在内部处理）
   */
  public static long generateDatacenterId(long datacenterBits) {
    try {
      String ip = InetAddress.getLocalHost().getHostAddress();
      int hashCode = ip.hashCode();
      long maxDatacenterId = ~(-1L << datacenterBits);
      return (hashCode & maxDatacenterId);
    } catch (Exception e) {
      long maxDatacenterId = ~(-1L << datacenterBits);
      return ThreadLocalRandom.current().nextLong(0, maxDatacenterId + 1);
    }
  }

  /**
   * 获取或创建 Snowflake ID 生成器的单例实例。 若单例注册表中不存在实例，则创建新实例并注册。
   *
   * @return Snowflake ID 生成器的单例实例
   */
  public static Snowflake getSnowflake() {
    Snowflake snowflake = Singleton.get(Snowflake.class.getName());
    if (Objects.isNull(snowflake)) {
      snowflake = new Snowflake();
      Singleton.put(snowflake);
    }
    return snowflake;
  }

  /**
   * 使用 Snowflake 算法生成下一个唯一 ID。 合并了 getSnowflake() 和 nextId() 调用的便捷方法。
   *
   * @return 长整型格式的下一个唯一 ID
   */
  public static long getSnowflakeNextId() {
    return getSnowflake().nextId();
  }

  /**
   * 使用 Snowflake 算法生成下一个唯一 ID 并返回字符串格式。 合并了 getSnowflake() 和 nextIdString() 调用的便捷方法。
   *
   * @return 字符串表示的下一个唯一 ID
   */
  public static String getSnowflakeNextIdString() {
    return getSnowflake().nextIdString();
  }
}
