package com.plansolve.farm.config;

import com.plansolve.farm.model.properties.WeChatProperties;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author: 高一平
 * @Date: 2018/9/27
 * @Description:
 **/
@Component
public class WeChatConfig {

    @Autowired
    private WeChatProperties weChatProperties;

    @Bean
    public WxMpService wxMpService() {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
        return wxMpService;
    }

    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {
        WxMpInMemoryConfigStorage wxMpConfigStorage = new WxMpInMemoryConfigStorage();
        wxMpConfigStorage.setAppId(weChatProperties.getMpAppId());
        wxMpConfigStorage.setSecret(weChatProperties.getMpAppSecret());
        return wxMpConfigStorage;
    }

}
