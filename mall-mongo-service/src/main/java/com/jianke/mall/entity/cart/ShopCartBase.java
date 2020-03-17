package com.jianke.mall.entity.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author rongliangkang
 * @create 2017-06-09 18:07
 **/
@Setter
@Getter
@ToString
public class ShopCartBase implements Serializable {
    /**
     * 商家列表
     */
    List<Merchant> merchants;

}
