package com.jianke.mall.repository;

import com.alibaba.fastjson.JSON;
import com.jianke.mall.entity.cart.ShopCartBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CartRepository {
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	public static String generateShopCartKey(String accountId) {
		return "com.jianke.mall.cart:shopcart:" + accountId;
	}

	public ShopCartBase get(String accountId) {
		String key = generateShopCartKey(accountId);
		String cartStr = redisTemplate.opsForValue().get(key);
		if(cartStr == null && "".equals(cartStr)) {
			return null;
		}
		return JSON.parseObject(cartStr, ShopCartBase.class);
	}
}
