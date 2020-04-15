package com.plansolve.farm.service.wechat.Impl;

import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.UserInfo;
import com.plansolve.farm.model.database.user.WeChatUser;
import com.plansolve.farm.repository.user.UserInfoRepository;
import com.plansolve.farm.repository.user.UserRepository;
import com.plansolve.farm.repository.user.WeChatUserRepository;
import com.plansolve.farm.service.wechat.WeChatUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 高一平
 * @Date: 2018/11/1
 * @Description:
 **/
@Service
@Slf4j
public class WeChatUserServiceImpl implements WeChatUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private WeChatUserRepository weChatUserRepository;

    /**
     * 保存用户信息
     *
     * @param user
     * @return
     */
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * 保存用户关联详情
     *
     * @param userInfo
     * @return
     */
    @Override
    public UserInfo save(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    /**
     * 保存用户微信相关信息
     *
     * @param weChatUser
     * @return
     */
    @Override
    public WeChatUser save(WeChatUser weChatUser) {
        return weChatUserRepository.save(weChatUser);
    }

    /**
     * 根据主键查询
     *
     * @param idUser
     * @return
     */
    @Override
    public User findUserById(Long idUser) {
        return userRepository.findByIdUser(idUser);
    }

    /**
     * 根据用户ID查询用户详情
     *
     * @param idUser
     * @return
     */
    @Override
    public UserInfo findUserInfoById(Long idUser) {
        return userInfoRepository.findByIdUser(idUser);
    }

    /**
     * 根据手机号码查询
     *
     * @param mobile
     * @return
     */
    @Override
    public User findUserByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    /**
     * 根据用户微信号查新用户
     *
     * @param openId
     * @return
     */
    @Override
    public WeChatUser findWeChatUserByOpenId(String openId) {
        return findWeChatUserByOpenId(openId);
    }

}
