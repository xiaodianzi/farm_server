package com.plansolve.farm.model.database.user;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: 高一平
 * @Date: 2018/11/5
 * @Description: 微信用户
 **/

@Data
@Entity
public class WeChatUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idWeChatUser; // 主键（数据库自增）

    @Column(nullable = false, updatable = false)
    private String openId; // 用户的唯一标识

    @Column(nullable = false, updatable = false)
    private Long idUser; // 主键

    @Column(nullable = false, updatable = false)
    private String type; // mp-公众号、applet-小程序、pay-支付

    private String nickname; // 用户昵称

    private String sex; // 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知

    private String province; // 用户个人资料填写的省份

    private String city; // 普通用户个人资料填写的城市

    private String country; // 国家，如中国为CN

    private String headImgUrl; // 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。

    private String unionid; // 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。

}
