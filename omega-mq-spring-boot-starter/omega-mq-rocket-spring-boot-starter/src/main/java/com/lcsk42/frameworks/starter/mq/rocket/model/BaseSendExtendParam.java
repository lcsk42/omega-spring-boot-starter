package com.lcsk42.frameworks.starter.mq.rocket.model;

import com.lcsk42.frameworks.starter.mq.rocket.enums.DelayLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class BaseSendExtendParam {

    /**
     * 业务事件类型标识
     */
    private String eventName;

    /**
     * 消息主题（MQ领域通用概念）
     */
    private String topic;

    /**
     * 消息标签（用于主题下的二级分类）
     */
    private String tag;

    /**
     * 消息的唯一键
     */
    private String keys;

    /**
     * 发送消息超时时间，单位毫秒
     */
    private Long timeout;

    /**
     * 延迟等级
     */
    private DelayLevel delayLevel;
}
