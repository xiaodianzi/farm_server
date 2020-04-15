package com.plansolve.farm.repository.log;

import com.plansolve.farm.model.database.log.BidOrderChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2018/8/6
 * @Description:
 **/
public interface BidOrderChangeLogRepository extends JpaRepository<BidOrderChangeLog, Long> {
}
