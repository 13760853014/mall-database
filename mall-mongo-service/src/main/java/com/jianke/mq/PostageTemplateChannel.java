//package com.jianke.mq;
//
//import org.springframework.cloud.stream.annotation.Input;
//import org.springframework.cloud.stream.annotation.Output;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.SubscribableChannel;
//
///**
// * @author XiaoXiong
// */
//public interface PostageTemplateChannel {
//
//    String POSTAGE_TEMPLATE_INPUT = "postage_template_delay_in";
//    String POSTAGE_TEMPLATE_OUTPUT = "postage_template_delay_out";
//
//    @Input(PostageTemplateChannel.POSTAGE_TEMPLATE_INPUT)
//    SubscribableChannel acceptEvent();
//
//    @Output(PostageTemplateChannel.POSTAGE_TEMPLATE_OUTPUT)
//    MessageChannel publishEvent();
//
//}
