package com.plansolve.farm.model.console;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/2/28
 * @Description:
 **/

@Data
public class AdminUserDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "手机号码不能为空")
    @Size(min = 11, max = 11, message = "请输入正确的手机号码")
    private String mobile;

    private String password;

    private String userState;

    @NotBlank(message = "用户角色不能为空")
    private String role;

    @NotNull(message = "是否为超级管理员")
    private String identity;

    private String createTime;

    private String updateTime;

    private String button;

}
