package com.plansolve.farm.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import com.plansolve.farm.model.properties.WeChatProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author: 高一平
 * @Date: 2018/9/27
 * @Description:
 **/
@Component
public class WeChatMiniAppConfig {

    @Autowired
    private WeChatProperties weChatProperties;

    @Bean
    public WxMaService wxMaService() {
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(wxMaConfig());
        return wxMaService;
    }

    @Bean
    public WxMaConfig wxMaConfig() {
        WxMaInMemoryConfig config = new WxMaInMemoryConfig();
        config.setAppid(weChatProperties.getMiniAppId());
        config.setSecret(weChatProperties.getMiniAppSecret());
        config.setToken(weChatProperties.getMiniAppToken());
        config.setAesKey(weChatProperties.getMiniAppAesKey());
        config.setMsgDataFormat(weChatProperties.getMiniAppMsgDataFormat());
        return config;
    }

}
