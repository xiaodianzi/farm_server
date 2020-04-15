package com.plansolve.farm.aspect.client;

import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.UserStateEnum;
import com.plansolve.farm.service.console.ScoreManageService;
import com.plansolve.farm.util.AppHttpUtil;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Andrew
 * @Date: 2018/3/21
 * @Description: 用户认证积分的切面
 **/

@Aspect
@Component
public class LoginInScoreAspect {

    private final Logger logger = LoggerFactory.getLogger(LoginInScoreAspect.class);

    @Autowired
    private ScoreManageService scoreManageService;

    @Pointcut("execution(public * com.plansolve.farm.controller.client.common.UserController.loginBy*(..))")
    public void loginInScoreAspect() {}

    @AfterReturning(value = "loginInScoreAspect()")
    public void scoreCacheManage() {
        /*User user = (User)AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        if (user.getUserState().equals(UserStateEnum.NORMOL.getState()) && user.getIsOperator() && user.getIsFarmer()){
            boolean authed = scoreManageService.getAuthedScore(user);
            if (!authed){
                logger.info("====================用户登录后的认证积分切面将执行用户的认证积分任务====================");
                scoreManageService.authPointTask(user, SysConstant.AUTH_ALL_TYPE);
            }
        }*/
        logger.info("====================用户认证积分奖励功能暂停开放====================");
    }

}
