package com.plansolve.farm.repository;

import com.plansolve.farm.model.database.Machinery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 农机
 **/
public interface MachineryRepository extends JpaRepository<Machinery, Long> {

    public Machinery findByIdMachinery(Long idMachinery);

    public List<Machinery> findByIdUserAndMachineryStateNot(Long idUser, String machineryState);

}
