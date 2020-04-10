package com.jianke.service.impl;

import com.alibaba.fastjson.JSON;
import com.jianke.entity.Coupon;
import com.jianke.entity.cart.ShopCartBase;
import com.jianke.entity.cart.ShopCartItem;
import com.jianke.vo.DeliveryTypeVo;
import com.jianke.vo.PostageTemplateVo;
import com.jianke.vo.PostageTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class PostageAlgorithm_bak {

    public static List<DeliveryTypeVo> calPostage(List<PostageTemplateVo> templateVos, ShopCartBase shopCartBase, String p, Integer payType, List<Coupon> coupons) {
        List<PostageTemplateVo> templates = templateVos.stream().filter(t -> t.getPlatforms().contains(p)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(templates)) {
            log.info("平台{}查询不到对应的运费模板，使用默认的技术配置模板", p);
        }

        //0、根据支付方式获取 特殊模板配置了不包邮是商品，这些商品不能参与免邮计算
        List<Long> unFreeProduct = unFreeProduct(templateVos, payType, p);

        //1、筛选特殊配置模板
        List<PostageTemplateVo> specialTemplates = templateVos.stream()
                .filter(t -> t.getType() == 1)
                .filter(t -> t.getPlatforms().contains(p))
                .sorted(Comparator.comparing(PostageTemplateVo::getFreePostagePrice))
                .collect(Collectors.toList());

        //2、从特殊模板获取免邮的快递方式
        List<ShopCartItem> hasCalItem = new ArrayList<>();
        for (PostageTemplateVo templateVo : specialTemplates) {
            //获取购物车中，能够使用该模板计算运费的商品，需要减去不包邮商品
            List<ShopCartItem> items = shopCartBase.getMerchants().stream().flatMap(cartItem -> cartItem.getItems().stream())
                    .collect(Collectors.toList()).stream()
                    .filter(item -> templateVo.getProductCodes().contains(item.getProductCode().intValue()))
                    .filter(item -> !unFreeProduct.contains(item.getProductCode()))
                    .collect(Collectors.toList());
            items.addAll(hasCalItem); //上一次特殊模板已经计算过的，不满足免邮的购物车商品
            List<Long> skuCodes = items.stream().map(ShopCartItem::getProductCode).collect(Collectors.toList());
            long itemAmount = calItemTotalNum(items);
            long couponValue = templateUseCouponAmount(items, coupons);
            boolean isFree = itemAmount - couponValue > templateVo.getFreePostagePrice();
            log.info("特殊模板【{}】，计算运费商品{}， 总金额{}, 可用优惠券金额{}, 最低免邮金额{}， 是否免邮={}", templateVo.getTemplateName(), skuCodes, itemAmount, couponValue, templateVo.getFreePostagePrice(), isFree);

            if (isFree) {
                //返回该模板对应的免邮快递
                List<DeliveryTypeVo> deliveryTypeVos = new ArrayList<>();
                for (PostageTypeVo type : templateVo.getPostageTypes()) {
                    if (payType.equals(type.getPayType()) && type.getIsAllowFree() == 1) {
                        deliveryTypeVos.addAll(type.getFreeDeliveryTypeVos());
                    }
                }
                if (!CollectionUtils.isEmpty(deliveryTypeVos)) {
                    log.info("特殊模板【{}】，获取到的免邮快递方式为：{}", templateVo.getTemplateName(), JSON.toJSONString(deliveryTypeVos));
                    log.info("此订单满足{}元包邮。已达到包邮门槛，整单包邮。", templateVo.getFreePostagePrice() / 100);
                    return deliveryTypeVos;
                }
            }
            hasCalItem.addAll(items);
        }

        //3、从通用模板获取不到免邮的快递方式
        List<DeliveryTypeVo> deliveryTypeVos = calCommonTemplate(templateVos, shopCartBase, p, payType, coupons);
        if (!CollectionUtils.isEmpty(deliveryTypeVos)) {
            return deliveryTypeVos;
        }

        //4、获取不到免邮的快递方式，获取最高金额的包邮快递方式
        List<DeliveryTypeVo> deliveryTypes = templates.stream().flatMap(t -> t.getPostageTypes().stream())
                .filter(pt -> pt.getIsAllowFree() == 0)
                .flatMap(d -> d.getFreeDeliveryTypeVos().stream())
                .sorted(Comparator.comparing(DeliveryTypeVo::getDeliveryPrice).reversed())
                .distinct().limit(2).collect(Collectors.toList());
        log.info("获取不到免邮的快递方式, 返回最高金额的包邮快递方式{}", JSON.toJSONString(deliveryTypeVos));
        postageDesc(templateVos, shopCartBase, p, payType);
        return deliveryTypes;
    }

    public static void postageDesc(List<PostageTemplateVo> templateVos, ShopCartBase shopCartBase, String p, Integer payType) {
        List<PostageTemplateVo> templateVoList = templateVos.stream()
                .filter(t -> t.getPlatforms().contains(p)).collect(Collectors.toList());
        if (templateVoList.size() == 1) {
            log.info("不包邮金额说明: 全部商品需满{}元包邮，当前未满足此条件。", templateVoList.get(0).getFreePostagePrice() / 100);
            return;
        }

        StringBuilder info = new StringBuilder();
        List<Long> unFreeProduct = unFreeProduct(templateVos, payType, p);
        if (!CollectionUtils.isEmpty(unFreeProduct)) {
            Map<Long, String> itemMap = shopCartBase.getMerchants().stream()
                    .flatMap(shopCart -> shopCart.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getProductCode(), item -> item.getProductName(), (i1, i2) -> i2));
            info.append("此订单中");
            for (Long skuCode : unFreeProduct) {
                info.append("此订单中" + itemMap.get(skuCode) + ",不参与包邮");
            }
            info.append("不参与包邮");
        }

        //1、筛选特殊配置模板
        List<PostageTemplateVo> specialTemplates = templateVos.stream()
                .filter(t -> t.getType() == 1)
                .filter(t -> t.getPlatforms().contains(p))
                .sorted(Comparator.comparing(PostageTemplateVo::getFreePostagePrice))
                .collect(Collectors.toList());

        for (PostageTemplateVo templateVo : specialTemplates) {

        }

    }



    public static List<Long> unFreeProduct(List<PostageTemplateVo> templateVos, Integer payType, String p) {
        List<Long> unFreeProduct = templateVos.stream()
                .filter(t -> t.getType() == 1)
                .filter(t -> t.getPlatforms().contains(p))
                .filter(t -> t.getPostageTypes().stream()
                            .filter(pt -> payType.equals(pt.getPayType()))
                            .filter(pt -> pt.getIsAllowFree() == 0).count() > 0)
                .flatMap(t -> t.getProductCodes().stream())
                .map(sku -> sku.longValue())
                .collect(Collectors.toList());
        log.info("支付方式{}, 特殊模板配置了不包邮是商品为：{}", payType, unFreeProduct);
        return unFreeProduct;
    }

    /**
     * 该特殊配置模板能够使用优惠券的金额
     * @param items
     * @param coupons
     * @return
     */
    public static long templateUseCouponAmount(List<ShopCartItem> items, List<Coupon> coupons) {
        if (CollectionUtils.isEmpty(coupons)) {
            return 0;
        }
        long useAmount = 0;
        List<Long> itemCodes = items.stream().map(ShopCartItem::getProductCode).collect(Collectors.toList());
        for (Coupon coupon : coupons) {
            if (coupon.getType() == 1) {
                useAmount = useAmount + coupon.getCouponValue();
            } else {
                //购物车计算该特殊配置模板的商品，是否包含在商品券中
                List<Long> contains = itemCodes.stream().filter(sku -> coupon.getProductCodes().contains(sku)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(contains)) {
                    useAmount = useAmount + coupon.getCouponValue();
                    log.info("购物车商品{}，可以使用商品券{}", contains, coupons);
                }
            }
        }
        return useAmount;
    }

    public static long calItemTotalNum(List<ShopCartItem> items) {
        long sum = 0;
        for (ShopCartItem item : items) {
            if (item.getCombineId() == null) {
                sum = sum + item.getActualPrice() * item.getProductNum();
            } else {
                sum = sum + item.getActualPrice() * item.getCombineNum() * item.getProductNum();
            }
        }
        return sum;
    }

    public static List<DeliveryTypeVo> calCommonTemplate(List<PostageTemplateVo> templateVos, ShopCartBase shopCartBase, String p, Integer payType, List<Coupon> coupons) {
        PostageTemplateVo commonTemplates = templateVos.stream().filter(t -> t.getType() == 0).filter(t -> t.getPlatforms().contains(p)).findFirst().orElse(null);
        if (commonTemplates == null) {
            log.info("平台{}查询不到对应的通用运费模板", p);
            return null;
        }

        //获取购物车中，能够使用该模板计算运费的商品
        List<ShopCartItem> items = shopCartBase.getMerchants().stream()
                .flatMap(cartItem -> cartItem.getItems().stream())
                .collect(Collectors.toList());
        long itemAmount = calItemTotalNum(items);
        long couponValue = templateUseCouponAmount(items, coupons);
        boolean isFree = itemAmount - couponValue > commonTemplates.getFreePostagePrice();
        log.info("通用模板【{}】，计算运费总金额{}, 可用优惠券金额{}, 最低免邮金额{}， 是否免邮={}", commonTemplates.getTemplateName(), itemAmount, couponValue, commonTemplates.getFreePostagePrice(), isFree);

        if (isFree) {
            //返回该模板对应的免邮快递
            List<DeliveryTypeVo> deliveryTypeVos = new ArrayList<>();
            for (PostageTypeVo type : commonTemplates.getPostageTypes()) {
                if (payType.equals(type.getPayType()) && type.getIsAllowFree() == 1) {
                    deliveryTypeVos.addAll(type.getFreeDeliveryTypeVos());
                }
            }
            if (!CollectionUtils.isEmpty(deliveryTypeVos)) {
                log.info("通用模板【{}】，获取到的免邮快递方式为：{}", commonTemplates.getTemplateName(), JSON.toJSONString(deliveryTypeVos));
                log.info("此订单满足{}元包邮。已达到包邮门槛，整单包邮。", commonTemplates.getFreePostagePrice() / 100);
                return deliveryTypeVos;
            }
        }
        return null;
    }
}
