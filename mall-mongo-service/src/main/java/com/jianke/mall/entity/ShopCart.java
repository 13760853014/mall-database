package com.jianke.mall.entity;

import com.jianke.mall.entity.cart.Merchant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author chenxiaoqi
 * @since 2020/01/20
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document
public class ShopCart {

    @Id
    private String accountId;

    /**
     * 商家列表
     */
    private List<Merchant> merchants;

}
