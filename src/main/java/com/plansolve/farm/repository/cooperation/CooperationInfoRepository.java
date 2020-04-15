package com.plansolve.farm.repository.cooperation;

import com.plansolve.farm.model.database.cooperation.CooperationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 合作社详情
 **/
public interface CooperationInfoRepository extends JpaRepository<CooperationInfo, Integer> {

    public CooperationInfo getByIdCooperation(Integer idCooperation);

}
