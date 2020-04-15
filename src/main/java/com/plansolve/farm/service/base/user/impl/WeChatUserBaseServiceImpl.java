package com.plansolve.farm.service.base.user.impl;

import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.database.user.WeChatUser;
import com.plansolve.farm.repository.user.WeChatUserRepository;
import com.plansolve.farm.service.base.user.WeChatUserBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 高一平
 * @Date: 2019/4/29
 * @Description:
 **/
@Slf4j
@Service
public class WeChatUserBaseServiceImpl implements WeChatUserBaseService {

    @Autowired
    private WeChatUserRepository weChatUserRepository;

    /**
     * 微信用户添加
     *
     * @param idUser
     * @param type
     * @param weChatUser
     * @return
     */
    @Override
    public WeChatUser insert(Long idUser, String type, WeChatUser weChatUser) {
        WeChatUser result = findByOpenId(weChatUser.getOpenId());
        if (result == null) {
            weChatUser.setIdUser(idUser);
            weChatUser.setType(type);
            weChatUser = weChatUserRepository.save(weChatUser);
            return weChatUser;
        } else {
            throw new ParamErrorException("[该微信账号已注册]");
        }
    }

    /**
     * 微信用户更新
     *
     * @param weChatUser
     * @return
     */
    @Override
    public WeChatUser update(WeChatUser weChatUser) {
        WeChatUser result = findByOpenId(weChatUser.getOpenId());
        if (result != null) {
            weChatUser.setIdWeChatUser(result.getIdWeChatUser());
            weChatUser.setIdUser(result.getIdUser());
            weChatUser.setType(result.getType());
            weChatUser = weChatUserRepository.save(weChatUser);
            return weChatUser;
        } else {
            throw new ParamErrorException("[该微信账号未注册]");
        }
    }

    /**
     * 查询用户
     *
     * @param openId
     * @return
     */
    @Override
    public WeChatUser findByOpenId(String openId) {
        return weChatUserRepository.findByOpenId(openId);
    }
}
