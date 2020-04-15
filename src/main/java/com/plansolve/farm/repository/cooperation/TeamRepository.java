package com.plansolve.farm.repository.cooperation;

import com.plansolve.farm.model.database.cooperation.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description: 合作社小队
 **/
public interface TeamRepository extends JpaRepository<Team, Integer> {

    public Team getOneByIdUserAndIdCooperation(Long idUser, Integer idCooperation);

    public Team getOneByIdTeam(Integer idTeam);

    public Team getTeamByIdCooperationAndTeamName(Integer idCooperation, String teamName);

    public List<Team> getTeamsByIdCooperation(Integer idCooperation);

}
