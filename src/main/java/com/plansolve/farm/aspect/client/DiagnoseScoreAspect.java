package com.plansolve.farm.aspect.client;

import com.plansolve.farm.model.PromotionActivityConstant;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.score.ScoreTaskDTO;
import com.plansolve.farm.model.database.agricultural.DiagnoseFeedback;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.type.ActivityTypeEnum;
import com.plansolve.farm.model.enums.type.ScoreTypeEnum;
import com.plansolve.farm.repository.activity.ActivitySwitchRepository;
import com.plansolve.farm.repository.score.ScoreLogRepository;
import com.plansolve.farm.service.console.ScoreManageService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.EncacheUtil;
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
 * @Description: 病虫害诊断积分任务切面
 **/

@Aspect
@Component
public class DiagnoseScoreAspect {

    private final Logger logger = LoggerFactory.getLogger(DiagnoseScoreAspect.class);

    @Autowired
    private ScoreManageService scoreManageService;

    @Autowired
    private ScoreLogRepository scoreLogRepository;

    @Autowired
    private ActivitySwitchRepository activitySwitchRepository;

    @Pointcut("execution(public * com.plansolve.farm.service.opencv.OpenService.saveDiagnoseFeedback(..))")
    public void diagnoseScoreAspect() {
    }

    @AfterReturning(pointcut = "diagnoseScoreAspect()", returning = "diagnoseFeedback")
    public ScoreTaskDTO diagnoseScoreTask(DiagnoseFeedback diagnoseFeedback) {
        ScoreTaskDTO scoreTask = null;
        //判断积分活动是否开启
        boolean valid = activitySwitchRepository.existsByActivityTypeAndValidIsTrue(ActivityTypeEnum.SCORE_EARN_ACTIVITY.getType());
        if (valid){
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            //奖励病虫害诊断积分
            EncacheUtil.getOrAddCache(SysConstant.SCORE_CACHE_NAME);
            Integer diagnoseTime = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.DIAGNOSE_SCORE_PLUS.getType());
            //有效返回诊断结果
            if (null != diagnoseFeedback) {
                //上限5分
                if (diagnoseTime < PromotionActivityConstant.DIAGNOSE_VALID_TIMES) {
                    logger.info("=============================病虫害诊断切面：病虫害诊断积分奖励完成==================================");
                    scoreTask = scoreManageService.diagnosePointTask(user);
                } else {
                    logger.info("==========================病虫害诊断切面：病虫害诊断积分任务次数已达上限==============================");
                    scoreTask = scoreManageService.getDefaultScoreTask(false, "病虫害诊断积分任务次数已达上限");
                }
            }
        }
        return scoreTask;
    }

}
