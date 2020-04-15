package com.plansolve.farm.repository.activity;

import com.plansolve.farm.model.database.promotion.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author: Andrew
 * @Date: 2019/5/28
 * @Description:
 */
public interface PrizeRepository extends JpaRepository<Prize, Long>, JpaSpecificationExecutor<Prize> {

    public Prize findByIdPrize(Long idPrize);

}
