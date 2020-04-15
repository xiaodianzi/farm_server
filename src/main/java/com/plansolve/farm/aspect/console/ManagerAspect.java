package com.plansolve.farm.aspect.console;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description:
 **/

@Aspect
@Component
public class ManagerAspect {

    private final Logger logger = LoggerFactory.getLogger(ManagerAspect.class);

    @Pointcut("execution(public * com.plansolve.farm.controller.console..*.*(..)) ")
    public void managerLogPointcut() {
    }

    @Before("managerLogPointcut()")
    public void managerLog(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("==================================控制台用户请求日志==================================");
        logger.info("url={}", request.getRequestURI());
        logger.info("method={}", request.getMethod());
        logger.info("ip={}", request.getRemoteAddr());
        logger.info("class_method={}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("args={}", joinPoint.getArgs());
        logger.info("==================================控制台用户请求日志==================================");
    }

    @AfterReturning(returning = "object", pointcut = "managerLogPointcut()")
    public void showReturn(Object object) {
        logger.info("==================================控制台用户响应日志==================================");
        logger.info("response={}", object);
        logger.info("==================================控制台用户响应日志==================================");
    }

}
