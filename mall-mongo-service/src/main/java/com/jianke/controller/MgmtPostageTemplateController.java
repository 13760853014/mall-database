package com.jianke.controller;

import com.jianke.demo.exception.BaseException;
import com.jianke.service.PostageTemplateService;
import com.jianke.vo.PostageTemplateParam;
import com.jianke.vo.PostageTemplateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 运费模板管理接口
 * @author shichenru
 * @since 2020-03-13
 */
@Slf4j
@RestController
@RequestMapping(value = "/mgmt/postageTemplate")
public class MgmtPostageTemplateController {


    @Autowired
    private PostageTemplateService postageTemplateService;

    //@PreAuthorize("hasAnyAuthority('promo:postageTemplateVo:edit')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<PostageTemplateVo> save(@RequestBody PostageTemplateVo postageTemplateVo) throws Exception {
        PostageTemplateVo vo = postageTemplateService.insert(postageTemplateVo);
        return new ResponseEntity<>(vo, HttpStatus.CREATED);
    }


    //@PreAuthorize("hasAnyAuthority('promo:postageTemplateVo:view')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Page<PostageTemplateVo>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody(required = false) PostageTemplateParam param) throws Exception {

        if (page < 1) {
            throw new BaseException("页数不合法！");
        }
        if (size <= 0) {
            throw new BaseException("页面显示数据条数不合法！");
        }
        Page<PostageTemplateVo> vo = postageTemplateService.findByPage(param, page, size);
        return new ResponseEntity<>(vo, HttpStatus.OK);
    }

    //@PreAuthorize("hasAnyAuthority('promo:postageTemplateVo:view')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<PostageTemplateVo> findOne(
            @PathVariable String id) throws BaseException {
        PostageTemplateVo postageTemplateVo = postageTemplateService.findById(id);
        return new ResponseEntity<>(postageTemplateVo, HttpStatus.OK);
    }

    //@PreAuthorize("hasAnyAuthority('promo:postageTemplateVo:edit')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<PostageTemplateVo> update(
            @RequestBody PostageTemplateVo postageTemplateVo,
            @PathVariable("id") String id) throws Exception {
        postageTemplateVo.setId(id);
        PostageTemplateVo vo = postageTemplateService.update(postageTemplateVo);
        return new ResponseEntity<>(vo, HttpStatus.OK);
    }


    //@PreAuthorize("hasAnyAuthority('promo:postageTemplateVo:edit')")
    @RequestMapping(value = "/stop/{id}", method = RequestMethod.PUT)
    public ResponseEntity stop(@PathVariable("id") String id) throws Exception {
        postageTemplateService.stopById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    //@PreAuthorize("hasAnyAuthority('promo:postageTemplateVo:view')")
    @RequestMapping(value = "/log/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<PostageTemplateVo>> findLogById(
            @PathVariable String id) throws BaseException {
        List<PostageTemplateVo> postageTemplateVo = postageTemplateService.findLogById(id);
        return new ResponseEntity<>(postageTemplateVo, HttpStatus.OK);
    }

    //@PreAuthorize("hasAnyAuthority('promo:postageTemplateVo:view')")
    @RequestMapping(value = "/productCodes", method = RequestMethod.GET)
    public ResponseEntity<List<PostageTemplateVo>> findBySkuCode(@RequestBody List<Integer> productCodes) {
        return new ResponseEntity<>(postageTemplateService.findByProductCodes(productCodes), HttpStatus.OK);
    }

    //@PreAuthorize("hasAnyAuthority('promo:postageTemplateVo:view')")
    @RequestMapping(value = "/{id}/productCodes", method = RequestMethod.GET)
    public ResponseEntity<List<Integer>> findProductCodes(@PathVariable String id) throws BaseException {
        return new ResponseEntity<>(postageTemplateService.findProducts(id), HttpStatus.OK);
    }

    //@PreAuthorize("hasAnyAuthority('promo:postageTemplateVo:view')")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.PUT)
    public ResponseEntity remove(@PathVariable String id, @RequestParam("productCode") Integer productCode) throws BaseException {
        postageTemplateService.remove(id, productCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
