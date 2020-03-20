package com.jianke.service;

import com.jianke.demo.exception.BaseException;
import com.jianke.vo.PostageTemplateParam;
import com.jianke.vo.PostageTemplateVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 运费模板接口
 * @author shichenru
 * @since 2020-03-13
 */
public interface PostageTemplateService {

    PostageTemplateVo insert(PostageTemplateVo vo) throws BaseException;

    PostageTemplateVo update(PostageTemplateVo vo) throws BaseException;

    void stopById(String id) throws BaseException;

    PostageTemplateVo findById(String id) throws BaseException;

    List<PostageTemplateVo> findLogById(String id) throws BaseException;

    Page<PostageTemplateVo> findByPage(PostageTemplateParam param, int page, int size) throws BaseException;

    List<PostageTemplateVo> findAvailableAll();

    void processDelayQueue(String id);

    List<PostageTemplateVo> findByProductCodes(List<Integer> productCodes);

    List<Integer> findProducts(String id) throws BaseException;

    void remove(String id, Integer productCode) throws BaseException;

    List<String> findByDeliveryId(String id);
}
