package com.plansolve.farm.controller.client.main.activity;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.PromotionActivityConstant;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.activity.PromotionActivityAppDTO;
import com.plansolve.farm.model.client.activity.PromotionWinnersDTO;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.promotion.PromotionActivity;
import com.plansolve.farm.model.database.promotion.PromotionPlayer;
import com.plansolve.farm.model.database.promotion.PromotionWinners;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.code.ResultEnum;
import com.plansolve.farm.model.enums.state.UserStateEnum;
import com.plansolve.farm.model.enums.type.ActivityTypeEnum;
import com.plansolve.farm.repository.activity.ActivitySwitchRepository;
import com.plansolve.farm.service.client.order.UserOrderClientSelectService;
import com.plansolve.farm.service.console.ActivityManageService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2018/3/20
 * @Description:
 **/
@Controller
@RequestMapping(value = "/farm/app/activity")
public class ActivityManageController extends BaseController {

    @Autowired
    private ActivityManageService activityManageService;

    @Autowired
    private UserOrderClientSelectService userOrderClientSelectService;

    @Autowired
    private ActivitySwitchRepository activitySwitchRepository;

    /**
     * 获取优惠季活动banner信息
     * @return 活动页面数据
     */
    @ResponseBody
    @RequestMapping(value = "/banners")
    public Result getBannersInfo() {
        List<PromotionActivityAppDTO> activityAppDTOS = new ArrayList<>();
        boolean valid = activitySwitchRepository.existsByActivityTypeAndValidIsTrue(ActivityTypeEnum.LOTTERY_ACTIVITY.getType());
        if(valid){
            List<PromotionActivity> promotionActivities = activityManageService.loadBannersInfo();
            if (null != promotionActivities){
                if (promotionActivities.size()>0){
                    for (PromotionActivity promotion: promotionActivities) {
                        PromotionActivityAppDTO promotionActivityAppDTO = new PromotionActivityAppDTO();
                        promotionActivityAppDTO.setIdPromotionActivity(promotion.getIdPromotionActivity());
                        promotionActivityAppDTO.setActivityName(promotion.getActivityName());
                        promotionActivityAppDTO.setPictureUrl(promotion.getPictureUrl());
                        activityAppDTOS.add(promotionActivityAppDTO);
                    }
                }
            }
        }
        return ResultUtil.success(activityAppDTOS);
    }

    /**
     * 优惠活动季详情数据
     * @return 活动页面数据
     */
    @ResponseBody
    @RequestMapping(value = "/details")
    public Result activityDetails(Long idPromotionActivity) {
        Result result = new Result();
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        if (null == idPromotionActivity){
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg("参数不能为空");
            return result;
        }
        PromotionActivityAppDTO promotionActivity = activityManageService.queryActivityDetails(idPromotionActivity, user);
        return ResultUtil.success(promotionActivity);
    }

    /**
     * 报名参加优惠活动
     * @return 活动页面数据
     */
    @ResponseBody
    @RequestMapping(value = "/sign-up")
    public Result signUpActivity(Long idPromotionActivity, String idFarmLands) {
        Result result = new Result();
        PromotionPlayer promotionPlayer = null;
        if (null == idPromotionActivity || StringUtils.isBlank(idFarmLands)){
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg("参数不能为空");
            return result;
        }
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        if (!UserStateEnum.NORMOL.getState().equals(user.getUserState())){
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg("请先认证");
            return result;
        }
        //查看报名资格
        Boolean qualification = activityManageService.checkQualification(idPromotionActivity);
        if (qualification){
            promotionPlayer = activityManageService.signUpActivity(idPromotionActivity, user, idFarmLands);
        }
        if (null != promotionPlayer){
            return ResultUtil.success("报名成功");
        }else{
            return ResultUtil.success("优惠活动太火爆了，请刷新后重试！");
        }
    }

    /**
     * 报名参加抽奖活动
     * @param  idPromotionActivity
     */
    @ResponseBody
    @RequestMapping(value = "/lottery/sign-up")
    public Result signUpLottery(Long idPromotionActivity) {
        Result result = new Result();
        Date start = DateUtils.parseDate(PromotionActivityConstant.LOTTERY_START_TIME);
        Date end = DateUtils.parseDate(PromotionActivityConstant.LOTTERY_END_TIME);
        long endTime = end.getTime();
        long time = new Date().getTime();
        if (time > endTime){
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg("活动报名时间已截止");
            return result;
        }
        if (null == idPromotionActivity){
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg("参数不能为空");
            return result;
        }
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        if (!UserStateEnum.NORMOL.getState().equals(user.getUserState())){
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg("请先认证");
            return result;
        }
        //在指定时间内微信支付订单完成的双方才具有抽奖报名资格
        List<UserOrder> userOrders = userOrderClientSelectService.listOnlinePaymentOrders(start, end, user.getIdUser());
        if (null != userOrders && userOrders.size()>0){
            PromotionPlayer promotionPlayer = activityManageService.signUpLottery(idPromotionActivity, user);
            if (null != promotionPlayer){
                return ResultUtil.success("抽奖活动参加成功");
            }else{
                return ResultUtil.success("抽奖活动太火爆了，请刷新后重试");
            }
        }else{
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg("暂无抽奖报名资格");
            return result;
        }
    }

    /**
     * 抽奖活动中奖人员名单
     * @param  idPromotionActivity
     */
    @ResponseBody
    @RequestMapping(value = "/lottery/winners")
    public Result lotteryWinners(Long idPromotionActivity) {
        Result result = new Result();
        long endTime = DateUtils.parseDate(PromotionActivityConstant.LOTTERY_END_TIME).getTime();
        long time = new Date().getTime();
        if (time < endTime){
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg("敬请期待");
            return result;
        }
        if (null == idPromotionActivity){
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg("参数不能为空");
            return result;
        }
        List<PromotionWinners> promotionWinners = activityManageService.lotteryWinners(idPromotionActivity);
        if (null != promotionWinners){
            List<PromotionWinnersDTO> promotionWinnersDTOS = activityManageService.lotteryWinnersDTO(promotionWinners);
            return ResultUtil.success(promotionWinnersDTOS);
        }else{
            result.setCode(ResultEnum.UNKNOWN_ERROR.getCode());
            result.setMsg("服务器忙，请稍后重试！");
            return result;
        }
    }

    /**
     * 随机抽奖
     * @param idPromotionActivity 活动id
     * @param winnerNumber 中奖人数
     * @param prize 奖品
     */
    @ResponseBody
    @RequestMapping(value = "/lottery/draw")
    public Result lotteryDraw(Long idPromotionActivity, Integer winnerNumber, String prize) {
        Result result = new Result();
        if (null == idPromotionActivity || null == winnerNumber || StringUtils.isBlank(prize)){
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg("参数不能为空");
            return result;
        }
        List<PromotionWinners> promotionWinners = activityManageService.randomLuckydraw(idPromotionActivity, winnerNumber, prize);
        if (null != promotionWinners){
            List<PromotionWinnersDTO> promotionWinnersDTOS = activityManageService.lotteryWinnersDTO(promotionWinners);
            result.setCode(ResultEnum.SUCCESS.getCode());
            result.setMsg("本次抽奖已完成");
            result.setData(promotionWinnersDTOS);
            return result;
        }else{
            result.setCode(ResultEnum.UNKNOWN_ERROR.getCode());
            result.setMsg("服务器忙，请稍后重试");
            return result;
        }
    }

}
