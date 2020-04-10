package com.jianke.controller;

import com.alibaba.fastjson.JSON;
import com.jianke.config.PostageProperties;
import com.jianke.demo.exception.BaseException;
import com.jianke.entity.cart.Merchant;
import com.jianke.entity.cart.ShopCartBase;
import com.jianke.entity.cart.ShopCartItem;
import com.jianke.service.PostageTemplateService;
import com.jianke.vo.DeliveryTypeVo;
import com.jianke.vo.PostageTemplateVo;
import com.jianke.vo.PostageTypeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/svc/postage")
public class PostageController {

    @Autowired
    private PostageProperties postageProperties;

    @Autowired
    private PostageTemplateService postageTemplateService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity findOne(@PathVariable String id) throws BaseException {
        postageTemplateService.sendDelayQueue(id);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    /**
     * 获取购物车
     *
     * @return
     */
    @GetMapping
    public void getDiffAccountId() throws Exception {
        Map<String, Map<String, PostageProperties.PostageData>> rules= postageProperties.getRules();
        System.out.println(JSON.toJSONString(postageProperties.getRules()));
    }

    public static PostageTemplateVo commonTemplate() {
        //设置通用运费模板
        PostageTemplateVo templateVo = new PostageTemplateVo();
        templateVo.setFreePostagePrice(99L);
        templateVo.setPlatforms(Arrays.asList("app","mobile","mini")).setStatus(1);
        templateVo.setType(0);
        templateVo.setTemplateName("通用模板");

        //设置快递方式  免邮/在线支付
        PostageTypeVo postageFree = new PostageTypeVo().setIsAllowFree(1).setPayType(99);
        DeliveryTypeVo free01 = new DeliveryTypeVo("7-顺丰-true-0");
        DeliveryTypeVo free02 = new DeliveryTypeVo("5-EMS-true-0");
        postageFree.setFreeDeliveryTypeVos(Arrays.asList(free01, free02));

        //设置快递方式  不免邮/货到付款
        PostageTypeVo postageUnfree = new PostageTypeVo().setIsAllowFree(0).setPayType(1);
        DeliveryTypeVo unFree01 = new DeliveryTypeVo("7-顺丰-false-10");
        DeliveryTypeVo unFree02 = new DeliveryTypeVo("5-EMS-false-15");
        postageUnfree.setUnFreeDeliveryTypeVos(Arrays.asList(unFree01, unFree02));

        templateVo.setPostageTypes(Arrays.asList(postageFree, postageUnfree));
        System.out.println(JSON.toJSONString(templateVo));
        return templateVo;
    }

    public static PostageTemplateVo specialTemplate2() {
        //设置通用运费模板
        PostageTemplateVo templateVo = new PostageTemplateVo();
        templateVo.setFreePostagePrice(150L);
        templateVo.setPlatforms(Arrays.asList("app","mobile","mini")).setStatus(1);
        templateVo.setType(1);
        templateVo.setTemplateName("特殊模板2222");
        templateVo.setProductCodes(Arrays.asList(21,22,23,24,25,26,27,28,29));

        //设置快递方式  免邮/在线支付
        PostageTypeVo postageFree = new PostageTypeVo().setIsAllowFree(1).setPayType(99);
        DeliveryTypeVo free01 = new DeliveryTypeVo("7-顺丰-true-0");
        DeliveryTypeVo free02 = new DeliveryTypeVo("5-EMS-true-0");
        postageFree.setFreeDeliveryTypeVos(Arrays.asList(free01, free02));

        //设置快递方式  不免邮/货到付款
        PostageTypeVo postageUnfree = new PostageTypeVo().setIsAllowFree(0).setPayType(1);
        DeliveryTypeVo unFree01 = new DeliveryTypeVo("7-顺丰-false-20");
        DeliveryTypeVo unFree02 = new DeliveryTypeVo("5-EMS-false-25");
        postageUnfree.setUnFreeDeliveryTypeVos(Arrays.asList(unFree01, unFree02));

        templateVo.setPostageTypes(Arrays.asList(postageFree, postageUnfree));
        System.out.println(JSON.toJSONString(templateVo));
        return templateVo;
    }

    public static PostageTemplateVo specialTemplate3() {
        //设置通用运费模板
        PostageTemplateVo templateVo = new PostageTemplateVo();
        templateVo.setFreePostagePrice(200L);
        templateVo.setPlatforms(Arrays.asList("app","mobile","mini")).setStatus(1);
        templateVo.setType(1);
        templateVo.setTemplateName("特殊模板333");
        templateVo.setProductCodes(Arrays.asList(31,32,33,34,35,36,37,38,39));

        //设置快递方式  免邮/在线支付
        PostageTypeVo postageFree = new PostageTypeVo().setIsAllowFree(1).setPayType(99);
        DeliveryTypeVo free01 = new DeliveryTypeVo("7-顺丰-true-0");
        DeliveryTypeVo free02 = new DeliveryTypeVo("5-EMS-true-0");
        postageFree.setFreeDeliveryTypeVos(Arrays.asList(free01, free02));

        //设置快递方式  不免邮/货到付款
        PostageTypeVo postageUnfree = new PostageTypeVo().setIsAllowFree(0).setPayType(1);
        DeliveryTypeVo unFree01 = new DeliveryTypeVo("7-顺丰-false-30");
        DeliveryTypeVo unFree02 = new DeliveryTypeVo("5-EMS-false-35");
        postageUnfree.setUnFreeDeliveryTypeVos(Arrays.asList(unFree01, unFree02));

        templateVo.setPostageTypes(Arrays.asList(postageFree, postageUnfree));
        System.out.println(JSON.toJSONString(templateVo));
        return templateVo;
    }

    public static void main(String[] args) {
        List<PostageTemplateVo> templateVos = Arrays.asList(commonTemplate(), specialTemplate2(), specialTemplate3());
        buildShopCartBase();

    }

    public static ShopCartBase buildShopCartBase() {
        Merchant merchant = new Merchant();
        ShopCartBase shop = new ShopCartBase(Arrays.asList(merchant));
        merchant.setMerchantCode(1).setMerchantName("健客自营");
        //编码-名称-数量-结算价格-是否处方药（0否1是）
        List<ShopCartItem> list = new ArrayList<>();
        list.add(new ShopCartItem("11-商品11-3-3000-0"));
        list.add(new ShopCartItem("12-商品12-5-4000-0"));
        list.add(new ShopCartItem("22-商品22-3-3000-0"));
        list.add(new ShopCartItem("23-商品23-3-3000-0"));
        list.add(new ShopCartItem("31-商品31-3-3000-0"));
        //list.addAll(new ShopCartItem().combine("31-商品31-3-3000-0","31-商品31-3-3000-0", 100001));
        merchant.setItems(list);
        System.out.println(JSON.toJSONString(shop));
        return shop;
    }
}
