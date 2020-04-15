package com.plansolve.farm.model.console.user;

import com.plansolve.farm.util.HtmlUtil;
import lombok.Data;

/**
 * @Author: 高一平
 * @Date: 2018/8/8
 * @Description:
 **/
@Data
public class AppUserDTO {

    private Long idUser;

    private String nickname; // 用户昵称

    private String mobile; // 联系电话

    private String userState; // 当前用户的状态

    private String identity; // 用户身份（农机手/种植户）

    private String position; // 用户职位（普通用户/社员/队长/社长）

    private String registTime; // 注册时间

    private String updateTime; // 更新时间

    /**
     * 相关操作按钮
     * 种植户订单/农机手订单/用户详情
     */
    private String button; // 相关链接按钮 （所属合作社、身份信息、土地、农机、相关订单）

    public String getButton() {
        return HtmlUtil.getButtonHtml(false, "/plansolve/manger/app/user/appUserDetail?idUser=" + idUser,
                "", "primary", "详情");
    }
}
