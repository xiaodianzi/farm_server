package com.plansolve.farm.aspect.client;

import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.CooperationService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.UserUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 校验用户农机手身份
 **/

@Aspect
@Component
public class OperatorAspect {

    private final Logger logger = LoggerFactory.getLogger(OperatorAspect.class);
    @Autowired
    private CooperationService cooperationService;

    @Pointcut("execution(public * com.plansolve.farm.controller.client.main.operator.OperatorOrderController.*(..))")
    public void operatorPointcut() {
    }

    @Before("operatorPointcut()")
    public void isOperator() {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        if (UserUtil.checkUserState(user)) {
            if (user.getOperator() == false) {
                logger.error("=======================================该用户不是农机手=====================================");
                boolean proprieter = cooperationService.proprieter(user);
                if (proprieter == false) {
                    logger.error("=======================================该用户不是社长=====================================");
                    throw new PermissionException("[该用户既不是农机手也不是社长，不能进行该操作]");
                }
            }
        }
    }

}
