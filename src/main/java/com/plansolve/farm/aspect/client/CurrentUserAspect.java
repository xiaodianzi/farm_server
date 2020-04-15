package com.plansolve.farm.aspect.client;

import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.util.AppHttpUtil;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户免登录状态校验
 * 判断用户是否登录，若否抛出异常，交由处理器统一返回错误信息给客户端，提示重新登录
 **/

@Aspect
@Component
public class CurrentUserAspect {

    private final Logger logger = LoggerFactory.getLogger(CurrentUserAspect.class);

    @Pointcut("execution(public * com.plansolve.farm.service.client.UserService.change*(..))")
    public void updateCurrentUserPointcut() {
    }

    @AfterReturning(returning = "object", pointcut = "updateCurrentUserPointcut()")
    public void updateCurrentUserAfterReturning(Object object) {
        HttpSession session = AppHttpUtil.getSession();
        Object user = session.getAttribute(SysConstant.CURRENT_USER);
        if (user != null) {
            logger.info("=======================================更新服务器中的用户=======================================");
            session.setAttribute(SysConstant.CURRENT_USER, object);
        }
    }

}
