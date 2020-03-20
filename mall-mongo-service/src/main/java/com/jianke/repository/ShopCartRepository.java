package com.jianke.repository;

import com.jianke.entity.ShopCart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopCartRepository extends MongoRepository<ShopCart, String>, PagingAndSortingRepository<ShopCart, String> {
    ShopCart findByAccountId(String accountId);

    ShopCart deleteByAccountId(String accountId);
}
