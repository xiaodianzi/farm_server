package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.exception.AddRepeatException;
import com.plansolve.farm.exception.NotExistException;
import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.PromotionActivityConstant;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.score.*;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.score.ExchangePermitUser;
import com.plansolve.farm.model.database.score.ScoreLog;
import com.plansolve.farm.model.database.score.ScoreRule;
import com.plansolve.farm.model.database.score.ScoreUser;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.UserStateEnum;
import com.plansolve.farm.model.enums.type.ScoreTypeEnum;
import com.plansolve.farm.repository.score.ExchangePermitUserRepository;
import com.plansolve.farm.repository.score.ScoreLogRepository;
import com.plansolve.farm.repository.score.ScoreRuleRepository;
import com.plansolve.farm.repository.score.ScoreUserRepository;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.service.console.ScoreManageService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EncacheUtil;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * @Author: Andrew
 * @Date: 2019/3/20
 * @Description: 积分管理接口具体实现方法
 */
@Service
public class ScoreManageServiceImpl implements ScoreManageService {

    public static final String NOTICE = "积分试运行期间，积分规则将会适时调整，带来不便敬请谅解！";

    @Autowired
    private UserService userService;

    @Autowired
    private ScoreRuleRepository scoreRuleRepository;

    @Autowired
    private ScoreUserRepository scoreUserRepository;

    @Autowired
    private ScoreLogRepository scoreLogRepository;

    @Autowired
    private ExchangePermitUserRepository exchangePermitUserRepository;

