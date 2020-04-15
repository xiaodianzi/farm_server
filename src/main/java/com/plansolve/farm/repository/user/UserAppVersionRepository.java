package com.plansolve.farm.repository.user;

import com.plansolve.farm.model.database.user.UserAppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2019/5/7
 * @Description:
 **/
public interface UserAppVersionRepository extends JpaRepository<UserAppVersion, Long> {
}
