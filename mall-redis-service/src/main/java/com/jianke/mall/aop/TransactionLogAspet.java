package com.jianke.mall.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Service层日志拦截注解
 * @author CGQ
 */
@Aspect
@Component
@Slf4j
public class TransactionLogAspet {

	// Service层切点
	@Pointcut("@annotation(com.jianke.mall.aop.TransactionLog)")
	public void logAspect() {
	}

	/**
	 * 前置通知 用于拦截Controller层记录用户的操作
	 * @param joinPoint 切点
	 */
	@Before("@annotation(com.jianke.mall.aop.TransactionLog)")
	public void doBefore(JoinPoint joinPoint) {
		System.out.println("注解类型前置通知" + joinPoint.getSignature().getName());
	}

	/**
	 * 后置通知 用于拦截Controller层记录用户的操作
	 * @param joinPoint 切点
	 */
	@After("@annotation(com.jianke.mall.aop.TransactionLog)")
	public void doAfter(JoinPoint joinPoint) {
		System.out.println("注解类型后置通知");
	}

}
