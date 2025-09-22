package com.lcsk42.frameworks.starter.common.snowflake;

import com.lcsk42.frameworks.starter.common.util.IdUtil;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Twitter Snowflake ID 生成器实现
 * <p>
 * 生成由以下部分组成的 64 位唯一 ID：
 * <ul>
 * <li>时间戳（自自定义纪元以来的毫秒数）</li>
 * <li>数据中心 ID（5 位）</li>
 * <li>工作机器 ID（5 位）</li>
 * <li>序列号（12 位）</li>
 * </ul>
 *
 * <p>
 * 结构组成：[1 位未使用][41 位时间戳][5 位数据中心 ID][5 位工作机器 ID][12 位序列号]
 *
 * <p>
 * 线程安全实现，每个工作机器每毫秒可生成最多 4096 个唯一 ID
 *
 * @see <a href="https://en.wikipedia.org/wiki/Snowflake_ID">Snowflake ID 算法</a>
 */
public class Snowflake implements Serializable, IdGenerator {
  // 自定义纪元（2020-01-01 00:00:00 UTC）
  private final static long START_TIMESTAMP = 1577808000000L;
  // 位分配配置
  private final static long WORKER_ID_BITS = 5L; // 工作机器 ID 占用的位数
  private final static long DATACENTER_ID_BITS = 5L; // 数据中心 ID 占用的位数
  private final static long SEQUENCE_BITS = 12L; // 序列号占用的位数
  // 最大允许值
  private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS); // 最大工作机器 ID（31）
  private final static long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS); // 最大数据中心 ID（31）
  // 位偏移配置
  private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
  private final static long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
  private final static long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
  // 序列号掩码（0b111111111111=0xfff=4095）
  private final static long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
  // 实例配置
  private final long workerId; // 工作机器标识符（0-31）
  private final long datacenterId; // 数据中心标识符（0-31）
  private long sequence = 0L; // 序列号（0-4095）
  private long lastTimestamp = -1L; // 上次生成 ID 的时间戳

  /**
   * 默认构造函数，使用自动生成的工作机器 ID 和 数据中心 ID 初始化 使用 {@link IdUtil} 在允许的位范围内生成有效 ID
   */
  public Snowflake() {
    this(IdUtil.generateWorkerId(WORKER_ID_BITS), IdUtil.generateWorkerId(DATACENTER_ID_BITS));
  }

  /**
   * 构造 Snowflake ID 生成器实例
   *
   * @param workerId 工作机器 ID（0 ≤ workerId ≤ 31）
   * @param datacenterId 数据中心 ID（0 ≤ datacenterId ≤ 31）
   * @throws IllegalArgumentException 如果 ID 超出有效范围
   */
  public Snowflake(long workerId, long datacenterId) {
    Validate.isTrue(workerId >= 0 && workerId <= MAX_WORKER_ID, "工作机器 ID 不能大于 %d 或小于 0",
        MAX_WORKER_ID);
    Validate.isTrue(datacenterId >= 0 && datacenterId <= MAX_DATACENTER_ID, "数据中心 ID 不能大于 %d 或小于 0",
        MAX_DATACENTER_ID);
    this.workerId = workerId;
    this.datacenterId = datacenterId;
  }

  /**
   * 生成下一个唯一 ID（线程安全）
   *
   * @return 64 位 Snowflake ID
   * @throws RuntimeException 如果系统时钟回拨
   */
  @Override
  public synchronized long nextId() {
    long timestamp = timeGen();
    // 检测时钟回拨
    if (timestamp < lastTimestamp) {
      throw new RuntimeException(String.format("时钟回拨。拒绝为 %d 毫秒生成 ID", lastTimestamp - timestamp));
    }
    // 处理同一毫秒内的冲突
    if (lastTimestamp == timestamp) {
      sequence = (sequence + 1) & SEQUENCE_MASK;
      if (sequence == 0) {
        timestamp = tilNextMillis(lastTimestamp); // 序列号耗尽，等待下一毫秒
      }
    } else {
      // 使用随机值初始化序列号以避免可预测的 ID
      sequence = ThreadLocalRandom.current().nextLong(1, 3);
    }
    lastTimestamp = timestamp;
    // 组合各部件生成 ID
    return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
        | (datacenterId << DATACENTER_ID_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;
  }

  /**
   * 生成下一个唯一 ID 的字符串形式
   *
   * @return Snowflake ID 的字符串表示
   */
  @Override
  public synchronized String nextIdString() {
    return Long.toString(nextId());
  }

  /**
   * 当序列号耗尽时阻塞至下一毫秒
   *
   * @param lastTimestamp 上次使用的时间戳
   * @return 当前时间戳（毫秒）
   */
  protected long tilNextMillis(long lastTimestamp) {
    long timestamp = timeGen();
    while (timestamp <= lastTimestamp) {
      timestamp = timeGen();
    }
    return timestamp;
  }

  /**
   * 获取当前时间的毫秒数
   *
   * @return 当前时间戳（毫秒）
   */
  protected long timeGen() {
    return System.currentTimeMillis();
  }

  /**
   * 将 Snowflake ID 分解为其组成部分
   *
   * @param id 要解析的 Snowflake ID
   * @return {@link SnowflakeIdInfo} 包含时间戳、数据中心 ID、工作机器 ID 和序列号
   */
  public SnowflakeIdInfo parseId(long id) {
    return SnowflakeIdInfo.builder().timestamp((id >> TIMESTAMP_SHIFT) + START_TIMESTAMP)
        .datacenterId((id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID)
        .workerId((id >> WORKER_ID_SHIFT) & MAX_WORKER_ID).sequence(id & SEQUENCE_MASK).build();
  }
}
