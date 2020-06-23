package com.jianke.mall.resource;

import com.jianke.mall.aop.TransactionLog;
import com.jianke.mall.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: mall-share
 * @description: ${description}
 * @author: chenguiquan
 * @create: 2019-08-12 20:23
 **/

@RestController
@RequestMapping("/svc/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;

    @RequestMapping(method = RequestMethod.GET, value = "/string")
    public ResponseEntity stringDemo() throws Exception {
        redisService.stringDemo();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public ResponseEntity listDemo() throws Exception {
        redisService.listDemo();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/set")
    public ResponseEntity setDemo() throws Exception {
        redisService.setDemo();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/hash")
    public ResponseEntity hashDemo() throws Exception {
        redisService.hashDemo();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/zset")
    public ResponseEntity zSetDemo() throws Exception {
        redisService.zSetDemo();
        return new ResponseEntity<>(HttpStatus.OK);
    }



}
