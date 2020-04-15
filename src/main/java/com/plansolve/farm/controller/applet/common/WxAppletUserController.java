package com.plansolve.farm.controller.applet.common;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.wxapplet.WeChatUserInfoDTO;
import com.plansolve.farm.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 高一平
 * @Date: 2019/4/29
 * @Description:
 **/
@Slf4j
@RestController
@RequestMapping("/wx/applet")
public class WxAppletUserController {

    @Autowired
    private WxMaService wxMaService;

    /**
     * 根据code向微信开放平台获取用户openId
     *
     * @param code 用户登录凭证（有效期五分钟）。开发者需要在开发者服务器后台调用 auth.code2Session，使用 code 换取 openid 和 session_key 等信息
     * @return
     * @throws WxErrorException
     */
    @RequestMapping("/login")
    public Result login(String code) throws WxErrorException {
        // 获取当前用户openId, sessionKey, unionId
        WxMaJscode2SessionResult result = wxMaService.jsCode2SessionInfo(code);
        return ResultUtil.success(null);
    }

    /**
     * 获取用户微信基本资料
     *
     * @param userInfo
     * @return
     */
    @RequestMapping("/userInfo")
    public Result userInfo(WeChatUserInfoDTO userInfo) {
        log.info(userInfo.toString());
        return ResultUtil.success(null);
    }

}
