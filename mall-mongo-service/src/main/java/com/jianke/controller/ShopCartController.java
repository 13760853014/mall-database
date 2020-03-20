package com.jianke.controller;

import com.jianke.entity.ShopCart;
import com.jianke.entity.cart.Merchant;
import com.jianke.entity.cart.ShopCartBase;
import com.jianke.repository.CartRepository;
import com.jianke.repository.ShopCartRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/svc/compare")
public class ShopCartController {

    @Autowired
    private ShopCartRepository shopCartRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 获取购物车
     *
     * @return
     */
    @GetMapping
    public List<CompareResult> getDiffAccountId() throws Exception {
        List<ShopCart> shopCarts = shopCartRepository.findAll();
        if (CollectionUtils.isEmpty(shopCarts)) {
            return null;
        }

        List<CompareResult> results = new ArrayList<>();
        for (ShopCart shopCart : shopCarts) {
            String accountId = shopCart.getAccountId();
            System.out.println(accountId);
            ShopCartBase shopCartBase = cartRepository.get(accountId);
            if (shopCart == null || shopCartBase == null || CollectionUtils.isEmpty(shopCart.getMerchants()) || CollectionUtils.isEmpty(shopCartBase.getMerchants())) {
                continue;
            }
            if (shopCart.getMerchants().size() != shopCartBase.getMerchants().size()) {
                results.add(new CompareResult(accountId, shopCart, shopCartBase));
                continue;
            }
            for (Merchant merchant : shopCart.getMerchants()) {
                for (Merchant merchant1 : shopCartBase.getMerchants()) {
                    if (!merchant.getMerchantCode().equals(merchant1.getMerchantCode())) {
                        continue;
                    }
                    if (merchant.getItems().size() != merchant1.getItems().size()) {
                        results.add(new CompareResult(accountId, shopCart, shopCartBase));
                    }
                }
            }
        }
        System.out.println(results.size());
        System.out.println(results.stream().map(s -> s.getAccountId()).collect(Collectors.joining(",")));
        return results;
    }

    /**
     * 获取购物车
     *
     * @return
     */
    @GetMapping(value = "/{accountId}")
    public CompareResult getByAccountId(@PathVariable("accountId") String accountId) throws Exception {
        ShopCart shopCart = shopCartRepository.findByAccountId(accountId);
        ShopCartBase shopCartBase = cartRepository.get(accountId);
        return new CompareResult(accountId, shopCart, shopCartBase);
    }

    /**
     * 获取购物车
     *
     * @return
     */
    @PutMapping(value = "/{accountId}")
    public CompareResult syncByAccountId(@PathVariable("accountId") String accountId) throws Exception {
        ShopCartBase shopCartBase = cartRepository.get(accountId);
        ShopCart shopCart = shopCartRepository.save(new ShopCart(accountId, shopCartBase.getMerchants()));
        return new CompareResult(accountId, shopCart, shopCartBase);
    }

    /**
     * 获取购物车
     *
     * @return
     */
    @GetMapping(value = "/sku/{sku}")
    public List<ShopCart> getBySku(@PathVariable("sku") Long sku) throws Exception {
        Query query = new Query();
        query.addCriteria(Criteria.where("merchants.items.productCode").is(sku));
        query.addCriteria(Criteria.where("merchants.items.addDate").gt(getDelayDate(-90)));
        query.addCriteria(Criteria.where("merchants.items.addPrice").gte(9000));
        //计算总数
        long total = mongoTemplate.count(query, ShopCart.class);
        System.out.println("total=" + total);
        List<ShopCart> accountIds = mongoTemplate.find(query, ShopCart.class, "accountId");
        List<ShopCart> shopCarts = mongoTemplate.find(query, ShopCart.class);
        return shopCarts;
    }



    public static Date getDelayDate(Integer deleyDate) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, deleyDate);
        return cal.getTime();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompareResult {
        private String accountId;
        private ShopCart shopCart;
        private ShopCartBase shopCartBase;

    }

}
