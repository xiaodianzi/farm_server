package com.plansolve.farm.aspect.client;

import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EncacheUtil;
import net.sf.ehcache.Cache;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * @Author: Andrew
 * @Date: 2018/3/21
 * @Description: 校验用户种植户身份
 **/

@Aspect
@Component
public class ScoreAspect {

    private final Logger logger = LoggerFactory.getLogger(ScoreAspect.class);

    @Pointcut("execution(public * com.plansolve.farm.controller.client.main.score.ScoreManageController.*(..))")
    public void scorePointcut() {
    }

    @Before("scorePointcut()")
    public void scoreCacheManage() {
        //动态创建积分管理缓存
        Cache scoreCache = EncacheUtil.getOrAddCache(SysConstant.SCORE_CACHE_NAME);
        if (null != scoreCache){
            logger.info(SysConstant.SCORE_CACHE_NAME+"缓存动态获取成功,时间：" + DateUtils.formatDateTime(new Date()));
        }else{
            logger.error(SysConstant.SCORE_CACHE_NAME+"缓存创建失败！时间：" + DateUtils.formatDateTime(new Date()));
        }
    }

}
