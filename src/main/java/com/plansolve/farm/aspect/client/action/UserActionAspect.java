package com.plansolve.farm.aspect.client.action;

import com.plansolve.farm.aspect.client.BaseAspect;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.base.user.UserBaseService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description:
 **/

@Slf4j
@Aspect
@Component
public class UserActionAspect extends BaseAspect {

    @Autowired
    private UserBaseService userService;

    @Pointcut("execution(public * com.plansolve.farm.controller.client.common.UserController.*(..)) " +
            "|| execution(public * com.plansolve.farm.controller.client.main.UserMainController.*(..)) " +
            "|| execution(public * com.plansolve.farm.controller.client.main.UserPayController.*(..)) " +
            "|| execution(public * com.plansolve.farm.controller.client.main.farmer.FarmlandController.*(..)) " +
            "|| execution(public * com.plansolve.farm.controller.client.main.operator.MachineryController.*(..)) " +
            "|| execution(public * com.plansolve.farm.controller.client.main.account..*.*(..))")
    public void userPointcut() {
    }

    @AfterReturning(returning = "object", pointcut = "userPointcut()")
    public void userAfterReturning(JoinPoint joinPoint, Object object) {
        try {
            // 解析返回结果，当返回码为200，即请求成功才可以存储用户行为
            Result result = (Result) object;
            if (result.getCode().equals(200)) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes.getRequest();

                User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);

                if (user == null) {
                    String mobile = (String) request.getSession().getAttribute(SysConstant.MOBILE);
                    if (mobile == null || mobile.isEmpty()) {
                        mobile = (String) request.getSession().getAttribute(SysConstant.VALIDATION_MOBILE);
                    }
                    if (mobile == null || mobile.isEmpty()) {
                        Object[] args = joinPoint.getArgs();
                        if (args != null && args.length > 0) {
                            String tag = "mobile=";
                            for (Object arg : args) {
                                String argStr = arg.toString().trim();

                                if (argStr.length() == 11) {
                                    user = userService.getValidatedUser(argStr);
                                } else if (StringUtil.checkStrExist(tag, argStr)) {
                                    argStr = argStr.substring(argStr.indexOf(tag) + tag.length() + 1, argStr.indexOf(tag) + tag.length() + 1 + 11);
                                    user = userService.getValidatedUser(argStr);
                                }
                                if (user != null) break;
                            }
                        }
                    } else {
                        user = userService.getValidatedUser(mobile);
                    }
                }

                if (user != null) {
                    String methodName = joinPoint.getSignature().getName();
                    if (!methodName.equals("getUser")
                            && !methodName.equals("checkUserMachineryType")
                            && !methodName.equals("getRegistCaptcha")
                            && !methodName.equals("getCaptcha")
                            && !methodName.equals("machineryList")
                            && !methodName.equals("farmlandList")
                            && !methodName.equals("getCertificate")
                            && !methodName.equals("bankList")
                            && !methodName.equals("bankCardList")
                            && !methodName.equals("getUserAccount")) {
                        saveLog(request, joinPoint, null, user, "app", "user");
                    }
                }

            }
        } catch (Exception e) {
            log.error("【用户行为捕捉失败】");
            e.printStackTrace();
        }
    }

}
