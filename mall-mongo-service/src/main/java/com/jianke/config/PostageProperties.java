package com.jianke.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @tool: Created By IntellJ IDEA
 * @company: www.jianke.com
 * @author: wangzhoujie
 * @date: 2018/10/30
 * @time: 20:11
 * @description:
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mall.settlement.postage")
public class PostageProperties {

    /**
     * 默认规则
     */
    private static final String DEFAULT = "default";

    /**
     * 按支付类型定义包邮规则
     */
    private Map<String, Map<String, PostageData>> rules;



    /**
     * 默认邮费
     */
    private Integer defaultTransport = 1000;

    public PostageData getRule(String channel, Integer payType) {
        Map<String, PostageData> channelRules = Optional.ofNullable(this.rules.get(channel)).orElse(this.rules.get(DEFAULT));
        return Optional.ofNullable(channelRules.get("payType" + payType)).orElse(channelRules.get(DEFAULT));
    }

    /**
     * @param code
     * @return
     */
    public List<TransportType> getDelivery(PostageProperties.PostageData rule, Integer code) {
        return Optional.ofNullable(rule.getTransportTypes().get("m" + code)).orElse(Collections.emptyList());
    }

    /**
     * 获取商家活动
     *
     * @param configs
     * @return
     */
    private TransportCost getActiviey(Map<String, TransportCost> configs, Integer merchantCode) {
        if (configs == null) {
            return null;
        }
        return configs.get("m" + merchantCode);
    }

    /**
     * 判断常规邮费是否包邮
     *
     * @param rule
     * @return
     */
    public Boolean checkOrdinaryFree(PostageData rule, Long cost, Integer platform) {
        if (rule.getIsFree()) {
            TransportCost transportCost = getOrdinaryCost(rule, platform);
            return transportCost != null && transportCost.getIsFree() && transportCost.getMinMoney() <= cost;
        }
        return false;
    }

    private TransportCost getOrdinaryCost(PostageData rule, Integer platform) {
        TransportCost cost = rule.getPlatforms().get("platform" + platform);
        if (cost == null) {
            cost = rule.getPlatforms().get(DEFAULT);
        }
        if (cost.getIsFree() && cost.getMinMoney() == null) {
            cost.setMinMoney(0L);
        }
        return cost;
    }

    /**
     * 校验商家活动是否有效
     *
     * @param rule
     * @return
     */
    public Boolean checkActivity(PostageData rule, Integer merchantCode, Integer platform) {
        TransportCost transportCost = getActiviey(rule.getPostageActivities(), merchantCode);
        long cureTime = System.currentTimeMillis();
        return transportCost != null &&
                !CollectionUtils.isEmpty(transportCost.getPlatform()) &&
                transportCost.getPlatform().contains(platform) &&
                transportCost.getBgnDate() != null &&
                transportCost.getEndDate() != null &&
                (cureTime > transportCost.getBgnDate().getTime() && cureTime < transportCost.getEndDate().getTime());
    }


    @Data
    public static class PostageData {
        private Boolean isFree = true;
        private Map<String, TransportCost> platforms;
        /**
         * 商户包邮活动
         */
        private Map<String, TransportCost> postageActivities;

        /**
         * 按商户配置快递方式
         */
        private Map<String, List<TransportType>> transportTypes;
    }


    /**
     * 免邮配置
     */
    @Setter
    @Getter
    @ToString
    public static class TransportCost {
        private Boolean isFree = true;
        //门槛
        private Long minMoney;
        //平台
        private List<Integer> platform;
        //开始时间
        private Date bgnDate;
        //结束时间
        private Date endDate;
    }

    /**
     * 快递方式
     */
    @Setter
    @Getter
    @ToString
    public static class TransportType {
        //值
        private Short deliveryType;
        //名称
        private String deliveryName;
        //邮费
        private Long cost;
        //是否默认
        private Boolean isDefault;
    }

}
