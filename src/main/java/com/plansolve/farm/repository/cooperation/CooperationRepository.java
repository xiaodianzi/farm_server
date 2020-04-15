package com.plansolve.farm.repository.cooperation;

import com.plansolve.farm.model.database.cooperation.Cooperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 合作社
 **/

public interface CooperationRepository extends JpaRepository<Cooperation, Integer> {

    public Cooperation getOneByIdCooperationAndCooperationState(Integer idCooperation, String state);

    public Cooperation getOneByIdCooperation(Integer idCooperation);

    public Cooperation getOneByIdCooperationAndCooperationStateNot(Integer idCooperation, String state);

    @Transactional
    @Modifying
    @Query("update Cooperation c set c.cooperationState = :state, c.description = :description, c.updateTime = :date where c.idCooperation = :idCooperation")
    public void updateInfo(@Param(value = "state") String state, @Param(value = "description") String description, @Param(value = "date") Date date, @Param(value = "idCooperation") Integer idCooperation);

    /**************************************用户数量统计**************************************/
    public Integer countByRegistTimeBefore(Date date);

    public Integer countByRegistTimeBetween(Date dateBegin, Date dateEnd);

    @Query(nativeQuery = true, value = "select count(distinct farm.cooperation.id_cooperation) " +
            "from farm.cooperation left join farm.address on cooperation.id_address = address.id_address " +
            "where address.city = :city and cooperation.regist_time < :date")
    public Integer placeAllCooperation(@Param("city") String city, @Param("date") Date date);

    @Query(nativeQuery = true, value = "select count(distinct farm.cooperation.id_cooperation) " +
            "from farm.cooperation left join farm.address on cooperation.id_address = address.id_address " +
            "where address.city = :city and cooperation.regist_time < :dateEnd and cooperation.regist_time > :dateBegin")
    public Integer placeCooperation(@Param("city") String city, @Param("dateBegin") Date dateBegin, @Param("dateEnd") Date dateEnd);
}
