package com.plansolve.farm.controller.wechat.common;

import com.plansolve.farm.controller.wechat.BaseController;
import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.exception.WeChatException;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.WeChatUser;
import com.plansolve.farm.service.common.CaptchaService;
import com.plansolve.farm.service.wechat.WeChatService;
import com.plansolve.farm.service.wechat.WeChatUserService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: 高一平
 * @Date: 2018/11/1
 * @Description:
 **/
@Slf4j
@Controller
@RequestMapping("/wechat")
public class WeChatUserController extends BaseController {

    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private WeChatUserService userService;
    @Autowired
    private WeChatService weChatService;
    @Value("${projectURL}")
    private String projectURL;

    /**
     * 跳转手机密码登录页面
     *
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "wechat/home/login";
    }

    /**
     * 发送手机验证码
     *
     * @param mobile
     * @return
     */
    @PostMapping("/sendCaptcha")
    @ResponseBody
    public String sendCaptcha(String mobile) {
        try {
            captchaService.sendCaptcha(mobile);
            return "短信发送成功";
        } catch (Exception e) {
            return "短信发送失败";
        }
    }

    /**
     * 设置用户微信信息
     *
     * @return
     */
    @GetMapping("/login")
    public String loginByWeChat() {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        WxMpOAuth2AccessToken token = (WxMpOAuth2AccessToken) AppHttpUtil.getSessionAttribute(SysConstant.WX_MP_OAUTH2_ACCESS_TOKEN);
        try {
            WxMpUser wxMpUser = weChatService.getWxMpUser(token);
            WeChatUser weChatUser = new WeChatUser();
            weChatUser.setIdUser(user.getIdUser());
            BeanUtils.copyProperties(wxMpUser, weChatUser);
            userService.save(weChatUser);
            return "redirect:/wechat/home";
        } catch (WxErrorException e) {
            log.error("【微信网页授权】{}", e);
            throw new WeChatException("[获取用户微信身份失败]");
        }
    }

    /**
     * 用户登录（手机/验证码登录）
     *
     * @param mobile  用户手机号码
     * @param captcha 用户验证码
     * @return
     */
    @PostMapping("/login")
    public String loginByCaptcha(String mobile, String captcha) {
        User user = userService.findUserByMobile(mobile);
        if (UserUtil.checkUserExist(user)) {
            if (captchaService.checkCaptcha(mobile, captcha)) {
                // 登录成功，将用户微信openID与该用户绑定，并将当前用户存进session中
                AppHttpUtil.getSession().setAttribute(SysConstant.CURRENT_USER, user);
                WxMpOAuth2AccessToken token = (WxMpOAuth2AccessToken) AppHttpUtil.getSessionAttribute(SysConstant.WX_MP_OAUTH2_ACCESS_TOKEN);
                if (token == null) {
                    String url = "/wechat/login";
                    return "redirect:/authorize?returnUrl=" + url;
                } else {
                    return loginByWeChat();
                }
            } else {
                log.error("用户手机验证码不匹配");
                throw new ParamErrorException("[手机验证码校验失败]");
            }
        } else {
            throw new ParamErrorException("[该用户不存在]");
        }
    }

}
