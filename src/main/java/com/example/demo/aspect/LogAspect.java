package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//AOP面向切片编程，例如通过定义log切片，统一增加对所有访问的日志记录
@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class.toString());

    @Before("execution(* com.example.demo.Controller.*.*(..))")
    public void beforeMethod(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        for(Object arg : joinPoint.getArgs()) {
            sb.append("arg:" + arg.toString() + " | ");
        }
        logger.info("before logger." + sb.toString());
    }

    @After("execution(* com.example.demo.Controller.*.*(..))")
    public void afterMethod(JoinPoint joinPoint) {
        logger.info("after logger.");
    }
}
