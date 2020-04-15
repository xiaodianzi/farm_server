package com.plansolve.farm.aspect.client;

import com.alibaba.fastjson.JSON;
import com.plansolve.farm.model.database.log.UserActionLog;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.repository.log.UserActionLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/6/12
 * @Description:
 **/
public class BaseAspect {

    @Pointcut("execution(public * com.plansolve.farm.controller.client.main.CooperationController.*(..))")
    public void cooperationPointcut() {
    }

    @Autowired
    private UserActionLogRepository logRepository;

    public UserActionLog saveLog(HttpServletRequest request, JoinPoint joinPoint, Object date,
                                  User user, String platform, String module) {
        UserActionLog log = new UserActionLog();

        if (user != null) {
            log.setIdUser(user.getIdUser());
        }
        log.setPlatform(platform);
        log.setModule(module);
        log.setUrl(request.getRequestURI());
        log.setIp(request.getRemoteAddr());
        String args = JSON.toJSONString(joinPoint.getArgs());
        log.setArgs(args);
        log.setActionTime(new Date());
        if (date != null) {
            log.setResult(JSON.toJSONString(date));
        }
        log = logRepository.save(log);
        return log;
    }

}
