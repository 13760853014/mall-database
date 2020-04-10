package com.jianke.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;

/**
 * @tool: Created By IntellJ IDEA
 * @company: www.jianke.com
 * @author: wangzhoujie
 * @date: 2018/1/30
 * @time: 15:29
 * @description: 新用户清除消息发布
 */
@Slf4j
@EnableBinding(PostageTemplateChannel.class)
public class PostageTemplatePublisher {

    @Autowired
    private PostageTemplateChannel postageTemplateChannel;

    public void sendDelayQueue(String id) {
        postageTemplateChannel.publishEvent()
                .send(MessageBuilder.withPayload(id)
                        .setHeader("x-delay", 10000).build());
    }
}
