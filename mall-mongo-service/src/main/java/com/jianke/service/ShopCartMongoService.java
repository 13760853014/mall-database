package com.jianke.service;

import com.jianke.entity.ShopCart;

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
