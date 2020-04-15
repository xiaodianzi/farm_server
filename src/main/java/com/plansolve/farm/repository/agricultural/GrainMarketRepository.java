package com.plansolve.farm.repository.agricultural;

import com.plansolve.farm.model.database.agricultural.GrainMarket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/3/29
 * @Description:
 */
public interface GrainMarketRepository extends JpaRepository<GrainMarket, Integer>, JpaSpecificationExecutor<GrainMarket> {

    public List<GrainMarket> findByIdUser(Pageable pageable, Long idUser);

    public List<GrainMarket> findByMarketType(Pageable pageable, String infoType);

    public List<GrainMarket> findByIdUserAndMarketType(Pageable pageable, Long idUser, String infoType);

    public Long countAllByIdUserAndMarketType(Long idUser, String infoType);

    public Long countAllByMarketType(String infoType);

}
