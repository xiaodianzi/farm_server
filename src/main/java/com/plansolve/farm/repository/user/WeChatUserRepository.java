package com.plansolve.farm.repository.user;

import com.plansolve.farm.model.database.user.WeChatUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2018/11/5
 * @Description:
 **/
public interface WeChatUserRepository extends JpaRepository<WeChatUser, Long> {

    public WeChatUser findByOpenId(String openId);

}
