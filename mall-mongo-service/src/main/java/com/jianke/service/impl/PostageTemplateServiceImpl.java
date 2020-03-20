package com.jianke.service.impl;

import com.alibaba.fastjson.JSON;
import com.jianke.demo.exception.BaseException;
import com.jianke.demo.utils.BeanUtil;
import com.jianke.demo.utils.DateUtils;
import com.jianke.entity.PostageTemplate;
import com.jianke.service.PostageTemplateService;
import com.jianke.vo.PostageTemplateParam;
import com.jianke.vo.PostageTemplateVo;
import com.jianke.vo.PostageTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 运费配置实现类
 * @author shichenru
 * @since 2020-03-13
 */
@Slf4j
@Service
public class PostageTemplateServiceImpl implements PostageTemplateService {

    @Autowired
    private MongoTemplate mongoTemplate;

//    @Autowired
//    private PostageTemplateChannel postageTemplateChannel;

    @Override
    public PostageTemplateVo insert(PostageTemplateVo templateVo) throws BaseException {
        this.validate(templateVo);
        this.validatePostageType(templateVo);
        this.validHasExistedProductCode(templateVo, templateVo.getPlatforms(), null);

        PostageTemplate postageTemplate = BeanUtil.convert(templateVo, PostageTemplate.class);
        postageTemplate.setCreatedDate(new Date());
        postageTemplate.setCreatedBy("");
        postageTemplate.setLastModifiedBy("");
        postageTemplate.setLastModifiedDate(new Date());
        postageTemplate.setDeleteFlag(0);
        postageTemplate.setRecordVersion(0);
        postageTemplate.setStatus(1);
        mongoTemplate.insert(postageTemplate);
        return beanToVo(postageTemplate);
    }

    @Override
    public PostageTemplateVo update(PostageTemplateVo vo) throws BaseException {
        this.validate(vo);
        this.validatePostageType(vo);
        Query relativeQuery = new Query(Criteria.where("relativeId").is(vo.getId()));
        PostageTemplate parentTemplate = mongoTemplate.findOne(relativeQuery, PostageTemplate.class);
        vo.setLastModifiedDate(new Date());
        vo.setLastModifiedBy("");
        //如果配置还没生效
        if (vo.getType() == 1 && parentTemplate.getBeginDate().after(new Date())) {
            mongoTemplate.save(vo);
        }
        //第一次更新
        if (parentTemplate == null) {
            return firstUpdate(vo);
        } else {
            //已经存在更新的子记录
            this.validHasExistedProductCode(vo, vo.getPlatforms(), Arrays.asList(vo.getId(), parentTemplate.getId()));
            vo.setDeleteFlag(0);
            vo.setStatus(1);
            PostageTemplate postageTemplate = BeanUtil.convert(vo, PostageTemplate.class);
            PostageTemplate template = mongoTemplate.findOne(new Query(Criteria.where("id").is(vo.getId())), PostageTemplate.class);
            if (template == null) {
                throw new BaseException("数据不存在");
            }
            if (template.getRecordVersion() != vo.getRecordVersion().intValue()) {
                throw new BaseException("更新失败, 数据以及被其它操作更新");
            }
            postageTemplate.setRecordVersion(template.getRecordVersion() + 1);
            postageTemplate.setRelativeId(template.getRelativeId());

            mongoTemplate.save(postageTemplate);
            sendDelayQueue(postageTemplate.getId(), postageTemplate.getBeginDate());
            return beanToVo(postageTemplate);
        }
    }

