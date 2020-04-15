package com.plansolve.farm.controller.client.main.score;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.PromotionActivityConstant;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.PageDTO;
import com.plansolve.farm.model.client.score.ExchangePermitUserDTO;
import com.plansolve.farm.model.client.score.ScoreTaskDTO;
import com.plansolve.farm.model.client.score.UserScoreDTO;
import com.plansolve.farm.model.client.score.UserScoreDetailDTO;
import com.plansolve.farm.model.database.score.ExchangePermitUser;
import com.plansolve.farm.model.database.score.ScoreLog;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.code.ResultEnum;
import com.plansolve.farm.model.enums.type.ActivityTypeEnum;
import com.plansolve.farm.model.enums.type.ScoreTypeEnum;
import com.plansolve.farm.repository.activity.ActivitySwitchRepository;
import com.plansolve.farm.repository.score.ScoreLogRepository;
import com.plansolve.farm.service.console.ScoreManageService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EncacheUtil;
import com.plansolve.farm.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/3/21
 * @Description: 用户积分任务管理类
 **/
@RestController
@RequestMapping(value = "/farm/score/manager")
public class ScoreManageController extends BaseController {

    private final static Logger log = LoggerFactory.getLogger(ScoreManageController.class);

    @Autowired
    private ScoreManageService scoreManageService;

    @Autowired
    private ScoreLogRepository scoreLogRepository;

    @Autowired
    private ActivitySwitchRepository activitySwitchRepository;

    /**
     * 获取用户积分页面数据
     */
    @RequestMapping(value = "/getUserScoreData")
    public Result getUserScoreData() {
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
        UserScoreDTO userScore = scoreManageService.getUserScoreData(user);
        if (null != userScore){
            return ResultUtil.success(userScore);
        }else{
            Result result = new Result();
            result.setCode(ResultEnum.SERVER_BUSY_ERROR.getCode());
            result.setMsg("服务器忙");
            return result;
        }
    }

    /**
     * 获取用户积分明细
     */
    @RequestMapping(value = "/getUserScoreDetails")
    public Result getUserScoreDetails(@RequestParam(defaultValue = "0") Integer pageNumber,
                                      @RequestParam(defaultValue = "20") Integer pageSize) {
        PageDTO<UserScoreDetailDTO> pageDTO = new PageDTO<>();
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
        List<UserScoreDetailDTO> userScoreDetails = scoreManageService.getUserScoreDetails(user, pageNumber, pageSize);
        Long total = scoreLogRepository.countByIdUser(user.getIdUser());
        pageDTO.setTotal(total);
        pageDTO.setRows(userScoreDetails);
        return ResultUtil.success(pageDTO);
    }

    /**
     * 用户签到积分任务
     */
    @RequestMapping(value = "/signPointTask")
    public Result<ScoreTaskDTO> signPointTask() {
        ScoreTaskDTO scoreTask = null;
        //判断赚取积分活动是否开启
        boolean valid = activitySwitchRepository.existsByActivityTypeAndValidIsTrue(ActivityTypeEnum.SCORE_EARN_ACTIVITY.getType());
        if (valid){
            HttpSession session = AppHttpUtil.getSession();
            User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
            String doneSingInTimesCacheKey = user.getMobile() + SysConstant.SIGN_IN_DONE_TIME_SUFFIX;
            Integer signScore = scoreLogRepository.countByChangeTimeAfterAndChangeTypeAndIdUser(DateUtils.getMonthStart(new Date()), ScoreTypeEnum.SIGN_IN_SCORE_PLUS.getType(), user.getIdUser());
            //月有效签到积分上限为15分
            if (signScore < PromotionActivityConstant.SIGN_IN_MONTH_VALID_TIMES){
                boolean signed = scoreLogRepository.existsByChangeTimeAfterAndChangeTypeAndIdUser(DateUtils.getDayBegin(new Date()), ScoreTypeEnum.SIGN_IN_SCORE_PLUS.getType(), user.getIdUser());
                if (signed) {
                    EncacheUtil.addNewCacheObj(doneSingInTimesCacheKey, SysConstant.SIGN_IN_VALID_TIMES);
                    scoreTask = scoreManageService.getDefaultScoreTask(false, "已达日上限");
                } else {
                    scoreTask = scoreManageService.signPointTask(user);
                }
            }else{
                scoreTask = scoreManageService.getDefaultScoreTask(false, "已达月上限");
            }
        }else{
            scoreTask = scoreManageService.getDefaultScoreTask(false, "赚积分活动已结束");
        }
        return ResultUtil.success(scoreTask);
    }

