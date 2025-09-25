package com.lcsk42.frameworks.starter.log.core.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 访问日志配置属性
 */
@Getter
@Setter
@NoArgsConstructor
public class AccessLogProperties {

    /**
     * 是否启用
     * <p>
     * 不记录请求日志也支持开启打印访问日志
     * </p>
     */
    private Boolean enabled = false;

    /**
     * 是否打印请求参数（body/query/form）
     * <p>开启后，访问日志会打印请求参数</p>
     */
    private Boolean printRequest = false;

    /**
     * 是否自动截断超长参数值（如 base64、大文本）
     * <p>开启后，超过指定长度的参数值将会自动截断处理</p>
     */
    private Boolean truncate = false;

    /**
     * 超长参数检测阈值（单位：字符）
     * <p>当参数值长度超过此值时，触发截断规则</p>
     * <p>默认：2000，仅在 {@link #truncate} 启用时生效</p>
     */
    private int threshold = 2000;

    /**
     * 超长参数最大保留长度（单位：字符）
     * <p>当参数超过 {@link #threshold} 时，强制截断到此长度</p>
     * <p>默认：50，仅在 {@link #truncate} 启用时生效</p>
     */
    private int maxLength = 50;

    /**
     * 截断后追加的后缀符号（如配置 "..." 会让截断内容更直观）
     * <p>建议配置 3-5 个非占宽字符，默认为 ...</p>
     * <p>仅在 {@link #truncate} 启用时生效</p>
     */
    private String longParamSuffix = "...";

    /**
     * 是否过滤敏感参数
     * <p>开启后会对敏感参数进行过滤，默认不过滤</p>
     */
    private Boolean filterSensitive = false;

    /**
     * 敏感参数字段列表（如：password,token,idCard）
     * <p>支持精确匹配（区分大小写）</p>
     * <p>示例值：password,oldPassword</p>
     */
    private List<String> sensitiveFields = List.of();
}