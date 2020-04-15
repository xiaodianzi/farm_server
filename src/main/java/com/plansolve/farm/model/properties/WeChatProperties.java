package com.plansolve.farm.model.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: 高一平
 * @Date: 2018/9/27
 * @Description:
 **/
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {

    private String miniAppId;
    private String miniAppSecret;
    private String miniAppToken;
    private String miniAppAesKey;
    private String miniAppMsgDataFormat;

    private String mpAppId;
    private String mpAppSecret;
    private String mpToken;

    private String appId;
    private String mchId;
    private String mchKey;
    private String keyPath;
    private String notifyUrl;
}
