package com.plansolve.farm.service.console.user;

import com.plansolve.farm.model.database.Machinery;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/6/4
 * @Description:
 **/
public interface ConsoleMachineryService {

    public List<Machinery> listByUser(Long idUser);

}
