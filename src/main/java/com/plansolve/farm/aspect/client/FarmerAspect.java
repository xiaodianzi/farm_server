package com.plansolve.farm.aspect.client;

import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.UserUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 校验用户种植户身份
 **/

@Aspect
@Component
public class FarmerAspect {

    private final Logger logger = LoggerFactory.getLogger(FarmerAspect.class);

    @Pointcut("execution(public * com.plansolve.farm.controller.client.main.farmer.FarmerOrderController.*(..))")
    public void farmerPointcut() {
    }

    @Before("farmerPointcut()")
    public void isFarmer() {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        if (UserUtil.checkUserState(user)) {
            if (user.getFarmer() == false) {
                logger.error("=======================================该用户不是种植户，不能进行该操作=====================================");
                throw new PermissionException("[该用户不是种植户，不能进行该操作]");
            }
        }
    }

}
