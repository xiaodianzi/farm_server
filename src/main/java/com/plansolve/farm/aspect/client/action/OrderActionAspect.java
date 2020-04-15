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
public class OrderActionAspect extends BaseAspect {

    @Pointcut("execution(public * com.plansolve.farm.controller.client.main.farmer.FarmerOrderController.*(..)) " +
            "|| execution(public * com.plansolve.farm.controller.client.main.farmer.FarmerPayController.*(..)) " +
            "|| execution(public * com.plansolve.farm.controller.client.main.operator.OperatorOrderController.*(..)) " +
            "|| execution(public * com.plansolve.farm.controller.client.main.operator.OperatorReportController.*(..)) " +
            "|| execution(public * com.plansolve.farm.controller.client.order.ClientFarmerOrderController.*(..))")
    public void orderPointcut() {
    }

    @AfterReturning(returning = "object", pointcut = "orderPointcut()")
    public void userAfterReturning(JoinPoint joinPoint, Object object) {
        try {
            // 解析返回结果，当返回码为200，即请求成功才可以存储用户行为
            Result result = (Result) object;
            if (result.getCode().equals(200)) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes.getRequest();

                User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);

                String methodName = joinPoint.getSignature().getName();
                if (!methodName.equals("payNotify")
                        && !methodName.equals("getOperators")
                        && !methodName.equals("getOperatorState")
                        && !methodName.equals("getDemandPriceAmount")
                        && !methodName.equals("getOperatorReport")
                        && !methodName.equals("getReportDetail")) {
                    saveLog(request, joinPoint, result.getData(), user, "app", "order");
                }
            }
        } catch (Exception e) {
            log.error("【用户行为捕捉失败】");
            e.printStackTrace();
        }
    }

}
