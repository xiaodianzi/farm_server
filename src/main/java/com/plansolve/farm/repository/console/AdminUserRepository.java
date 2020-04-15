package com.plansolve.farm.repository.console;

import com.plansolve.farm.model.database.console.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/2/28
 * @Description:
 **/
public interface AdminUserRepository extends JpaRepository<AdminUser, Integer> {

    public AdminUser findByMobileAndUserStateNot(String mobile, String state);

    public AdminUser findByIdAdminUser(Integer idAdminUser);

    public List<AdminUser> findByUserStateNot(String state);

}
