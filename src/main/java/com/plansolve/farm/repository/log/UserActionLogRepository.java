package com.plansolve.farm.repository.log;

import com.plansolve.farm.model.database.log.UserActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2019/6/11
 * @Description:
 **/
public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {
}
