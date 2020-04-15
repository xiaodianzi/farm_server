package com.plansolve.farm.controller.console.main.score;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.client.score.ScoreIndexDTO;
import com.plansolve.farm.model.console.PageDTO;
import com.plansolve.farm.model.database.score.ScoreRule;
import com.plansolve.farm.service.console.ScoreManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2018/3/20
 * @Description:
 **/
@Controller
@RequestMapping(value = "/manager/app/score")
public class ScoreManagePageController extends BaseController {

    @Autowired
    private ScoreManageService scoreManageService;

    /**
     * 跳转到积分规则信息页面
     * @return 积分规则数据
     */
    @RequestMapping(value = "/scoreInfoPage")
    public String scoreInfoPage() {
        return "score/score";
    }

    /**
     * 积分规则数据
     * @return 积分规则数据
     */
    @RequestMapping(value = "/getScoreInfo")
    @ResponseBody
    public PageDTO<ScoreIndexDTO> getScoreInfo(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset, ModelMap modelMap) {
        Integer page = getPage(limit, offset);
        Page<ScoreRule> scoreRulesPage;
        scoreRulesPage = scoreManageService.getManageScoreRulesInfo(page, limit);
        List<ScoreRule> scoreRules = scoreRulesPage.getContent();
        List<ScoreIndexDTO> userScoreDTOS = scoreManageService.loadUserScoreDTOs(scoreRules);
        PageDTO<ScoreIndexDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(scoreRulesPage.getTotalElements());
        pageDTO.setRows(userScoreDTOS);
        return pageDTO;
    }

    /**
     * 新增积分规则后跳转到积分规则信息页面
     * @return 积分规则数据
     */
    @RequestMapping(value = "/addScorePage")
    public String addScorePage(ScoreRule scoreRule) {
        scoreManageService.addOrUpdateScoreRule(scoreRule);
        return "redirect:/manager/app/score/scoreInfoPage";
    }

    /**
     * 修改积分规则后跳转到积分规则信息页面
     * @return 积分规则数据
     */
    @RequestMapping(value = "/updateScorePage")
    public String updateScorePage(ScoreRule scoreRule) {
        scoreManageService.addOrUpdateScoreRule(scoreRule);
        return "redirect:/manager/app/score/scoreInfoPage";
    }

}
