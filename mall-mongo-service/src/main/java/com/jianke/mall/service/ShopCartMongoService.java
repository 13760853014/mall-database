package com.jianke.mall.service;

import com.jianke.mall.entity.ShopCart;

/**
 * 异步保存mongo数据
 *
 * @author chenxiaoqi
 * @since 2020/02/28
 */
public interface ShopCartMongoService {

    void save(ShopCart shopCart);

    void deleteCart(String accountId);
}
