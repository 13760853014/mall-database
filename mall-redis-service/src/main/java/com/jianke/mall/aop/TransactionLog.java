package com.jianke.mall.aop;

import java.lang.annotation.*;

/**
 * Service层日志拦截注解
 * @author CGQ
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TransactionLog {
}
