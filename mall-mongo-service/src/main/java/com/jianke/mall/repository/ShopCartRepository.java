package com.jianke.mall.repository;

import com.jianke.mall.entity.ShopCart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopCartRepository extends MongoRepository<ShopCart, String>, PagingAndSortingRepository<ShopCart, String> {
    ShopCart findByAccountId(String accountId);

    ShopCart deleteByAccountId(String accountId);
}
