package com.plansolve.farm.controller.mp;

import com.plansolve.farm.exception.WeChatException;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.properties.WeChatProperties;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.StringUtil;
import com.plansolve.farm.util.WeChatMessageDigestUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.util.Arrays;

/**
 * @Author: 高一平
 * @Date: 2018/10/15
 * @Description:
 **/
@Controller
@Slf4j
public class WeChatController {

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private WxMpService wxMpService;
    @Value("${projectURL}")
    private String projectURL;

    /**
     * 第二步：验证消息的确来自微信服务器
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421135319
     * <p>
     * 开发者通过检验signature对请求进行校验（下面有校验方式）。
     * 若确认此次GET请求来自微信服务器，请原样返回echostr参数内容，则接入生效，成为开发者成功，否则接入失败。
     * 加密/校验流程如下：
     * 1）将token、timestamp、nonce三个参数进行字典序排序
     * 2）将三个参数字符串拼接成一个字符串进行sha1加密
     * 3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
     *
     * @param signature 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param echostr   随机字符串
     * @return echostr
     */
    @RequestMapping(value = "/echostr")
    @ResponseBody
    public String echostr(String signature, String timestamp, String nonce, String echostr) {
        String token = weChatProperties.getMpToken();
        if (!StringUtil.areNotEmpty(signature, timestamp, nonce, token, echostr)) {
            return null;
        }
        log.info("echostr={}", echostr);
        String[] a = new String[]{timestamp, nonce, token};
        Arrays.sort(a);
        StringBuilder builder = new StringBuilder();
        for (String s : a) {
            builder.append(s);
        }
        String signature2 = WeChatMessageDigestUtil.getInstance().encipher(builder.toString());
        if (!signature2.equals(signature)) {
            return null;
        }
        return echostr;
    }

    /**
     * 微信网页验证--获取用户身份第一步
     *
     * @param returnUrl
     * @return
     */
    @GetMapping(value = "/authorize")
    public String authorize(String returnUrl) {
        // 获取AccessToken的地址
        String url = projectURL + "/userToken";
        // 拼接微信网页验证地址
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_USER_INFO, URLEncoder.encode(returnUrl));
        return "redirect:" + redirectUrl;
    }

    /**
     * 微信网页验证--获取用户身份第二步
     *
     * @param code
     * @param returnUrl
     * @return
     */
    @GetMapping("/userToken")
    public String userToken(String code, @RequestParam("state") String returnUrl) {
        WxMpOAuth2AccessToken token = new WxMpOAuth2AccessToken();
        try {
            token = wxMpService.oauth2getAccessToken(code);
            AppHttpUtil.getSession().setAttribute(SysConstant.WX_MP_OAUTH2_ACCESS_TOKEN, token);
        } catch (WxErrorException e) {
            log.error("【微信网页授权】{}", e);
            throw new WeChatException("[获取用户微信身份失败]");
        }
        return "redirect:" + returnUrl;
    }

}
