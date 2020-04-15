package com.plansolve.farm.service.console;

import com.plansolve.farm.model.client.CooperationInfoDTO;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2018/8/2
 * @Description:
 */
public interface AppCooperationService {

    /**
     * 查询合作社信息列表
     * @param limit 每页显示多少条
     * @param offset 当前页
     * @return
     */
    public List<CooperationInfoDTO> getCooperationInfo(Integer limit, Integer offset, String keyword);

    /**
     * 分页查询合作社成员信息
     * @param limit 每页显示多少条
     * @param offset 当前页
     * @param keyword 查询条件
     * @return
     */
    public List<CooperationInfoDTO> queryMembers(Integer limit, Integer offset, String keyword);

}