    private PostageTemplateVo firstUpdate(PostageTemplateVo vo) throws BaseException {
        this.validHasExistedProductCode(vo, vo.getPlatforms(), Arrays.asList(vo.getId()));
        PostageTemplate subTemplate = BeanUtil.convert(vo, PostageTemplate.class);
        subTemplate.setLastModifiedDate(new Date());
        subTemplate.setLastModifiedBy("");
        subTemplate.setDeleteFlag(1); //子记录初始数据是未启用的，等MQ消息更新为生效
        subTemplate.setRecordVersion(0);
        subTemplate.setStatus(1);
        subTemplate.setId(null);
        mongoTemplate.insert(subTemplate);
        sendDelayQueue(subTemplate.getId(), subTemplate.getBeginDate());

        //更新父记录的RelativeId
        Query parentQuery = new Query(Criteria.where("id").is(vo.getId()));
        PostageTemplate parentTemplate = mongoTemplate.findOne(parentQuery, PostageTemplate.class);
        parentTemplate.setRelativeId(subTemplate.getId());
        mongoTemplate.save(parentTemplate);
        return beanToVo(subTemplate);
    }



    @Override
    public void stopById(String id) throws BaseException {
        Query query = new Query(Criteria.where("id").is(id));
        PostageTemplate postageTemplate = mongoTemplate.findOne(query, PostageTemplate.class);
        if (postageTemplate == null) {
            throw new BaseException("数据不存在");
        }
        if (postageTemplate.getType() == 0) {
            throw new BaseException("通用模板不可停止");
        }
        postageTemplate.setLastModifiedBy("");
        postageTemplate.setLastModifiedDate(new Date());
        postageTemplate.setStatus(0);
        postageTemplate.setRecordVersion(postageTemplate.getRecordVersion() + 1);
        mongoTemplate.save(postageTemplate);
    }

    @Override
    public PostageTemplateVo findById(String id) throws BaseException {
        Query query = new Query(Criteria.where("id").is(id));
        PostageTemplate postageTemplate = mongoTemplate.findOne(query, PostageTemplate.class);
        if (postageTemplate == null) {
            return null;
        }
        PostageTemplateVo vo = beanToVo(postageTemplate);
        return vo;
    }

    @Override
    public List<PostageTemplateVo> findLogById(String id) throws BaseException {
        List<PostageTemplate> templates = mongoTemplate.findAll(PostageTemplate.class);
        List<PostageTemplate> result = new ArrayList<>();
        while (true) {
            PostageTemplate logTemplate = findByRelativeId(templates, id);
            if (logTemplate == null) {
                break;
            }
            result.add(logTemplate);
            id = logTemplate.getId();
        }
        return BeanUtil.convert(result, PostageTemplateVo.class);
    }

    private PostageTemplate findByRelativeId(List<PostageTemplate> templates, String id) {
        return templates.stream().filter(t -> id.equals(t.getRelativeId())).findFirst().orElse(null);
    }

    @Override
    public Page<PostageTemplateVo> findByPage(PostageTemplateParam param, int page, int size) {
        Pageable pageable = new PageRequest(page, size);
        Query query = new Query();
        if (param != null) {
            if (!StringUtils.isEmpty(param.getTemplateName())) {
                query.addCriteria(Criteria.where("templateName").is(param.getTemplateName()));
            }
            if (param.getStatus() != null) {
                if (param.getStatus() == 2) {
                    query.addCriteria(Criteria.where("status").is(1))
                            .addCriteria(Criteria.where("beginDate").gt(new Date()));
                } else {
                    query.addCriteria(Criteria.where("status").is(param.getStatus()));
                }
            }
            if (param.getFreePostagePrice() != null) {
                query.addCriteria(Criteria.where("freePostagePrice").is(param.getFreePostagePrice()));
            }
            if (param.getProductCode() != null) {
                query.addCriteria(Criteria.where("productCodes").in(param.getProductCode()));
            }
            if (!StringUtils.isEmpty(param.getPlatform())) {
                query.addCriteria(Criteria.where("platforms").in(param.getPlatform()));
            }
            if (param.getPayType() != null) {
                query.addCriteria(Criteria.where("postageTypes.payType").is(param.getPayType()));
            }
            if (!StringUtils.isEmpty(param.getDeliveryType())) {
                List<String> deliveryTypeIds = Arrays.asList("1");//deliveryTypeService.findByLogisticsNum(param.getDeliveryType());
                query.addCriteria(new Criteria().orOperator(
                        Criteria.where("postageTypes.freeDeliveryTypeIds").in(deliveryTypeIds),
                        Criteria.where("postageTypes.unFreeDeliveryTypeIds").in(deliveryTypeIds)));
            }
            if (param.getIsAllowFree() != null) {
                query.addCriteria(Criteria.where("postageTypes.isAllowFree").is(param.getIsAllowFree()));
            }
        }
        query.addCriteria(Criteria.where("deleteFlag").is(0));
        //计算总数
        long total = mongoTemplate.count(query, PostageTemplate.class);
        //查询结果集
        List<PostageTemplate> postageTemplates = mongoTemplate.find(query.with(pageable), PostageTemplate.class);
        Page<PostageTemplateVo> result = new PageImpl(beanToVoList(postageTemplates), pageable, total);
        return result;
    }

