package com.plansolve.farm.aspect.client;

import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.util.AppHttpUtil;
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
public class AppAspect {

    private final Logger logger = LoggerFactory.getLogger(AppAspect.class);

    @Pointcut("execution(public * com.plansolve.farm.controller.client..*.*(..)) ")
    public void AppLogPointcut() {
    }

    @Before("AppLogPointcut()")
    public void appLog(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("==================================APP用户请求日志==================================");
        logger.info("当前用户={}", AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER));
        logger.info("token={}", request.getHeader("Token"));
        logger.info("url={}", request.getRequestURI());
        logger.info("method={}", request.getMethod());
        logger.info("ip={}", request.getRemoteAddr());
        logger.info("class_method={}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("args={}", joinPoint.getArgs());
        logger.info("==================================APP用户请求日志==================================");
    }

    @AfterReturning(returning = "object", pointcut = "AppLogPointcut()")
    public void showReturn(Object object) {
        logger.info("==================================APP用户响应日志==================================");
        logger.info("response={}", object);
        logger.info("==================================APP用户响应日志==================================");
    }

}
