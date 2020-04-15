package com.plansolve.farm.repository.cooperation;

import com.plansolve.farm.model.database.cooperation.InviteRelationShip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2018/6/13
 * @Description:
 */
public interface InviteRelationShipRepository extends JpaRepository<InviteRelationShip, Integer> {

    public List<InviteRelationShip> getInviteRelationShipsByIdCooperationAndInviterState(Integer idCooperation, String inviterState);

    public InviteRelationShip getOneByIdCooperationAndIdInviterAndInviterStateNot(Integer idCooperation, Long idInviter, String inviterState);

    public InviteRelationShip getOneByIdCooperationAndIdInviterAndInviterState(Integer idCooperation, Long idInviter, String state);

}
