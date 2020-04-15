package com.plansolve.farm.repository;

import com.plansolve.farm.model.database.Farmland;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 农田
 **/

public interface FarmlandRepository extends JpaRepository<Farmland, Long> {

    public Farmland findByIdFarmland(Long idFarmland);

    public List<Farmland> findByIdUserAndFarmlandStateNot(Long idUser, String farmlandState);

}
