package com.plansolve.farm.controller.console.main.cooperation;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.client.CooperationInfoDTO;
import com.plansolve.farm.model.console.PageDTO;
import com.plansolve.farm.repository.cooperation.CooperationRepository;
import com.plansolve.farm.repository.user.UserRepository;
import com.plansolve.farm.service.console.AppCooperationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2018/8/2
 * @Description:
 */
@RestController
@RequestMapping(value = "/manger/app/cooperation")
public class AppCooperationController extends BaseController {

    @Autowired
    private AppCooperationService cooperationService;

    @Autowired
    private CooperationRepository cooperationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 查询合作社信息列表
     *
     * @param limit   每页显示多少条
     * @param offset  当前页
     * @param keyword 搜索关键字
     * @return
     */
    @GetMapping(value = "/information")
    public PageDTO<CooperationInfoDTO> getCooperationInfo(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset, String keyword) {
        PageDTO<CooperationInfoDTO> pageDTO = new PageDTO<>();
        Long count = cooperationRepository.count();
        Integer pageNumber = getPage(limit, offset);
        List<CooperationInfoDTO> cooperations = cooperationService.getCooperationInfo(limit, pageNumber, keyword);
        pageDTO.setTotal(count);
        pageDTO.setRows(cooperations);
        return pageDTO;
    }

    /**
     * 查询合作社成员信息
     *
     * @param limit   每页显示多少条
     * @param offset  当前页
     * @param keyword 查询条件
     * @return
     */
    @GetMapping(value = "/queryMembers")
    public PageDTO<CooperationInfoDTO> getMembers(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset, String keyword) {
        PageDTO<CooperationInfoDTO> pageDTO = new PageDTO<>();
        Integer pageNumber = getPage(limit, offset);
        List<CooperationInfoDTO> members = cooperationService.queryMembers(limit, pageNumber, keyword);
        Long count = 0L;
        if (StringUtils.isNotBlank(keyword)){
            Integer cooperationId = Integer.valueOf(keyword);
            count = userRepository.countByIdCooperation(cooperationId);
        }else{
            count = userRepository.countByIdCooperationNotNull();
        }
        pageDTO.setTotal(count);
        pageDTO.setRows(members);
        return pageDTO;
    }

}
