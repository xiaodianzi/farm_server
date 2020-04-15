package com.plansolve.farm.model.client.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description:
 **/

public class UserIdCardDTO {

    @NotBlank(message = "真实姓名不能为空")
    private String realname; // 真实姓名

    @NotBlank(message = "证件号码不能为空")
    @Size(min = 15, max = 18, message = "请输入正确的证件号码")
    private String idCardNo; // 证件号码

    private String state; // 验证状态

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "UserIdCardDTO{" +
                "realname='" + realname + '\'' +
                ", idCardNo='" + idCardNo + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
