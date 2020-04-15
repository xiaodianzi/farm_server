package com.plansolve.farm.controller.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/
public class BaseController {

    private final static Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * 校验用户免登录状态
     */
    @ModelAttribute
    public void checkUserLoginStatus() {
    }

    /**
     * 获取页码
     *
     * @param limit  每页数据条数
     * @param offset 起始索引
     * @return
     */
    public Integer getPage(Integer limit, Integer offset) {
        Integer page = offset / limit;
        return page;
    }

    /**
     * 跳转错误页面
     *
     * @param msg
     * @param url
     * @param model
     * @return
     */
    public String error(String msg, String url, Model model) {
        model.addAttribute("msg", msg);
        model.addAttribute("url", url);
        return "console/error";
    }
}
