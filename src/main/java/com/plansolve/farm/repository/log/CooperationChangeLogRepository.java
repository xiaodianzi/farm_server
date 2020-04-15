package com.plansolve.farm.repository.log;

import com.plansolve.farm.model.database.log.CooperationChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 合作社变更日志
 **/

public interface CooperationChangeLogRepository extends JpaRepository<CooperationChangeLog, Integer> {
}
