package com.plansolve.farm.service.client;

import com.plansolve.farm.model.client.TeamDTO;

/**
 * @Author: 高一平
 * @Date: 2018/6/19
 * @Description: 小队相关接口
 **/
public interface TeamService {

    /**
     * 根据主键查询小队
     *
     * @param idTeam
     * @return
     */
    public TeamDTO findById(Integer idTeam);

}
