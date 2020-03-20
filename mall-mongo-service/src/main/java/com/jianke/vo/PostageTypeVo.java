package com.jianke.vo;

import lombok.Data;

import java.util.List;

@Data
public class PostageTypeVo {

    /**
     * 支付类型, 1货到付款， 99在线
     */
    private Integer payType;

    /**
     * 是否允许包邮 0:是 1：否
     */
    private Integer isAllowFree;

    /**
     * 允许包邮快递方式
     */
    private List<String> freeDeliveryTypeIds;

    /**
     * 允许包邮快递方式
     */
    private List<DeliveryTypeVo> freeDeliveryTypeVos;

    /**
     * 不允许包邮快递方式
     */
    private List<String> unFreeDeliveryTypeIds;

    /**
     * 不允许包邮快递方式
     */
    private List<DeliveryTypeVo> unFreeDeliveryTypeVos;

}
