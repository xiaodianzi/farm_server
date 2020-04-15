package com.plansolve.farm.service.wechat;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 高一平
 * @Date: 2018/11/5
 * @Description:
 **/
@Slf4j
@Service
public class WeChatService {

    @Autowired
    private WxMpService wxMpService;

    /**
     * 获取用户微信个人信息
     *
     * @param accessToken
     * @return
     * @throws WxErrorException
     */
    public WxMpUser getWxMpUser(WxMpOAuth2AccessToken accessToken) throws WxErrorException {
        Boolean valid = validAccessToken(accessToken);
        if (!valid) {
            accessToken = refreshAccessToken(accessToken);
        }
        return wxMpService.oauth2getUserInfo(accessToken, null);
    }

    /**
     * 验证AccessToken是否有效
     *
     * @param accessToken
     * @return
     */
    private Boolean validAccessToken(WxMpOAuth2AccessToken accessToken) {
        return wxMpService.oauth2validateAccessToken(accessToken);
    }

    /**
     * 刷新AccessToken
     *
     * @param accessToken
     * @return
     * @throws WxErrorException
     */
    private WxMpOAuth2AccessToken refreshAccessToken(WxMpOAuth2AccessToken accessToken) throws WxErrorException {
        return wxMpService.oauth2refreshAccessToken(accessToken.getRefreshToken());
    }

}
