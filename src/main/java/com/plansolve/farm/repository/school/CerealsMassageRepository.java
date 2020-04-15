package com.plansolve.farm.repository.school;

import com.plansolve.farm.model.database.school.CerealsMassage;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/5/15
 * @Description:
 **/
public interface CerealsMassageRepository extends JpaRepository<CerealsMassage, Integer> {

    public List<CerealsMassage> findByIdCerealsMassageType(Integer idCerealsMassageType, Sort sort);

    public CerealsMassage findByIdCerealsMassage(Integer idCerealsMassage);

}
