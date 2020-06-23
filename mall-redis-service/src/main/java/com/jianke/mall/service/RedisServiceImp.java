package com.jianke.mall.service;

import com.alibaba.fastjson.JSON;
import com.jianke.mall.aop.TransactionLog;
import com.jianke.mall.entity.Student;
import com.jianke.mall.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImp implements RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @TransactionLog
    public void stringDemo() {
        List<String> keys = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            Student student = new Student(i, "student-" + i, i);
            String key = RedisKeyUtil.getStudentKey(student.getName());
            redisTemplate.opsForValue().set(key, student, 60, TimeUnit.SECONDS);
            keys.add(key);
        }
        for (String key : keys) {
            System.out.println(redisTemplate.opsForValue().get(key));
        }
    }

    /**
     * 保存了20个
     */
    @Override
    public void listDemo() {
        Object obj = "aaa";
        try {
            String key = RedisKeyUtil.getStudentKey("listDemo");
            ListOperations<String, Student> setOperations = redisTemplate.opsForList();
            for (long i = 0; i < 10; i++) {
                Student student = new Student(i, "student-" + i, i);
                setOperations.leftPush(key, student);
                setOperations.leftPush(key, student);
            }
            obj = redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(JSON.toJSONString(obj));
    }

    /**
     * 保存了10个
     */
    @Override
    public void setDemo() {
        String key = RedisKeyUtil.getStudentKey("setDemo");
        SetOperations<String, Student> setOperations = redisTemplate.opsForSet();
        for (long i = 0; i < 10; i++) {
            Student student = new Student(i, "student-" + i, i);
            setOperations.add(key, student);
            setOperations.add(key, student);
        }
        SetOperations<String, Student> operations = redisTemplate.opsForSet();
        operations.members(key).stream().forEach(s -> System.out.println(JSON.toJSONString(s)));
    }

    /**
     * 保存了10个
     */
    @Override
    public void hashDemo() {
        String key = RedisKeyUtil.getStudentKey("hashDemo");
        HashOperations<String, String, Student> setOperations = redisTemplate.opsForHash();
        for (long i = 0; i < 10; i++) {
            Student student = new Student(i, "student-" + i, i);
            setOperations.put(key, student.getId().toString(), student);
        }
        for (long i = 0; i < 10; i++) {
            System.out.println(redisTemplate.opsForHash().get(key, String.valueOf(i)));;
        }
    }

    /**
     * 保存了10个
     */
    @Override
    public void zSetDemo() {
        String key = RedisKeyUtil.getStudentKey("zSetDemo");
        ZSetOperations<String, Student> setOperations = redisTemplate.opsForZSet();
        for (long i = 0; i < 10; i++) {
            Student student = new Student(i, "student-" + i, i);
            setOperations.add(key, student, student.getScore());
            setOperations.add(key, student, student.getScore());
        }
        ZSetOperations<String, Student> operations = redisTemplate.opsForZSet();
        operations.rangeByScore(key, 1, 5).stream().forEach(s -> System.out.println(JSON.toJSONString(s)));
    }


}
