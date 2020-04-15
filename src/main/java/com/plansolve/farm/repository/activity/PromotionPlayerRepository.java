package com.plansolve.farm.repository.activity;

import com.plansolve.farm.model.database.promotion.PromotionPlayer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: Andrew
 * @Date: 2019/5/28
 * @Description:
 */
public interface PromotionPlayerRepository extends JpaRepository<PromotionPlayer, Long>, JpaSpecificationExecutor<PromotionPlayer> {

    public List<PromotionPlayer> findByIdPromotionActivity(Long idPromotionActivity);

    public List<PromotionPlayer> findByIdUser(Long idUser);

    public CopyOnWriteArrayList<PromotionPlayer> findByIdPromotionActivityAndValidIsTrue(Long idPromotionActivity);

    public boolean existsByIdPromotionActivityAndIdUserAndValidIsTrue(Long idPromotionActivity, Long idUser);

    public PromotionPlayer findByIdPromotionActivityAndIdUser(Long idPromotionActivity, Long idUser);

    public Page<PromotionPlayer> findByValidIsTrue(Pageable pageable);

}
