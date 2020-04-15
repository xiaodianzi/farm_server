package com.plansolve.farm.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.plansolve.farm.model.properties.WeChatProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author: 高一平
 * @Date: 2018/11/14
 * @Description:
 **/

@Component
public class WeChatPayConfig {

    @Autowired
    private WeChatProperties weChatProperties;

    @Bean
    public WxPayService wxPayService(){
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig());
        return wxPayService;
    }

    @Bean
    public WxPayConfig wxPayConfig(){
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(weChatProperties.getAppId());
        wxPayConfig.setMchId(weChatProperties.getMchId());
        wxPayConfig.setMchKey(weChatProperties.getMchKey());
        wxPayConfig.setKeyPath(weChatProperties.getKeyPath());
        wxPayConfig.setNotifyUrl(weChatProperties.getNotifyUrl());
        return wxPayConfig;
    }

}
