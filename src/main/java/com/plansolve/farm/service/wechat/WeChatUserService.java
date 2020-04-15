package com.plansolve.farm.service.wechat;

import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.UserInfo;
import com.plansolve.farm.model.database.user.WeChatUser;

/**
 * @Author: 高一平
 * @Date: 2018/10/25
 * @Description:
 **/
public interface WeChatUserService {

    /**
     * 保存用户信息
     *
     * @param user
     * @return
     */
    public User save(User user);

    /**
     * 保存用户关联详情
     *
     * @param userInfo
     * @return
     */
    public UserInfo save(UserInfo userInfo);

    /**
     * 保存用户微信相关信息
     *
     * @param weChatUser
     * @return
     */
    public WeChatUser save(WeChatUser weChatUser);

    /**
     * 根据主键查询
     *
     * @param idUser
     * @return
     */
    public User findUserById(Long idUser);

    /**
     * 根据用户ID查询用户详情
     *
     * @param idUser
     * @return
     */
    public UserInfo findUserInfoById(Long idUser);

    /**
     * 根据手机号码查询
     *
     * @param mobile
     * @return
     */
    public User findUserByMobile(String mobile);

    /**
     * 根据用户微信号查新用户
     *
     * @param openId
     * @return
     */
    public WeChatUser findWeChatUserByOpenId(String openId);

}
