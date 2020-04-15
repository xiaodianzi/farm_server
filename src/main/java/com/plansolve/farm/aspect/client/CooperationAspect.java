package com.plansolve.farm.aspect.client;

import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.util.AppHttpUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Andrew
 * @Date: 2019/04/24
 * @Description: 合作社接口请求之前更新当前用户的信息
 **/

@Aspect
@Component
public class CooperationAspect extends BaseAspect {

    private final Logger logger = LoggerFactory.getLogger(CooperationAspect.class);

    @Autowired
    private UserService userService;

    @Before("cooperationPointcut()")
    public void updateCurrentUser() {
        User u = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        //实时更新session中的用户信息
        if (null != u){
            User user = userService.findUser(u.getIdUser());
            logger.info("=======================================合作社操作之前更新session中的当前用户=======================================");
            AppHttpUtil.getSession().setAttribute(SysConstant.CURRENT_USER, user);
        }
    }

}
