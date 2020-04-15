package com.plansolve.farm.model.client.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @Author: 高一平
 * @Date: 2018/6/6
 * @Description: 手机号码及其设备码
 **/
public class MobileDTO {

    @NotBlank(message = "手机号码不能为空")
    @Size(min = 11, max = 11, message = "请输入正确的手机号码")
    private String mobile;

    @NotBlank(message = "手机设备码不能为空")
    private String androidMAC;

    private String registId; //消息推送id

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAndroidMAC() {
        return androidMAC;
    }

    public void setAndroidMAC(String androidMAC) {
        this.androidMAC = androidMAC;
    }

    public String getRegistId() {
        return registId;
    }

    public void setRegistId(String registId) {
        this.registId = registId;
    }

    @Override
    public String toString() {
        return "MobileDTO{" +
                "mobile='" + mobile + '\'' +
                ", androidMAC='" + androidMAC + '\'' +
                '}';
    }
}