    /**
     * 用户认证积分任务
     */
    @RequestMapping(value = "/authPointTask")
    public void authPointTask(String authType) {
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
        scoreManageService.authPointTask(user, authType);
    }

    /**
     * 用户分享积分任务
     */
    @RequestMapping(value = "/shareAppPointTask")
    public Result<ScoreTaskDTO> sharePointTask() {
        ScoreTaskDTO scoreTask = null;
        //判断赚取积分活动是否开启
        boolean valid = activitySwitchRepository.existsByActivityTypeAndValidIsTrue(ActivityTypeEnum.SCORE_EARN_ACTIVITY.getType());
        if (valid){
            HttpSession session = AppHttpUtil.getSession();
            User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
            scoreTask = scoreManageService.sharePointTask(user);
        }else{
            scoreTask = scoreManageService.getDefaultScoreTask(false, "赚积分活动已结束");
        }
        return ResultUtil.success(scoreTask);
    }

    /**
     * 用户接单积分任务
     */
    @RequestMapping(value = "/acceptOrderPointTask")
    public Result<ScoreTaskDTO> acceptOrderPointTask() {
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
        ScoreTaskDTO scoreTask = scoreManageService.acceptOrderPointTask(user);
        return ResultUtil.success(scoreTask);
    }

    /**
     * 用户病害诊断积分任务
     */
    @RequestMapping(value = "/diagnosePointTask")
    public Result<ScoreTaskDTO> diagnosePointTask() {
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
        Integer diagnoseTime = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.DIAGNOSE_SCORE_PLUS.getType());
        ScoreTaskDTO scoreTask = null;
        //每使用一次积1分，积分上限为5分
        if (diagnoseTime<SysConstant.DIAGNOSE_VALID_TIMES){
            scoreTask = scoreManageService.diagnosePointTask(user);
        }else{
            scoreTask = scoreManageService.getDefaultScoreTask(false, "病虫害诊断积分任务次数已达上限");
        }
        return ResultUtil.success(scoreTask);
    }

    /**
     * 查询所有获得积分兑换服务的用户
     */
    @PostMapping(value = "/query/permitusers")
    public Result queryPermitUser() {
        List<ExchangePermitUser> users = scoreManageService.queryUsersByPermitUser();
        List<ExchangePermitUserDTO> exchangePermitUserDTOS = scoreManageService.loadExchangePermitUserDTO(users);
        return ResultUtil.success(exchangePermitUserDTOS);
    }

    /**
     * 向指定用户兑换积分
     * @param idExchangePermitUser 积分兑换服务id
     * @param score 积分数
     * @return
     */
    @PostMapping(value = "/exchange")
    public Result exchangeScore(Long idExchangePermitUser, Integer score) {
        Result result = new Result();
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
        if (null == idExchangePermitUser || null == score){
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg("参数错误");
            return result;
        }
        User permitUser = scoreManageService.getUserByExchangePermitUser(idExchangePermitUser);
        if(null == permitUser){
            result.setCode(ResultEnum.NOT_EXIST.getCode());
            result.setMsg("服务机构不存在");
            return result;
        }else{
            if (permitUser.getIdUser().equals(user.getIdUser())) {
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg("积分兑换不能是同一个人");
                return result;
            }
        }
        ScoreLog savedCurrentUserScoreLog = scoreManageService.exchangeScore(user, permitUser, score);
        if (null != savedCurrentUserScoreLog){
            return ResultUtil.success("兑换成功");
        }else{
            result.setCode(ResultEnum.SERVER_BUSY_ERROR.getCode());
            result.setMsg("服务器忙");
            return result;
        }
    }

}
