package com.plansolve.farm.service.console.user.impl;

import com.plansolve.farm.model.console.user.AppFarmlandDTO;
import com.plansolve.farm.model.database.Address;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.enums.state.FarmlandStateEnum;
import com.plansolve.farm.repository.AddressRepository;
import com.plansolve.farm.repository.FarmlandRepository;
import com.plansolve.farm.service.base.common.AddressBaseService;
import com.plansolve.farm.service.console.user.ConsoleFarmlandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/6/3
 * @Description:
 **/
@Service
public class ConsoleFarmlandServiceImpl implements ConsoleFarmlandService {

    @Autowired
    private FarmlandRepository repository;
    @Autowired
    private AddressBaseService addressBaseService;

    /**
     * 查询用户土地
     *
     * @param idUser
     * @return
     */
    @Override
    public List<Farmland> listByUser(Long idUser) {
        List<Farmland> farmlands = repository.findByIdUserAndFarmlandStateNot(idUser, FarmlandStateEnum.DELETED.getState());
        return farmlands;
    }

    @Override
    public AppFarmlandDTO loadDTO(Farmland farmland) {
        AppFarmlandDTO dto = new AppFarmlandDTO();
        dto.setIdFarmland(farmland.getIdFarmland());
        dto.setFarmlandName(farmland.getFarmlandName());
        dto.setFarmlandAcreage(farmland.getFarmlandAcreage());
        Address address = addressBaseService.getAddress(farmland.getIdAddress());
        dto.setFarmlandAddress(address.getProvince() + address.getCity() + address.getCounty() + address.getDetail());
        return dto;
    }

    @Override
    public List<AppFarmlandDTO> loadDTO(List<Farmland> farmlands) {
        List<AppFarmlandDTO> dtos = new ArrayList<>();
        if (farmlands != null && farmlands.size() > 0) {
            for (Farmland farmland : farmlands) {
                AppFarmlandDTO dto = loadDTO(farmland);
                dtos.add(dto);
            }
        }
        return dtos;
    }
}
