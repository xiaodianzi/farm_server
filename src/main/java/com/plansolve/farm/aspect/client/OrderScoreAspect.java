package com.plansolve.farm.aspect.client;

import com.plansolve.farm.model.client.score.ScoreTaskDTO;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.model.enums.type.ActivityTypeEnum;
import com.plansolve.farm.repository.activity.ActivitySwitchRepository;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.service.console.ScoreManageService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ANDREW
 * @Date: 2019/5/13
 * @Description: 下单赚积分切面
 **/
@Aspect
@Component
public class OrderScoreAspect {

    private final Logger logger = LoggerFactory.getLogger(OrderScoreAspect.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ScoreManageService scoreManageService;

    @Autowired
    private ActivitySwitchRepository activitySwitchRepository;

    /**
     * 订单状态通知切面
     */
    @Pointcut("execution(public * com.plansolve.farm.service.client.OrderService.paidOrder(..))")
    public void orderNoticePointcut() {
    }

    /*******************************************************用户通知*******************************************************/

    @AfterReturning(returning = "userOrder", pointcut = "orderNoticePointcut()")
    public ScoreTaskDTO orderNoticeAfterReturning(UserOrder userOrder) {
        ScoreTaskDTO scoreTask = null;
        //判断积分活动是否开启
        boolean valid = activitySwitchRepository.existsByActivityTypeAndValidIsTrue(ActivityTypeEnum.SCORE_EARN_ACTIVITY.getType());
        if (valid){
            if(null != userOrder){
                //判断订单状态
                if (userOrder.getUserOrderState().equals(OrderStateEnum.PREPAID.getState())) {
                    //奖励种植户下单积分
                    User farmer = userService.findUser(userOrder.getCreateBy());
                    scoreTask = scoreManageService.createOrderPointTask(farmer, userOrder);
                    if (scoreTask.getResult()){
                        logger.info("====================================下单积分任务切面:积分奖励已完成================================");
                    }else{
                        logger.info("=============================下单积分任务切面:下单赚积分任务已达月上限==============================");
                    }
                }
            }
        }
        return scoreTask;
    }

    /*******************************************************订单通知*******************************************************/

}