    private List<PostageTemplateVo> beanToVoList(List<PostageTemplate> postageTemplateList) {
        List<PostageTemplateVo> list = new ArrayList<>();
        for (PostageTemplate postageTemplate : postageTemplateList) {
            PostageTemplateVo vo = BeanUtil.convert(postageTemplate, PostageTemplateVo.class);
            if (vo.getStatus() == 0) {
                vo.setStatusName("已失效");
            } else if ((vo.getBeginDate() == null || vo.getBeginDate().getTime() < System.currentTimeMillis())) {
                vo.setStatusName("已生效");
            } else {
                vo.setStatusName("待生效");
            }
            list.add(vo);
        }
        return list;
    }

    @Override
    public List<PostageTemplateVo> findAvailableAll() {
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(1));
        query.addCriteria(Criteria.where("beginDate").lte(new Date()));
        query.addCriteria(Criteria.where("deleteFlag").is(0));
        List<PostageTemplate> postageTemplate = mongoTemplate.find(query, PostageTemplate.class);
        if (CollectionUtils.isEmpty(postageTemplate)) {
            return null;
        }
        return postageTemplate.stream().map(this::beanToVo).collect(Collectors.toList());
    }

    private PostageTemplateVo beanToVo(PostageTemplate postageTemplate) {
        PostageTemplateVo vo = BeanUtil.convert(postageTemplate, PostageTemplateVo.class);
        //快递类型转换
        if (CollectionUtils.isEmpty(vo.getPostageTypes())) {
//            List<DeliveryTypeVo> deliveryTypes = deliveryTypeService.findAll();
//            Map<String, DeliveryTypeVo> map = deliveryTypes.stream().collect(Collectors.toMap(d -> d.getId(), d -> d, (d1, d2) -> d2));
//            for (PostageTypeVo postageTypeVo : vo.getPostageTypes()) {
//                postageTypeVo.setFreeDeliveryTypeVos(postageTypeVo.getFreeDeliveryTypeIds().stream().map(map::get).collect(Collectors.toList()));
//                postageTypeVo.setUnFreeDeliveryTypeVos(postageTypeVo.getUnFreeDeliveryTypeIds().stream().map(map::get).collect(Collectors.toList()));
//            }
        }
        if (vo.getStatus() == 0) {
            vo.setStatusName("已失效");
        } else if ((vo.getBeginDate() == null || vo.getBeginDate().getTime() < System.currentTimeMillis())) {
            vo.setStatusName("已生效");
        } else {
            vo.setStatusName("待生效");
        }
        return vo;
    }
    private List<PostageTemplateVo> findCommonSetting() {
        Query query = new Query();
        query.addCriteria(Criteria.where("type").is(0));
        query.addCriteria(Criteria.where("deleteFlag").is(0));
        return beanToVoList(mongoTemplate.find(query, PostageTemplate.class));
    }

    private void validate(PostageTemplateVo templateVo) throws BaseException {
        if (templateVo.getBeginDate() == null) {
            throw new BaseException("开始时间不能为空");
        }
        if (templateVo.getType() == null) {
            throw new BaseException("配置类别不能为空");
        }
        if (CollectionUtils.isEmpty(templateVo.getPlatforms())) {
            throw new BaseException("生效平台不能为空");
        }
        if (templateVo.getType() == 1 && templateVo.getProductCodes() == null) {
            throw new BaseException("特殊配置商品id不能为空");
        }
        if (templateVo.getType() == 1 && StringUtils.isEmpty(templateVo.getTemplateName())) {
            throw new BaseException("配置名不能为空");
        }
        //查询通用模板所有配置
        List<PostageTemplateVo> freeDelivery = findCommonSetting();
        if (freeDelivery.size() > 0) {
            //如果是通用模板的新增
            if (StringUtils.isEmpty(templateVo.getId()) && templateVo.getType() == 0) {
                List<String> platforms = freeDelivery.stream().flatMap(t -> Stream.of(t.getPlatforms()))
                        .collect(Collectors.toList())
                        .stream().flatMap(t -> t.stream())
                        .collect(Collectors.toList());
                List<String> intersection = platforms.stream().filter(t -> templateVo.getPlatforms().contains(t)).collect(Collectors.toList());
                if (intersection.size() > 0) {
                    throw new BaseException("一个平台只可以配置一个通用配置");
                }
            } else if (templateVo.getType() == 1) {
                Long maxFreePostagePrice = freeDelivery.stream().flatMap(t -> Stream.of(t.getFreePostagePrice()))
                        .collect(Collectors.toList())
                        .stream().max(Long::compareTo).orElse(null);
                if (templateVo.getFreePostagePrice() < maxFreePostagePrice) {
                    throw new BaseException("包邮门槛不可低于通用配置包邮门槛");
                }
            }
        }
    }

    private void validatePostageType(PostageTemplateVo templateVo) throws BaseException {
        if (CollectionUtils.isEmpty(templateVo.getPostageTypes())) {
            throw new BaseException("快递方式不能为空");
        }

        Map<Integer, PostageTypeVo> deliveryTypes = templateVo.getPostageTypes().stream()
                .collect(Collectors.toMap(PostageTypeVo::getPayType, t -> t));
        for (PostageTypeVo p : deliveryTypes.values()) {
            if (p.getFreeDeliveryTypeIds() == null || p.getFreeDeliveryTypeIds().size() > 2) {
                throw new BaseException("包邮情况必填,且不能多于两个公司");
            }
        }
    }

    private void validHasExistedProductCode(PostageTemplateVo vo, List<String> platforms, List<String> ignoreIds) throws BaseException {
        if (!CollectionUtils.isEmpty(vo.getProductCodes())) {
            Query query = new Query();
            query.addCriteria(Criteria.where("status").is(1));  //查询非停用状态的模板配置
            query.addCriteria(Criteria.where("platforms").in(platforms));
            query.addCriteria(Criteria.where("deleteFlag").is(0));
            query.addCriteria(Criteria.where("type").is(1));
            if (!CollectionUtils.isEmpty(ignoreIds)) {
                Criteria.where("id").ne(ignoreIds); //查询不包括当前Id的产品
            }

            List<PostageTemplate> postageTemplate = mongoTemplate.find(query, PostageTemplate.class);
            if (!CollectionUtils.isEmpty(postageTemplate)) {
                List<Integer> productCodes = postageTemplate.stream().flatMap(t -> t.getProductCodes().stream()).collect(Collectors.toList());
                for (Integer code : vo.getProductCodes()) {
                    if (productCodes.contains(code)) {
                        throw new BaseException("产品" + code + "已经存在");
                    }
                }
            }
        }
    }

    /**
     * 发送邮费配置模板 开始的延时消息
     * @param id
     * @param beginDate
     */
    private void sendDelayQueue(String id, Date beginDate) {
        long startDelay = DateUtils.getDelay(beginDate) + 5000;
        startDelay = startDelay > 0 ? startDelay : 1000;
//        postageTemplateChannel.publishEvent()
//                .send(MessageBuilder.withPayload(id)
//                .setHeader("x-delay", startDelay).build());
    }

    /**
     * 处理邮费配置模板延时消息
     * @param id
     */
    @Override
    public void processDelayQueue(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        PostageTemplate postageTemplate = mongoTemplate.findOne(query, PostageTemplate.class);
        log.info("开始 处理邮费配置模板延时消息,根据Id={}查询结果为{},", id, JSON.toJSON(postageTemplate));
        if (postageTemplate == null) {
            log.info("邮费配置模板Id={}查询结果为空,", id);
        } else if (Math.abs(DateUtils.getDelay(postageTemplate.getBeginDate())) > 300 * 1000) {
            //当前时间和模板开始时间相差超过5分钟，不处理
            log.info("Id={}, 当前时间和模板开始时间相差超过5分钟，不处理", id);
        } else {
            Query parentQuery = new Query(Criteria.where("relativeId").is(id));
            PostageTemplate parentTemplate = mongoTemplate.findOne(parentQuery, PostageTemplate.class);
            //父记录不为空，才需要处理,  父记录删除，子记录生效
            if (parentTemplate != null) {
                log.info("开始 处理邮费配置模板延时消息,根据Id={}查询父记录结果为{},", id, JSON.toJSON(parentTemplate));
                parentTemplate.setDeleteFlag(1);
                mongoTemplate.save(parentTemplate);

                postageTemplate.setDeleteFlag(0);
                mongoTemplate.save(postageTemplate);
            } else {
                log.info("开始 处理邮费配置模板延时消息,根据Id={}查询父记录结果为空,，不处理", id);
            }
        }
    }
    /**
     * 查询某商品配置的id
     */
    @Override
    public List<PostageTemplateVo> findByProductCodes(List<Integer> productCodes) {
        Query query = new Query();
        query.addCriteria(Criteria.where("productCodes").in(productCodes));
        query.addCriteria(Criteria.where("deleteFlag").is(0));
        query.addCriteria(Criteria.where("status").is(1));
        List<PostageTemplate> list = mongoTemplate.find(query, PostageTemplate.class);
        return beanToVoList(list);
    }

    /**
     * 查询某商品配置的id
     */
    @Override
    public List<Integer> findProducts(String id) throws BaseException {
        PostageTemplate postageTemplate = mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), PostageTemplate.class);
        if (postageTemplate == null) {
            throw new BaseException("没有查询到配置");
        }
        return postageTemplate.getProductCodes();
    }

    /**
     * 从配置中移出某商品
     */
    @Override
    public void remove(String id, Integer productCode) throws BaseException {
        if (StringUtils.isEmpty(id)) {
            throw new BaseException("配置id不能为空");
        }
        if (productCode == null) {
            throw new BaseException("产品编码不能为空");
        }
        Query query = new Query(Criteria.where("id").is(id));
        PostageTemplate postageTemplate = mongoTemplate.findOne(query, PostageTemplate.class);
        if (postageTemplate != null && !CollectionUtils.isEmpty(postageTemplate.getProductCodes())) {
            postageTemplate.getProductCodes().remove(productCode);
            mongoTemplate.save(postageTemplate);
        }
    }

    /**
     * 根据快递配置id查询影响的运费配置
     * @param id
     * @return
     */
    @Override
    public List<String> findByDeliveryId(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("postageTypes.freeDeliveryTypeIds").in(id),
                Criteria.where("postageTypes.unFreeDeliveryTypeIds").in(id)));
        List<PostageTemplate> list = mongoTemplate.find(query, PostageTemplate.class);
        List<String> templateIds = null;
        if (list != null && list.size() > 0) {
            templateIds = list.stream().map(t -> t.getId()).collect(Collectors.toList());
        }
        return templateIds;
    }
}
