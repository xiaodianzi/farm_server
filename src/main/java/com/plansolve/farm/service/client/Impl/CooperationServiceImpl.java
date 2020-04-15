package com.plansolve.farm.service.client.Impl;

import cn.jpush.api.push.PushResult;
import com.plansolve.farm.exception.*;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.*;
import com.plansolve.farm.model.client.order.OrderDTO;
import com.plansolve.farm.model.client.order.OrderSimpleDTO;
import com.plansolve.farm.model.client.order.OrderStatisticsDTO;
import com.plansolve.farm.model.client.order.StatisticsDTO;
import com.plansolve.farm.model.client.user.FarmlandDTO;
import com.plansolve.farm.model.client.user.UserInfoDTO;
import com.plansolve.farm.model.database.Address;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.database.Machinery;
import com.plansolve.farm.model.database.cooperation.Cooperation;
import com.plansolve.farm.model.database.cooperation.CooperationInfo;
import com.plansolve.farm.model.database.cooperation.InviteRelationShip;
import com.plansolve.farm.model.database.cooperation.Team;
import com.plansolve.farm.model.database.log.CooperationChangeLog;
import com.plansolve.farm.model.database.order.CompletionReport;
import com.plansolve.farm.model.database.order.OrderOperator;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.*;
import com.plansolve.farm.model.enums.code.ResultEnum;
import com.plansolve.farm.model.enums.state.FarmlandStateEnum;
import com.plansolve.farm.model.enums.state.MachineryStateEnum;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.model.enums.state.UserStateEnum;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.repository.AddressRepository;
import com.plansolve.farm.repository.FarmlandRepository;
import com.plansolve.farm.repository.MachineryRepository;
import com.plansolve.farm.repository.cooperation.CooperationInfoRepository;
import com.plansolve.farm.repository.cooperation.CooperationRepository;
import com.plansolve.farm.repository.cooperation.InviteRelationShipRepository;
import com.plansolve.farm.repository.cooperation.TeamRepository;
import com.plansolve.farm.repository.log.CooperationChangeLogRepository;
import com.plansolve.farm.repository.user.UserRepository;
import com.plansolve.farm.service.client.*;
import com.plansolve.farm.service.common.FileService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.Jdpush;
import com.plansolve.farm.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.DataFormatException;

/**
 * @Author: Andrew
 * @Date: 2018/6/12
 * @Description: 合作社接口实现类
 */

@Service
public class CooperationServiceImpl implements CooperationService {

    private static final Logger logger = LoggerFactory.getLogger(CooperationServiceImpl.class);

    private static final Integer INIT_VALUE = 0;

    private static final Integer NONE_VALUE = -1;

    private static final String INIT_TEAM_NAME = "一队";

    private static final String MSG_PREFIX = "合作社消息: ";

    private static final String MSG_UPDATE_PREFIX = "合作社更新: ";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CooperationRepository cooperationRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CooperationInfoRepository cooperationInfoRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private CooperationChangeLogRepository cooperationChangeLogRepository;

    @Autowired
    private InviteRelationShipRepository inviteRelationShipRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private FarmlandRepository farmlandRepository;

    @Autowired
    private MachineryRepository machineryRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private MachineryService machineryService;

    @Autowired
    private FarmlandService farmlandService;

    @Autowired
    private OrderOperatorService orderOperatorService;

