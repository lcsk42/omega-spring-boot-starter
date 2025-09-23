package com.lcsk42.frameworks.starter.mq.rocket.producer;

import com.alibaba.fastjson2.JSON;
import com.lcsk42.frameworks.starter.mq.rocket.model.BaseSendExtendParam;
import com.lcsk42.frameworks.starter.mq.rocket.model.MessageWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCommonSendProducerTemplate<T> {

    // RocketMQ 模板，用于发送消息
    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 抽象方法：构建消息发送的扩展参数
     *
     * @param messageSendEvent 消息事件对象
     * @return 消息发送的扩展参数
     */
    protected abstract BaseSendExtendParam buildBaseSendExtendParam(T messageSendEvent);

    /**
     * 抽象方法：构建消息对象
     *
     * @param messageSendEvent 消息事件对象
     * @param requestParam     消息发送的扩展参数
     * @return 构建好的消息对象
     */
    protected abstract Message<?> buildMessage(T messageSendEvent, BaseSendExtendParam requestParam);

    /**
     * 默认的消息构建方法
     *
     * @param messageSendEvent 消息事件对象
     * @param requestParam     消息发送的扩展参数
     * @return 构建好的消息对象
     */
    protected Message<?> buildDefaultMessage(T messageSendEvent, BaseSendExtendParam requestParam) {
        // 如果扩展参数中没有指定 keys，则生成一个随机 UUID 作为 keys
        String keys = StringUtils.isEmpty(requestParam.getKeys()) ? UUID.randomUUID().toString() : requestParam.getKeys();
        return MessageBuilder
                // 消息体：包含keys和消息事件对象
                .withPayload(MessageWrapper.of(requestParam.getKeys(), messageSendEvent))
                // 设置消息头：消息的keys
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                // 设置消息头：消息的tag
                .setHeader(MessageConst.PROPERTY_TAGS, requestParam.getTag())
                .build();
    }

    /**
     * 发送消息方法
     *
     * @param messageSendEvent 要发送的消息事件对象
     * @return 消息发送结果
     */
    public SendResult sendMessage(T messageSendEvent) {
        // 构建消息发送的扩展参数
        BaseSendExtendParam baseSendExtendParam = buildBaseSendExtendParam(messageSendEvent);
        SendResult sendResult;
        try {
            // 构建消息目的地（topic:tag格式）
            StringBuilder destinationBuilder = new StringBuilder(baseSendExtendParam.getTopic());
            if (StringUtils.isNotBlank(baseSendExtendParam.getTag())) {
                destinationBuilder.append(":").append(baseSendExtendParam.getTag());
            }

            // 同步发送消息
            sendResult = rocketMQTemplate.syncSend(
                    destinationBuilder.toString(),
                    buildMessage(messageSendEvent, baseSendExtendParam),
                    baseSendExtendParam.getTimeout(),
                    baseSendExtendParam.getDelayLevel().getLevel()
            );

            // 记录发送成功的日志
            log.info(
                    "[{}] SendStatus：{}，MessageId：{}，MessageKeys：{}",
                    baseSendExtendParam.getEventName(),
                    sendResult.getSendStatus(),
                    sendResult.getMsgId(),
                    baseSendExtendParam.getKeys()
            );
        } catch (Throwable ex) {
            // 记录发送失败的日志
            log.error(
                    "[{}] Message sending failed, message body：{}",
                    baseSendExtendParam.getEventName(),
                    JSON.toJSONString(messageSendEvent),
                    ex);
            throw ex;
        }
        return sendResult;
    }
}
