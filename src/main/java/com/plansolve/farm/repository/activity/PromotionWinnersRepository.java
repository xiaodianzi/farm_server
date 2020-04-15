package com.plansolve.farm.repository.activity;

import com.plansolve.farm.model.database.promotion.PromotionWinners;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/6/3
 * @Description:
 */
public interface PromotionWinnersRepository extends JpaRepository<PromotionWinners, Long>, JpaSpecificationExecutor<PromotionWinners> {

    public List<PromotionWinners> findByIdPromotionActivityAndValidIsTrue(Long idPromotionActivity);

    public PromotionWinners findByIdUserAndValidIsTrue(Long idUser);

    public Page<PromotionWinners> findByValidIsTrue(Pageable pageable);

}
