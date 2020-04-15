package com.plansolve.farm.service.client;

import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.client.CooperationDTO;
import com.plansolve.farm.model.client.CooperationInfoDTO;
import com.plansolve.farm.model.client.MembersDTO;
import com.plansolve.farm.model.database.cooperation.Cooperation;
import com.plansolve.farm.model.database.cooperation.InviteRelationShip;
import com.plansolve.farm.model.database.cooperation.Team;
import com.plansolve.farm.model.database.user.User;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: Andrew
 * @Date: 2018/6/12
 * @Description: 合作社相关接口
 */
public interface CooperationService {

    /**
     * 创建合作社
     *
     * @param user        平台正式用户
     * @param cooperation
     * @return
     */
    public Result createCooperation(User user, CooperationDTO cooperation);

    /**
     * 申请加入合作社
     *
     * @param user           申请者
     * @param cooperationNum 合作社编号
     * @return
     */
    public Result joinCooperation(User user, String cooperationNum);

    /**
     * 根据社长手机号查询合作社
     *
     * @param mobile 社长手机号
     * @return
     */
    public Result queryCooperationByMobile(String mobile);

    /**
     * 解散合作社
     *
     * @param user 社长
     * @return
     */
    public Result dissolveCooperation(User user, String cooperationNum);

    /**
     * 批准加入合作社
     *
     * @param user   社长
     * @param member 申请用户
     * @return
     */
    public Result approve(User user, MembersDTO member);

    /**
     * 个人退出合作社
     *
     * @param user 社员
     * @return
     */
    public Result exit(User user);

    /**
     * 给申请加入合作社的新成员分配角色
     *
     * @param user：社长
     * @param members：申请者
     * @return
     */
    public Result appointRole(User user, MembersDTO members);

    /**
     * 根据主键查询合作社
     *
     * @param idCooperation 合作社主键
     * @return 合作社详情与地址等基本信息
     */
    public CooperationInfoDTO findById(Integer idCooperation);

    /**
     * 根据主键查询合作社
     *
     * @param idCooperation
     * @return
     */
    public Cooperation getById(Integer idCooperation);

    /**
     * 根据合作社编号查询社员信息
     *
     * @param user
     * @param cooperationNum
     * @return
     */
    public Result listMembers(User user, String cooperationNum);

    /**
     * 根据合作社id查询合作社成员
     * @param idCooperation
     * @return
     */
    public List<User> members(Integer idCooperation);

    /**
     * 根据合作社编号查询所有小队农机手信息列表并排序
     *
     * @param cooperationNum
     * @return
     */
    public Result listOperators(User user, String cooperationNum, String startTime, Integer days);

    /**
     * 提供内部调用接口方法：查询小队农机手信息列表，直接返回map集合
     *
     * @param cooperationNum
     * @return
     */
    public Map<String, List<MembersDTO>> teamOperators(String cooperationNum, String startTime, Integer days);

    /**
     * 查询所有已经发送加入合作社申请的新社员
     *
     * @param user 社长
     * @return
     */
    public Result listNewMembers(User user);

    /**
     * 查询当前用户关联的合作社
     *
     * @param user
     * @return
     */
    public Result myCooperation(User user);

    /**
     * 判断是否是社长
     *
     * @param user
     * @return
     */
    public Result checkProprieterRole(User user);

    /**
     * 判断是否是队长
     *
     * @param user
     * @return
     */
    public Result checkCaptainRole(User user);

    /**
     * 查询合作社社长信息
     *
     * @param idCooperation
     * @return
     */
    public User findProprieterById(Integer idCooperation);

    /**
     * 判断是否是社长(内部接口)
     *
     * @param user
     * @return
     */
    public boolean proprieter(User user);

    /**
     * 判断是否是队长(内部接口)
     *
     * @param user
     * @return
     */
    public boolean captain(User user);

    /**
     * 查询用户所在合作社的社长信息
     *
     * @param user 所查社员
     * @return
     */
    public User findProprieterByUser(User user);

    /**
     * 修改合作社信息
     * @param user
     * @param cooperation
     * @return
     */
    public Result updateCooperation(User user, CooperationDTO cooperation);

    /**
     * 社长查询合作社的小队列表
     * @param user
     * @return
     */
    public Result teamlist(User user);

    /**
     * 根据手机号查询社员的农机信息
     * @param mobile
     * @return
     */
    public Result findMachineryInfo(User user, String mobile);

    /**
     * 根据手机号查询社员的农田信息
     * @param mobile
     * @return
     */
    public Result findFarmInfo(User user, String mobile);

    /**
     * 查询社员加入合作社的信息
     * @param user
     * @return
     */
    public InviteRelationShip findInviterState(User user);

    /**
     * 社长根据手机号查询已经提交加入申请的社员信息
     * @param user
     * @param mobile
     * @return
     */
    public Result findInviterInfo(User user, String mobile);

    /**
     * 获取社员下给合作社的订单
     * @param user
     * @param mobile
     * @return
     */
    public Result toCooperationOrder(User user, String mobile);

    /**
     * 获取当前用户的合作社订单
     * @param user
     * @return
     */
    public Result finishedCooperativeOrder(User user, String date);

    /**
     * 社长查看社员未完成的协同作业订单
     * @param user
     * @param mobile
     * @return
     */
    public Result unfinishedCooperativeOrder(User user, String mobile);

    /**
     * 统计用户所在合作社的月度订单信息
     * @param user
     * @param month
     * @return
     */
    public Result statisticsCooperativeOrder(User user, String month);

    /**
     * 查询合作社用户的小队信息
     * @param user
     * @return
     */
    public Team queryTeamInfo(User user);

    /**
     * 社长查询可以邀请的农机手
     * @param user 当前用户
     * @param orderNum 订单号
     * @return
     */
    public Result queryAvailableInviters(User user, String orderNum);

    /**
     * 合作社统计信息表
     * @param user
     * @param getAll 是否全部到处
     * @param startTime 起始月份
     * @param endTime 结束月份
     * @return
     */
    public Map<String, List> statisticsOrders(User user, boolean getAll, Date startTime, Date endTime);

    /**
     * 根据合作社编号把社长的信息组装后返回dto
     * @param cooperationNum
     * @return
     */
    public MembersDTO dtoProprieter(String cooperationNum);

    /**
     * 社长开除指定社员
     * @param user 社长
     * @param mobile 社员手机号
     * @return
     */
    public Result dismissMember(User user, String mobile);

    /**
     * 查询当前社员状态
     * @param user
     * @return
     */
    public Result checkMemberState(User user, String cooperationNum);

    /**
     * 拒绝加入合作社
     * @param user 社长
     * @param mobile 用户手机号
     * @return
     */
    public Result refuseNewMember(User user, String mobile);

    /**
     * 作业统计-合作社统计（日期查询）
     * @param user
     * @param startTime 查询起始日
     * @param endTime 查询截止日
     * @return
     */
    public Result statisticsCooperativeOrders(User user, String startTime, String endTime);

    /**
     * 作业统计-合作社统计-社员订单详情
     * @param user 社长
     * @param mobile 社员手机号
     * @param startTime 起始日期
     * @param endTime 结束日期
     * @return
     */
    public Result statisticsMemberOrder(User user, String mobile, String startTime, String endTime);

}
