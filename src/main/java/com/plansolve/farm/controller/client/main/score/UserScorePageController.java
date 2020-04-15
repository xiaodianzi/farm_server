package com.plansolve.farm.controller.client.main.score;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.PromotionActivityConstant;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.score.ScoreIndexDTO;
import com.plansolve.farm.model.client.score.UserScoreDataDTO;
import com.plansolve.farm.model.database.score.ScoreRule;
import com.plansolve.farm.model.database.score.ScoreUser;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.code.ResultEnum;
import com.plansolve.farm.model.enums.type.ActivityTypeEnum;
import com.plansolve.farm.repository.activity.ActivitySwitchRepository;
import com.plansolve.farm.repository.score.ScoreUserRepository;
import com.plansolve.farm.service.console.ScoreManageService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.EncacheUtil;
import com.plansolve.farm.util.ResultUtil;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Andrew
 * @Date: 2018/3/20
 * @Description: 积分活动相关接口
 **/
@Controller
@RequestMapping(value = "/farm/score")
public class UserScorePageController extends BaseController {

    private static final String SCORE_PAGE_URL = "/farm/score/appUserScore";

    @Autowired
    private ScoreManageService scoreManageService;

    @Autowired
    private ScoreUserRepository scoreUserRepository;

    @Autowired
    private ActivitySwitchRepository activitySwitchRepository;

    /**
     * 用户积分交互页面
     * @return
     */
    @RequestMapping(value = "/appUserScore")
    public String appUserScore(Model model, @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        Page<ScoreRule> scoreRulesInfo = scoreManageService.getScoreRulesInfo(pageNo, pageSize);
        if (null != scoreRulesInfo) {
            List<ScoreRule> scoreRules = scoreRulesInfo.getContent();
            if (scoreRules.size() > 0) {
                ScoreUser scoreUser = scoreUserRepository.findOneByIdUser(user.getIdUser());
                if (null != scoreUser) {
                    String todayScoreCacheKey = user.getMobile() + "_TODAY_SCORE";
                    Element cacheObj = EncacheUtil.getCacheObj(todayScoreCacheKey);
                    if (null == cacheObj) {
                        EncacheUtil.addNewCacheObj(todayScoreCacheKey, SysConstant.INITIAL_USER_SCORE);
                        model.addAttribute("todayScore", SysConstant.INITIAL_USER_SCORE);
                    } else {
                        Integer todayScore = (Integer) cacheObj.getObjectValue();
                        model.addAttribute("todayScore", todayScore);
                    }
                    model.addAttribute("userScore", scoreUser.getLatestScore());
                } else {
                    model.addAttribute("todayScore", SysConstant.INITIAL_USER_SCORE);
                    model.addAttribute("userScore", SysConstant.INITIAL_USER_SCORE);
                }
            }
            List<ScoreIndexDTO> scoreDTOS = scoreManageService.loadScoreIndexData(user, scoreRules);
            model.addAttribute("scoreRules", scoreDTOS);
        }
        //判断赚取积分活动是否开启
        boolean valid = activitySwitchRepository.existsByActivityTypeAndValidIsTrue(ActivityTypeEnum.SCORE_EARN_ACTIVITY.getType());
        if (valid){
            model.addAttribute("switch", 1);
        }else{
            model.addAttribute("switch", 0);
        }
        return "user/userCredits";
    }

    /**
     * 用户积分交互页面URL
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/appUserScorePage")
    public Result appUserScorePage() {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        Result result = new Result();
        if (null != user) {
            Map<String, String> map = new HashMap<>();
            map.put("scorePageUrl", SCORE_PAGE_URL);
            result.setCode(ResultEnum.SUCCESS.getCode());
            result.setMsg("获取用户积分web页面成功");
            result.setData(map);
        }else{
            result.setCode(ResultEnum.LOGIN_ERROR.getCode());
            result.setMsg("请先登录");
        }
        return result;
    }

    /**
     * @param pageNumber
     * @param pageSize
     * @return 返回用户积分信息的json字符串
     */
    @ResponseBody
    @RequestMapping(value = "/user/data")
    public Result appUserScore(@RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "10") Integer pageSize) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        Page<ScoreRule> scoreRulesInfo = scoreManageService.getScoreRulesInfo(pageNumber, pageSize);
        UserScoreDataDTO userScoreDataDTO = null;
        if (null != scoreRulesInfo) {
            List<ScoreRule> scoreRules = scoreRulesInfo.getContent();
            if (scoreRules.size() > 0) {
                userScoreDataDTO = scoreManageService.loadUserScoreData(user, scoreRules);
            }
        }
        return ResultUtil.success(userScoreDataDTO);
    }


    /**
     * @return 查询积分活动日期是否结束
     */
    @ResponseBody
    @RequestMapping(value = "/time/valid")
    public Result appUserScore() {
        boolean valid = activitySwitchRepository.existsByActivityTypeAndValidIsTrue(ActivityTypeEnum.SCORE_EXCHANGE_ACTIVITY.getType());
        if (valid){
            return ResultUtil.success("true");
        }else{
            return ResultUtil.success("false");
        }
    }

}
