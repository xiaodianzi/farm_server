package com.plansolve.farm.repository.agricultural;

import com.plansolve.farm.model.database.agricultural.CropsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author: Andrew
 * @Date: 2019/2/18
 * @Description:
 */
public interface CropsDataRepository extends JpaRepository<CropsData, Integer>, JpaSpecificationExecutor<CropsData> {

    public CropsData findOneById(Integer id);

    public CropsData findOneByCropType(String cropType);


}
