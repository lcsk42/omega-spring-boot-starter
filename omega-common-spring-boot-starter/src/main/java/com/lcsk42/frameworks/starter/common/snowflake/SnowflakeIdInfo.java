package com.lcsk42.frameworks.starter.common.snowflake;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 表示 Snowflake ID 的分解信息
 * <p>
 * Snowflake ID 是 Twitter 开发的分布式唯一 ID 生成算法， 通常包含以下组件：
 * <ul>
 * <li><b>timestamp</b> - 自纪元以来的毫秒数（通常使用自定义纪元）</li>
 * <li><b>datacenterId</b> - 数据中心标识符</li>
 * <li><b>workerId</b> - 工作机器标识符</li>
 * <li><b>sequence</b> - 同一毫秒内的递增序列号</li>
 * </ul>
 * 该类提供了这些组件的结构化分解。
 *
 * @see <a href="https://en.wikipedia.org/wiki/Snowflake_ID">Wikipedia 上的 Snowflake ID</a>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnowflakeIdInfo {
  /**
   * Snowflake ID 的时间戳组件（自纪元以来的毫秒数）
   */
  private long timestamp;

  /**
   * 数据中心标识符组件
   */
  private long datacenterId;

  /**
   * 工作机器标识符组件
   */
  private long workerId;

  /**
   * 序列号组件（用于同一毫秒内生成的 ID）
   */
  private long sequence;
}
