package com.plansolve.farm.service.base.user;

import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.UserInfo;

/**
 * @Author: 高一平
 * @Date: 2019/6/12
 * @Description:
 **/
public interface UserBaseService {

    public User getUser(Long idUser);

    public User getUser(String mobile);

    public UserInfo getUserInfo(Long idUser);

    public User getValidatedUser(String mobile);

    public User insert(User user);

    public User update(User user, String detail);

    public User verify(Long idUser);

    public User delete(Long idUser);

}
