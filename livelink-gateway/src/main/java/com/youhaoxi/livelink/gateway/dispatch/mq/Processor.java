package com.youhaoxi.livelink.gateway.dispatch.mq;

/**
 * 处理从mq消费的消息
 */
public interface Processor {
    void process(byte[] body);
}
