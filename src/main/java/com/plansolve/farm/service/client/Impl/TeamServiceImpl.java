package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.model.client.TeamDTO;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.database.cooperation.Team;
import com.plansolve.farm.repository.cooperation.TeamRepository;
import com.plansolve.farm.service.client.TeamService;
import com.plansolve.farm.service.client.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 高一平
 * @Date: 2018/6/19
 * @Description:小队相关接口客户端实现
 **/
@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserService userService;

    /**
     * 根据主键查询小队
     *
     * @param idTeam
     * @return
     */
    @Override
    public TeamDTO findById(Integer idTeam) {
        Team team = teamRepository.getOneByIdTeam(idTeam);
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamName(team.getTeamName());

        if (team.getIdUser() != null && team.getIdUser() > 0) {
            UserDTO userDTO = userService.findUser(team.getIdUser(), false);
            teamDTO.setCaptain(userDTO);
        }
        return teamDTO;
    }
}
