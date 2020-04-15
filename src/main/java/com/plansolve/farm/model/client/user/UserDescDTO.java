package com.plansolve.farm.model.client.user;

import lombok.Data;

import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/9/3
 * @Description:
 **/
@Data
public class UserDescDTO {

    private String nickname; // 昵称

    private Date birthday; // 出生日期

    private String sex; // 性别

    private String email; // 邮箱

    private String qq; // QQ号

}
