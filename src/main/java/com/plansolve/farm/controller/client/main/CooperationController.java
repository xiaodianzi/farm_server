package com.plansolve.farm.controller.client.main;

import com.plansolve.farm.exception.LoginException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.AddressDTO;
import com.plansolve.farm.model.client.CooperationDTO;
import com.plansolve.farm.model.client.CooperationInfoDTO;
import com.plansolve.farm.model.client.MembersDTO;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.repository.user.UserRepository;
import com.plansolve.farm.service.client.CooperationService;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2018/6/12
 * @Description: 合作社
 */

@RestController
@RequestMapping("/farm/cooperation")
public class CooperationController {

    @Autowired
    private CooperationService cooperationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * 创建合作社
     * @param cooperation
     * @return
     */
    @PostMapping("/create")
    public Result createCooperation(@Valid CooperationDTO cooperation, @Valid AddressDTO addressDTO){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        cooperation.setAddressDTO(addressDTO);
        return cooperationService.createCooperation(user, cooperation);
    }

    /**
     * 申请加入合作社
     * @param cooperationNum
     * @return
     */
    @PostMapping("/join")
    public Result joinCooperation(String cooperationNum){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.joinCooperation(user, cooperationNum);
    }

    /**
     * 批准加入合作社
     * @param member 申请用户
     * @return
     */
    @PostMapping("/approve")
    public Result approve(MembersDTO member){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.approve(user, member);
    }

    /**
     * 给申请加入合作社的新成员分配角色
     * @param members
     * @return
     */
    @PostMapping("/appoint")
    public Result appointTeamRole(MembersDTO members){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.appointRole(user, members);
    }

    /**
     * 个人退出合作社
     * @return
     */
    @PostMapping("/exit")
    public Result exit(){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.exit(user);
    }

    /**
     * 根据社长手机号查询合作社
     * @param mobile
     * @return
     */
    @PostMapping("/information")
    public Result findOneByMobile(String mobile){
        return cooperationService.queryCooperationByMobile(mobile);
    }

    /**
     * 根据合作社编号查询社员信息
     * @param cooperationNum
     * @return
     */
    @PostMapping("/memberlist")
    public Result listMembers(String cooperationNum){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.listMembers(user, cooperationNum);
    }

    /**
     * 解散合作社
     * @return
     */
    @PostMapping("/dissolve")
    public Result dissolveCooperation(String cooperationNum){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.dissolveCooperation(user, cooperationNum);
    }

    /**
     * 查询合作社所有小队中农机手成员列表
     * @return
     */
    @PostMapping("/operators")
    public Result listOperators(String cooperationNum, String startTime, Integer days){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.listOperators(user, cooperationNum, startTime, days);
    }

    /**
     * 查询合作社小队信息列表
     * @return
     */
    @PostMapping("/teamlist")
    public Result teamlist(){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.teamlist(user);
    }

    /**
     * 查询所有已经发送了加入合作社申请用户信息
     * @return
     */
    @PostMapping("/newmembers")
    public Result listNewMembers(){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.listNewMembers(user);
    }

    /**
     * 查询当前用户关联的合作社信息
     * @return 合作社信息
     */
    @PostMapping("/mine")
    public Result myCooperation(){
        List<MembersDTO> members = new ArrayList<>();
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        if(null != user){
            Result result = cooperationService.myCooperation(user);
            if(null != user.getIdCooperation()){
                MembersDTO proprieterDto = cooperationService.dtoProprieter(user.getIdCooperation().toString());
                members.add(proprieterDto);
                CooperationInfoDTO cooperationInfoDTO = (CooperationInfoDTO)result.getData();
                List<MembersDTO> membersDTOS = cooperationInfoDTO.getMembers();
                if(null !=  membersDTOS && membersDTOS.size() > 0){
                    for (MembersDTO member: membersDTOS) {
                        members.add(member);
                    }
                }
                cooperationInfoDTO.setMembers(members);
                result.setData(cooperationInfoDTO);
            }
            return result;
        }
        throw new LoginException("您还未登陆，请登陆后再试试吧！");
    }

