package com.plansolve.farm.service.console.user;

import com.plansolve.farm.model.console.user.AppFarmlandDTO;
import com.plansolve.farm.model.database.Farmland;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/6/3
 * @Description:
 **/
public interface ConsoleFarmlandService {

    /**
     * 查询用户土地
     *
     * @param idUser
     * @return
     */
    public List<Farmland> listByUser(Long idUser);

    public AppFarmlandDTO loadDTO(Farmland farmland);

    public List<AppFarmlandDTO> loadDTO(List<Farmland> farmlands);

}
