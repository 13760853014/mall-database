package com.jianke.service;

import com.alibaba.fastjson.JSON;
import com.jianke.entity.Coupon;
import com.jianke.entity.cart.Merchant;
import com.jianke.entity.cart.ShopCartBase;
import com.jianke.entity.cart.ShopCartItem;
import com.jianke.vo.DeliveryTypeVo;
import com.jianke.vo.PostageTemplateVo;
import com.jianke.vo.PostageTypeVo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CalPostageService {

    public static PostageTemplateVo commonTemplate1() {
        //设置通用运费模板
        PostageTemplateVo templateVo = new PostageTemplateVo();
        templateVo.setFreePostagePrice(99 * 100L);
        templateVo.setPlatforms(Arrays.asList("app","mobile","mini")).setStatus(1);
        templateVo.setType(0).setTemplateName("通用模板");

        //设置快递方式  是否支持免邮(1是/0否)-在线支付,  免邮快递方式， 不免邮快递方式
        PostageTypeVo onlinePay = new PostageTypeVo("1-99", "7-顺丰-0|5-EMS-0", "7-顺丰-10|5-EMS-12");
        //设置快递方式  是否免邮(1是/0否)-货到付款
        PostageTypeVo offlinePay = new PostageTypeVo("1-1", "7-顺丰-0|5-EMS-0", "7-顺丰-10|5-EMS-12");
        templateVo.addPostageType(onlinePay);
        templateVo.addPostageType(offlinePay);
        System.out.println(templateVo.getTemplateName() + "------" + JSON.toJSONString(templateVo));
        return templateVo;
    }

    public static PostageTemplateVo specialTemplate2() {
        //设置通用运费模板
        PostageTemplateVo templateVo = new PostageTemplateVo();
        templateVo.setFreePostagePrice(15 * 100L);
        templateVo.setPlatforms(Arrays.asList("app","mobile","mini")).setStatus(1);
        templateVo.setType(1).setTemplateName("特殊模板2222");
        templateVo.setProductCodes(Arrays.asList(21,22,23,24,25,26,27,28,29));

        //设置快递方式  是否支持免邮(1是/0否)-在线支付,  免邮快递方式， 不免邮快递方式
        PostageTypeVo onlinePay = new PostageTypeVo("1-99", "7-顺丰-0|5-EMS-0", "7-顺丰-10|5-EMS-12");
        //设置快递方式  是否免邮(1是/0否)-货到付款
        PostageTypeVo offlinePay = new PostageTypeVo("1-1", "7-顺丰-0|5-EMS-0", "7-顺丰-10|5-EMS-12");
        templateVo.addPostageType(onlinePay);
        templateVo.addPostageType(offlinePay);
        System.out.println(templateVo.getTemplateName() + "------" + JSON.toJSONString(templateVo));
        return templateVo;
    }

    public static PostageTemplateVo specialTemplate3() {
        //设置通用运费模板
        PostageTemplateVo templateVo = new PostageTemplateVo();
        templateVo.setFreePostagePrice(20 * 100L);
        templateVo.setPlatforms(Arrays.asList("app","mobile","mini")).setStatus(1);
        templateVo.setType(1).setTemplateName("特殊模板333");
        templateVo.setProductCodes(Arrays.asList(31,32,33,34,35,36,37,38,39));

        //设置快递方式  是否支持免邮(1是/0否)-在线支付,  免邮快递方式， 不免邮快递方式
        PostageTypeVo onlinePay = new PostageTypeVo("1-99", "7-顺丰-0|5-EMS-0", "7-顺丰-10|5-EMS-12");
        //设置快递方式  是否免邮(1是/0否)-货到付款
        PostageTypeVo offlinePay = new PostageTypeVo("0-1", "7-顺丰-10|5-EMS-12");
        templateVo.addPostageType(onlinePay);
        templateVo.addPostageType(offlinePay);
        System.out.println(templateVo.getTemplateName() + "------" + JSON.toJSONString(templateVo));
        return templateVo;
    }

    public static void main(String[] args) {
        List<PostageTemplateVo> templateVos = Arrays.asList(commonTemplate1(), specialTemplate2(), specialTemplate3());
        ShopCartBase shopCartBase = buildShopCartBase();
        //优惠券金额-类型-商品（1全场券，2商品券， 商品用,隔开）
        List<Coupon> coupons = Arrays.asList(new Coupon("1000-1"), new Coupon("1000-2-11,12"));
        String platform = "app";
        Integer payType = 99;
        boolean isFree = PostageAlgorithm.calPostageIsFree(templateVos, shopCartBase, platform, payType, coupons);
        List<DeliveryTypeVo> deliveryTypeVos = PostageAlgorithm.getPostageType(templateVos, shopCartBase, platform, payType, isFree);
        log.info("平台{}，支付方式{}，是否包邮{}，返回的快递方式{}", platform, payType, isFree, JSON.toJSONString(deliveryTypeVos));
    }

    public static ShopCartBase buildShopCartBase() {
        Merchant merchant = new Merchant();
        ShopCartBase shop = new ShopCartBase(Arrays.asList(merchant));
        merchant.setMerchantCode(1).setMerchantName("健客自营");
        //编码-名称-数量-单个商品价格(分)-是否处方药（0否1是）
        List<ShopCartItem> list = new ArrayList<>();
        list.add(new ShopCartItem("11-商品11-3-3000-0"));
        list.add(new ShopCartItem("12-商品12-5-4000-0"));
        list.add(new ShopCartItem("22-商品22-3-3000-0"));
        list.add(new ShopCartItem("23-商品23-3-3000-0"));
        list.add(new ShopCartItem("31-商品31-3-3000-0"));
        //list.addAll(new ShopCartItem().combine("31-商品31-3-3000-0","31-商品31-3-3000-0", 100001));
        merchant.setItems(list);
        System.out.println("购物车商品------" + JSON.toJSONString(shop));
        return shop;
    }
}
