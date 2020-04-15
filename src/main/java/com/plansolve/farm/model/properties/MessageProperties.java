package com.plansolve.farm.model.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description: 手机发送验证服务所需属性
 **/
@Component
@ConfigurationProperties(prefix = "message")
public class MessageProperties {

    public static String url;

    public static String id;

    public static String pwd;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
