package com.plansolve.farm.repository.activity;

import com.plansolve.farm.model.database.promotion.PromotionActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/5/28
 * @Description:
 */
public interface PromotionActivityRepository extends JpaRepository<PromotionActivity, Long>, JpaSpecificationExecutor<PromotionActivity> {

    public PromotionActivity findOneByIdPromotionActivity(Long idPromotionActivity);

    public PromotionActivity findByActivityName(String activityName);

    public Page<PromotionActivity> findByIsValidTrue(Pageable pageable);

    public List<PromotionActivity> findByIsValidTrue();

}
