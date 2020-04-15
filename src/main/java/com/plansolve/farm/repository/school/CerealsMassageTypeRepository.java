package com.plansolve.farm.repository.school;

import com.plansolve.farm.model.database.school.CerealsMassageType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2019/5/15
 * @Description:
 **/
public interface CerealsMassageTypeRepository extends JpaRepository<CerealsMassageType, Integer> {

    public CerealsMassageType findByIdCerealsMassageType(Integer idCerealsMassageType);

}