    /**
     * 修改合作社
     * @param cooperation
     * @param addressDTO
     * @return
     */
    @PostMapping("/update")
    public Result updateCooperation(@Valid CooperationDTO cooperation, @Valid AddressDTO addressDTO){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        cooperation.setAddressDTO(addressDTO);
        return cooperationService.updateCooperation(user, cooperation);
    }

    /**
     * 查询自己是否是社长
     * @return
     */
    @PostMapping("/proprieter")
    public Result proprieterRole(){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.checkProprieterRole(user);

    }

    /**
     * 查询自己是否是队长
     * @return
     */
    @PostMapping("/captain")
    public Result captainRole(){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.checkCaptainRole(user);
    }

    /**
     * 根据手机号查询社员的农机信息
     * @param mobile
     * @return
     */
    @PostMapping("/machineryInfo")
    public Result findMachineryInfo(String mobile){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.findMachineryInfo(user, mobile);
    }

    /**
     * 根据手机号查询社员的农田信息
     * @param mobile
     * @return
     */
    @PostMapping("/farmlandInfo")
    public Result findFarmInfo(String mobile){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.findFarmInfo(user, mobile);
    }

    /**
     * 根据手机号查询提交了加入申请的社员信息
     * @param mobile
     * @return
     */
    @PostMapping("/inviterInfo")
    public Result findInviterInfo(String mobile){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.findInviterInfo(user, mobile);
    }

    /**
     * 获取社员下给合作社的订单
     * @param mobile
     * @return
     */
    @PostMapping("/toCooperationOrder")
    public Result toCooperationOrder(String mobile){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.toCooperationOrder(user, mobile);
    }

    /**
     * 获取当前用户的合作社订单
     * @return
     */
    @PostMapping("/finishedCooperativeOrder")
    public Result finishedCooperativeOrder(String date){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.finishedCooperativeOrder(user, date);
    }

    /**
     * 获取社员未完成的合作社的订单
     * @param mobile
     * @return
     */
    @PostMapping("/unfinishedCooperativeOrder")
    public Result unfinishedCooperativeOrder(String mobile){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.unfinishedCooperativeOrder(user, mobile);
    }

    /**
     * 社长查询可以邀请的农机手
     * @param orderNum 订单号
     * @return
     */
    @PostMapping("/queryAvailableInviters")
    public Result queryInviters(String orderNum){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.queryAvailableInviters(user, orderNum);
    }

    /**
     * 社长开除指定社员
     * @param mobile 社员手机号
     * @return
     */
    @PostMapping("/dismissMember")
    public Result dismissMember(String mobile){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.dismissMember(user, mobile);
    }

    /**
     * 查询当前社员状态
     * @return
     */
    @PostMapping("/memberState")
    public Result checkMemberState(String cooperationNum){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.checkMemberState(user, cooperationNum);

    }

    /**
     * 拒绝加入合作社
     * @param mobile 申请用户手机号
     * @return
     */
    @PostMapping("/refuseNewMember")
    public Result refuseNewMember(String mobile){
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.refuseNewMember(user, mobile);
    }

    /**
     * 实时更新指定用户信息
     * @param mobile
     * @return
     */
    @PostMapping("/updateUserInfo")
    public Result updateUserInfo(String mobile){
        User user = new User();
        if(StringUtils.isNotBlank(mobile)){
            user = userRepository.findByMobile(mobile);
            User u =  userService.changeCooperationRelation(user);
            return ResultUtil.success(u);
        }
        return ResultUtil.success(user);
    }

    /**
     * 作业统计-合作社统计-社员订单详情
     * @param mobile
     * @param startTime
     * @param endTime
     * @return
     */
    @PostMapping("/statisticsMemberOrder")
    public Result statisticsMemberOrder(String mobile, String startTime, String endTime) {
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.statisticsMemberOrder(user, mobile, startTime, endTime);
    }

}
