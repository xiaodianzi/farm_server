package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.client.CooperationInfoDTO;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.database.Machinery;
import com.plansolve.farm.model.database.cooperation.Cooperation;
import com.plansolve.farm.model.database.cooperation.CooperationInfo;
import com.plansolve.farm.model.database.cooperation.Team;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.CooperationStateEnum;
import com.plansolve.farm.model.enums.state.FarmlandStateEnum;
import com.plansolve.farm.model.enums.state.MachineryStateEnum;
import com.plansolve.farm.repository.FarmlandRepository;
import com.plansolve.farm.repository.MachineryRepository;
import com.plansolve.farm.repository.cooperation.CooperationInfoRepository;
import com.plansolve.farm.repository.cooperation.CooperationRepository;
import com.plansolve.farm.repository.cooperation.TeamRepository;
import com.plansolve.farm.repository.user.UserRepository;
import com.plansolve.farm.service.console.AppCooperationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2018/8/2
 * @Description:
 */
@Service
public class AppCooperationServiceImpl implements AppCooperationService {

    private static final Integer NONE_VALUE = -1;

    private static final Integer INIT_VALUE = 0;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmlandRepository farmlandRepository;

    @Autowired
    private MachineryRepository machineryRepository;

    @Autowired
    private CooperationRepository cooperationRepository;

    @Autowired
    private CooperationInfoRepository cooperationInfoRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public List<CooperationInfoDTO> getCooperationInfo(Integer limit, Integer offset, String search) {
        List<CooperationInfoDTO> cooperationInfoDTOList = new ArrayList<>();
        Pageable pageable = new PageRequest(offset, limit, Sort.Direction.DESC, "registTime");
        Page<Cooperation> cooperations = cooperationRepository.findAll(pageable);
        if (null != cooperations && cooperations.getSize() > 0) {
            for (Cooperation cooperation : cooperations) {
                CooperationInfoDTO dto = new CooperationInfoDTO();
                dto.setIdCooperation(cooperation.getIdCooperation());
                dto.setCooperationName(cooperation.getCooperationName());
                User u = userRepository.findByIdUser(cooperation.getIdUser().longValue());
                if (null != u) {
                    dto.setPrimaryName(u.getNickname());
                    dto.setMobile(u.getMobile());
                }
                CooperationInfo cooperationInfo = cooperationInfoRepository.getByIdCooperation(cooperation.getIdCooperation());
                if (null != cooperationInfo) {
                    dto.setBusiness_license(cooperationInfo.getBusiness_license());
                }
                dto.setCreateTime(cooperation.getRegistTime().toString().trim());
                dto.setCooperationState(cooperation.getCooperationState());
                cooperationInfoDTOList.add(dto);
            }
        }
        return cooperationInfoDTOList;
    }

    @Override
    public List<CooperationInfoDTO> queryMembers(Integer limit, Integer offset, String keyword) {
        List<CooperationInfoDTO> membersList = new ArrayList<>();
        Pageable pageable = new PageRequest(offset, limit, Sort.Direction.DESC, "registTime");
        if (StringUtils.isNotBlank(keyword)) {
            Integer cooperationId = Integer.valueOf(keyword);
            //动态分页查询
            User user = new User();
            user.setIdCooperation(cooperationId);
            //创建匹配器，即如何使用查询条件
            ExampleMatcher matcher = ExampleMatcher.matching().withMatcher(cooperationId.toString(), ExampleMatcher.GenericPropertyMatchers.exact());  //忽略属性：是否关注。因为是基本类型，需要忽略掉
            //创建实例
            Example<User> ex = Example.of(user, matcher);
            Page<User> members = userRepository.findAll(ex, pageable);
            if (null != members) {
                if (null != members && members.getTotalElements() > 0) {
                    for (User member : members) {
                        CooperationInfoDTO dtoMember = dtoMembersFactory(member);
                        membersList.add(dtoMember);
                    }
                }
            }
        } else {
            //查看合作社的所有成员
            Page<User> users = userRepository.findAllByIdCooperationNotNull(pageable);
            if (null != users) {
                List<User> userList = users.getContent();
                if (userList.size() > 0) {
                    for (User member : userList) {
                        CooperationInfoDTO dtoMember = dtoMembersFactory(member);
                        membersList.add(dtoMember);
                    }
                }
            }
        }
        return membersList;
    }

    /**
     * 把用户信息组装成页面显示的数据
     *
     * @param user
     * @return
     */
    private CooperationInfoDTO dtoMembersFactory(User user) {
        CooperationInfoDTO membersDTO = new CooperationInfoDTO();
        membersDTO.setIdCooperation(user.getIdCooperation());
        membersDTO.setMobile(user.getMobile());
        membersDTO.setNickname(user.getNickname());
        //设置职务和队别
        if (null != user.getIdTeam()) {
            if (user.getIdTeam().toString().trim().equals(AppCooperationServiceImpl.NONE_VALUE.toString().trim())) {
                membersDTO.setPosition(CooperationStateEnum.PROPRIETER.getMessage());
                membersDTO.setTeamName("无");
            } else {
                Team team = teamRepository.getOneByIdTeam(user.getIdTeam());
                if (null != team) {
                    if (null != team.getIdUser()) {
                        if (user.getIdUser().toString().trim().equals(team.getIdUser().toString().trim())) {
                            membersDTO.setPosition(CooperationStateEnum.CAPTAIN.getMessage());
                        } else {
                            membersDTO.setPosition(CooperationStateEnum.MEMBER.getMessage());
                        }
                    } else {
                        membersDTO.setPosition(CooperationStateEnum.MEMBER.getMessage());
                    }
                    membersDTO.setTeamName(team.getTeamName());
                }
            }
        } else {
            throw new PermissionException("该用户还不是合作社成员！");
        }
        //设置农田信息
        List<Farmland> farmlands = farmlandRepository.findByIdUserAndFarmlandStateNot(user.getIdUser(), FarmlandStateEnum.DELETED.getState());
        if (null != farmlands && farmlands.size() > 0) {
            Float farmlandAcreage = 0.00F;
            for (Farmland f : farmlands) {
                if (null != f) {
                    farmlandAcreage += f.getFarmlandAcreage();
                }
            }
            // 设置农田面积
            membersDTO.setFarmlandAcreage(farmlandAcreage);
            // 设置农田数量
            membersDTO.setFarmlandNumber(farmlands.size());
        } else {
            // 没有农田信息的设置默认值，方便前端解析
            membersDTO.setFarmlandAcreage(0.00F);
            membersDTO.setFarmlandNumber(AppCooperationServiceImpl.INIT_VALUE);
        }
        //设置农机信息
        List<Machinery> machineryList = machineryRepository.findByIdUserAndMachineryStateNot(user.getIdUser(), MachineryStateEnum.DELETED.getState());
        if (null != machineryList && machineryList.size() > 0) {
            // 设置农机数量
            membersDTO.setMachineryNumber(machineryList.size());
        } else {
            // 没有农机的值为0
            membersDTO.setMachineryNumber(AppCooperationServiceImpl.INIT_VALUE);
        }
        return membersDTO;
    }

}
