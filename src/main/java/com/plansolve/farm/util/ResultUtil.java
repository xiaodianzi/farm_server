package com.plansolve.farm.util;

import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.enums.code.ResultEnum;
import org.springframework.ui.Model;

/**
 * @Author: 高一平
 * @Date: 2018/6/6
 * @Description:
 **/
public class ResultUtil {
    /**
     * 封装操作成功时返回对象
     *
     * @param object
     * @return
     */
    public static Result success(Object object) {
        return new Result(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), object);
    }

    /**
     * 微信页面错误页
     *
     * @param msg
     * @param url
     * @param model
     * @return
     */
    public static String error(String msg, String url, Model model) {
        model.addAttribute("msg", msg);
        model.addAttribute("url", url);
        return "wechat/home/error";
    }
}
