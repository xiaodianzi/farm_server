package com.plansolve.farm.repository.user;

import com.plansolve.farm.model.database.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户详细信息
 **/

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    public UserInfo findByIdUser(Long idUser);

}
