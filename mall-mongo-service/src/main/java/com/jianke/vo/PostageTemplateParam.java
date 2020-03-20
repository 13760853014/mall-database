package com.jianke.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class PostageTemplateParam implements Serializable {
    //2: 待生效
    private Integer status;
    private String templateName;
    private Integer isAllowFree;
    private Long freePostagePrice;
    private String platform;
    private Integer productCode;
    private Integer payType;
    private Integer deliveryType;
}
