package com.plansolve.farm.controller.console.main.cooperation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: Andrew
 * @Date: 2018/8/2
 * @Description:
 */
@Controller
@RequestMapping(value = "/manger/cooperation")
public class AppCooperationPageController {

    /**
     * 查询合作社成员导航
     * @return
     */
    @GetMapping(value = "/membersPage")
    public String membersPage(){
        return "cooperation/members";
    }

    /**
     * 查询合作社基本信息导航
     * @return
     */
    @GetMapping(value = "/informationPage")
    public String informationPage(){
        return "cooperation/cooperationInfo";
    }

}