    @Override
    public Page<ScoreRule> getManageScoreRulesInfo(Integer pageNo, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.ASC, "idScoreRule");
        Page<ScoreRule> scoreRules = scoreRuleRepository.findAll(pageable);
        return scoreRules;
    }

    @Override
    public Page<ScoreRule> getScoreRulesInfo(Integer pageNo, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.ASC, "idScoreRule");
        Page<ScoreRule> scoreRules = scoreRuleRepository.findByIsValidTrue(pageable);
        return scoreRules;
    }

    @Override
    public List<ScoreIndexDTO> loadUserScoreDTOs(List<ScoreRule> scoreRules) {
        List<ScoreIndexDTO> scoreIndexDTOS = new ArrayList<>();
        if (null != scoreRules) {
            if (scoreRules.size() > 0) {
                for (ScoreRule scoreRule : scoreRules) {
                    ScoreIndexDTO userScoreDTO = new ScoreIndexDTO(scoreRule.getIdScoreRule(), scoreRule.getRuleName(), scoreRule.getIncreaseScore(),
                            scoreRule.getMemo(), scoreRule.getTaskLabel(),
                            scoreRule.getRuleType(), scoreRule.getAvailableTask(), scoreRule.getRangeType(), scoreRule.getActNum(),
                            scoreRule.getValidTimes(), scoreRule.getIsValid());
                    // BeanUtils.copyProperties(scoreRule, userScoreDTO);
                    scoreIndexDTOS.add(userScoreDTO);
                }
            }
        }
        return scoreIndexDTOS;
    }

    @Override
    @Transactional
    public ScoreRule addOrUpdateScoreRule(ScoreRule scoreRule) {
        if (null != scoreRule) {
            //修改
            if (null != scoreRule.getIdScoreRule()) {
                ScoreRule byIdScoreRule = scoreRuleRepository.findOneByIdScoreRule(scoreRule.getIdScoreRule());
                if (null == byIdScoreRule) {
                    throw new NotExistException("目标对象不存在");
                }
            } else {
                //新增
                ScoreRule rule = scoreRuleRepository.findByRuleName(scoreRule.getRuleName());
                if (null != rule) {
                    throw new AddRepeatException("");
                }
            }
            ScoreRule savedRule = scoreRuleRepository.save(scoreRule);
            return savedRule;
        }
        return null;
    }

    @Override
    @Transactional
    public ScoreTaskDTO signPointTask(User user) {
        //动态获取当前用户的积分缓存——element key：mobile_SIGNIN
        String signCacheKey = user.getMobile() + SysConstant.SIGN_IN_CACHE_KEY_SUFFIX;
        String todayScoreCacheKey = user.getMobile() + SysConstant.TODAY_SCORE_SUFFIX;
        String doneSingInTimesCacheKey = user.getMobile() + SysConstant.SIGN_IN_DONE_TIME_SUFFIX;
        ScoreUser scoreUser = scoreUserRepository.findOneByIdUser(user.getIdUser());
        //首次参加积分任务的用户先创建积分用户表
        if (null == scoreUser) {
            scoreUser = initScoreUser(user);
        }
        Integer orignalScore = scoreUser.getLatestScore();
        ScoreTaskDTO scoreTask = new ScoreTaskDTO();
        scoreTask.setRuleName(SysConstant.SIGN_IN_RULE_NAME);
        EncacheUtil.addNewCacheObj(doneSingInTimesCacheKey, SysConstant.SIGN_IN_VALID_TIMES);
        Integer latestScore = orignalScore + SysConstant.SIGN_IN_VALID_SCORE;
        //更新积分用户数据
        scoreUser.setLatestScore(latestScore);
        scoreUser.setLatestChangeTime(new Date());
        scoreUserRepository.save(scoreUser);
        boolean todayScoreExsited = EncacheUtil.checkElementCached(todayScoreCacheKey);
        if (todayScoreExsited) {
            Element cacheObj = EncacheUtil.getCacheObj(todayScoreCacheKey);
            Integer todayOrignalScore = (Integer) cacheObj.getObjectValue();
            Integer todayScore = todayOrignalScore + SysConstant.SIGN_IN_VALID_SCORE;
            EncacheUtil.addNewCacheObj(todayScoreCacheKey, todayScore);
            scoreTask.setTodayScore(todayScore);
        } else {
            Integer todayScore = SysConstant.SIGN_IN_VALID_SCORE;
            EncacheUtil.addNewCacheObj(todayScoreCacheKey, todayScore);
            scoreTask.setTodayScore(todayScore);
        }
        scoreTask.setLatestScore(latestScore);
        scoreTask.setDoneTimes(SysConstant.SIGN_IN_VALID_TIMES);
        scoreTask.setAvailableTimes(SysConstant.SIGN_IN_VALID_TIMES);
        scoreTask.setResult(true);
        scoreTask.setResultInfo("签到成功");
        EncacheUtil.addNewCacheObj(signCacheKey, scoreTask);
        //积分日志操作数据
        ScoreLog scoreLog = new ScoreLog();
        scoreLog.setIdUser(user.getIdUser());
        scoreLog.setScoreOld(orignalScore);
        scoreLog.setChangeType(ScoreTypeEnum.SIGN_IN_SCORE_PLUS.getType());
        scoreLog.setChangeScore(SysConstant.SIGN_IN_VALID_SCORE);
        scoreLog.setScoreNew(latestScore);
        scoreLog.setDetail("每日签到");
        scoreLog.setChangeTime(new Date());
        //更新积分操作日志表
        updateScoreLog(scoreLog);
        return scoreTask;
    }

    @Override
    @Transactional
    public ScoreTaskDTO authPointTask(User user, String authType) {
        ScoreTaskDTO scoreTask = null;
        if (authType.equals(SysConstant.AUTH_ALL_TYPE)) {
            boolean userAuthed = scoreLogRepository.existsByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.AUTH_USER_SCORE_PLUS.getType());
            boolean farmlandAuthed = scoreLogRepository.existsByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.AUTH_FARMLAND_SCORE_PLUS.getType());
            boolean machineryAuthed = scoreLogRepository.existsByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.AUTH_MACHINERY_SCORE_PLUS.getType());
            if (!userAuthed) {
                authTask(user, SysConstant.AUTH_USER_TYPE);
            }
            if (!farmlandAuthed) {
                authTask(user, SysConstant.AUTH_FARMLAND_TYPE);
            }
            if (!machineryAuthed) {
                authTask(user, SysConstant.AUTH_MACHINERY_TYPE);
            }
        } else {
            scoreTask = authTask(user, authType);
        }
        return scoreTask;
    }

    @Override
    @Transactional
    public ScoreTaskDTO sharePointTask(User user) {
        ScoreTaskDTO scoreTask = new ScoreTaskDTO();
        //动态获取当前用户的积分缓存——element key：mobile_SIGNIN
        String shareAppCacheKey = user.getMobile() + SysConstant.SHARE_APP_CACHE_KEY_SUFFIX;
        String todayScoreCacheKey = user.getMobile() + SysConstant.TODAY_SCORE_SUFFIX;
        String doneShareAppTimesCacheKey = user.getMobile() + SysConstant.SHARE_APP_DONE_TIME_SUFFIX;
        ScoreUser scoreUser = scoreUserRepository.findOneByIdUser(user.getIdUser());
        //首次参加积分任务的用户先创建积分用户表
        if (null == scoreUser) {
            scoreUser = initScoreUser(user);
        }
        Integer orignalScore = scoreUser.getLatestScore();
        scoreTask.setRuleName(SysConstant.SHARE_APP_RULE_NAME);
        Element doneShareAppTimeCache = EncacheUtil.getCacheObj(doneShareAppTimesCacheKey);
        Integer doneShareAppTime = 0;
        if (null != doneShareAppTimeCache) {
            doneShareAppTime = (Integer) doneShareAppTimeCache.getObjectValue() + SysConstant.SCORE_ONCE_TIME;
            EncacheUtil.addNewCacheObj(doneShareAppTimesCacheKey, doneShareAppTime);
            scoreTask.setDoneTimes(doneShareAppTime);
        } else {
            EncacheUtil.addNewCacheObj(doneShareAppTimesCacheKey, SysConstant.SCORE_ONCE_TIME);
            scoreTask.setDoneTimes(SysConstant.SCORE_ONCE_TIME);
            doneShareAppTime = SysConstant.SCORE_ONCE_TIME;
        }
        //每分享2次积1分，每日积分上限为1分，月积分上限为10分
        Integer shareScore = scoreLogRepository.countByChangeTimeAfterAndChangeTypeAndIdUser(DateUtils.getMonthStart(new Date()), ScoreTypeEnum.SHARE_APP_SCORE_PLUS.getType(), user.getIdUser());
        if (shareScore < PromotionActivityConstant.SHARE_APP_MONTH_VALID_TIMES){
            if (doneShareAppTime == 2) {
                Integer latestScore = orignalScore + SysConstant.SHARE_APP_VALID_SCORE;
                //更新积分用户数据
                scoreUser.setLatestScore(latestScore);
                scoreUser.setLatestChangeTime(new Date());
                scoreUserRepository.save(scoreUser);
                //同步更新今日累计积分
                boolean todayScoreExsited = EncacheUtil.checkElementCached(todayScoreCacheKey);
                if (todayScoreExsited) {
                    Element cacheObj = EncacheUtil.getCacheObj(todayScoreCacheKey);
                    Integer todayOrignalScore = (Integer) cacheObj.getObjectValue();
                    Integer todayScore = todayOrignalScore + SysConstant.SHARE_APP_VALID_SCORE;
                    EncacheUtil.addNewCacheObj(todayScoreCacheKey, todayScore);
                    scoreTask.setTodayScore(todayScore);
                } else {
                    Integer todayScore = SysConstant.SHARE_APP_VALID_SCORE;
                    EncacheUtil.addNewCacheObj(todayScoreCacheKey, todayScore);
                    scoreTask.setTodayScore(todayScore);
                }
                scoreTask.setLatestScore(latestScore);
                scoreTask.setResult(true);
                scoreTask.setResultInfo("分享APP成功");
                EncacheUtil.addNewCacheObj(shareAppCacheKey, scoreTask);
                //积分日志操作数据
                ScoreLog scoreLog = new ScoreLog();
                scoreLog.setIdUser(user.getIdUser());
                scoreLog.setScoreOld(orignalScore);
                scoreLog.setChangeType(ScoreTypeEnum.SHARE_APP_SCORE_PLUS.getType());
                scoreLog.setChangeScore(SysConstant.SHARE_APP_VALID_SCORE);
                scoreLog.setScoreNew(latestScore);
                scoreLog.setDetail("分享APP");
                scoreLog.setChangeTime(new Date());
                //更新积分操作日志表
                updateScoreLog(scoreLog);
            } else {
                scoreTask = getDefaultScoreTask(false, "分享成功，离奖励只差一点点");
            }
        }else{
            scoreTask = getDefaultScoreTask(false, "分享成功，离奖励只差一点点");
        }
        return scoreTask;
    }

    @Override
    @Transactional
    public ScoreTaskDTO createOrderPointTask(User user, UserOrder userOrder) {
        ScoreTaskDTO scoreTask = new ScoreTaskDTO();
        //动态获取当前用户的积分缓存——element key：mobile_SIGNIN
        String createOrderScoreCacheKey = user.getMobile() + SysConstant.CREATE_ORDER_CACHE_KEY_SUFFIX;
        String todayScoreCacheKey = user.getMobile() + SysConstant.TODAY_SCORE_SUFFIX;
        String doneCreateTimesCacheKey = user.getMobile() + SysConstant.CREATE_ORDER_DONE_TIME_SUFFIX;
        //关联积分任务表
        ScoreUser scoreUser = scoreUserRepository.findOneByIdUser(user.getIdUser());
        //首次参加积分任务的用户先创建积分用户表
        if (null == scoreUser) {
            scoreUser = initScoreUser(user);
        }
        Integer orignalScore = scoreUser.getLatestScore();
        scoreTask.setRuleName(SysConstant.CREATE_ORDER_RULE_NAME);
        //获取当月已完成次数——月上限2次
        Integer createdOrignalTime = scoreLogRepository.countByChangeTimeAfterAndChangeTypeAndIdUser(DateUtils.getMonthStart(new Date()), ScoreTypeEnum.CREATE_ORDER_SCORE_PLUS.getType(), user.getIdUser());
        if (createdOrignalTime < PromotionActivityConstant.CREATE_ORDER_MONTH_VALID_TIMES){
            Integer createdTime = createdOrignalTime + 1;
            EncacheUtil.addNewCacheObj(doneCreateTimesCacheKey, createdTime);
            //最新积分变量初始化
            Integer latestScore = 0;
            if (null != userOrder.getIsOnlinePayment()){
                if (userOrder.getIsOnlinePayment()){
                    latestScore = orignalScore + SysConstant.CREATE_ORDER_WXPAYED_SCORE;
                }else{
                    latestScore = orignalScore + SysConstant.CREATE_ORDER_OFFLINE_SCORE;
                }
            }else{
                latestScore = orignalScore + SysConstant.CREATE_ORDER_OFFLINE_SCORE;
            }
            //更新积分用户数据
            scoreUser.setLatestScore(latestScore);
            scoreUser.setLatestChangeTime(new Date());
            scoreUserRepository.save(scoreUser);
            boolean todayScoreExsited = EncacheUtil.checkElementCached(todayScoreCacheKey);
            Integer todayScore = 0;
            if (todayScoreExsited) {
                Element cacheObj = EncacheUtil.getCacheObj(todayScoreCacheKey);
                Integer todayOrignalScore = (Integer) cacheObj.getObjectValue();
                if (null != userOrder.getIsOnlinePayment()){
                    if (userOrder.getIsOnlinePayment()){
                        todayScore = todayOrignalScore + SysConstant.CREATE_ORDER_WXPAYED_SCORE;
                    }else{
                        todayScore = todayOrignalScore + SysConstant.CREATE_ORDER_OFFLINE_SCORE;
                    }
                }else {
                    todayScore = todayOrignalScore + SysConstant.CREATE_ORDER_OFFLINE_SCORE;
                }
                EncacheUtil.addNewCacheObj(todayScoreCacheKey, todayScore);
                scoreTask.setTodayScore(todayScore);
            } else {
                if (null != userOrder.getIsOnlinePayment()){
                    if (userOrder.getIsOnlinePayment()){
                        todayScore = SysConstant.CREATE_ORDER_WXPAYED_SCORE;
                    }else{
                        todayScore = SysConstant.CREATE_ORDER_OFFLINE_SCORE;
                    }
                }else {
                    todayScore = SysConstant.CREATE_ORDER_OFFLINE_SCORE;
                }
                EncacheUtil.addNewCacheObj(todayScoreCacheKey, todayScore);
                scoreTask.setTodayScore(todayScore);
            }
            scoreTask.setLatestScore(latestScore);
            scoreTask.setDoneTimes(createdTime);
            scoreTask.setAvailableTimes(SysConstant.CREATE_ORDER_VALID_TIMES);
            scoreTask.setResult(true);
            scoreTask.setResultInfo("下单积分任务已完成");
            EncacheUtil.addNewCacheObj(createOrderScoreCacheKey, scoreTask);
            //积分日志操作数据
            ScoreLog scoreLog = new ScoreLog();
            scoreLog.setIdUser(user.getIdUser());
            scoreLog.setScoreOld(orignalScore);
            scoreLog.setChangeType(ScoreTypeEnum.CREATE_ORDER_SCORE_PLUS.getType());
            if (null != userOrder.getIsOnlinePayment()){
                if (userOrder.getIsOnlinePayment()){
                    scoreLog.setChangeScore(SysConstant.CREATE_ORDER_WXPAYED_SCORE);
                }else{
                    scoreLog.setChangeScore(SysConstant.CREATE_ORDER_OFFLINE_SCORE);
                }
            }else {
                scoreLog.setChangeScore(SysConstant.CREATE_ORDER_OFFLINE_SCORE);
            }
            scoreLog.setScoreNew(latestScore);
            scoreLog.setDetail("农机需求订单");
            scoreLog.setChangeTime(new Date());
            //更新积分操作日志表
            updateScoreLog(scoreLog);
        }else{
            scoreTask = getDefaultScoreTask(false, "下单赚积分任务已达月上限");
        }
        return scoreTask;
    }

    @Override
    @Transactional
    public ScoreTaskDTO acceptOrderPointTask(User user) {
        ScoreTaskDTO scoreTask = new ScoreTaskDTO();
        //动态获取当前用户的积分缓存——element key：mobile_SIGNIN
        String acceptOrderScoreCacheKey = user.getMobile() + SysConstant.ACCEPT_ORDER_CACHE_KEY_SUFFIX;
        String todayScoreCacheKey = user.getMobile() + SysConstant.TODAY_SCORE_SUFFIX;
        String doneAcceptTimesCacheKey = user.getMobile() + SysConstant.ACCEPT_ORDER_DONE_TIME_SUFFIX;
        //关联积分任务表
        ScoreUser scoreUser = scoreUserRepository.findOneByIdUser(user.getIdUser());
        //首次参加积分任务的用户先创建积分用户表
        if (null == scoreUser) {
            scoreUser = initScoreUser(user);
        }
        Integer orignalScore = scoreUser.getLatestScore();
        scoreTask.setRuleName(SysConstant.ACCEPT_ORDER_RULE_NAME);
        //获取已完成次数
        Integer acceptOrignalTime = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.ACCEPT_ORDER_SCORE_PLUS.getType());
        Integer acceptedTime = acceptOrignalTime + 1;
        EncacheUtil.addNewCacheObj(doneAcceptTimesCacheKey, acceptedTime);
        Integer latestScore = orignalScore + SysConstant.ACCEPT_ORDER_VALID_SCORE;
        //更新积分用户数据
        scoreUser.setLatestScore(latestScore);
        scoreUser.setLatestChangeTime(new Date());
        scoreUserRepository.save(scoreUser);
        boolean todayScoreExsited = EncacheUtil.checkElementCached(todayScoreCacheKey);
        if (todayScoreExsited) {
            Element cacheObj = EncacheUtil.getCacheObj(todayScoreCacheKey);
            Integer todayOrignalScore = (Integer) cacheObj.getObjectValue();
            Integer todayScore = todayOrignalScore + SysConstant.ACCEPT_ORDER_VALID_SCORE;
            EncacheUtil.addNewCacheObj(todayScoreCacheKey, todayScore);
            scoreTask.setTodayScore(todayScore);
        } else {
            Integer todayScore = SysConstant.ACCEPT_ORDER_VALID_SCORE;
            EncacheUtil.addNewCacheObj(todayScoreCacheKey, todayScore);
            scoreTask.setTodayScore(todayScore);
        }
        scoreTask.setLatestScore(latestScore);
        scoreTask.setDoneTimes(acceptedTime);
        scoreTask.setAvailableTimes(SysConstant.ACCEPT_ORDER_VALID_TIMES);
        scoreTask.setResult(true);
        scoreTask.setResultInfo("接单积分任务已完成");
        EncacheUtil.addNewCacheObj(acceptOrderScoreCacheKey, scoreTask);
        //积分日志操作数据
        ScoreLog scoreLog = new ScoreLog();
        scoreLog.setIdUser(user.getIdUser());
        scoreLog.setScoreOld(orignalScore);
        scoreLog.setChangeType(ScoreTypeEnum.ACCEPT_ORDER_SCORE_PLUS.getType());
        scoreLog.setChangeScore(SysConstant.ACCEPT_ORDER_VALID_SCORE);
        scoreLog.setScoreNew(latestScore);
        scoreLog.setDetail("接单作业");
        scoreLog.setChangeTime(new Date());
        //更新积分操作日志表
        updateScoreLog(scoreLog);
        return scoreTask;
    }

    @Override
    @Transactional
    public ScoreTaskDTO diagnosePointTask(User user) {
        ScoreTaskDTO scoreTask = new ScoreTaskDTO();
        //动态获取当前用户的积分缓存——element key：mobile_SIGNIN
        String diagnoseScoreCacheKey = user.getMobile() + SysConstant.DIAGNOSE_CACHE_KEY_SUFFIX;
        String todayScoreCacheKey = user.getMobile() + SysConstant.TODAY_SCORE_SUFFIX;
        String doneDiagnoseTimesCacheKey = user.getMobile() + SysConstant.DIAGNOSE_DONE_TIME_SUFFIX;
        //关联积分任务表
        ScoreUser scoreUser = scoreUserRepository.findOneByIdUser(user.getIdUser());
        //首次参加积分任务的用户先创建积分用户表
        if (null == scoreUser) {
            scoreUser = initScoreUser(user);
        }
        Integer orignalScore = scoreUser.getLatestScore();
        scoreTask.setRuleName(SysConstant.DIAGNOSE_RULE_NAME);
        //获取已完成次数
        Integer diagnoseOrignalTime = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.DIAGNOSE_SCORE_PLUS.getType());
        Integer diagnosedTime = diagnoseOrignalTime + 1;
        EncacheUtil.addNewCacheObj(doneDiagnoseTimesCacheKey, diagnosedTime);
        Integer latestScore = orignalScore + SysConstant.DIAGNOSE_VALID_SCORE;
        //更新积分用户数据
        scoreUser.setLatestScore(latestScore);
        scoreUser.setLatestChangeTime(new Date());
        scoreUserRepository.save(scoreUser);
        boolean todayScoreExsited = EncacheUtil.checkElementCached(todayScoreCacheKey);
        if (todayScoreExsited) {
            Element cacheObj = EncacheUtil.getCacheObj(todayScoreCacheKey);
            Integer todayOrignalScore = (Integer) cacheObj.getObjectValue();
            Integer todayScore = todayOrignalScore + SysConstant.DIAGNOSE_VALID_SCORE;
            EncacheUtil.addNewCacheObj(todayScoreCacheKey, todayScore);
            scoreTask.setTodayScore(todayScore);
        } else {
            Integer todayScore = SysConstant.DIAGNOSE_VALID_SCORE;
            EncacheUtil.addNewCacheObj(todayScoreCacheKey, todayScore);
            scoreTask.setTodayScore(todayScore);
        }
        scoreTask.setLatestScore(latestScore);
        scoreTask.setDoneTimes(diagnosedTime);
        scoreTask.setAvailableTimes(SysConstant.DIAGNOSE_VALID_TIMES);
        scoreTask.setResult(true);
        scoreTask.setResultInfo("病虫害诊断积分任务已完成");
        EncacheUtil.addNewCacheObj(diagnoseScoreCacheKey, scoreTask);
        //积分日志操作数据
        ScoreLog scoreLog = new ScoreLog();
        scoreLog.setIdUser(user.getIdUser());
        scoreLog.setScoreOld(orignalScore);
        scoreLog.setChangeType(ScoreTypeEnum.DIAGNOSE_SCORE_PLUS.getType());
        scoreLog.setChangeScore(SysConstant.DIAGNOSE_VALID_SCORE);
        scoreLog.setScoreNew(latestScore);
        scoreLog.setDetail("病虫害诊断");
        scoreLog.setChangeTime(new Date());
        //更新积分操作日志表
        updateScoreLog(scoreLog);
        return scoreTask;
    }

    @Override
    public List<ScoreIndexDTO> loadScoreIndexData(User user, List<ScoreRule> scoreRules) {
        //动态获取积分缓存
        EncacheUtil.getOrAddCache(SysConstant.SCORE_CACHE_NAME);
        List<ScoreIndexDTO> scoreDTOS = new ArrayList<>();
        if (scoreRules.size() > 0) {
            for (ScoreRule scoreRule : scoreRules) {
                ScoreIndexDTO scoreIndexDTO = new ScoreIndexDTO();
                scoreIndexDTO.setIdScoreRule(scoreRule.getIdScoreRule());
                scoreIndexDTO.setRuleName(scoreRule.getRuleName());
                scoreIndexDTO.setIncreaseScore(scoreRule.getIncreaseScore());
                scoreIndexDTO.setMemo(scoreRule.getMemo());
                scoreIndexDTO.setTaskLabel(scoreRule.getTaskLabel());
                scoreIndexDTO.setAvailableTask(scoreRule.getAvailableTask());
                switch (scoreRule.getRuleName()) {
                    case SysConstant.SIGN_IN_RULE_NAME:
                        String doneSingInTimesCacheKey = user.getMobile() + SysConstant.SIGN_IN_DONE_TIME_SUFFIX;
                        scoreIndexDTO.setAvailableTimes(SysConstant.SIGN_IN_VALID_TIMES);
                        scoreIndexDTO.setScoreTaskUrl(SysConstant.SIGN_IN_LABLE);
                        Element signCacheObj = EncacheUtil.getCacheObj(doneSingInTimesCacheKey);
                        if (null == signCacheObj) {
                            boolean signed = scoreLogRepository.existsByChangeTimeAfterAndChangeTypeAndIdUser(DateUtils.getDayBegin(new Date()), ScoreTypeEnum.SIGN_IN_SCORE_PLUS.getType(), user.getIdUser());
                            if (signed) {
                                EncacheUtil.addNewCacheObj(doneSingInTimesCacheKey, SysConstant.SIGN_IN_VALID_TIMES);
                                scoreIndexDTO.setDoneTimes(SysConstant.SIGN_IN_VALID_TIMES);

                            } else {
                                EncacheUtil.addNewCacheObj(doneSingInTimesCacheKey, SysConstant.INITIAL_DONE_TIMES);
                                scoreIndexDTO.setDoneTimes(SysConstant.INITIAL_DONE_TIMES);
                            }
                        } else {
                            scoreIndexDTO.setDoneTimes((Integer) signCacheObj.getObjectValue());
                        }
                        break;
                    case SysConstant.AUTH_RULE_NAME:
                        String doneAuthTimesCacheKey = user.getMobile() + SysConstant.AUTH_DONE_TIME_SUFFIX;
                        scoreIndexDTO.setAvailableTimes(SysConstant.AUTH_VALID_TIMES);
                        scoreIndexDTO.setScoreTaskUrl(SysConstant.AUTH_URL_LABLE);
                        Integer authedTime = getAuthedTime(user);
                        EncacheUtil.addNewCacheObj(doneAuthTimesCacheKey, authedTime);
                        scoreIndexDTO.setDoneTimes(authedTime);
                        break;
                    case SysConstant.SHARE_APP_RULE_NAME:
                        String doneShareAppTimesCacheKey = user.getMobile() + SysConstant.SHARE_APP_DONE_TIME_SUFFIX;
                        scoreIndexDTO.setAvailableTimes(SysConstant.SHARE_VALID_TIMES);
                        scoreIndexDTO.setScoreTaskUrl(SysConstant.SHARE_APP_LABLE);
                        boolean sharedApp = EncacheUtil.checkElementCached(doneShareAppTimesCacheKey);
                        if (sharedApp) {
                            Element shareAppCacheKey = EncacheUtil.getCacheObj(doneShareAppTimesCacheKey);
                            Integer sharedAppTimes = (Integer) shareAppCacheKey.getObjectValue();
                            scoreIndexDTO.setDoneTimes(sharedAppTimes);
                        } else {
                            scoreIndexDTO.setDoneTimes(SysConstant.INITIAL_DONE_TIMES);
                        }
                        break;
                    case SysConstant.CREATE_ORDER_RULE_NAME:
                        String doneCreateOrderTimesCacheKey = user.getMobile() + SysConstant.CREATE_ORDER_DONE_TIME_SUFFIX;
                        scoreIndexDTO.setAvailableTimes(SysConstant.CREATE_ORDER_VALID_TIMES);
                        scoreIndexDTO.setScoreTaskUrl(SysConstant.CREATE_ORDER_LABLE);
                        Element createOrderCacheObj = EncacheUtil.getCacheObj(doneCreateOrderTimesCacheKey);
                        if (null == createOrderCacheObj) {
                            Integer createOrderTime = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.CREATE_ORDER_SCORE_PLUS.getType());
                            EncacheUtil.addNewCacheObj(doneCreateOrderTimesCacheKey, createOrderTime);
                            scoreIndexDTO.setDoneTimes(createOrderTime);
                        } else {
                            scoreIndexDTO.setDoneTimes((Integer) createOrderCacheObj.getObjectValue());
                        }
                        break;
                    case SysConstant.ACCEPT_ORDER_RULE_NAME:
                        String doneAcceptOrderTimesCacheKey = user.getMobile() + SysConstant.ACCEPT_ORDER_DONE_TIME_SUFFIX;
                        scoreIndexDTO.setAvailableTimes(SysConstant.ACCEPT_ORDER_VALID_TIMES);
                        scoreIndexDTO.setScoreTaskUrl(SysConstant.ACCEPT_ORDER_LABLE);
                        Element acceptOrderCacheObj = EncacheUtil.getCacheObj(doneAcceptOrderTimesCacheKey);
                        if (null == acceptOrderCacheObj) {
                            Integer acceptOrderTime = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.ACCEPT_ORDER_SCORE_PLUS.getType());
                            EncacheUtil.addNewCacheObj(doneAcceptOrderTimesCacheKey, acceptOrderTime);
                            scoreIndexDTO.setDoneTimes(acceptOrderTime);
                        } else {
                            scoreIndexDTO.setDoneTimes((Integer) acceptOrderCacheObj.getObjectValue());
                        }
                        break;
                    case SysConstant.DIAGNOSE_RULE_NAME:
                        String doneDiagnoseTimesCacheKey = user.getMobile() + SysConstant.DIAGNOSE_DONE_TIME_SUFFIX;
                        scoreIndexDTO.setAvailableTimes(SysConstant.DIAGNOSE_VALID_TIMES);
                        scoreIndexDTO.setScoreTaskUrl(SysConstant.DIAGNOSE_URL_LABLE);
                        Element diagnoseCacheObj = EncacheUtil.getCacheObj(doneDiagnoseTimesCacheKey);
                        if (null == diagnoseCacheObj) {
                            Integer diagnoseTime = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.DIAGNOSE_SCORE_PLUS.getType());
                            EncacheUtil.addNewCacheObj(doneDiagnoseTimesCacheKey, diagnoseTime);
                            scoreIndexDTO.setDoneTimes(diagnoseTime);
                        } else {
                            scoreIndexDTO.setDoneTimes((Integer) diagnoseCacheObj.getObjectValue());
                        }
                        break;
                    default:
                        break;
                }
                scoreDTOS.add(scoreIndexDTO);
            }
        }
        return scoreDTOS;
    }

    @Override
    public UserScoreDataDTO loadUserScoreData(User user, List<ScoreRule> scoreRules) {
        //动态获取积分缓存
        EncacheUtil.getOrAddCache(SysConstant.SCORE_CACHE_NAME);
        UserScoreDataDTO userScoreData = new UserScoreDataDTO();
        List<String> noticeList = new ArrayList<>();
        noticeList.add(NOTICE);
        userScoreData.setNotices(noticeList);
        ScoreUser scoreUser = scoreUserRepository.findOneByIdUser(user.getIdUser());
        //设置最新积分和今日积分
        if (null != scoreUser) {
            String todayScoreCacheKey = user.getMobile() + "_TODAY_SCORE";
            Element cacheObj = EncacheUtil.getCacheObj(todayScoreCacheKey);
            if (null == cacheObj) {
                EncacheUtil.addNewCacheObj(todayScoreCacheKey, SysConstant.INITIAL_USER_SCORE);
                userScoreData.setTodayScore(SysConstant.INITIAL_USER_SCORE);
            } else {
                Integer todayScore = (Integer) cacheObj.getObjectValue();
                userScoreData.setTodayScore(todayScore);
            }
            userScoreData.setLatestScore(scoreUser.getLatestScore());
        }
        if (scoreRules.size() > 0) {
            List<ScoreIndexDTO> scoreIndexDTOList = new ArrayList<>();
            for (ScoreRule scoreRule : scoreRules) {
                ScoreIndexDTO scoreIndexDTO = new ScoreIndexDTO();
                scoreIndexDTO.setIdScoreRule(scoreRule.getIdScoreRule());
                scoreIndexDTO.setRuleName(scoreRule.getRuleName());
                scoreIndexDTO.setIncreaseScore(scoreRule.getIncreaseScore());
                scoreIndexDTO.setMemo(scoreRule.getMemo());
                scoreIndexDTO.setRuleType(scoreRule.getRuleType());
                scoreIndexDTO.setAvailableTask(scoreRule.getAvailableTask());
                switch (scoreRule.getRuleName()) {
                    case SysConstant.SIGN_IN_RULE_NAME:
                        String doneSingInTimesCacheKey = user.getMobile() + SysConstant.SIGN_IN_DONE_TIME_SUFFIX;
                        scoreIndexDTO.setAvailableTimes(SysConstant.SIGN_IN_VALID_TIMES);
                        Element signCacheObj = EncacheUtil.getCacheObj(doneSingInTimesCacheKey);
                        Integer signTime = 0;
                        if (null == signCacheObj) {
                            boolean signed = scoreLogRepository.existsByChangeTimeAfterAndChangeTypeAndIdUser(DateUtils.getDayBegin(new Date()), ScoreTypeEnum.SIGN_IN_SCORE_PLUS.getType(), user.getIdUser());
                            if (signed) {
                                EncacheUtil.addNewCacheObj(doneSingInTimesCacheKey, SysConstant.SIGN_IN_VALID_TIMES);
                                scoreIndexDTO.setDoneTimes(SysConstant.SIGN_IN_VALID_TIMES);
                                signTime = SysConstant.SIGN_IN_VALID_TIMES;
                            } else {
                                EncacheUtil.addNewCacheObj(doneSingInTimesCacheKey, SysConstant.INITIAL_DONE_TIMES);
                                scoreIndexDTO.setDoneTimes(SysConstant.INITIAL_DONE_TIMES);
                                signTime = SysConstant.INITIAL_DONE_TIMES;
                            }
                        } else {
                            scoreIndexDTO.setDoneTimes((Integer) signCacheObj.getObjectValue());
                            signTime = (Integer) signCacheObj.getObjectValue();
                        }
                        scoreIndexDTO.setDoneLabel("已获" + signTime + "次");
                        break;
                    case SysConstant.AUTH_RULE_NAME:
                        String doneAuthTimesCacheKey = user.getMobile() + SysConstant.AUTH_DONE_TIME_SUFFIX;
                        scoreIndexDTO.setAvailableTimes(SysConstant.AUTH_VALID_TIMES);
                        Integer authedTime = getAuthedTime(user);
                        EncacheUtil.addNewCacheObj(doneAuthTimesCacheKey, authedTime);
                        scoreIndexDTO.setDoneTimes(authedTime);
                        scoreIndexDTO.setDoneLabel("已获" + authedTime + "次");
                        break;
                    case SysConstant.SHARE_APP_RULE_NAME:
                        String doneShareAppTimesCacheKey = user.getMobile() + SysConstant.SHARE_APP_DONE_TIME_SUFFIX;
                        scoreIndexDTO.setAvailableTimes(SysConstant.SHARE_VALID_TIMES);
                        boolean sharedApp = EncacheUtil.checkElementCached(doneShareAppTimesCacheKey);
                        Integer sharedTimes = 0;
                        if (sharedApp) {
                            Element shareAppCacheKey = EncacheUtil.getCacheObj(doneShareAppTimesCacheKey);
                            Integer sharedAppTimes = (Integer) shareAppCacheKey.getObjectValue();
                            scoreIndexDTO.setDoneTimes(sharedAppTimes);
                            sharedTimes = sharedAppTimes;
                        } else {
                            scoreIndexDTO.setDoneTimes(SysConstant.INITIAL_DONE_TIMES);
                            sharedTimes = SysConstant.INITIAL_DONE_TIMES;
                        }
                        scoreIndexDTO.setDoneLabel("已获" + sharedTimes + "次");
                        break;
                    case SysConstant.CREATE_ORDER_RULE_NAME:
                        String doneCreateOrderTimesCacheKey = user.getMobile() + SysConstant.CREATE_ORDER_DONE_TIME_SUFFIX;
                        scoreIndexDTO.setAvailableTimes(SysConstant.CREATE_ORDER_VALID_TIMES);
                        Element createOrderCacheObj = EncacheUtil.getCacheObj(doneCreateOrderTimesCacheKey);
                        Integer createdTime = 0;
                        if (null == createOrderCacheObj) {
                            Integer createOrderTime = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.CREATE_ORDER_SCORE_PLUS.getType());
                            EncacheUtil.addNewCacheObj(doneCreateOrderTimesCacheKey, createOrderTime);
                            scoreIndexDTO.setDoneTimes(createOrderTime);
                            createdTime = createOrderTime;
                        } else {
                            scoreIndexDTO.setDoneTimes((Integer) createOrderCacheObj.getObjectValue());
                            createdTime = (Integer) createOrderCacheObj.getObjectValue();
                        }
                        scoreIndexDTO.setDoneLabel("已获" + createdTime + "次");
                        break;
                    case SysConstant.ACCEPT_ORDER_RULE_NAME:
                        String doneAcceptOrderTimesCacheKey = user.getMobile() + SysConstant.ACCEPT_ORDER_DONE_TIME_SUFFIX;
                        scoreIndexDTO.setAvailableTimes(SysConstant.ACCEPT_ORDER_VALID_TIMES);
                        Element acceptOrderCacheObj = EncacheUtil.getCacheObj(doneAcceptOrderTimesCacheKey);
                        Integer acceptedTime = 0;
                        if (null == acceptOrderCacheObj) {
                            Integer acceptOrderTime = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.ACCEPT_ORDER_SCORE_PLUS.getType());
                            EncacheUtil.addNewCacheObj(doneAcceptOrderTimesCacheKey, acceptOrderTime);
                            scoreIndexDTO.setDoneTimes(acceptOrderTime);
                            acceptedTime = acceptOrderTime;
                        } else {
                            scoreIndexDTO.setDoneTimes((Integer) acceptOrderCacheObj.getObjectValue());
                            acceptedTime = (Integer) acceptOrderCacheObj.getObjectValue();
                        }
                        scoreIndexDTO.setDoneLabel("已获" + acceptedTime + "次");
                        break;
                    case SysConstant.DIAGNOSE_RULE_NAME:
                        String doneDiagnoseTimesCacheKey = user.getMobile() + SysConstant.DIAGNOSE_DONE_TIME_SUFFIX;
                        scoreIndexDTO.setAvailableTimes(SysConstant.DIAGNOSE_VALID_TIMES);
                        Element diagnoseCacheObj = EncacheUtil.getCacheObj(doneDiagnoseTimesCacheKey);
                        Integer diagnosedTime = 0;
                        if (null == diagnoseCacheObj) {
                            Integer diagnoseTime = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.DIAGNOSE_SCORE_PLUS.getType());
                            EncacheUtil.addNewCacheObj(doneDiagnoseTimesCacheKey, diagnoseTime);
                            scoreIndexDTO.setDoneTimes(diagnoseTime);
                            diagnosedTime = diagnoseTime;
                        } else {
                            scoreIndexDTO.setDoneTimes((Integer) diagnoseCacheObj.getObjectValue());
                            diagnosedTime = (Integer) diagnoseCacheObj.getObjectValue();
                        }
                        scoreIndexDTO.setDoneLabel("已获" + diagnosedTime + "次");
                        break;
                    default:
                        break;
                }
                scoreIndexDTOList.add(scoreIndexDTO);
            }
            userScoreData.setScoreIndexDTOS(scoreIndexDTOList);
        }
        return userScoreData;
    }

    @Override
    public ScoreTaskDTO getDefaultScoreTask(boolean result, String resultInfo) {
        ScoreTaskDTO scoreTask = new ScoreTaskDTO();
        scoreTask.setResult(result);
        scoreTask.setResultInfo(resultInfo);
        return scoreTask;
    }

    @Override
    @Transactional
    public UserScoreDTO getUserScoreData(User user) {
        UserScoreDTO userScoreDTO = new UserScoreDTO();
        ScoreUser scoreUser = scoreUserRepository.findOneByIdUser(user.getIdUser());
        if (null == scoreUser) {
            scoreUser = initScoreUser(user);
        }
        Integer latestScore = scoreUser.getLatestScore();
        userScoreDTO.setLatestScore(latestScore);
        String todayScoreCacheKey = user.getMobile() + SysConstant.TODAY_SCORE_SUFFIX;
        boolean todayScoreExsited = EncacheUtil.checkElementCached(todayScoreCacheKey);
        if (todayScoreExsited) {
            Element cacheObj = EncacheUtil.getCacheObj(todayScoreCacheKey);
            Integer todayScore = (Integer) cacheObj.getObjectValue();
            userScoreDTO.setTodayScore(todayScore);
        } else {
            userScoreDTO.setTodayScore(SysConstant.INITIAL_USER_SCORE);
        }
        List<ScoreRule> scoreRules = scoreRuleRepository.findByIsValidTrue();
        for (ScoreRule scoreRule : scoreRules) {
            ScoreRuleDTO sr = new ScoreRuleDTO();
            sr.setTaskScore(scoreRule.getIncreaseScore());
            switch (scoreRule.getRuleName()) {
                case SysConstant.SIGN_IN_RULE_NAME:
                    Integer doneSingTimes = scoreLogRepository.countByChangeTimeAfterAndChangeTypeAndIdUser(DateUtils.getDayBegin(new Date()), ScoreTypeEnum.SIGN_IN_SCORE_PLUS.getType(), user.getIdUser());
                    sr.setDoneTimes(doneSingTimes);
                    userScoreDTO.setSign(sr);
                    break;
                case SysConstant.AUTH_RULE_NAME:
                    Integer authedTime = getAuthedTime(user);
                    sr.setDoneTimes(authedTime);
                    userScoreDTO.setAuth(sr);
                    break;
                case SysConstant.SHARE_APP_RULE_NAME:
                    String doneShareTimeCacheKey = user.getMobile() + SysConstant.SHARE_APP_DONE_TIME_SUFFIX;
                    Element shareCacheObj = EncacheUtil.getCacheObj(doneShareTimeCacheKey);
                    if (null != shareCacheObj) {
                        Integer doneShareTimes = (Integer) shareCacheObj.getObjectValue();
                        sr.setDoneTimes(doneShareTimes);
                    } else {
                        sr.setDoneTimes(SysConstant.INITIAL_DONE_TIMES);
                    }
                    userScoreDTO.setShareApp(sr);
                    break;
                case SysConstant.CREATE_ORDER_RULE_NAME:
                    Integer doneCreateOrderTimes = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.CREATE_ORDER_SCORE_PLUS.getType());
                    sr.setDoneTimes(doneCreateOrderTimes);
                    userScoreDTO.setCreatOrder(sr);
                    break;
                case SysConstant.ACCEPT_ORDER_RULE_NAME:
                    Integer doneAcceptTimes = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.ACCEPT_ORDER_SCORE_PLUS.getType());
                    sr.setDoneTimes(doneAcceptTimes);
                    userScoreDTO.setAcceptOrder(sr);
                    break;
                case SysConstant.DIAGNOSE_RULE_NAME:
                    Integer doneDiagnoseTimes = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.DIAGNOSE_SCORE_PLUS.getType());
                    sr.setDoneTimes(doneDiagnoseTimes);
                    userScoreDTO.setDiagnose(sr);
                    break;
                default:
                    break;
            }
        }
        return userScoreDTO;
    }

    @Override
    public User getUserByExchangePermitUser(Long idExchangePermitUser) {
        ExchangePermitUser exchangePermitUser = exchangePermitUserRepository.findByIdExchangePermitUser(idExchangePermitUser);
        if (null != exchangePermitUser) {
            User permitUser = userService.findUser(exchangePermitUser.getIdUser());
            return permitUser;
        }
        return null;
    }

    @Override
    public List<UserScoreDetailDTO> getUserScoreDetails(User user, Integer pageNumber, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "changeTime");
        List<ScoreLog> scoreLogs = scoreLogRepository.findByIdUser(pageable, user.getIdUser());
        List<UserScoreDetailDTO> userScoreDetailDTOList = new ArrayList<>();
        if (null != scoreLogs) {
            for (ScoreLog scoreLog : scoreLogs) {
                UserScoreDetailDTO userScoreDetailDTO = new UserScoreDetailDTO();
                userScoreDetailDTO.setChangeScore(scoreLog.getChangeScore());
                userScoreDetailDTO.setChangTime(DateUtils.formatDate(scoreLog.getChangeTime(), "yyyy-MM-dd HH:mm:ss"));
                userScoreDetailDTO.setDetail(scoreLog.getDetail());
                userScoreDetailDTOList.add(userScoreDetailDTO);
            }
        }
        return userScoreDetailDTOList;
    }

    @Override
    public boolean getAuthedScore(User user) {
        boolean userAuthed = scoreLogRepository.existsByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.AUTH_USER_SCORE_PLUS.getType());
        boolean farmlandAuthed = scoreLogRepository.existsByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.AUTH_FARMLAND_SCORE_PLUS.getType());
        boolean machineryAuthed = scoreLogRepository.existsByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.AUTH_MACHINERY_SCORE_PLUS.getType());
        if (userAuthed && farmlandAuthed && machineryAuthed) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public ScoreLog exchangeScore(User user, User permitUser, Integer score) {
        if (!user.getUserState().equals(UserStateEnum.NORMOL.getState()) || !permitUser.getUserState().equals(UserStateEnum.NORMOL.getState())) {
            throw new PermissionException("请先认证");
        }
        ScoreUser currentScoreUser = scoreUserRepository.findOneByIdUser(user.getIdUser());
        if (null == currentScoreUser) {
            currentScoreUser = initScoreUser(user);
        }
        ScoreUser permitScoreUser = scoreUserRepository.findOneByIdUser(permitUser.getIdUser());
        if (null == permitScoreUser) {
            permitScoreUser = initScoreUser(permitUser);
        }
        //当前用户积分变化日志表
        ScoreLog currentUserScoreLog = new ScoreLog();
        //兑换用户积分变化日志表
        ScoreLog permitUserScoreLog = new ScoreLog();
        currentUserScoreLog.setIdUser(user.getIdUser());
        currentUserScoreLog.setScoreOld(currentScoreUser.getLatestScore());
        currentUserScoreLog.setChangeType(ScoreTypeEnum.SCORE_EXCHANGE_REDUCE.getType());
        permitUserScoreLog.setIdUser(permitUser.getIdUser());
        permitUserScoreLog.setScoreOld(permitScoreUser.getLatestScore());
        permitUserScoreLog.setChangeType(ScoreTypeEnum.SCORE_EXCHANGE_INCREASE.getType());
        //积分IO操作
        boolean flag = exchangeScoreIO(currentScoreUser, permitScoreUser, score);
        if (!flag){
            throw new RuntimeException("积分兑换服务正忙，请稍后重试");
        }
        currentUserScoreLog.setChangeScore(-score);
        currentUserScoreLog.setScoreNew(currentScoreUser.getLatestScore());
//        currentUserScoreLog.setDetail("您兑换商品消费了"+score+"积分,用户"+permitUser.getMobile()+"赚了"+score+"积分。");
        currentUserScoreLog.setDetail("兑换积分商品");
        currentUserScoreLog.setChangeTime(new Date());
        //持久化日志
        ScoreLog savedCurrentUserScoreLog = scoreLogRepository.save(currentUserScoreLog);
        permitUserScoreLog.setChangeScore(score);
        permitUserScoreLog.setScoreNew(permitScoreUser.getLatestScore());
//        permitUserScoreLog.setDetail("您提供了兑换商品服务赚了"+score+"积分,用户"+user.getMobile()+"消费了"+score+"积分。");
        permitUserScoreLog.setDetail("获得商品积分");
        permitUserScoreLog.setChangeTime(new Date());
        //持久化
        scoreLogRepository.save(permitUserScoreLog);
        return savedCurrentUserScoreLog;
    }

    @Override
    @Transactional
    public boolean exchangeScoreIO(ScoreUser currentScoreUser, ScoreUser permitScoreUser, Integer score) {
        Integer currentUserlatestScore = currentScoreUser.getLatestScore();
        if (currentUserlatestScore < score) {
            throw new PermissionException("积分不足");
        }
        currentScoreUser.setLatestScore(currentUserlatestScore - score);
        currentScoreUser.setLatestChangeTime(new Date());
        ScoreUser savedCurrentScoreUser = scoreUserRepository.save(currentScoreUser);
        if (null != savedCurrentScoreUser) {
            Integer permitUserlatestScore = permitScoreUser.getLatestScore();
            permitScoreUser.setLatestScore(permitUserlatestScore + score);
            permitScoreUser.setLatestChangeTime(new Date());
            ScoreUser savedPermitScoreUser = scoreUserRepository.save(permitScoreUser);
            if (null != savedPermitScoreUser) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ExchangePermitUser> queryUsersByPermitUser() {
        List<ExchangePermitUser> all = exchangePermitUserRepository.findAllByValidIsTrue();
        return all;
    }

    @Override
    public List<ExchangePermitUserDTO> loadExchangePermitUserDTO(List<ExchangePermitUser> exchangePermitUsers) {
        List<ExchangePermitUserDTO> exchangePermitUserDTOS = new ArrayList<>();
        if (exchangePermitUsers.size() > 0){
            for (ExchangePermitUser permitUser:exchangePermitUsers) {
                ExchangePermitUserDTO exchangePermitUserDTO = new ExchangePermitUserDTO();
                exchangePermitUserDTO.setIdExchangePermitUser(permitUser.getIdExchangePermitUser());
                exchangePermitUserDTO.setIdUser(permitUser.getIdUser());
                exchangePermitUserDTO.setProvider(permitUser.getProvider());
                exchangePermitUserDTO.setLocation(permitUser.getLocation());
                exchangePermitUserDTO.setRemark(permitUser.getRemark());
                exchangePermitUserDTO.setMobile(userService.findUser(permitUser.getIdUser()).getMobile());
                exchangePermitUserDTO.setCreateTime(DateUtils.formatDate(permitUser.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                exchangePermitUserDTOS.add(exchangePermitUserDTO);
            }
        }
        return exchangePermitUserDTOS;
    }

    /**
     * 更新积分操作日志
     *
     * @param scoreLog
     * @return
     */
    private ScoreLog updateScoreLog(ScoreLog scoreLog) {
        ScoreLog savedScoreLog = null;
        if (null != scoreLog) {
            savedScoreLog = scoreLogRepository.save(scoreLog);
        }
        return savedScoreLog;
    }

    /**
     * 当前用户初始化积分账户
     *
     * @param user
     * @return
     */
    private ScoreUser initScoreUser(User user) {
        boolean existed = scoreUserRepository.existsByIdUser(user.getIdUser());
        ScoreUser savedScoreUser = null;
        if (!existed){
            ScoreUser scoreUser = new ScoreUser();
            scoreUser.setIdUser(user.getIdUser());
            scoreUser.setLatestScore(SysConstant.INITIAL_USER_SCORE);
            scoreUser.setCreateTime(new Date());
            scoreUser.setLatestChangeTime(new Date());
            savedScoreUser = scoreUserRepository.save(scoreUser);
        }
        return savedScoreUser;
    }

    private synchronized ScoreTaskDTO authTask(User user, String authType) {
        ScoreTaskDTO scoreTask = new ScoreTaskDTO();
        //动态获取当前用户的积分缓存——element key：mobile_SIGNIN
        String authCacheKey = user.getMobile() + SysConstant.AUTH_CACHE_KEY_SUFFIX;
        String todayScoreCacheKey = user.getMobile() + SysConstant.TODAY_SCORE_SUFFIX;
        String doneAuthTimesCacheKey = user.getMobile() + SysConstant.AUTH_DONE_TIME_SUFFIX;
        ScoreUser scoreUser = scoreUserRepository.findOneByIdUser(user.getIdUser());
        //首次参加积分任务的用户先创建积分用户表
        if (null == scoreUser) {
            scoreUser = initScoreUser(user);
        }
        Integer orignalScore = scoreUser.getLatestScore();
        scoreTask.setRuleName(SysConstant.AUTH_RULE_NAME);
        EncacheUtil.addNewCacheObj(doneAuthTimesCacheKey, SysConstant.AUTH_VALID_TIMES);
        //积分日志操作数据
        ScoreLog scoreLog = new ScoreLog();
        scoreLog.setIdUser(user.getIdUser());
        scoreLog.setScoreOld(orignalScore);
        Integer latestScore = 0;
        Integer increaseScore = 0;
        if (authType.equals(SysConstant.AUTH_USER_TYPE)) {
            latestScore = orignalScore + SysConstant.AUTH_USER_VALID_SCORE;
            increaseScore = SysConstant.AUTH_USER_VALID_SCORE;
            scoreLog.setChangeType(ScoreTypeEnum.AUTH_USER_SCORE_PLUS.getType());
            scoreLog.setChangeScore(SysConstant.AUTH_USER_VALID_SCORE);
            scoreLog.setDetail("个人信息认证");
        } else if (authType.equals(SysConstant.AUTH_FARMLAND_TYPE)) {
            latestScore = orignalScore + SysConstant.AUTH_FARMLAND_VALID_SCORE;
            increaseScore = SysConstant.AUTH_FARMLAND_VALID_SCORE;
            scoreLog.setChangeType(ScoreTypeEnum.AUTH_FARMLAND_SCORE_PLUS.getType());
            scoreLog.setChangeScore(SysConstant.AUTH_FARMLAND_VALID_SCORE);
            scoreLog.setDetail("土地信息认证");
        } else if (authType.equals(SysConstant.AUTH_MACHINERY_TYPE)) {
            latestScore = orignalScore + SysConstant.AUTH_MACHINERY_VALID_SCORE;
            increaseScore = SysConstant.AUTH_MACHINERY_VALID_SCORE;
            scoreLog.setChangeType(ScoreTypeEnum.AUTH_MACHINERY_SCORE_PLUS.getType());
            scoreLog.setChangeScore(SysConstant.AUTH_MACHINERY_VALID_SCORE);
            scoreLog.setDetail("农机信息认证");
        } else {
            latestScore = orignalScore;
        }
        //更新积分用户数据
        scoreUser.setLatestScore(latestScore);
        scoreUser.setLatestChangeTime(new Date());
        scoreUserRepository.save(scoreUser);
        boolean todayScoreExsited = EncacheUtil.checkElementCached(todayScoreCacheKey);
        Integer todayScore = 0;
        if (todayScoreExsited) {
            Element cacheObj = EncacheUtil.getCacheObj(todayScoreCacheKey);
            if (null != cacheObj) {
                Integer todayOrignalScore = (Integer) cacheObj.getObjectValue();
                todayScore = todayOrignalScore + increaseScore;
            } else {
                todayScore = increaseScore;
            }
        } else {
            todayScore = increaseScore;
        }
        //今日积分数据加入缓存
        EncacheUtil.addNewCacheObj(todayScoreCacheKey, todayScore);
        scoreTask.setTodayScore(todayScore);
        scoreTask.setLatestScore(latestScore);
        Integer authedTimes = getAuthedTime(user);
        scoreTask.setDoneTimes(authedTimes + SysConstant.SCORE_ONCE_TIME);
        scoreTask.setAvailableTimes(SysConstant.AUTH_VALID_TIMES);
        scoreTask.setResult(true);
        scoreTask.setResultInfo("认证成功");
        EncacheUtil.addNewCacheObj(authCacheKey, scoreTask);
        //完善剩下的积分操作日志数据
        scoreLog.setScoreNew(latestScore);
        scoreLog.setChangeTime(new Date());
        //积分操作日志数据持久化
        updateScoreLog(scoreLog);
        return scoreTask;
    }

    private Integer getAuthedTime(User user) {
        Integer authTime = 0;
        boolean userAuthed = scoreLogRepository.existsByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.AUTH_USER_SCORE_PLUS.getType());
        boolean farmlandAuthed = scoreLogRepository.existsByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.AUTH_FARMLAND_SCORE_PLUS.getType());
        boolean machineryAuthed = scoreLogRepository.existsByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.AUTH_MACHINERY_SCORE_PLUS.getType());
        if (userAuthed) {
            authTime += 1;
        }
        if (farmlandAuthed) {
            authTime += 1;
        }
        if (machineryAuthed) {
            authTime += 1;
        }
        return authTime;
    }

}