    @Autowired
    private OperatorReportService operatorReportService;

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public Result createCooperation(User user, CooperationDTO cooperationDTO) {
        Result result = new Result();
        if (null != user) {
            // 未认证不允许创建
            if (!"normal".equals(user.getUserState())) {
                logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + ", 创建合作社失败！");
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg("您还没有认证，认证通过后再来创建合作社吧！");
                return result;
            }
            // 有合作社的不允许创建
            if (proprieterRole(user)) {
                logger.error("您已经创建过合作社，请先解散后再重新创建！");
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg("您已经创建过合作社，请先解散后再重新创建！");
                return result;
            }
            if (null != cooperationDTO) {
                // 默认社长不参加小队
                user.setIdTeam(CooperationServiceImpl.NONE_VALUE);
                if (null != cooperationDTO.getAvatar()) {
                    try {
                        String avatar = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.COOPERATION_PICTURE, cooperationDTO.getAvatar());
                        user.setAvatar(avatar);
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error("用户头像保存失败！");
                    }
                }
                //设置关联关系
                Cooperation cooperation = new Cooperation();
                cooperation.setIdUser(user.getIdUser());
                cooperation.setCooperationName(cooperationDTO.getCooperationName());
                cooperation.setCooperationState(CooperationStateEnum.NORMOL.getState());
                cooperation.setCooperationType(CooperationStateEnum.COMMON.getState());
                cooperation.setDescription("id为" + user.getIdUser() + "的用户成功创建了合作社。");
                cooperation.setRegistTime(new Date());
                Address address = new Address();
                if (null != cooperationDTO.getAddressDTO()) {
                    if (StringUtils.isNotBlank(cooperationDTO.getAddressDTO().getProvince())) {
                        address.setProvince(cooperationDTO.getAddressDTO().getProvince());
                    }
                    if (StringUtils.isNotBlank(cooperationDTO.getAddressDTO().getCity())) {
                        address.setCity(cooperationDTO.getAddressDTO().getCity());

                    }
                    if (StringUtils.isNotBlank(cooperationDTO.getAddressDTO().getCounty())) {
                        address.setCounty(cooperationDTO.getAddressDTO().getCounty());
                    }
                    if (StringUtils.isNotBlank(cooperationDTO.getAddressDTO().getTown())) {
                        address.setTown(cooperationDTO.getAddressDTO().getTown());
                    }
                    if (null != cooperationDTO.getAddressDTO().getLatitude()) {
                        address.setLatitude(cooperationDTO.getAddressDTO().getLatitude());
                    }
                    if (null != cooperationDTO.getAddressDTO().getLongitude()) {
                        address.setLongitude(cooperationDTO.getAddressDTO().getLongitude());
                    }
                    if (StringUtils.isNotBlank(cooperationDTO.getAddressDTO().getDetail())) {
                        address.setDetail(cooperationDTO.getAddressDTO().getDetail());
                    }
                    address.setCreateDate(new Date());
                    address.setUpdateDate(new Date());
                }
                Address a = addressRepository.save(address);
                cooperation.setIdAddress(a.getIdAddress());
                cooperation.setSno(CooperationServiceImpl.INIT_VALUE);
                Cooperation c = cooperationRepository.save(cooperation);
                user.setIdCooperation(c.getIdCooperation());
//                userRepository.save(user);
                // 实时更新当前session用户
                userService.changeCooperationRelation(user);
                CooperationInfo cooperationInfo = new CooperationInfo();
                cooperationInfo.setIdCooperation(c.getIdCooperation());
                cooperationInfo.setBusiness_license(cooperationDTO.getBusiness_license());
                if (null != cooperationDTO.getLicense_pic()) {
                    try {
                        String licensePic = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.COOPERATION_PICTURE, cooperationDTO.getLicense_pic());
                        cooperationInfo.setLicense_pic(licensePic);
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error("合作社营业执照图片保存失败！");
                    }
                }
                cooperationInfo.setHistoryOrderNum(CooperationServiceImpl.INIT_VALUE);
                CooperationInfo coopeInfo = cooperationInfoRepository.save(cooperationInfo);
                // 创建默认的小队
                Team team = new Team();
                team.setIdCooperation(c.getIdCooperation());
                team.setTeamName(CooperationServiceImpl.INIT_TEAM_NAME);
                teamRepository.save(team);
                // 同步日志变更信息
                saveCooperationChangeLog(c.getIdCooperation(), CooperationStateEnum.COMMON.getState(), user.getIdUser(), "id为" + user.getIdUser() + "的用户成功创建了合作社。");
                logger.info("id为" + user.getIdUser() + "的用户成功创建了合作社。");
                // 返回结果集
                result.setCode(ResultEnum.SUCCESS.getCode());
                result.setMsg(ResultEnum.SUCCESS.getMessage());
                CooperationDTO dto = new CooperationDTO();
                dto.setCooperationNum(StringUtil.prefixStr(c.getIdCooperation().toString(), 8, "0"));
                dto.setCooperationName(c.getCooperationName());
                // 设置地址
                AddressDTO addressDTO = new AddressDTO();
                addressDTO.setProvince(a.getProvince());
                addressDTO.setCity(a.getCity());
                addressDTO.setCounty(a.getCounty());
                addressDTO.setTown(a.getTown());
                addressDTO.setDetail(a.getDetail());
                addressDTO.setLatitude(a.getLatitude());
                addressDTO.setLongitude(a.getLongitude());
                dto.setAddressDTO(addressDTO);
                dto.setBusiness_license(coopeInfo.getBusiness_license());
                dto.setLicense_url(coopeInfo.getLicense_pic());
                result.setData(dto);
            }
        } else {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "【User对象为null】, 合作社创建失败！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 合作社创建失败！");
        }
        return result;
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public Result joinCooperation(User user, String cooperationNum) {
        Result result = new Result();
        if (null != user) {
            if (!"normal".equals(user.getUserState())) {
                logger.error("您还没有认证，认证通过后再加入合作社吧！");
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg("您还没有认证，认证通过后再加入合作社吧！");
                return result;
            }
            if (null != user.getIdCooperation()) {
                logger.error("您已经是合作社成员，请先退出后再申请加入！");
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg("您已经是合作社成员，请先退出后再申请加入！");
                return result;
            }
            if (StringUtils.isNotBlank(cooperationNum)) {
                Integer cooperationId = Integer.valueOf(cooperationNum);
                Cooperation cooperation = cooperationRepository.getOneByIdCooperationAndCooperationState(cooperationId, CooperationStateEnum.NORMOL.getState());
                if (null != cooperation) {
                    if (null != inviteRelationShipRepository.getOneByIdCooperationAndIdInviterAndInviterState(cooperation.getIdCooperation(), user.getIdUser(), CooperationStateEnum.APPLY.getState())) {
                        logger.error("您已经提交了加入合作社的申请，请勿重复提交！");
                        result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                        result.setMsg("您已经提交了加入合作社的申请，请勿重复提交！");
                        return result;
                    }
                    InviteRelationShip inviteRelationShip = inviteRelationShipRepository.getOneByIdCooperationAndIdInviterAndInviterState(cooperation.getIdCooperation(), user.getIdUser(), CooperationStateEnum.REFUSED.getState());
                    // 如果之前有拒绝过的直接在原来的关系表中更新（确保只有一个有效）
                    if (null != inviteRelationShip) {
                        inviteRelationShip.setInviterState(CooperationStateEnum.APPLY.getState());
                        inviteRelationShip.setRemark("id为" + user.getIdUser() + "的用户申请加入合作社。");
                        inviteRelationShip.setCreateTime(new Date());
                        inviteRelationShipRepository.save(inviteRelationShip);
                    } else {
                        InviteRelationShip relationShip = new InviteRelationShip();
                        relationShip.setIdCooperation(cooperationId);
                        relationShip.setIdInviter(user.getIdUser());
                        relationShip.setIdProprieter(cooperation.getIdUser());
                        relationShip.setInviterState(CooperationStateEnum.APPLY.getState());
                        relationShip.setRemark("id为" + user.getIdUser() + "的用户申请加入合作社。");
                        relationShip.setCreateTime(new Date());
                        inviteRelationShipRepository.save(relationShip);
                    }
                    // 同步日志信息
                    saveCooperationChangeLog(cooperationId, CooperationStateEnum.COMMON.getState(), user.getIdUser(), "id为" + user.getIdUser() + "的用户申请加入合作社。");
                    logger.info("id为" + user.getIdUser() + "的用户申请加入合作社。");
                    // 给社长发送推送消息
                    User proprieter = findProprieterById(cooperationId);
                    List<User> users = new ArrayList<>();
                    users.add(proprieter);
                    String msg = MSG_PREFIX + "用户" + user.getNickname() + "申请加入您的合作社，赶快去看看吧!";
                    PushResult pushResult = Jdpush.pushMessageUtil(msg, users);
                    if (null != pushResult && pushResult.isResultOK()) {
                        logger.info("社员申请加入合作社的消息推送成功。");
                    } else {
                        logger.error("社员申请加入合作社的消息推送失败！");
                    }
                    // 返回结果集
                    result.setCode(ResultEnum.SUCCESS.getCode());
                    result.setMsg("加入合作社的申请发送" + ResultEnum.SUCCESS.getMessage() + ", 请耐心等待社长批准。");
                    CooperationDTO dto = new CooperationDTO();
                    dto.setCooperationNum(cooperationNum);
                    result.setData(dto);
                } else {
                    logger.error(ResultEnum.PARAM_ERROR.getMessage() + "【参数：合作社编号】, 查询合作社的信息为空或者合作社的状态为非正常状态！");
                    result.setCode(ResultEnum.PARAM_ERROR.getCode());
                    result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 查询合作社的信息为空或者合作社的状态为非正常状态！");
                }
            }

        } else {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "【User对象为null】, 加入合作社失败！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 加入合作社失败！");
        }
        return result;
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public Result queryCooperationByMobile(String mobile) {
        Result result = new Result();
        if (StringUtils.isNotBlank(mobile)) {
            User user = userRepository.findByMobile(mobile);
            if (null != user) {
                if (proprieterRole(user)) {
                    CooperationInfoDTO cooperation = getCooperationMembers(user.getIdCooperation(), DateUtils.getDate(), 1);
                    if (null != cooperation) {
                        // 返回结果集
                        result.setCode(ResultEnum.SUCCESS.getCode());
                        result.setMsg("查询合作社" + ResultEnum.SUCCESS.getMessage() + "。");
                        result.setData(cooperation);
                    }
                } else {
                    logger.error("查询的用户下面并没有合作社，请核实！");
                    result.setCode(ResultEnum.NOT_EXIST.getCode());
                    result.setMsg("查询的用户下面并没有合作社，请核实！");
                    return result;
                }
            } else {
                logger.error("手机号无法查询到用户, 合作社信息查询失败！");
                result.setCode(ResultEnum.PARAM_ERROR.getCode());
                result.setMsg("手机号无法查询到用户, 合作社信息查询失败！");
            }
        } else {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "【参数mobile为null】, 合作社查询失败！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 合作社查询失败！");
        }

        return result;
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public Result dissolveCooperation(User user, String cooperationNum) {
        Result result = new Result();
        if (null != user) {
            // 只有社长才能解散合作社
            if (proprieterRole(user)) {
                if (StringUtils.isNotBlank(cooperationNum)) {
                    if (Integer.valueOf(cooperationNum) == user.getIdCooperation()) {
                        // 合作社有未完成的订单时，不允许解散合作社
                        List<User> members = userRepository.findAllByIdCooperationAndIdTeamIsNotAndUserStateNot(user.getIdCooperation(), CooperationServiceImpl.NONE_VALUE, CooperationStateEnum.DELETED.getState());
                        if (null != members && members.size() > 0) {
                            for (User member : members) {
                                List<UserOrder> userOrders = orderService.getUnfinishedCooperativeOrder(member);
                                if (null != userOrders && userOrders.size() > 0) {
                                    logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + ", 合作社还有未完成的订单！");
                                    result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                                    result.setMsg("合作社还有未完成的订单，不允许解散合作社！");
                                    return result;
                                }
                            }
                        }
                        // 合作社设为失效的状态
                        cooperationRepository.updateInfo(CooperationStateEnum.FAILURE.getState(), "社长成功解散了合作社。", new Date(), user.getIdCooperation());
                        // 同步日志信息
                        saveCooperationChangeLog(user.getIdCooperation(), CooperationStateEnum.COMMON.getState(), user.getIdUser(), "id为" + user.getIdUser() + "的用户解散了合作社。");
                        logger.info("id为" + user.getIdUser() + "的用户解散了合作社。");
                        // 将合作社小队的信息删除-暂不做删除处理
                        /*List<Team> teams = teamRepository.getTeamsByIdCooperation(user.getIdCooperation());
                        if(null != teams && teams.size() > 0){
                            for (Team t: teams) {
                                teamRepository.delete(t);
                            }
                        }*/
                        // 用户和合作社解绑
                        List<User> users = userRepository.findAllByIdCooperation(user.getIdCooperation());
                        if (null != users && users.size() > 0) {
                            // 该成员下到合作社内部的订单处理
                            orderService.cancleCooperationOrder(users);
                            for (User u : users) {
                                u.setIdCooperation(null);
                                u.setIdTeam(null);
                                // 如果是社长本人，就实时更新当前session
                                if (user.getIdUser().toString().equals(u.getIdUser().toString())) {
                                    userService.changeCooperationRelation(u);
                                } else {
                                    //如果不是本人就更新数据库
                                    userRepository.save(u);
                                }
                            }
                        }
                        // 给所有的社员推送消息
                        List<User> memberList = members(user.getIdCooperation());
                        String msg = MSG_UPDATE_PREFIX + "社长解散了合作社，赶快去加入其它的合作社吧!";
                        PushResult pushResult = Jdpush.pushMessageUtil(msg, memberList);
                        if (null != pushResult && pushResult.isResultOK()) {
                            logger.info("社长解散了合作社的消息推送成功。");
                        } else {
                            logger.error("社长解散了合作社的消息推送失败！");
                        }
                        // 返回结果集
                        result.setCode(ResultEnum.SUCCESS.getCode());
                        result.setMsg("解散合作社" + ResultEnum.SUCCESS.getMessage() + "。");
                    }
                }

            } else {
                logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + ",只有社长才能解散合作社！");
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ",只有社长才能解散合作社！");
                return result;
            }
        } else {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "【User对象为null】, 请登陆后再试试吧！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 当前用户为null, 请登陆后再试试吧！！");
        }
        return result;
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public Result approve(User user, MembersDTO member) {
        Result result = new Result();
        if (null != user) {
            // 只有社长才能批准加入合作社
            if (proprieterRole(user)) {
                if (null != member) {
                    if (StringUtils.isNotBlank(member.getInviterNum())) {
                        Long idInviter = Long.valueOf(member.getInviterNum());
                        User u = userRepository.findByIdUser(idInviter);
                        // 只有提交加入合作社申请的成员才能被批准
                        InviteRelationShip relationShip = inviteRelationShipRepository.getOneByIdCooperationAndIdInviterAndInviterState(user.getIdCooperation(), idInviter, CooperationStateEnum.APPLY.getState());
                        if (null != relationShip) {
                            if (null != relationShip.getIdTeam()) {
                                logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + ", 该成员已经加入了合作社小队，请勿重复添加！");
                                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                                result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 该用户已经加入了合作社小队，请勿重复添加！");
                                return result;
                            }
                            // 批准成员加入时全部加入默认的小队/角色为队员
                            Team t = teamRepository.getTeamByIdCooperationAndTeamName(user.getIdCooperation(), CooperationServiceImpl.INIT_TEAM_NAME);
                            //关联已有的小队
                            relationShip.setIdTeam(t.getIdTeam());
                            if (null != u) {
                                if (null != relationShip.getIdCooperation()) {
                                    //申请人和合作社、小队进行绑定
                                    bind(u, t.getIdTeam(), relationShip.getIdCooperation());
                                }
                            }
                            /*if(StringUtils.isNotBlank(member.getTeamName())){
                                Team t = teamRepository.getTeamByIdCooperationAndTeamName(user.getIdCooperation(), member.getTeamName());
                                // 通过参数小队的名称查不出小队，那就是新的小队啦
                                if(null == t){
                                    //创建新的小队
                                    Team team = createTeam(user, member.getTeamName());
                                    if(null != team){
                                        relationShip.setIdTeam(team.getIdTeam());
                                        if(null != u){
                                            if(null != relationShip.getIdCooperation()){
                                                //申请人和合作社进行绑定
                                                bind(u, team.getIdTeam(), relationShip.getIdCooperation());
                                            }
                                        }
                                        //如果是队长，同步关联队长信息
                                        if(member.isCaptain()){
                                            relationShip.setInviterRole(CooperationStateEnum.CAPTAIN.getState());
                                            team.setIdUser(idInviter);
                                            teamRepository.save(team);
                                        }else{
                                            relationShip.setInviterRole(CooperationStateEnum.MEMBER.getState());
                                        }
                                    }

                                }else{
                                    //关联已有的小队
                                    relationShip.setIdTeam(t.getIdTeam());
                                    if(null != u){
                                        if(null != relationShip.getIdCooperation()){
                                            //申请人和合作社进行绑定
                                            bind(u, t.getIdTeam(), relationShip.getIdCooperation());
                                        }
                                    }
                                    //如果是队长，并且该小队还没有指定队长，同步关联队长信息
                                    if(member.isCaptain()){
                                        if(null == t.getIdUser()){
                                            relationShip.setInviterRole(CooperationStateEnum.CAPTAIN.getState());
                                            t.setIdUser(idInviter);
                                            teamRepository.save(t);
                                        }else{
                                            throw new PermissionErrorException("该小队已经有队长了，请勿重复指定！");
                                        }
                                    }else{
                                        relationShip.setInviterRole(CooperationStateEnum.MEMBER.getState());
                                    }
                                }
                            }
                            else {
                                logger.error(ResultEnum.PARAM_ERROR.getMsg()+", 请给合作社新成员指派小队！");
                                result.setCode(ResultEnum.PARAM_ERROR.getCode());
                                result.setMsg(ResultEnum.PARAM_ERROR.getMsg()+", 请给合作社新成员指派小队！");
                                return result;
                            }*/
                            relationShip.setInviterRole(CooperationStateEnum.MEMBER.getState());
                            relationShip.setInviterState(CooperationStateEnum.PERMITTED.getState());
                            relationShip.setPermitTime(new Date());
                            relationShip.setUpdateTime(new Date());
                            relationShip.setRemark("社长批准了" + u.getNickname() + "加入合作社。");
                            inviteRelationShipRepository.save(relationShip);
                            // 同步合作社日志变更信息
                            saveCooperationChangeLog(user.getIdCooperation(), CooperationStateEnum.COMMON.getState(), user.getIdUser(), "社长批准了id为" + idInviter + "的用户加入了合作社。");
                            // 发送推送消息给加入申请者
                            List<User> users = new ArrayList<>();
                            users.add(u);
                            String msg = MSG_UPDATE_PREFIX + "社长批准了您的加入申请, 请赶快去看看吧。";
                            PushResult pushResult = Jdpush.pushMessageUtil(msg, users);
                            if (pushResult != null && pushResult.isResultOK()) {
                                logger.info("批准加入合作社申请的消息推送成功。");
                            } else {
                                logger.error("批准加入合作社申请的消息推送失败！");
                            }
                            // 返回结果集
                            MembersDTO m = new MembersDTO();
                            m.setInviterNum(member.getInviterNum());
                            result.setCode(ResultEnum.SUCCESS.getCode());
                            result.setMsg(ResultEnum.SUCCESS.getMessage() + ": 社长批准了" + u.getNickname() + "加入合作社。");
                            result.setData(m);
                        } else {
                            logger.error(ResultEnum.PARAM_ERROR.getMessage() + ", 该成员还没有申请加入合作社！");
                            result.setCode(ResultEnum.PARAM_ERROR.getCode());
                            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 该成员还没有申请加入合作社！");
                            return result;
                        }
                    }
                }
            } else {
                logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有社长有权限批准成员加入合作社！");
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有社长有权限批准成员加入合作社！");
                return result;
            }
        } else {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "【User对象为null】, 批准加入合作社失败！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 批准加入合作社失败！");
        }
        return result;
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public Result exit(User user) {
        Result result = new Result();
        if (null != user) {
            boolean flag = orderOperatorService.isCooperationOrderFinished(user);
            if (flag) {
                logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + ", 请先处理未完成的合作社订单！");
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 请先处理未完成的合作社订单！");
                return result;
            }
        }
        //普通成员退出合作社
        if (memberRole(user)) {
            // 该成员下到合作社内部的订单处理
            orderService.cancleCooperationOrder(user);
            // 给社长发送推送消息
            User proprieter = findProprieterById(user.getIdCooperation());
            if (null != proprieter) {
                List<User> users = new ArrayList<>();
                users.add(proprieter);
                String msg = MSG_PREFIX + "社员" + user.getNickname() + "退出了合作社，赶快去看看吧!";
                PushResult pushResult = Jdpush.pushMessageUtil(msg, users);
                if (null != pushResult && pushResult.isResultOK()) {
                    logger.info("社员退出合作社的消息推送成功。");
                } else {
                    logger.error("社员退出合作社的消息推送失败！");
                }
            }
            //队员退出合作社
            User member =  unbind(user);
            // 实时更新当前session用户
            userService.changeCooperationRelation(member);
            // 返回结果集
            result.setCode(ResultEnum.SUCCESS.getCode());
            result.setMsg("退出合作社" + ResultEnum.SUCCESS.getMessage() + "。");
        }else{
            throw new PermissionException("");
        }
        return result;
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public Result appointRole(User user, MembersDTO members) {
        Result result = new Result();
        InviteRelationShip relationShip = appointTeamRole(user, members);
        if (null != relationShip) {
            User u = userRepository.findByMobile(members.getMobile());
            //成功后发送推送消息给关联用户
            List<User> users = new ArrayList<>();
            users.add(u);
            String msg = MSG_UPDATE_PREFIX + "社长给您指派了新的角色, 请赶快去看看吧。";
            PushResult pushResult = Jdpush.pushMessageUtil(msg, users);
            if (pushResult != null && pushResult.isResultOK()) {
                logger.info("社长给社员指派新角色的消息推送成功。");
            } else {
                logger.error("社长给社员指派新角色的消息推送失败！");
            }
            result.setCode(ResultEnum.SUCCESS.getCode());
            result.setMsg(ResultEnum.SUCCESS.getMessage());
            result.setData(relationShip);
            // 同步合作社日志变更信息
            saveCooperationChangeLog(user.getIdCooperation(), CooperationStateEnum.COMMON.getState(), user.getIdUser(), "社长给id为" + relationShip.getIdInviter() + "的新成员分配了角色。");
        } else {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，合作社新成员分配角色失败！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 合作社新成员分配角色失败！");
        }
        return result;
    }

    /**
     * 根据主键查询合作社
     *
     * @param idCooperation 合作社主键
     * @return 合作社详情与地址等基本信息
     */
    @Override
    @Cacheable(cacheNames = "cacheValues")
    public CooperationInfoDTO findById(Integer idCooperation) {
        if (null != idCooperation) {
            CooperationInfoDTO cooperation = getCooperationMembers(idCooperation, DateUtils.getDate(), 1);
            if (null != cooperation) {
                return cooperation;
            }
        }
        // TODO
        return null;
    }

    @Override
    public Cooperation getById(Integer idCooperation) {
        if (null != idCooperation) {
            Cooperation cooperation = cooperationRepository.getOneByIdCooperationAndCooperationStateNot(idCooperation, CooperationStateEnum.FAILURE.getState());
            if (null != cooperation) {
                return cooperation;
            }
        }
        return null;
    }

    @Override
    public Result listMembers(User user, String cooperationNum) {
        Result result = new Result();
        CooperationInfoDTO cooperationInfoDTO = new CooperationInfoDTO();
        if (StringUtils.isNotBlank(cooperationNum)) {
            Integer cooperationId = Integer.valueOf(cooperationNum);
            if (null != cooperationId) {
                CopyOnWriteArrayList<User> members = userRepository.findAllByIdCooperationAndIdTeamIsNotAndUserStateNot(cooperationId, CooperationServiceImpl.NONE_VALUE, UserStateEnum.DELETED.getState());
//                Map<String, List<MembersDTO>> membersMap = sortMemberList(cooperationId, members, DateUtils.getDate(), 1);
                Map<String, List<User>> userMap = membersSortList(members);
                List<MembersDTO> mapToList = sortMapToList(cooperationId, userMap, DateUtils.getDate(), 1);
                /*List<MembersDTO> membersMap = members(cooperationId, DateUtils.getDate(), 1);
                List<MembersDTO> memberList = new ArrayList<>();
                for (Map.Entry entry : membersMap.entrySet()) {
                    String key = entry.getKey().toString();
                    List<MembersDTO> list= (List) entry.getValue();
                    for (MembersDTO member : list) {
                        memberList.add(member);
                    }
                }*/
                cooperationInfoDTO.setCooperationNum(cooperationNum);
                cooperationInfoDTO.setMembers(mapToList);
                if (null != mapToList) {
                    result.setCode(ResultEnum.SUCCESS.getCode());
                    result.setMsg(ResultEnum.SUCCESS.getMessage());
                } else {
                    logger.info("该合作社下的成员信息为空。");
                    result.setCode(ResultEnum.NOT_EXIST.getCode());
                    result.setMsg("该合作社下的成员信息为空。");
                }
                result.setData(cooperationInfoDTO);
            }

        } else {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，参数不能为空！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 参数不能为空！");
        }
        return result;
    }

    @Override
    public List<User> members(Integer idCooperation) {
        List<User> members = new ArrayList<>();
        if (null != idCooperation) {
            members = userRepository.findAllByIdCooperationAndUserStateNot(idCooperation, UserStateEnum.DELETED.getState());
        }
        return members;
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public Result listOperators(User user, String cooperationNum, String startTime, Integer days) {
        Result result = new Result();
        CooperationInfoDTO cooperationInfoDTO = new CooperationInfoDTO();
        // 只有社长才能查询合作社信息
        if (null != user) {
            if (!proprieterRole(user)) {
                logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有社长有权限查询合作社信息！");
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有社长有权限查询合作社信息！");
                return result;
            }
        }
        if (StringUtils.isNotBlank(cooperationNum)) {
            Integer cooperationId = Integer.valueOf(cooperationNum);
            if (null != cooperationId) {
                //查询合作社中所有的农机手
                CopyOnWriteArrayList<User> operatorList = userRepository.findAllByIdCooperationAndIsOperatorAndIdTeamIsNotAndUserStateNot(cooperationId, true, CooperationServiceImpl.NONE_VALUE, UserStateEnum.DELETED.getState());
                Map<String, List<MembersDTO>> members = listTeamMembers(cooperationId, operatorList, startTime, days);
                Cooperation cooperation = cooperationRepository.getOneByIdCooperation(cooperationId);
                if (null != cooperation) {
                    if (null != cooperation.getCooperationName()) {
                        cooperationInfoDTO.setCooperationName(cooperation.getCooperationName());
                    }
                }
                cooperationInfoDTO.setCooperationNum(cooperationNum);
                cooperationInfoDTO.setTeamOprators(members);
                if (null != members && members.size() > 0) {
                    logger.info("合作社小队成员查询成功。");
                    result.setCode(ResultEnum.SUCCESS.getCode());
                    result.setMsg(ResultEnum.SUCCESS.getMessage());
                } else {
                    logger.info("该合作社下的小队成员信息为空。");
                    result.setCode(ResultEnum.NOT_EXIST.getCode());
                    result.setMsg("该合作社下的小队成员信息为空。");
                }
                result.setData(cooperationInfoDTO);
            }
        } else {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，参数不能为空！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 参数不能为空！");
        }
        return result;
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public Map<String, List<MembersDTO>> teamOperators(String cooperationNum, String startTime, Integer days) {
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        // 只有社长才能查询合作社信息
        if (null != user) {
            if (!proprieterRole(user)) {
                throw new PermissionException("只有社长才能查询合作社农机手信息！");
            }
        }
        if (StringUtils.isNotBlank(cooperationNum)) {
            Integer cooperationId = Integer.valueOf(cooperationNum);
            if (null != cooperationId) {
                //查询合作社中所有的农机手
                CopyOnWriteArrayList<User> operatorList = userRepository.findAllByIdCooperationAndIsOperatorAndIdTeamIsNotAndUserStateNot(cooperationId, true, CooperationServiceImpl.NONE_VALUE, UserStateEnum.DELETED.getState());
                Map<String, List<MembersDTO>> teamOperators = listTeamMembers(cooperationId, operatorList, startTime, days);
                if (null != teamOperators) {
                    if (teamOperators.size() > 0) {
                        logger.info("查询合作社所有小队中农机手成员成功。");
                        return teamOperators;
                    } else {
                        logger.info("该合作社下所有小队中没有农机手成员。");
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Result listNewMembers(User user) {
        Result result = new Result();
        CooperationInfoDTO cooperationInfoDTO = new CooperationInfoDTO();
        if (null != user) {
            //只有社长才能查询新成员
            if (proprieterRole(user)) {
                //查询合作社中所有申请加入的新成员
                List<InviteRelationShip> inviteRelationShips = inviteRelationShipRepository.getInviteRelationShipsByIdCooperationAndInviterState(user.getIdCooperation(), CooperationStateEnum.APPLY.getState());
                List<User> newMembers = new ArrayList<>();
                if (null != inviteRelationShips && inviteRelationShips.size() > 0) {
                    for (InviteRelationShip relations : inviteRelationShips) {
                        if (null != relations.getIdInviter()) {
                            User u = userRepository.findByIdUser(relations.getIdInviter());
                            if (null != u) {
                                if (!UserStateEnum.DELETED.getState().equals(u.getUserState())) {
                                    newMembers.add(u);
                                }
                            }
                        }
                    }
                }
                // 准备新成员列表的集合
                List<MembersDTO> members = new ArrayList<>();
                if (null != newMembers && newMembers.size() > 0) {
                    for (User u : newMembers) {
                        if (null != u) {
                            MembersDTO membersDTO = dtoFactory(user.getIdCooperation(), u, DateUtils.getDate(), 1);
                            if (null != membersDTO) {
                                members.add(membersDTO);
                            }
                        }
                    }
                }
                cooperationInfoDTO.setNewMembers(members);
                Cooperation cooperation = cooperationRepository.getOneByIdCooperation(user.getIdCooperation());
                if (null != cooperation) {
                    if (null != cooperation.getCooperationName()) {
                        cooperationInfoDTO.setCooperationName(cooperation.getCooperationName());
                    }
                }
                cooperationInfoDTO.setCooperationNum(StringUtil.prefixStr(user.getIdCooperation().toString(), 8, "0"));

                if (null != members && members.size() > 0) {
                    logger.info("合作社申请加入的新成员查询成功。");
                    result.setCode(ResultEnum.SUCCESS.getCode());
                    result.setMsg(ResultEnum.SUCCESS.getMessage());
                } else {
                    logger.info("该合作社下的新成员信息为空。");
                    result.setCode(ResultEnum.NOT_EXIST.getCode());
                    result.setMsg("该合作社下的新成员信息为空。");
                }
                result.setData(cooperationInfoDTO);
            }
        } else {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，参数不能为空！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 参数不能为空！");
        }
        return result;
    }

    @Override
    public Result myCooperation(User user) {
        Result result = new Result();
        if (null != user) {
            if (null != user.getIdCooperation()) {
                CooperationInfoDTO cooperation = getCooperationMembers(user.getIdCooperation(), DateUtils.getDate(), 1);
                if (null != cooperation) {
                    // 返回结果集
                    result.setCode(ResultEnum.SUCCESS.getCode());
                    result.setMsg("查询合作社" + ResultEnum.SUCCESS.getMessage() + "。");
                    result.setData(cooperation);
                } else {
                    logger.info(ResultEnum.NOT_EXIST.getMessage() + ", 该用户没有关联的合作社！");
                    result.setCode(ResultEnum.NOT_EXIST.getCode());
                    result.setMsg(ResultEnum.NOT_EXIST.getMessage() + ", 该用户没有关联的合作社！");
                }
            } else {
                result.setCode(ResultEnum.NOT_EXIST.getCode());
                result.setMsg(ResultEnum.NOT_EXIST.getMessage() + ", 该用户没有关联的合作社！");
            }
        } else {
            throw new LoginException("您还没有登陆，请登陆后再操作！");
        }
        return result;
    }

    @Override
    public Result checkProprieterRole(User user) {
        Result result = new Result();
        if (null != user) {
            boolean flag = proprieterRole(user);
            result.setData(flag);
        } else {
            result.setData(false);
        }
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMsg(ResultEnum.SUCCESS.getMessage());
        return result;
    }

    @Override
    public Result checkCaptainRole(User user) {
        Result result = new Result();
        if (null != user) {
            boolean flag = captainRole(user);
            result.setData(flag);
        } else {
            result.setData(false);
        }
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMsg(ResultEnum.SUCCESS.getMessage());
        return result;
    }

    @Override
    public boolean proprieter(User user) {
        if (null != user) {
            return proprieterRole(user);
        }
        return false;
    }

    @Override
    public boolean captain(User user) {
        if (null != user) {
            return captainRole(user);
        }
        return false;
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public User findProprieterById(Integer idCooperation) {
        if (null != idCooperation) {
            Cooperation cooperation = cooperationRepository.getOneByIdCooperationAndCooperationStateNot(idCooperation, CooperationStateEnum.FAILURE.getState());
            if (null != cooperation) {
                if (null != cooperation.getIdUser()) {
                    User proprieter = userRepository.findByIdUser(cooperation.getIdUser());
                    return proprieter;
                }
            }
        } else {
            logger.error("参数：idCooperation为null，社长信息无法查询！");
        }
        return null;
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public User findProprieterByUser(User user) {
        if (null != user) {
            if (null != user.getIdCooperation()) {
                return findProprieterById(user.getIdCooperation());
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public Result updateCooperation(User user, CooperationDTO cooperation) {
        Result result = new Result();
        if (null == user) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，【User对象为null】, 请登陆后再试试吧！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 【User对象为null】, 请登陆后再试试吧！");
            return result;
        }
        // 只有社长才能修改合作社
        if (proprieterRole(user)) {
            CooperationDTO dto = update(user, cooperation);
            logger.info(ResultEnum.SUCCESS.getMessage() + "，社长更新合作社信息成功。");
            result.setCode(ResultEnum.SUCCESS.getCode());
            result.setMsg(ResultEnum.SUCCESS.getMessage() + ", 社长更新合作社信息成功。");
            result.setData(dto);
        } else {
            logger.info(ResultEnum.PERMISSION_ERROR.getMessage() + "，只有社长才能更新合作社信息！");
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有社长才能更新合作社信息！");
        }
        return result;
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public Result teamlist(User user) {
        Result result = new Result();
        if (null == user) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，【User对象为null】, 请登陆后再试试吧！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 【User对象为null】, 请登陆后再试试吧！");
            return result;
        }
        // 只有社长才能查看小队列表
        if (proprieterRole(user)) {
            List<Team> teamList = teamRepository.getTeamsByIdCooperation(user.getIdCooperation());
            if (null != teamList && teamList.size() > 0) {
                List<TeamDTO> teamNameList = new ArrayList<>();
                for (Team team : teamList) {
                    if (null != team) {
                        if (StringUtils.isNotBlank(team.getTeamName())) {
                            TeamDTO dto = new TeamDTO();
                            dto.setTeamName(team.getTeamName());
                            teamNameList.add(dto);
                        }
                    }
                }
                logger.info(ResultEnum.SUCCESS.getMessage() + "，社长查询小队信息成功。");
                result.setCode(ResultEnum.SUCCESS.getCode());
                result.setMsg(ResultEnum.SUCCESS.getMessage() + ", 社长查询小队信息成功。");
                result.setData(teamNameList);
            } else {
                logger.error(ResultEnum.NOT_EXIST.getMessage() + "，合作社小队信息为空。");
                result.setCode(ResultEnum.NOT_EXIST.getCode());
                result.setMsg(ResultEnum.NOT_EXIST.getMessage() + ", 合作社小队信息为空。");
            }
        } else {
            logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + "，只有社长才能查询小队信息！");
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有社长才能查询小队信息！");
        }
        return result;
    }

    @Override
    public Result findMachineryInfo(User user, String mobile) {
        Result result = new Result();
        if (null == user) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，【User对象为null】, 请登陆后再试试吧！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 【User对象为null】, 请登陆后再试试吧！");
            return result;
        }
        if (StringUtils.isBlank(mobile)) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + ", 参数mobile不能为空！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 参数mobile不能为空！");
            return result;
        }
        User member = userRepository.findByMobile(mobile);
        // 只有社长才能查看队员信息
        if (proprieterRole(user)) {
            if (null != member) {
                if (null != member.getIdCooperation()) {
                    if (member.getIdCooperation().toString().equals(user.getIdCooperation().toString())) {
                        List<Machinery> machineryInfo = machineryService.list(member.getIdUser());
                        result.setCode(ResultEnum.SUCCESS.getCode());
                        result.setMsg(ResultEnum.SUCCESS.getMessage());
                        result.setData(machineryInfo);
                    }
                } else {
                    result.setCode(ResultEnum.NOT_EXIST.getCode());
                    result.setMsg(ResultEnum.NOT_EXIST.getMessage() + ", 该用户还不是合作社成员。");
                }
            } else {
                result.setCode(ResultEnum.NOT_EXIST.getCode());
                result.setMsg(ResultEnum.NOT_EXIST.getMessage() + ", 该队员不存在。");
            }
        } else {
            logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + "，只有社长才能查询队员信息！");
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有社长才能查询队员信息！");
        }
        return result;
    }

    @Override
    public Result findFarmInfo(User user, String mobile) {
        Result result = new Result();
        if (null == user) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，【User对象为null】, 请登陆后再试试吧！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 【User对象为null】, 请登陆后再试试吧！");
            return result;
        }
        if (StringUtils.isBlank(mobile)) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + ", 参数mobile不能为空！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 参数mobile不能为空！");
            return result;
        }
        User member = userRepository.findByMobile(mobile);
        // 只有社长才能查看队员信息
        if (proprieterRole(user)) {
            if (null != member) {
                if (null != member.getIdCooperation()) {
                    if (member.getIdCooperation().toString().equals(user.getIdCooperation().toString())) {
                        List<FarmlandDTO> farmland = farmlandService.list(member.getIdUser());
                        result.setCode(ResultEnum.SUCCESS.getCode());
                        result.setMsg(ResultEnum.SUCCESS.getMessage());
                        result.setData(farmland);
                    }
                } else {
                    result.setCode(ResultEnum.NOT_EXIST.getCode());
                    result.setMsg(ResultEnum.NOT_EXIST.getMessage() + ", 该用户还不是合作社成员。");
                }
            } else {
                result.setCode(ResultEnum.NOT_EXIST.getCode());
                result.setMsg(ResultEnum.NOT_EXIST.getMessage() + ", 该队员不存在。");
            }
        } else {
            logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + "，只有社长才能查询队员信息！");
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有社长才能查询队员信息！");
        }
        return result;
    }

    @Override
    public InviteRelationShip findInviterState(User user) {
        InviteRelationShip inviter = new InviteRelationShip();
        if (null != user) {
            if (null != user.getIdCooperation()) {
                inviter = inviteRelationShipRepository.getOneByIdCooperationAndIdInviterAndInviterState(user.getIdCooperation(), user.getIdUser(), CooperationStateEnum.PERMITTED.getState());
            } else {
                throw new NotExistException("该用户还不是合作社的成员！");
            }
        } else {
            throw new ParamErrorException("参数user对象不能为空！");
        }
        return inviter;
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public Result findInviterInfo(User user, String mobile) {
        Result result = new Result();
        if (null == user) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，【User对象为null】, 请登陆后再试试吧！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 【User对象为null】, 请登陆后再试试吧！");
            return result;
        }
        if (StringUtils.isBlank(mobile)) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + ", 参数mobile不能为空！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 参数mobile不能为空！");
            return result;
        }
        User member = userRepository.findByMobile(mobile);
        // 只有社长才能查看申请加入的队员信息
        if (proprieterRole(user)) {
            if (null != member) {
                UserInfoDTO userInfo = new UserInfoDTO();
                userInfo.setNickname(member.getNickname());
                userInfo.setMobile(member.getMobile());
                userInfo.setFarmer(member.getFarmer());
                userInfo.setOperator(member.getOperator());
                userInfo.setUserState(member.getUserState());
                List<FarmlandDTO> farmlands = farmlandService.list(member.getIdUser());
                userInfo.setFarmlands(farmlands);
                List<Machinery> machineryInfo = machineryService.list(member.getIdUser());
                userInfo.setMachinerys(machineryInfo);
                result.setCode(ResultEnum.SUCCESS.getCode());
                result.setMsg(ResultEnum.SUCCESS.getMessage());
                result.setData(userInfo);
            } else {
                result.setCode(ResultEnum.NOT_EXIST.getCode());
                result.setMsg(ResultEnum.NOT_EXIST.getMessage() + ", 该用户不存在。");
            }
        } else {
            logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + "，只有社长才能查询申请加入队员的信息！");
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有社长才能查询申请加入队员的信息！");
        }
        return result;
    }

    @Override
    public Result toCooperationOrder(User user, String mobile) {
        Result result = new Result();
        if (null == user) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，【User对象为null】, 请登陆后再试试吧！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 【User对象为null】, 请登陆后再试试吧！");
            return result;
        }
        if (StringUtils.isBlank(mobile)) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + ", 参数mobile不能为空！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 参数mobile不能为空！");
            return result;
        }
        User member = userRepository.findByMobile(mobile);
        if (null == user.getIdCooperation()) {
            logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + ", 您还不是合作社成员！");
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 您还不是合作社成员！");
            return result;
        }
        if (null != member) {
            if (null == member.getIdCooperation()) {
                logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + ", 查询的用户还不是合作社成员！");
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 查询的用户还不是合作社成员！");
                return result;
            }
            if (member.getIdCooperation().toString().equals(user.getIdCooperation().toString())) {
                List<UserOrder> userOrders = orderService.getToCooperationOrder(member);
                List<OrderDTO> orderDto = orderService.loadDTOs(userOrders);
                result.setCode(ResultEnum.SUCCESS.getCode());
                result.setMsg(ResultEnum.SUCCESS.getMessage());
                result.setData(orderDto);
            } else {
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 非本社成员，无权查看！");
            }

        } else {
            result.setCode(ResultEnum.NOT_EXIST.getCode());
            result.setMsg(ResultEnum.NOT_EXIST.getMessage() + ", 查询的订单不存在！");
        }
        return result;
    }

    @Override
    public Result finishedCooperativeOrder(User user, String date) {
        Result result = new Result();
        if (null == user) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，【User对象为null】, 请登陆后再试试吧！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 【User对象为null】, 请登陆后再试试吧！");
            return result;
        }
        if (StringUtils.isBlank(date)) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，参数date不能为空！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 参数date不能为空！");
            return result;
        }
        User member = userRepository.findByIdUser(user.getIdUser());
        if (null == user.getIdCooperation()) {
            logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + ", 您还不是合作社成员！");
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 您还不是合作社成员！");
            return result;
        }
        if (null != member) {
            if (null == member.getIdCooperation()) {
                logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + ", 查询的用户还不是合作社成员！");
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 查询的用户还不是合作社成员！");
                return result;
            }
            if (member.getIdCooperation().toString().equals(user.getIdCooperation().toString())) {
                if (date.indexOf("/") > 0) {
                    date = date.substring(0, date.indexOf("/"));
                }
                StatisticsDTO statisticsDTO = new StatisticsDTO();
                Map<Integer, OrderStatisticsDTO> maps = orderService.userStatistics(member, date);
                List<OrderStatisticsDTO> orderStatisticsDTOS = new ArrayList<>();
                BigDecimal amount = new BigDecimal(0.00);
                float totalArea = 0.00f;
                Integer orderNum = 0;
                //遍历所有月份的统计信息
                if (null != maps && maps.size() > 0) {
                    for (int i = 1; i <= 12; i++) {
                        OrderStatisticsDTO orderStatisticsDTO = maps.get(i);
                        if (null != orderStatisticsDTO) {
                            if (null != orderStatisticsDTO.getArea()) {
                                totalArea += orderStatisticsDTO.getArea();
                            }
                            if (null != orderStatisticsDTO.getIncome()) {
                                amount = amount.add(orderStatisticsDTO.getIncome());
                            }
                            if (null != orderStatisticsDTO.getOrderNum()) {
                                orderNum += orderStatisticsDTO.getOrderNum();
                            }
                            orderStatisticsDTO.setMonth(i + "月");
                            orderStatisticsDTOS.add(orderStatisticsDTO);
                        }
                    }
                }
                statisticsDTO.setOrderStatisticsDTOS(orderStatisticsDTOS);
                statisticsDTO.setAmount(amount);
                statisticsDTO.setTotalArea(totalArea);
                statisticsDTO.setOrderNum(orderNum);

//                if(null != orderDtos && orderDtos.size() > 0){
//                    BigDecimal amount = new BigDecimal(0.00);
//                    float totalArea = 0.00f;
//                    for (OrderDTO orderDTO: orderDtos) {
//                        //如果是协同作业的订单
//                        if(orderDTO.getCooperative()){
//                            ReportDTO completionReport = operatorReportService.getOperatorReport(orderDTO.getUserOrderNo(), member.getMobile());
//                            if(null != completionReport){
//                                BigDecimal acres = new BigDecimal(completionReport.getAcre());
//                                orderDTO.setIncome(acres.multiply(orderDTO.getPrice()));
//                                totalArea += completionReport.getAcre();
//                            }
//                        }else{
//                            BigDecimal acres = new BigDecimal(orderDTO.getArce());
//                            orderDTO.setIncome(acres.multiply(orderDTO.getPrice()));
//                            totalArea += orderDTO.getArce();
//                        }
//                    }
//                    for (OrderDTO orderDTO: orderDtos) {
//                        if(null != orderDTO.getIncome()){
//                            amount = amount.add(orderDTO.getIncome());
//                        }
//                    }
//                    statisticsDTO.setOrderDTOS(orderDtos);
//                    statisticsDTO.setAmount(amount);
//                    statisticsDTO.setOrderNum(orderDtos.size());
//                    statisticsDTO.setTotalArea(totalArea);
//                }
                result.setCode(ResultEnum.SUCCESS.getCode());
                result.setMsg(ResultEnum.SUCCESS.getMessage());
                result.setData(statisticsDTO);
            } else {
                result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
                result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 非本社成员，无权查看！");
            }
        } else {
            result.setCode(ResultEnum.NOT_EXIST.getCode());
            result.setMsg(ResultEnum.NOT_EXIST.getMessage() + ", 查询的订单不存在！");
        }
        return result;
    }

    @Override
    public Result unfinishedCooperativeOrder(User user, String mobile) {
        Result result = new Result();
        if (null == user) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，【User对象为null】, 请登陆后再试试吧！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 【User对象为null】, 请登陆后再试试吧！");
            return result;
        }
        if (StringUtils.isBlank(mobile)) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + ", 参数mobile不能为空！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 参数mobile不能为空！");
            return result;
        }
        User member = userRepository.findByMobile(mobile);
        //只有社长才能查看社员的未完成订单信息
        if (proprieter(user)) {
            List<UserOrder> userOrders = orderService.getUnfinishedCooperativeOrder(member);
            List<OrderDTO> orderDto = orderService.loadDTOs(userOrders);
            result.setCode(ResultEnum.SUCCESS.getCode());
            result.setMsg(ResultEnum.SUCCESS.getMessage());
            result.setData(orderDto);
        } else {
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有社长才能查看该数据信息！");
        }
        return result;
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public Result statisticsCooperativeOrder(User user, String month) {
        Result result = new Result();
        if (null == user) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，【User对象为null】, 请登陆后再试试吧！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 【User对象为null】, 请登陆后再试试吧！");
            return result;
        }
        if (StringUtils.isBlank(month)) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + ", 参数month不能为空！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 参数month不能为空！");
            return result;
        }
        //只能是合作社成员才能查询统计信息
        if (cooperationRole(user)) {
            List<User> users = userRepository.findAllByIdCooperation(user.getIdCooperation());
            if (null != users && users.size() > 0) {
                StatisticsDTO statisticsDTO = new StatisticsDTO();
                BigDecimal amount = new BigDecimal(0.00);
                float totalArea = 0.00f;
                Integer totalOrders = 0;
                Date date = DateUtils.parseDate(month);
                List<OrderStatisticsDTO> dtoList = orderService.getStatisticalMsg(users, user.getIdCooperation(), date, user);
                if (null != dtoList && dtoList.size() > 0) {
                    for (OrderStatisticsDTO orderStatisticsDTO : dtoList) {
                        if (null != orderStatisticsDTO.getIncome()) {
                            amount = amount.add(orderStatisticsDTO.getIncome());
                        }
                        if (null != orderStatisticsDTO.getArea()) {
                            totalArea += orderStatisticsDTO.getArea();
                        }
                        if (null != orderStatisticsDTO.getOrderNum()) {
                            totalOrders += orderStatisticsDTO.getOrderNum();
                        }
                    }
                }
                statisticsDTO.setTotalArea(totalArea);
                statisticsDTO.setAmount(amount);
                statisticsDTO.setOrderNum(totalOrders);
                statisticsDTO.setOrderStatisticsDTOS(dtoList);
                result.setCode(ResultEnum.SUCCESS.getCode());
                result.setMsg(ResultEnum.SUCCESS.getMessage());
                result.setData(statisticsDTO);
            } else {
                result.setCode(ResultEnum.NOT_EXIST.getCode());
                result.setMsg(ResultEnum.NOT_EXIST.getMessage() + ", 该合作社下的成员为空！");
            }
        } else {
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有合作社成员才能查看合作社的统计信息！");
        }
        return result;
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public Result statisticsCooperativeOrders(User user, String startTime, String endTime) {
        Result result = new Result();
        if (null == user) {
            throw new LoginException("登陆失效，请登陆后再试试吧！");
        }
        if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
            throw new ParamErrorException("参数不能为空！");
        }
        //只能是合作社成员才能查询统计信息
        if (cooperationRole(user)) {
            List<User> users = userRepository.findAllByIdCooperation(user.getIdCooperation());
            if (null != users && users.size() > 0) {
                StatisticsDTO statisticsDTO = new StatisticsDTO();
                BigDecimal amount = new BigDecimal(0.00);
                float totalArea = 0.00f;
                Integer totalOrders = 0;
                Date start = DateUtils.parseDate(startTime);
                Date end = DateUtils.parseDate(endTime);
                if (null == start || null == end) {
                    try {
                        throw new DataFormatException("日期参数格式不正确！");
                    } catch (DataFormatException e) {
                        e.printStackTrace();
                    }
                }
                List<OrderStatisticsDTO> dtoList = orderService.getStatisticalMsg(users, user.getIdCooperation(), start, end, user);
                if (null != dtoList && dtoList.size() > 0) {
                    for (OrderStatisticsDTO orderStatisticsDTO : dtoList) {
                        if (null != orderStatisticsDTO.getIncome()) {
                            amount = amount.add(orderStatisticsDTO.getIncome());
                        }
                        if (null != orderStatisticsDTO.getArea()) {
                            totalArea += orderStatisticsDTO.getArea();
                        }
                        if (null != orderStatisticsDTO.getOrderNum()) {
                            totalOrders += orderStatisticsDTO.getOrderNum();
                        }
                    }
                }
                statisticsDTO.setTotalArea(totalArea);
                statisticsDTO.setAmount(amount);
                statisticsDTO.setOrderNum(totalOrders);
                statisticsDTO.setOrderStatisticsDTOS(dtoList);
                result.setCode(ResultEnum.SUCCESS.getCode());
                result.setMsg(ResultEnum.SUCCESS.getMessage());
                result.setData(statisticsDTO);
            } else {
                result.setCode(ResultEnum.NOT_EXIST.getCode());
                result.setMsg(ResultEnum.NOT_EXIST.getMessage() + ", 该合作社下的成员为空！");
            }
        } else {
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有合作社成员才能查看合作社的统计信息！");
        }
        return result;
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public Result statisticsMemberOrder(User user, String mobile, String startTime, String endTime) {
        Result result = new Result();
        if (null == user) {
            throw new LoginException("登陆失效，请登陆后再试试吧！");
        }
        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
            throw new ParamErrorException("参数不能为空！");
        }
        Date start = DateUtils.parseDate(startTime);
        Date end = DateUtils.parseDate(endTime);
        if (null == start || null == end) {
            try {
                throw new DataFormatException("日期参数格式不正确！");
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
        }
        User member = userRepository.findByMobile(mobile);
        if (null != member) {
            //只能社长才能查询社员的订单信息或者自己查自己的订单信息
            if (proprieterRole(user) || user.getIdUser().toString().trim().equals(member.getIdUser().toString().trim())) {
                if (user.getIdCooperation().toString().equals(member.getIdCooperation().toString())) {
                    List<UserOrder> userOrders = orderService.getUserStatisticalOrder(member, start, end);
                    if (null != userOrders && userOrders.size() > 0) {
                        List<OrderSimpleDTO> dtoList = orderService.loadSimpleDTOs(userOrders);
                        result.setData(dtoList);
                    } else {
                        result.setData(new ArrayList<>());
                    }
                    result.setCode(ResultEnum.SUCCESS.getCode());
                    result.setMsg(ResultEnum.SUCCESS.getMessage());
                } else {
                    throw new PermissionException("该用户还不是本合作社成员！");
                }
            }else{
                throw new PermissionException("用户权限不足！");
            }
        } else {
            throw new NotExistException("");
        }
        return result;
    }

    @Override
    public Team queryTeamInfo(User user) {
        // 确保用户是合作社普通成员
        if (memberRole(user)) {
            if (null != user.getIdTeam()) {
                Team team = teamRepository.getOneByIdTeam(user.getIdTeam());
                return team;
            }
        }
        return null;
    }

    @Override
    public Result queryAvailableInviters(User user, String orderNum) {
        Result result = new Result();
        if (null == user) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + "，【User对象为null】, 请登陆后再试试吧！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 【User对象为null】, 请登陆后再试试吧！");
            return result;
        }
        if (StringUtils.isBlank(orderNum)) {
            logger.error(ResultEnum.PARAM_ERROR.getMessage() + ", 参数orderNum不能为空！");
            result.setCode(ResultEnum.PARAM_ERROR.getCode());
            result.setMsg(ResultEnum.PARAM_ERROR.getMessage() + ", 参数orderNum不能为空！");
            return result;
        }
        // 只有社长才能查看可以邀请的农机手
        if (proprieterRole(user)) {
            List<User> inviters = new ArrayList<>();
            CopyOnWriteArrayList<User> operators = userRepository.findAllByIdCooperationAndIsOperatorAndIdTeamIsNotAndUserStateNot(user.getIdCooperation(), true, CooperationServiceImpl.NONE_VALUE, UserStateEnum.DELETED.getState());
            if (null != operators && operators.size() > 0) {
                for (User inviter : operators) {
                    UserOrder userOrder = orderService.getUserOrder(orderNum);
                    if (null != userOrder) {
                        // 已经加入邀请的农机手
                        OrderOperator orderOperator = orderOperatorService.getOperator(inviter.getIdUser(), userOrder.getIdUserOrder());
                        // 没有该订单农机类型的农机手
                        boolean flag = machineryService.checkSingleUserMachineryType(inviter, userOrder.getMachineryType());
                        // 下单人不允许作业
                        if (null != orderOperator || !flag
                                || userOrder.getCreateBy().toString().trim().equals(inviter.getIdUser().toString().trim())) {
                            operators.remove(inviter);
                        }
                    }
                }
                if (null != operators && operators.size() > 0) {
                    List<MembersDTO> members = new ArrayList<>();
                    for (User inviter : operators) {
                        MembersDTO member = dtoFactory(user.getIdCooperation(), inviter, DateUtils.getDate(), 1);
                        if (null != member) {
                            members.add(member);
                        }
                    }
                    result.setData(members);
                }
                result.setCode(ResultEnum.SUCCESS.getCode());
                result.setMsg(ResultEnum.SUCCESS.getMessage());
            } else {
                result.setCode(ResultEnum.NOT_EXIST.getCode());
                result.setMsg(ResultEnum.NOT_EXIST.getMessage());
            }
        } else {
            result.setCode(ResultEnum.PERMISSION_ERROR.getCode());
            result.setMsg(ResultEnum.PERMISSION_ERROR.getMessage() + ", 只有社长才能查看可邀请农机手的信息！");
        }
        return result;
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public Map<String, List> statisticsOrders(User user, boolean getAll, Date startTime, Date endTime) {
        //全部按照月份查询
        getAll = false;
        Map<String, List> statisticsMap = new HashMap<>();
        if (null == user || null == startTime || null == endTime) {
            throw new ParamErrorException("参数不正确！");
        }
        //必须是合作社成员才能获取统计信息
        if (cooperationRole(user)) {
            Map<String, List<User>> teamUsersMap = new HashMap<>();
            List<User> proList = new ArrayList<>();
            User pro = findProprieterById(user.getIdCooperation());
            if (null != pro) {
                proList.add(pro);
                teamUsersMap.put(CooperationStateEnum.PROPRIETER.getMessage(), proList);
            }
            List<Team> teamList = teamRepository.getTeamsByIdCooperation(user.getIdCooperation());
            //按照小队分成不同的用户组
            if (null != teamList && teamList.size() > 0) {
                for (Team team : teamList) {
                    if (null != team) {
                        List<User> teamOperators = userRepository.findByIdCooperationAndIdTeam(user.getIdCooperation(), team.getIdTeam());
                        if (StringUtils.isNotBlank(team.getTeamName())) {
                            teamUsersMap.put(team.getTeamName(), teamOperators);
                        }
                    }
                }
            }
            //表一list集合
            List<StatisticsCooperationDTO> statisticsCooperationDTOS = new ArrayList<>();
            //表二list集合
            List<StatisticsOperatorsDTO> statisticsOperatorsDTOS = new ArrayList<>();
            //表三list集合
            List<StatisticsOrderDTO> statisticsOrderDTOS = new ArrayList<>();
            //遍历所有小队的成员
            for (Map.Entry entry : teamUsersMap.entrySet()) {
                String key = entry.getKey().toString();
                List<User> userList = (List<User>) entry.getValue();
                /**
                 * 统计合作社表一
                 */
                //所有用户去重后的订单集合
                Set<UserOrder> orderSet = new HashSet<>();
                for (User operator : userList) {
                    if (null != operator) {
                        List<UserOrder> orders = orderService.getCooperationOrder(operator, getAll, startTime, endTime);
                        for (UserOrder order : orders) {
                            orderSet.add(order);
                        }
                    }
                }
                //所有订单完结的月份集合
                Set<Date> dateSet = new HashSet<>();
                Iterator<UserOrder> it = orderSet.iterator();
                while (it.hasNext()) {
                    UserOrder order = it.next();
                    if (null != order.getStartTime()) {
                        Date month = null;
                        if (null != order.getPeriod()) {
                            Date finishedDate = DateUtils.getDate_PastOrFuture_Day(order.getStartTime(), order.getPeriod());
                            month = DateUtils.getMonthStart(finishedDate);
                        } else {
                            month = DateUtils.getMonthStart(order.getStartTime());
                        }
                        dateSet.add(month);
                    }
                }
                //遍历月份的set集合
                Iterator<Date> dateIterator = dateSet.iterator();
                while (dateIterator.hasNext()) {
                    //表一封装类
                    StatisticsCooperationDTO statisticsCooperationDTO = new StatisticsCooperationDTO();
                    //表一队别
                    statisticsCooperationDTO.setTeamName(key);
                    //初始化数据
                    Integer workingOrderNum = 0;
                    Integer commitOrderNum = 0;
                    Integer finishedOrderNum = 0;
                    BigDecimal income = new BigDecimal("0.00");
                    Date d = dateIterator.next();
                    String dateTime = DateUtils.formatDate(d, "yyyy/MM");
                    //月份
                    statisticsCooperationDTO.setStartTime(dateTime);
                    Iterator<UserOrder> it2 = orderSet.iterator();
                    while (it2.hasNext()) {
                        UserOrder order = it2.next();
                        if (null != order) {
                            if (null != order.getStartTime()) {
                                Date overTime = null;
                                if (null != order.getPeriod()) {
                                    overTime = DateUtils.getDate_PastOrFuture_Day(order.getStartTime(), order.getPeriod());
                                } else {
                                    overTime = DateUtils.getDate_PastOrFuture_Day(order.getStartTime(), 1);
                                }
                                if (null != overTime) {
                                    if (overTime.after(d) && overTime.before(DateUtils.getMonthEnd(d))) {
                                        if (order.getUserOrderState().equals(OrderStateEnum.WORKING.getState())
                                                || order.getUserOrderState().equals(OrderStateEnum.CHECKING.getState())
                                                || order.getUserOrderState().equals(OrderStateEnum.FINISHED.getState())) {
                                            workingOrderNum += 1;
                                        }
                                        if (order.getUserOrderState().equals(OrderStateEnum.CHECKING.getState())
                                                || order.getUserOrderState().equals(OrderStateEnum.FINISHED.getState())) {
                                            commitOrderNum += 1;
                                        }
                                        if (order.getUserOrderState().equals(OrderStateEnum.FINISHED.getState())) {
                                            finishedOrderNum += 1;
                                        }
                                        //小队订单的收入
                                        income = income.add(order.getPrice().multiply(new BigDecimal(order.getArce())));
                                    }
                                }
                            }
                        }
                    }
                    //作业单数
                    statisticsCooperationDTO.setWorkingOrderNum(workingOrderNum);
                    //提交单数
                    statisticsCooperationDTO.setCommitOrderNum(commitOrderNum);
                    //结算单数
                    statisticsCooperationDTO.setFinishedOrderNum(finishedOrderNum);
                    //收入
                    statisticsCooperationDTO.setIncome(income);
                    statisticsCooperationDTOS.add(statisticsCooperationDTO);
                }
                /**
                 * 统计合作社表二/表三
                 */
                if (null != userList && userList.size() > 0) {
                    //遍历合作社每个小队的所有成员
                    for (User operator : userList) {
                        if (null != operator) {
                            //遍历月份的set集合
                            Iterator<Date> mmIt = dateSet.iterator();
                            while (mmIt.hasNext()) {
                                //表二封装类
                                StatisticsOperatorsDTO statisticsOperatorsDTO = new StatisticsOperatorsDTO();
                                Float acre = 0.0F;
                                Integer orderNum = 0;
                                BigDecimal opIncome = new BigDecimal(0);
                                //表二队别
                                statisticsOperatorsDTO.setTeamName(key);
                                //表二农机手
                                statisticsOperatorsDTO.setOperatorName(operator.getNickname());
                                Date d = mmIt.next();
                                String mm = DateUtils.formatDate(d, "yyyy/MM");
                                //表二月份
                                statisticsOperatorsDTO.setStartTime(mm);
                                //遍历该用户的所有订单放入set集合
                                Set<UserOrder> userOrderSet = new HashSet<>();
                                //查找该用户当月的订单
                                List<UserOrder> orders = orderService.getCooperationOrder(operator, getAll, d);
                                if (null != orders) {
                                    if (orders.size() > 0) {
                                        for (UserOrder order : orders) {
                                            userOrderSet.add(order);
                                        }
                                    }
                                }
                                //遍历用户当月的订单
                                if (null != userOrderSet && userOrderSet.size() > 0) {
                                    for (UserOrder order : userOrderSet) {
                                        if (null != order) {
                                            //表三封装类
                                            StatisticsOrderDTO statisticsOrderDTO = new StatisticsOrderDTO();
                                            //表三队别
                                            statisticsOrderDTO.setTeamName(key);
                                            //表三农机手
                                            statisticsOrderDTO.setOperatorName(operator.getNickname());
                                            //表三月份
                                            statisticsOrderDTO.setMonth(mm);
                                            CompletionReport completionReport = operatorReportService.getOperatorReport(order.getIdUserOrder(), operator.getIdUser());
                                            if (null != completionReport) {
                                                //统计该用户的作业亩数
                                                if (null != completionReport.getAcre()) {
                                                    acre += completionReport.getAcre();
                                                    //统计该用户的收入
                                                    if (null != order.getPrice()) {
                                                        if (order.getUserOrderState().equals(OrderStateEnum.CHECKING.getState())
                                                                || order.getUserOrderState().equals(OrderStateEnum.FINISHED.getState())) {
                                                            opIncome = opIncome.add(order.getPrice().multiply(new BigDecimal(completionReport.getAcre())));
                                                        }
                                                    }
                                                }
                                            }
                                            //统计用户的作业单数
                                            if (order.getUserOrderState().equals(OrderStateEnum.CHECKING.getState())
                                                    || order.getUserOrderState().equals(OrderStateEnum.FINISHED.getState())) {
                                                orderNum += 1;
                                            }
                                            //表三订单号
                                            statisticsOrderDTO.setUserOrderNo(order.getUserOrderNo());
                                            //表三作业时间
                                            statisticsOrderDTO.setStartTime(DateUtils.formatDate(order.getStartTime(), "yyyy/MM/dd"));
                                            //表三作业周期
                                            statisticsOrderDTO.setPeriod(order.getPeriod());
                                            //表三农机类型
                                            statisticsOrderDTO.setMachineType(order.getMachineryType());
                                            //表三作业名称
                                            statisticsOrderDTO.setCropName(order.getCropName());
                                            if (order.getUserOrderState().equals(OrderStateEnum.CHECKING.getState())
                                                    || order.getUserOrderState().equals(OrderStateEnum.FINISHED.getState())) {
                                                if (null != order.getArce()) {
                                                    //表三作业亩数
                                                    statisticsOrderDTO.setFarmlandAcreage(order.getArce());
                                                }
                                            }
                                            if (null != order.getIdFarmland()) {
                                                StringBuilder adressDetail = new StringBuilder();
                                                Farmland farmland = farmlandRepository.findByIdFarmland(order.getIdFarmland());
                                                FarmlandDTO farmlandDTO = farmlandService.loadDTO(farmland);
                                                if (null != farmlandDTO) {
                                                    if (null != farmlandDTO.getAddress()) {
                                                        AddressDTO addressDTO = farmlandDTO.getAddress();
                                                        if (null != addressDTO) {
                                                            if (null != addressDTO.getProvince()) {
                                                                adressDetail.append(addressDTO.getProvince());
                                                            }
                                                            if (null != addressDTO.getCity()) {
                                                                adressDetail.append(addressDTO.getCity());
                                                            }
                                                            if (null != addressDTO.getCounty()) {
                                                                adressDetail.append(addressDTO.getCounty());
                                                            }
                                                            if (null != addressDTO.getTown()) {
                                                                adressDetail.append(addressDTO.getTown());
                                                            }
                                                            if (null != addressDTO.getDetail()) {
                                                                adressDetail.append(addressDTO.getDetail());
                                                            }
                                                            //表三作业地址
                                                            statisticsOrderDTO.setAddress(adressDetail.toString().trim());
                                                        }
                                                    }
                                                }
                                            }
                                            //表三数据放入集合
                                            statisticsOrderDTOS.add(statisticsOrderDTO);
                                        }
                                    }
                                    statisticsOperatorsDTO.setFarmlandAcreage(acre);
                                    statisticsOperatorsDTO.setFinishedOrderNum(orderNum);
                                    statisticsOperatorsDTO.setIncome(opIncome);
                                    //表二数据放入集合
                                    statisticsOperatorsDTOS.add(statisticsOperatorsDTO);
                                }
                            }
                        }
                    }
                }
            }
            statisticsMap.put("合作社统计信息表一", statisticsCooperationDTOS);
            statisticsMap.put("合作社统计信息表二", statisticsOperatorsDTOS);
            statisticsMap.put("合作社统计信息表三", statisticsOrderDTOS);
        }
        return statisticsMap;
    }

    @Override
    public MembersDTO dtoProprieter(String cooperationNum) {
        MembersDTO membersDTO = new MembersDTO();
        if (StringUtils.isNotBlank(cooperationNum)) {
            Integer cooperationId = Integer.valueOf(cooperationNum);
            if (null != cooperationId) {
                List<User> users = userRepository.findByIdCooperationAndIdTeam(cooperationId, CooperationServiceImpl.NONE_VALUE);
                if (null != users && users.size() == 1) {
                    User proprieter = users.get(0);
                    membersDTO = dtoFactory(cooperationId, proprieter, DateUtils.getDate(), 1);
                }
            }
        } else {
            throw new ParamErrorException("参数合作社编号错误！");
        }
        return membersDTO;
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public Result dismissMember(User user, String mobile) {
        if (null == user) {
            throw new LoginException("您还没有登陆，请登陆后再试试吧！");
        }
        if (StringUtils.isBlank(mobile)) {
            throw new ParamErrorException("参数错误！");
        }
        User member = userRepository.findByMobile(mobile);
        //只有社长才能开除指定社员
        if (proprieter(user)) {
            if (null != member) {
                // 同步日志变更信息
                saveCooperationChangeLog(user.getIdCooperation(), CooperationStateEnum.COMMON.getState(), user.getIdUser(), "社长" + user.getNickname() + "开除了id为" + member.getIdUser() + "的社员。");
                // 给该社员发送推送消息
                List<User> users = new ArrayList<>();
                users.add(member);
                String msg = MSG_UPDATE_PREFIX + "社长" + user.getNickname() + "把您踢出了合作社，赶快去看看吧!";
                PushResult pushResult = Jdpush.pushMessageUtil(msg, users);
                if (null != pushResult && pushResult.isResultOK()) {
                    logger.info("社员被踢出合作社的消息推送成功。");
                } else {
                    logger.error("社员被踢出合作社的消息推送失败！");
                }
                // 开除该社员
                unbind(member);
                Result result = new Result();
                result.setCode(ResultEnum.SUCCESS.getCode());
                result.setMsg("社长剔除社员成功！");
                return result;
//                return unbind(member);
            } else {
                throw new NotExistException("指定用户不存在！");
            }
        } else {
            throw new PermissionException("权限不足！");
        }
    }

    @Override
    public Result checkMemberState(User user, String cooperationNum) {
        Result result = new Result();
        if (null == user) {
            throw new LoginException("您还没有登陆，请登陆后再试试吧！");
        }
        if (StringUtils.isBlank(cooperationNum)) {
            throw new ParamErrorException("参数错误！");
        }
        Integer cooperationId = Integer.valueOf(cooperationNum);
        if (null != cooperationId) {
            InviteRelationShip inviteRelationShip = inviteRelationShipRepository.getOneByIdCooperationAndIdInviterAndInviterStateNot(cooperationId, user.getIdUser(), CooperationStateEnum.DELETED.getState());
            if (null != inviteRelationShip) {
                result.setData(inviteRelationShip);
                result.setCode(ResultEnum.SUCCESS.getCode());
                result.setMsg(ResultEnum.SUCCESS.getMessage());
            } else {
                throw new NotExistException("");
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackOn = {Exception.class})
    public Result refuseNewMember(User user, String mobile) {
        Result result = new Result();
        if (null == user) {
            throw new LoginException("您还没有登陆，请登陆后再试试吧！");
        }
        if (StringUtils.isBlank(mobile)) {
            throw new ParamErrorException("参数错误！");
        }
        //只有社长才能拒绝新成员的申请
        if (proprieter(user)) {
            User member = userRepository.findByMobile(mobile);
            if (null != member) {
                InviteRelationShip inviteRelationShip = inviteRelationShipRepository.getOneByIdCooperationAndIdInviterAndInviterState(user.getIdCooperation(), member.getIdUser(), CooperationStateEnum.APPLY.getState());
                if (null != inviteRelationShip) {
                    inviteRelationShip.setInviterState(CooperationStateEnum.REFUSED.getState());
                    inviteRelationShip.setRemark("社长" + user.getNickname() + "拒绝了id为" + member.getIdUser() + "的用户加入合作社。");
                    inviteRelationShip.setUpdateTime(new Date());
                    inviteRelationShipRepository.save(inviteRelationShip);
                    // 同步日志变更信息
                    saveCooperationChangeLog(user.getIdCooperation(), CooperationStateEnum.COMMON.getState(), user.getIdUser(), "社长" + user.getNickname() + "拒绝了id为" + member.getIdUser() + "的用户加入合作社。");
                    // 给该用户发送推送消息
                    List<User> users = new ArrayList<>();
                    users.add(member);
                    String msg = MSG_PREFIX + "社长" + user.getNickname() + "拒绝您加入合作社，赶快去看看吧!";
                    PushResult pushResult = Jdpush.pushMessageUtil(msg, users);
                    if (null != pushResult && pushResult.isResultOK()) {
                        logger.info("社员被拒绝加入合作社的消息推送成功。");
                    } else {
                        logger.error("社员被拒绝加入合作社的消息推送失败！");
                    }
                    result.setData(inviteRelationShip);
                    result.setCode(ResultEnum.SUCCESS.getCode());
                    result.setMsg(ResultEnum.SUCCESS.getMessage());
                } else {
                    throw new NotExistException("");
                }
            } else {
                throw new NotExistException("指定用户不存在！");
            }
        } else {
            throw new PermissionException("");
        }
        return result;
    }

    //查询合作社小队农机手信息并排序
    private Map<String, List<MembersDTO>> listTeamMembers(Integer cooperationId, CopyOnWriteArrayList<User> operatorList, String startTime, Integer days) {
        Map<String, List<MembersDTO>> operatorMap = new HashMap<>();
        if (null != operatorList) {
            if (operatorList.size() > 0) {
                for (User operator : operatorList) {
                    if (null != operator) {
                        Team team = teamRepository.getOneByIdUserAndIdCooperation(operator.getIdUser(), cooperationId);
                        if (null != team) {
                            Map<String, Integer> map = new HashMap<>();
                            if (StringUtils.isNotBlank(team.getTeamName())) {
                                map.put(team.getTeamName(), team.getIdTeam());
                                User captain = userRepository.findByIdUser(operator.getIdUser());
                                if (null != captain) {
                                    //保证每次都是干净的容器
                                    List<MembersDTO> members = new ArrayList<>();
                                    MembersDTO membersDTO = dtoFactory(cooperationId, captain, startTime, days);
                                    if (null != membersDTO) {
                                        members.add(membersDTO);
                                    }
                                    operatorList.remove(captain);
                                    //查询同一个小队的组员信息
                                    if (null != operatorList && operatorList.size() > 0) {
                                        for (User op : operatorList) {
                                            if (null != op) {
                                                Team tt = teamRepository.getOneByIdUserAndIdCooperation(op.getIdUser(), cooperationId);
                                                if (null == tt) {
                                                    if (null != op.getIdTeam()) {
                                                        if (op.getIdTeam() == map.get(team.getTeamName())) {
                                                            MembersDTO dto = dtoFactory(cooperationId, op, startTime, days);
                                                            if (null != dto) {
                                                                members.add(dto);
                                                            }
                                                            operatorList.remove(op);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    // 将返回的数据放入list集合
                                    operatorMap.put(team.getTeamName(), members);
                                }

                            }
                        } else {
                            // 队员角色
                            if (null != operator.getIdTeam() && !CooperationServiceImpl.NONE_VALUE.equals(operator.getIdTeam())) {
                                Team t = teamRepository.getOneByIdTeam(operator.getIdTeam());
                                //本队没有队长
                                if (noCaptainTeamMember(t)) {
                                    //准备干净的容器
                                    List<MembersDTO> members = new ArrayList<>();
                                    MembersDTO dto = dtoFactory(cooperationId, operator, startTime, days);
                                    if (null != dto) {
                                        members.add(dto);
                                    }
                                    operatorList.remove(operator);
                                    //查询本队其它组员的信息
                                    if (null != operatorList && operatorList.size() > 0) {
                                        for (User op : operatorList) {
                                            if (null != op) {
                                                //1.确保成员不是队长
                                                if (null == teamRepository.getOneByIdUserAndIdCooperation(op.getIdUser(), cooperationId)) {
                                                    //2.确保该成员是本小队的成员
                                                    if (null != op.getIdTeam()) {
                                                        if (op.getIdTeam() == operator.getIdTeam()) {
                                                            MembersDTO dto1 = dtoFactory(cooperationId, op, startTime, days);
                                                            if (null != dto1) {
                                                                members.add(dto1);
                                                            }
                                                            operatorList.remove(op);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    // 将返回的数据放入list集合
                                    operatorMap.put(t.getTeamName(), members);
                                }
                            }
                        }
                    }
                    if (operatorList.size() == 0) {
                        break;
                    }
                }
            }
        }
        return operatorMap;
    }

    //查询合作社成员信息列表并排序
    private Map<String, List<MembersDTO>> sortMemberList(Integer cooperationId, CopyOnWriteArrayList<User> memberList, String startTime, Integer days) {
        Map<String, List<MembersDTO>> operatorMap = new HashMap<>();
        if (null != memberList) {
            if (memberList.size() > 0) {
                for (User member : memberList) {
                    if (null != member) {
                        Team team = teamRepository.getOneByIdUserAndIdCooperation(member.getIdUser(), cooperationId);
                        if (null != team) {
                            Map<String, Integer> map = new HashMap<>();
                            if (StringUtils.isNotBlank(team.getTeamName())) {
                                map.put(team.getTeamName(), team.getIdTeam());
                                User captain = userRepository.findByIdUser(member.getIdUser());
                                if (null != captain) {
                                    //保证每次都是干净的容器
                                    List<MembersDTO> members = new ArrayList<>();
                                    MembersDTO membersDTO = dtoFactory(cooperationId, captain, startTime, days);
                                    if (null != membersDTO) {
                                        members.add(membersDTO);
                                    }
                                    memberList.remove(captain);
                                    //查询同一个小队的组员信息
                                    if (null != memberList && memberList.size() > 0) {
                                        for (User op : memberList) {
                                            if (null != op) {
                                                Team tt = teamRepository.getOneByIdUserAndIdCooperation(op.getIdUser(), cooperationId);
                                                if (null == tt) {
                                                    if (null != op.getIdTeam()) {
                                                        if (op.getIdTeam() == map.get(team.getTeamName())) {
                                                            MembersDTO dto = dtoFactory(cooperationId, op, startTime, days);
                                                            if (null != dto) {
                                                                members.add(dto);
                                                            }
                                                            memberList.remove(op);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    // 将返回的数据放入list集合
                                    operatorMap.put(team.getTeamName(), members);
                                }

                            }
                        } else {
                            // 队员角色
                            if (null != member.getIdTeam() && !"-1".equals(member.getIdTeam().toString())) {
                                Team t = teamRepository.getOneByIdTeam(member.getIdTeam());
                                //本队没有队长
                                if (noCaptainTeam(t)) {
                                    //准备干净的容器
                                    List<MembersDTO> members = new ArrayList<>();
                                    MembersDTO dto = dtoFactory(cooperationId, member, startTime, days);
                                    if (null != dto) {
                                        members.add(dto);
                                    }
                                    memberList.remove(member);
                                    //查询本队其它组员的信息
                                    if (null != memberList && memberList.size() > 0) {
                                        for (User op : memberList) {
                                            if (null != op) {
                                                //1.确保成员不是队长
                                                if (null == teamRepository.getOneByIdUserAndIdCooperation(op.getIdUser(), cooperationId)) {
                                                    //2.确保该成员是本小队的成员
                                                    if (null != op.getIdTeam()) {
                                                        if (op.getIdTeam() == member.getIdTeam()) {
                                                            MembersDTO dto1 = dtoFactory(cooperationId, op, startTime, days);
                                                            if (null != dto1) {
                                                                members.add(dto1);
                                                            }
                                                            memberList.remove(op);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    // 将返回的数据放入list集合
                                    operatorMap.put(t.getTeamName(), members);
                                }
                            }
                        }
                    }
                    if (memberList.size() == 0) {
                        break;
                    }
                }
            }
        }
        return operatorMap;
    }

    //排序社员列表
    private Map<String, List<User>> membersSortList(CopyOnWriteArrayList<User> memberList) {
        Map<String, List<User>> userMap = new ConcurrentHashMap<>();
        if (null != memberList) {
            if (memberList.size() > 0) {
                for (User u : memberList) {
                    User user = memberList.get(0);
                    if (null != user) {
                        // 队员角色
                        if (null != user.getIdTeam() && !CooperationServiceImpl.NONE_VALUE.equals(user.getIdTeam().toString())) {
                            Team t = teamRepository.getOneByIdTeam(user.getIdTeam());
                            if (null != t) {
                                if (StringUtils.isNotBlank(t.getTeamName())) {
                                    List<User> users = new ArrayList<>();
                                    String teamName = t.getTeamName();
                                    users.add(user);
                                    memberList.remove(user);
                                    //遍历其余的用户
                                    for (User member : memberList) {
                                        if (null != member) {
                                            // 队员角色
                                            if (null != member.getIdTeam() && !CooperationServiceImpl.NONE_VALUE.equals(member.getIdTeam().toString())) {
                                                Team t1 = teamRepository.getOneByIdTeam(member.getIdTeam());
                                                if (null != t1) {
                                                    if (StringUtils.isNotBlank(t1.getTeamName())) {
                                                        if (t1.getTeamName().equals(teamName)) {
                                                            users.add(member);
                                                            memberList.remove(member);
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                        if (memberList.size() == 0) {
                                            break;
                                        }
                                    }
                                    userMap.put(teamName, users);
                                }

                            }
                        }
                    }
                    if (memberList.size() == 0) {
                        break;
                    }
                }
            }
        }
        return userMap;
    }

    private List<MembersDTO> sortMapToList(Integer cooperationId, Map<String, List<User>> map, String startTime, Integer days) {
        List<MembersDTO> memberList = new ArrayList<>();
        if (null != cooperationId) {
            if (null != map) {
                // 先把默认的小队放到list集合中
                List<User> initTeam = map.get(CooperationServiceImpl.INIT_TEAM_NAME);
                if (null != initTeam && initTeam.size() > 0) {
                    Team team = teamRepository.getOneByIdTeam(initTeam.get(0).getIdTeam());
                    //默认小队有队长
                    if (null != team.getIdUser()) {
                        for (User u : initTeam) {
                            if (u.getIdUser().toString().equals(team.getIdUser().toString())) {
                                MembersDTO member0 = dtoFactory(cooperationId, u, startTime, days);
                                if (memberList.size() > 0) {
                                    //将队长放在首位
                                    MembersDTO m0 = memberList.get(0);
                                    if (null != m0) {
                                        memberList.remove(0);
                                    }
                                    memberList.add(0, member0);
                                    memberList.add(m0);
                                } else {
                                    memberList.add(member0);
                                }
                            } else {
                                MembersDTO members = dtoFactory(cooperationId, u, startTime, days);
                                memberList.add(members);
                            }
                        }
                    } else {
                        //小队没有队长
                        if (initTeam.size() > 0) {
                            for (User u2 : initTeam) {
                                MembersDTO membersDTO = dtoFactory(cooperationId, u2, startTime, days);
                                memberList.add(membersDTO);
                            }
                        }
                    }
                    //把默认的小队移除掉
                    map.remove(CooperationServiceImpl.INIT_TEAM_NAME);
                }
                // 遍历其余小队的map集合
                for (Map.Entry entry : map.entrySet()) {
                    List<User> list = (List) entry.getValue();
                    if (list.size() > 0) {
                        Integer tid = list.get(0).getIdTeam();
                        Team team1 = teamRepository.getOneByIdTeam(tid);
                        if (null != team1.getIdUser()) {
                            //准备一个干净的容器
                            List<MembersDTO> lists = new ArrayList<>();
                            for (User u3 : list) {
                                if (u3.getIdUser().toString().equals(team1.getIdUser().toString())) {
                                    //将队长放在首位
                                    MembersDTO members0 = dtoFactory(cooperationId, u3, startTime, days);
                                    if (lists.size() > 0) {
                                        MembersDTO mm = lists.get(0);
                                        lists.add(0, members0);
                                        lists.add(mm);
                                    } else {
                                        lists.add(members0);
                                    }
                                } else {
                                    MembersDTO members = dtoFactory(cooperationId, u3, startTime, days);
                                    lists.add(members);
                                }
                            }
                            for (int i = 0; i < lists.size(); i++) {
                                memberList.add(lists.get(i));
                            }
                            lists.clear();
                        } else {
                            //小队没有队长,准备一个干净的容器
                            List<MembersDTO> lists = new ArrayList<>();
                            if (list.size() > 0) {
                                for (User u5 : list) {
                                    MembersDTO membersDTO = dtoFactory(cooperationId, u5, startTime, days);
                                    lists.add(membersDTO);
                                }
                                for (int i = 0; i < lists.size(); i++) {
                                    memberList.add(lists.get(i));
                                }
                                lists.clear();
                            }
                        }
                    }

                }
            }
        }
        return memberList;
    }

    //确认该小队没有队长或者队长身份不是农机手
    private boolean noCaptainTeamMember(Team t) {
        if (null != t) {
            if (null == t.getIdUser()) {
                return true;
            } else {
                User user = userRepository.findByIdUser(t.getIdUser());
                if (null != user) {
                    if (!user.getOperator()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //确认该小队没有队长
    private boolean noCaptainTeam(Team t) {
        if (null != t) {
            if (null == t.getIdUser()) {
                return true;
            }
        }
        return false;
    }

    //装配返回的用户数据类型
    private MembersDTO dtoFactory(Integer cooperationId, User member, String startTime, Integer days) {
        MembersDTO membersDTO = new MembersDTO();
        if (null != member) {
            membersDTO.setMobile(member.getMobile());
            membersDTO.setInviterNum(StringUtil.prefixStr(member.getIdUser().toString(), 8, "0"));
            membersDTO.setNickname(member.getNickname());
            membersDTO.setIdTeam(member.getIdTeam());
            //设置合作社成员的忙闲状态
            if (null != startTime && null != days) {
                Date startDate = DateUtils.parseDate(startTime);
                boolean busy = orderService.isBusy(member, startDate, days);
                membersDTO.setBusy(busy);
            }
            //设置小队信息
            Team captain = teamRepository.getOneByIdUserAndIdCooperation(member.getIdUser(), cooperationId);
            if (null != captain) {
                membersDTO.setCaptain(true);
            } else {
                membersDTO.setCaptain(false);
            }
            if (null != member.getIdTeam()) {
                Team t = teamRepository.getOneByIdTeam(member.getIdTeam());
                if (null != t) {
                    membersDTO.setTeamName(t.getTeamName());
                }
            }
            //设置农田信息
            List<Farmland> farmlands = farmlandRepository.findByIdUserAndFarmlandStateNot(member.getIdUser(), FarmlandStateEnum.DELETED.getState());
            if (null != farmlands && farmlands.size() > 0) {
                Float farmlandAcreage = getFarmArea(farmlands);
                // 设置农田面积
                membersDTO.setFarmlandAcreage(farmlandAcreage);
                // 设置农田数量
                membersDTO.setFarmlandNumber(farmlands.size());
            } else {
                // 没有农田信息的设置默认值，方便前端解析
                membersDTO.setFarmlandAcreage(0.00F);
                membersDTO.setFarmlandNumber(CooperationServiceImpl.INIT_VALUE);
            }
            //设置农机信息
            List<Machinery> machineryList = machineryRepository.findByIdUserAndMachineryStateNot(member.getIdUser(), MachineryStateEnum.DELETED.getState());
            if (null != machineryList && machineryList.size() > 0) {
                Integer machineryNum = getMachineryNum(machineryList);
                // 设置农机数量
                membersDTO.setMachineryNumber(machineryNum);
            } else {
                // 没有农机的值为0
                membersDTO.setMachineryNumber(CooperationServiceImpl.INIT_VALUE);
            }
        } else {
            logger.error("参数User对象不能为空！");
        }
        return membersDTO;
    }

    //获取用户农田面积
    private Float getFarmArea(List<Farmland> farmlands) {
        Float farmlandAcreage = 0.00F;
        if (null != farmlands && farmlands.size() > 0) {
            for (Farmland f : farmlands) {
                if (null != f) {
                    farmlandAcreage += f.getFarmlandAcreage();
                }
            }
        }
        return farmlandAcreage;
    }

    //获取用户农机数量
    private Integer getMachineryNum(List<Machinery> machinerys) {
        Integer machineryNum = 0;
        if (null != machinerys && machinerys.size() > 0) {
            for (Machinery machinery : machinerys) {
                if (null != machinery) {
                    machineryNum += machinery.getCount();
                }
            }
        }
        return machineryNum;
    }

    //查询合作社成员信息
    private List<MembersDTO> members(Integer cooperationId, String startTime, Integer days) {
        List<MembersDTO> members = new ArrayList<>();
        if (null != cooperationId) {
            //设置合作社成员信息
            List<User> userList = userRepository.findAllByIdCooperationAndIdTeamIsNotAndUserStateNot(cooperationId, CooperationServiceImpl.NONE_VALUE, UserStateEnum.DELETED.getState());
            if (null != userList && userList.size() > 0) {
                for (User u : userList) {
                    if (null != u) {
                        MembersDTO membersDTO = null;
                        if (!CooperationServiceImpl.NONE_VALUE.equals(u.getIdTeam())) {
                            membersDTO = dtoFactory(cooperationId, u, startTime, days);
                        }
                        if (null != membersDTO) {
                            members.add(membersDTO);
                        }
                    }
                }
            }
        }
        return members;
    }

    //创建指定的小队
    private Team createTeam(User user, String teamName) {
        if (null != user) {
            // 只有社长才能创建小队
            if (proprieterRole(user)) {
                if (StringUtils.isNotBlank(teamName)) {
                    Team t = teamRepository.getTeamByIdCooperationAndTeamName(user.getIdCooperation(), teamName);
                    if (null == t) {
                        Team team = new Team();
                        //创建新的小队
                        team.setTeamName(teamName);
                        team.setIdCooperation(user.getIdCooperation());
                        return teamRepository.save(team);
                    } else {
                        logger.error(ResultEnum.PARAM_ERROR.getMessage() + "：该小队已存在，请勿重复创建！");
                    }
                }
            } else {
                logger.error(ResultEnum.PERMISSION_ERROR.getMessage() + "只有社长才能创建合作社的小队！");
            }
        }
        return null;
    }

    //给合作社新加入的成员分配小队角色
    private InviteRelationShip appointTeamRole(User user, MembersDTO members) {
        if (null != user) {
            // 只有社长才能分配成员角色
            if (proprieterRole(user)) {
                if (null != members) {
                    if (StringUtils.isNotBlank(members.getTeamName())) {
                        Team t = teamRepository.getTeamByIdCooperationAndTeamName(user.getIdCooperation(), members.getTeamName());
                        if (null != t) {
                            // 在已有的小队里关联角色
                            if (null != members.getMobile()) {
                                User member = userRepository.findByMobile(members.getMobile());
                                if (null != member) {
                                    // 获取已批准加入合作社的用户
                                    InviteRelationShip relationShip = inviteRelationShipRepository.getOneByIdCooperationAndIdInviterAndInviterState(user.getIdCooperation(), member.getIdUser(), CooperationStateEnum.PERMITTED.getState());
                                    if (null != relationShip) {
                                        // 如果该成员本身就是队长，需先解除队长职务
                                        Team team = teamRepository.getOneByIdUserAndIdCooperation(member.getIdUser(), user.getIdCooperation());
                                        if (null != team) {
                                            team.setIdUser(null);
                                            teamRepository.save(team);
                                        }
                                        if (members.isCaptain()) {
                                            if (member.getOperator()) {
                                                if (null == t.getIdUser()) {
                                                    // 设置队长人选
                                                    relationShip.setInviterRole(CooperationStateEnum.CAPTAIN.getState());
                                                    // 同步关联小队队长信息
                                                    t.setIdUser(member.getIdUser());
                                                    teamRepository.save(t);
                                                } else {
                                                    throw new ParamErrorException("该合作社的小队已经有队长人选，请勿重复设置！");
                                                }
                                            } else {
                                                throw new ParamErrorException("该社员不是农机手，所以无法指定为队长！");
                                            }
                                        } else {
                                            relationShip.setInviterRole(CooperationStateEnum.MEMBER.getState());
                                        }
                                        relationShip.setIdTeam(t.getIdTeam());
                                        relationShip.setRemark("社长为社员" + member.getNickname() + "分配了角色。");
                                        relationShip.setUpdateTime(new Date());
                                        inviteRelationShipRepository.save(relationShip);
                                        // 同步用户的小队信息
                                        //member.setIdCooperation(user.getIdCooperation());
                                        member.setIdTeam(t.getIdTeam());
                                        userRepository.save(member);
                                        return relationShip;
                                    }
                                }
                            }
                        } else {
                            // 创建新的小队并分配成员角色
                            Team newTeam = createTeam(user, members.getTeamName());
                            if (null != newTeam) {
                                if (null != members.getMobile()) {
                                    User member = userRepository.findByMobile(members.getMobile());
                                    if (null != member) {
                                        // 获取已批准加入合作社的用户
                                        InviteRelationShip relationShip = inviteRelationShipRepository.getOneByIdCooperationAndIdInviterAndInviterState(user.getIdCooperation(), member.getIdUser(), CooperationStateEnum.PERMITTED.getState());
                                        if (null != relationShip) {
                                            // 如果该成员本身就是其它小队的队长，需先解除队长职务
                                            Team team = teamRepository.getOneByIdUserAndIdCooperation(member.getIdUser(), user.getIdCooperation());
                                            if (null != team) {
                                                team.setIdUser(null);
                                                teamRepository.save(team);
                                            }
                                            if (members.isCaptain()) {
                                                if (member.getOperator()) {
                                                    // 设置新小队的队长人选
                                                    relationShip.setInviterRole(CooperationStateEnum.CAPTAIN.getState());
                                                    // 同步关联小队队长信息
                                                    newTeam.setIdUser(member.getIdUser());
                                                    teamRepository.save(newTeam);
                                                } else {
                                                    throw new ParamErrorException("该社员不是农机手，所以无法指定为队长！");
                                                }
                                            } else {
                                                relationShip.setInviterRole(CooperationStateEnum.MEMBER.getState());
                                            }
                                            relationShip.setIdTeam(newTeam.getIdTeam());
                                            relationShip.setRemark("社长为社员" + member.getNickname() + "分配了角色。");
                                            relationShip.setUpdateTime(new Date());
                                            inviteRelationShipRepository.save(relationShip);
                                            // 同步用户的小队信息
                                            member.setIdTeam(newTeam.getIdTeam());
                                            userRepository.save(member);
                                            return relationShip;
                                        } else {
                                            throw new ParamErrorException("该用户还没有提交加入合作社的申请，请先提交加入申请！");
                                        }
                                    }
                                }
                            } else {
                                throw new ParamErrorException("合作社创建新小队失败！");
                            }
                        }
                    } else {
                        throw new ParamErrorException("指定小队的名称不能为空！");
                    }
                }
            } else {
                throw new ParamErrorException("只有社长才能分配新成员角色！");
            }
        }
        return null;
    }

    //用户绑定合作社
    private void bind(User u, Integer idTeam, Integer idCooperation) {
        if (null != u) {
            //绑定合作社
            if (null != idCooperation) {
                u.setIdCooperation(idCooperation);
            }
            //绑定合作社小队
            if (null != idTeam) {
                u.setIdTeam(idTeam);
            }
            userRepository.save(u);
        }
    }

    //用户解绑合作社
    private User unbind(User user) {
        Integer idCooperation = null;
        if (null != user) {
            if (null != user.getIdCooperation()) {
                // 只有已经加入合作社的成员才能解绑
                InviteRelationShip relationShip = inviteRelationShipRepository.getOneByIdCooperationAndIdInviterAndInviterState(user.getIdCooperation(), user.getIdUser(), CooperationStateEnum.PERMITTED.getState());
                if (null != relationShip) {
                    idCooperation = relationShip.getIdCooperation();
                    relationShip.setInviterState(CooperationStateEnum.DELETED.getState());
                    relationShip.setRemark("id为" + user.getIdUser() + "的合作社成员解绑合作社成功。");
                    relationShip.setUpdateTime(new Date());
                    inviteRelationShipRepository.save(relationShip);
                } else {
                    throw new PermissionException("");
                }
                // 队长解绑小队
                if (captainRole(user)) {
                    Team team = teamRepository.getOneByIdTeam(user.getIdTeam());
                    team.setIdUser(null);
                    teamRepository.save(team);
                }
                // 用户解绑合作社
                user.setIdCooperation(null);
                user.setIdTeam(null);
                userRepository.save(user);
                // 同步合作社变更信息
                saveCooperationChangeLog(idCooperation, CooperationStateEnum.COMMON.getState(), user.getIdUser(), "id为" + user.getIdUser() + "的用户退出了合作社。");
                logger.info(ResultEnum.SUCCESS.getMessage() + ": 社员" + user.getNickname() + "解绑合作社成功。");
            }
        } else {
            logger.error("社长不能退出自己创建的合作社！");
            throw new PermissionException("社长不能退出自己创建的合作社!");
        }
        return user;
    }

    // 核实合作社社长的身份
    private boolean proprieterRole(User user) {
        if (null != user) {
            if (null != user.getIdCooperation()) {
                Cooperation cooperation = cooperationRepository.getOneByIdCooperation(user.getIdCooperation());
                if (null != cooperation) {
                    if (null != cooperation.getIdUser()) {
                        if (user.getIdUser().toString().equals(cooperation.getIdUser().toString())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // 核实合作社普通成员的身份
    private boolean memberRole(User user) {
        if (null != user) {
            if (null != user.getIdCooperation()) {
                Cooperation cooperation = cooperationRepository.getOneByIdCooperation(user.getIdCooperation());
                if (null != cooperation) {
                    if (null != cooperation.getIdUser()) {
                        if (!user.getIdUser().toString().equals(cooperation.getIdUser().toString())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // 核实合作社队长的身份
    private boolean captainRole(User user) {
        if (null != user) {
            if (null != user.getIdCooperation() && null != user.getIdTeam()) {
                Team team = teamRepository.getOneByIdTeam(user.getIdTeam());
                if (null != team) {
                    if (null != team.getIdUser()) {
                        if (user.getIdUser().toString().equals(team.getIdUser().toString())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // 核实合作社成员的身份
    private boolean cooperationRole(User user) {
        if (null != user) {
            if (null != user.getIdCooperation()) {
                Cooperation cooperation = cooperationRepository.getOneByIdCooperation(user.getIdCooperation());
                if (null != cooperation) {
                    return true;
                }
            }
        }
        return false;
    }

    // 同步日志变更信息
    private void saveCooperationChangeLog(Integer idCooperation, String cooperationState, Long idUser, String detail) {
        CooperationChangeLog cooperationChangeLog = new CooperationChangeLog();
        if (null != idCooperation) {
            cooperationChangeLog.setIdCooperation(idCooperation);
        }
        if (StringUtils.isNotBlank(cooperationState)) {
            cooperationChangeLog.setChangeType(cooperationState);
        }
        if (null != idUser) {
            cooperationChangeLog.setChangeBy(idUser);
        }
        if (StringUtils.isNotBlank(detail)) {
            cooperationChangeLog.setDetail(detail);
        }
        cooperationChangeLog.setChangeTime(new Date());
        try {
            cooperationChangeLogRepository.save(cooperationChangeLog);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("合作社日志信息变更失败！");
        }
    }

    private CooperationInfoDTO getCooperationMembers(Integer idCooperation, String startTime, Integer days) {
        CooperationInfoDTO cooperationInfoDTO = new CooperationInfoDTO();
        if (null != idCooperation) {
            Cooperation cooperation = cooperationRepository.getOneByIdCooperationAndCooperationStateNot(idCooperation, CooperationStateEnum.FAILURE.getState());
            if (null != cooperation) {
                //合作社编号
                cooperationInfoDTO.setCooperationNum(StringUtil.prefixStr(cooperation.getIdCooperation().toString(), 8, "0"));
                //合作社名称
                cooperationInfoDTO.setCooperationName(cooperation.getCooperationName());
                //设置社长信息
                User proprieter = userRepository.findByIdUser(cooperation.getIdUser());
                if (null != proprieter) {
                    cooperationInfoDTO.setPrimaryName(proprieter.getNickname());
                    cooperationInfoDTO.setMobile(proprieter.getMobile());
                    cooperationInfoDTO.setAvatar(proprieter.getAvatar());
                }
                //设值合作社地址
                Address address = addressRepository.findByIdAddress(cooperation.getIdAddress());
                if (null != address) {
                    AddressDTO addressDTO = new AddressDTO();
                    if (StringUtils.isNotBlank(address.getProvince())) {
                        addressDTO.setProvince(address.getProvince());
                    }
                    if (StringUtils.isNotBlank(address.getCity())) {
                        addressDTO.setCity(address.getCity());
                    }
                    if (StringUtils.isNotBlank(address.getCounty())) {
                        addressDTO.setCounty(address.getCounty());
                    }
                    if (StringUtils.isNotBlank(address.getTown())) {
                        addressDTO.setTown(address.getTown());

                    }
                    if (StringUtils.isNotBlank(address.getDetail())) {
                        addressDTO.setDetail(address.getDetail());
                    }
                    cooperationInfoDTO.setAddressDTO(addressDTO);
                }
                //设置合作社认证信息
                CooperationInfo cooperationInfo = cooperationInfoRepository.getByIdCooperation(idCooperation);
                if (null != cooperationInfo) {
                    cooperationInfoDTO.setBusiness_license(cooperationInfo.getBusiness_license());
                    cooperationInfoDTO.setLicense_pic(cooperationInfo.getLicense_pic());
                }
                CopyOnWriteArrayList<User> memberLists = userRepository.findAllByIdCooperationAndIdTeamIsNotAndUserStateNot(idCooperation, CooperationServiceImpl.NONE_VALUE, UserStateEnum.DELETED.getState());
                Map<String, List<MembersDTO>> membersMap = sortMemberList(idCooperation, memberLists, startTime, days);
                List<MembersDTO> memberList = new ArrayList<>();
                for (Map.Entry entry : membersMap.entrySet()) {
                    List<MembersDTO> list = (List) entry.getValue();
                    for (int i = 0; i < list.size(); i++) {
                        memberList.add(list.get(i));
                    }
                }
                cooperationInfoDTO.setMembers(memberList);
                //社长农机数量
                List<Machinery> machineryList = machineryRepository.findByIdUserAndMachineryStateNot(proprieter.getIdUser(), MachineryStateEnum.DELETED.getState());
                Integer machineryNum = getMachineryNum(machineryList);
                //社长农田面积
                List<Farmland> farmlands = farmlandRepository.findByIdUserAndFarmlandStateNot(proprieter.getIdUser(), FarmlandStateEnum.DELETED.getState());
                Float farmArea = getFarmArea(farmlands);
                Integer farmNum = farmlands.size();
                //社员土地农机信息
                if (null != memberList && memberList.size() > 0) {
                    for (MembersDTO member : memberList) {
                        farmArea += member.getFarmlandAcreage();
                        machineryNum += member.getMachineryNumber();
                        farmNum += member.getFarmlandNumber();
                    }
                }
                //设置土地面积、土地数量和农机数量
                cooperationInfoDTO.setFarmlandsAcreage(farmArea);
                cooperationInfoDTO.setMachineryCount(machineryNum);
                cooperationInfoDTO.setFarmlandCount(farmNum);
            } else {
                cooperationInfoDTO = null;
            }
        } else {
            logger.error("参数：合作社id的值不能为空！");
            throw new NullParamException("参数：合作社id的值不能为空！");
        }
        return cooperationInfoDTO;
    }

    // 更新合作社信息
    private CooperationDTO update(User user, CooperationDTO cooperationDTO) {
        CooperationDTO dto = new CooperationDTO();
        if (null != cooperationDTO) {
            if (null != cooperationDTO.getAvatar()) {
                try {
                    String avatar = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.COOPERATION_PICTURE, cooperationDTO.getAvatar());
                    user.setAvatar(avatar);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("用户头像保存失败！");
                }
            }
            Cooperation cp = cooperationRepository.getOneByIdCooperation(user.getIdCooperation());
            // 更新合作社
            Cooperation cooperation = new Cooperation();
            cooperation.setIdCooperation(user.getIdCooperation());
            cooperation.setCooperationName(cooperationDTO.getCooperationName());
            cooperation.setDescription(DateUtils.getDateTime() + "—— 社长成功修改了合作社信息。");
            cooperation.setCooperationState(cp.getCooperationState());
            cooperation.setCooperationType(cp.getCooperationType());
            cooperation.setSno(cp.getSno());
            cooperation.setUpdateTime(new Date());
            Address address = new Address();
            if (null != cooperationDTO.getAddressDTO()) {
                if (null != cp) {
                    if (null != cp.getIdAddress()) {
                        //设置更新地址的主键
                        address.setIdAddress(cp.getIdAddress());
                    }
                }
                // 地址中省市县乡四级地址信息不能更改(即使有参数值也不会更新，接收参数仅仅为了避免报错)
                if (StringUtils.isNotBlank(cooperationDTO.getAddressDTO().getProvince())) {
                    address.setProvince(cooperationDTO.getAddressDTO().getProvince());
                }
                if (StringUtils.isNotBlank(cooperationDTO.getAddressDTO().getCity())) {
                    address.setCity(cooperationDTO.getAddressDTO().getCity());

                }
                if (StringUtils.isNotBlank(cooperationDTO.getAddressDTO().getCounty())) {
                    address.setCounty(cooperationDTO.getAddressDTO().getCounty());
                }
                if (StringUtils.isNotBlank(cooperationDTO.getAddressDTO().getTown())) {
                    address.setTown(cooperationDTO.getAddressDTO().getTown());
                }
                // 同步更新地址信息
                if (null != cooperationDTO.getAddressDTO().getLatitude()) {
                    address.setLatitude(cooperationDTO.getAddressDTO().getLatitude());
                }
                if (null != cooperationDTO.getAddressDTO().getLongitude()) {
                    address.setLongitude(cooperationDTO.getAddressDTO().getLongitude());
                }
                if (StringUtils.isNotBlank(cooperationDTO.getAddressDTO().getDetail())) {
                    address.setDetail(cooperationDTO.getAddressDTO().getDetail());
                }
                address.setUpdateDate(new Date());
            }
            Address a = addressRepository.save(address);
            Cooperation c = cooperationRepository.save(cooperation);
            // 更新合作社认证信息
            CooperationInfo cooperationInfo = new CooperationInfo();
            // 查询合作社认证信息的原数据
            CooperationInfo cooInfo = cooperationInfoRepository.getByIdCooperation(cp.getIdCooperation());
            cooperationInfo.setIdCooperation(cp.getIdCooperation());
            cooperationInfo.setBusiness_license(cooperationDTO.getBusiness_license());
            cooperationInfo.setHistoryOrderNum(cooInfo.getHistoryOrderNum());
            if (null != cooperationDTO.getLicense_pic()) {
                try {
                    String licensePic = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.COOPERATION_PICTURE, cooperationDTO.getLicense_pic());
                    cooperationInfo.setLicense_pic(licensePic);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("合作社营业执照图片保存失败！");
                }
            }
            CooperationInfo coopeInfo = cooperationInfoRepository.save(cooperationInfo);
            // 同步日志变更信息
            saveCooperationChangeLog(c.getIdCooperation(), CooperationStateEnum.COMMON.getState(), user.getIdUser(), "id为" + user.getIdUser() + "的用户成功更新了合作社信息。");
            logger.info("id为" + user.getIdUser() + "的用户成功更新了合作社信息。");
            // 返回结果集
            dto.setCooperationName(c.getCooperationName());
            // 设置地址
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setProvince(a.getProvince());
            addressDTO.setCity(a.getCity());
            addressDTO.setCounty(a.getCounty());
            addressDTO.setTown(a.getTown());
            addressDTO.setDetail(a.getDetail());
            addressDTO.setLatitude(a.getLatitude());
            addressDTO.setLongitude(a.getLongitude());
            dto.setAddressDTO(addressDTO);
            dto.setBusiness_license(coopeInfo.getBusiness_license());
            dto.setLicense_url(coopeInfo.getLicense_pic());
        }
        return dto;
    }

}
