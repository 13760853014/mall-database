package com.jianke.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @Author: scr
 * @Date: 2020/3/10 12:05
 */
@Setter
@Getter
@ToString
public class DeliveryTypeVo {

    private String id;
    //物流公司名
    private String logisticsName;
    //快递公司编码
    private String logisticsCode;
    //快递公司健客编号
    private String logisticsNum;
    //快递费用
    private Long deliveryPrice;
    //是否包邮 true:包邮
    private Boolean isFree;
    private Date createdDate;

    private Date lastModifiedDate;

    private String createdName;

    private String lastModifiedName;
}
