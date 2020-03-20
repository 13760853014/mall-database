package com.jianke.service.impl;

import com.alibaba.fastjson.JSON;
import com.jianke.entity.ShopCart;
import com.jianke.repository.ShopCartRepository;
import com.jianke.service.ShopCartMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chenxiaoqi
 * @since 2020/02/28
 */
@Slf4j
@Service
public class ShopCartMongoServiceImpl implements ShopCartMongoService {


    @Autowired
    private ShopCartRepository shopCartRepository;

    @Override
    public void save(ShopCart shopCart) {
        try {
            shopCartRepository.save(shopCart);
            log.info("Redis加入购物车保存价格， accountId={},Cart={}", shopCart.getAccountId(), JSON.toJSONString(shopCart));
        } catch (Exception e) {
            log.error("保存mongo购物车数据异常,shopCart:{}", JSON.toJSONString(shopCart), e);
        }
    }

    @Override
    public void deleteCart(String accountId) {
        try {
            shopCartRepository.deleteByAccountId(accountId);
            log.info("Redis删除购物车保存价格， accountId={}", accountId);
        } catch (Exception e) {
            log.error("删除mongo购物车数据异常,accountId:{}", accountId, e);
        }
    }

}
