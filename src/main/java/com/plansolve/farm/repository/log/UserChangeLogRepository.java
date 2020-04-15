package com.plansolve.farm.repository.log;

import com.plansolve.farm.model.database.log.UserChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 记录用户账号变更情况（变更密码，变更手机号码，以及用户的新增验证冻结删除等）
 **/
public interface UserChangeLogRepository extends JpaRepository<UserChangeLog, Long> {
}
