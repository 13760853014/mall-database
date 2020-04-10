package com.jianke.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author XiaoXiong
 */
public interface PostageTemplateChannel {

    String POSTAGE_TEMPLATE_IN = "postage_template_delay_in_1";
    String POSTAGE_TEMPLATE_OUT = "postage_template_delay_out_1";

    @Input(PostageTemplateChannel.POSTAGE_TEMPLATE_IN)
    SubscribableChannel acceptEvent();

    @Output(PostageTemplateChannel.POSTAGE_TEMPLATE_OUT)
    MessageChannel publishEvent();

}
