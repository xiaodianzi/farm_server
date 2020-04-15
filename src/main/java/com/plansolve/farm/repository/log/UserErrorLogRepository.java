package com.plansolve.farm.repository.log;

import com.plansolve.farm.model.database.log.UserErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2019/6/21
 * @Description:
 **/
public interface UserErrorLogRepository extends JpaRepository<UserErrorLog, Long> {
}
