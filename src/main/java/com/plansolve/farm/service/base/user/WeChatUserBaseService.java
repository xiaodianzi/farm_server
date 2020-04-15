package com.plansolve.farm.service.base.user;

import com.plansolve.farm.model.database.user.WeChatUser;

/**
 * @Author: 高一平
 * @Date: 2019/4/29
 * @Description:
 **/
public interface WeChatUserBaseService {

    public WeChatUser insert(Long idUser, String type, WeChatUser weChatUser);

    public WeChatUser update(WeChatUser weChatUser);

    public WeChatUser findByOpenId(String openId);

}
