package com.plansolve.farm.controller.wechat;

import com.plansolve.farm.exception.WeChatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: 高一平
 * @Date: 2018/10/15
 * @Description:
 **/
@Slf4j
public class BaseController {

    @ModelAttribute
    private void comeFromWeChat(HttpServletRequest request) {
        String ua = request.getHeader("user-agent");
        if (!(ua != null && !ua.isEmpty() && ua.toLowerCase().contains("micromessenger"))) {
            throw new WeChatException("[这不是来自微信浏览器的请求]");
        }
    }

}
