package com.plansolve.farm.model.wxapplet;

import lombok.Data;

/**
 * @Author: 高一平
 * @Date: 2019/4/29
 * @Description:
 **/
@Data
public class WeChatUserInfoDTO {

    private String nickName;

    private Integer gender; // 0	未知  1  男性  2	  女性

    private String avatarUrl;

    private String country;

    private String province;

    private String city;

    private String language;

}
