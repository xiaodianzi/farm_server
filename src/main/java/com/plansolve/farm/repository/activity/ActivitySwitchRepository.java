package com.plansolve.farm.repository.activity;

import com.plansolve.farm.model.database.promotion.ActivitySwitch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author: Andrew
 * @Date: 2019/6/6
 * @Description:
 */
public interface ActivitySwitchRepository extends JpaRepository<ActivitySwitch, Long>, JpaSpecificationExecutor<ActivitySwitch> {

    public boolean existsByActivityTypeAndValidIsTrue(String activityType);

}
