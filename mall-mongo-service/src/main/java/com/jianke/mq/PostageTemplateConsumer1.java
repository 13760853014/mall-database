package com.jianke.mq;

import com.alibaba.fastjson.JSON;
import com.jianke.demo.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Date;

/**
 * 运费模板消费类
 * @author shichenru
 * @since 2020-03-13
 */
@EnableBinding(PostageTemplateChannel.class)
@Slf4j
public class PostageTemplateConsumer1 {

    @Autowired
    private PostageTemplatePublisher postageTemplatePublisher;

    @StreamListener(PostageTemplateChannel.POSTAGE_TEMPLATE_IN)
    public void consumer(@Payload String id) throws BaseException {
        log.info(new Date() + " 收到[邮费配置模板]开始的延时消息{}", JSON.toJSONString(id));
        try {
            //postageTemplateService.processDelayQueue(id);
        } catch (Exception e) {
            log.error("处理出错", e);
        }

        postageTemplatePublisher.sendDelayQueue(id);
    }
}
