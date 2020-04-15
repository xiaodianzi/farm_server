package com.plansolve.farm.controller.console.main.activity;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.client.activity.PromotionActivityDTO;
import com.plansolve.farm.model.console.PageDTO;
import com.plansolve.farm.model.console.activity.PromotionActivityWebDTO;
import com.plansolve.farm.model.database.promotion.PromotionActivity;
import com.plansolve.farm.model.database.promotion.PromotionPlayer;
import com.plansolve.farm.model.database.promotion.PromotionWinners;
import com.plansolve.farm.service.console.ActivityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2018/3/20
 * @Description:
 **/
@Controller
@RequestMapping(value = "/manager/web/activity")
public class ActivityManagePageController extends BaseController {

    @Autowired
    private ActivityManageService activityManageService;

    /**
     * 跳转到活动信息页面
     * @return 积分规则数据
     */
    @RequestMapping(value = "/page/index")
    public String scoreInfoPage() {
        return "activity/activityIndex";
    }

    /**
     * 跳转到活动参加人员信息页面
     * @return 优惠活动参加人员信息列表
     */
    @RequestMapping(value = "/page/players")
    public String playersListPage() {
        return "activity/playersList";
    }

    /**
     * 跳转到中奖人员信息页面
     * @return 优惠活动中奖人员信息列表
     */
    @RequestMapping(value = "/page/winners")
    public String winnersListPage() {
        return "activity/winnersList";
    }

    /**
     * 新增活动规则后跳转到积分规则信息页面
     * @return 积分规则数据
     */
    @RequestMapping(value = "/add")
    public String addScorePage(PromotionActivityDTO promotionActivity) {
        activityManageService.addOrUpdatePromotionActivity(promotionActivity);
        return "redirect:/manager/web/activity/page/index";
    }

    /**
     * 活动信息列表数据
     * @return 积分列表
     */
    @ResponseBody
    @RequestMapping(value = "/query")
    public PageDTO<PromotionActivityDTO> getScoreInfo(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset, ModelMap modelMap) {
        Integer page = getPage(limit, offset);
        Page<PromotionActivity> promotionActivityPage = activityManageService.queryActivityInfo(page, limit);
        List<PromotionActivity> promotionActivities = promotionActivityPage.getContent();
        List<PromotionActivityDTO> promotionActivityDTOS = activityManageService.loadPromotionActivityDTOS(promotionActivities);
        PageDTO<PromotionActivityDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(promotionActivityPage.getTotalElements());
        pageDTO.setRows(promotionActivityDTOS);
        return pageDTO;
    }

    /**
     * 修改活动规则后跳转到积分规则信息页面
     * @return 积分规则数据
     */
    @RequestMapping(value = "/update")
    public String updateScorePage(PromotionActivityDTO promotionActivity) {
        activityManageService.addOrUpdatePromotionActivity(promotionActivity);
        return "redirect:/manager/web/activity/page/index";
    }

    /**
     * 获取活动参与人员信息列表
     * @param limit
     * @param offset
     * @param promotionActivityWebDTO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/players")
    public PageDTO<PromotionActivityWebDTO> playersPage(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset, PromotionActivityWebDTO promotionActivityWebDTO) {
        PageDTO<PromotionActivityWebDTO> pageDTO = new PageDTO<>();
        Integer pageNumber = getPage(limit, offset);
        //确保后台页面在没有数据的时候不显示内容
        List<PromotionActivityWebDTO> promotionActivityWebDTOS = new ArrayList<>();
        Page<PromotionPlayer> promotionPlayers = activityManageService.queryActivityPlayers(pageNumber, limit, promotionActivityWebDTO);
        if (null != promotionPlayers){
            promotionActivityWebDTOS = activityManageService.loadPromotionActivityWebDTOS(promotionPlayers.getContent());
            pageDTO.setTotal(promotionPlayers.getTotalElements());
            pageDTO.setRows(promotionActivityWebDTOS);
        }else{
            pageDTO.setTotal(0L);
            pageDTO.setRows(promotionActivityWebDTOS);
        }
        return pageDTO;
    }

    /**
     * 获取活动参与人员信息列表
     * @param limit
     * @param offset
     * @param promotionActivityWebDTO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/winners")
    public PageDTO<PromotionActivityWebDTO> winnersPage(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset, PromotionActivityWebDTO promotionActivityWebDTO) {
        PageDTO<PromotionActivityWebDTO> pageDTO = new PageDTO<>();
        Integer pageNumber = getPage(limit, offset);
        Page<PromotionWinners> winners = activityManageService.queryActivityWinners(pageNumber, limit, promotionActivityWebDTO);
        //确保后台页面在没有数据的时候不显示内容
        List<PromotionActivityWebDTO> promotionActivityWebDTOS = new ArrayList<>();
        if (null != winners){
            promotionActivityWebDTOS = activityManageService.loadActivityWinnersDTOS(winners.getContent());
            pageDTO.setTotal(winners.getTotalElements());
            pageDTO.setRows(promotionActivityWebDTOS);
        }else{
            pageDTO.setTotal(0L);
            pageDTO.setRows(promotionActivityWebDTOS);
        }
        return pageDTO;
    }

}
