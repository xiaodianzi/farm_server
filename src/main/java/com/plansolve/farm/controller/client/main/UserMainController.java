package com.plansolve.farm.controller.client.main;

import com.plansolve.farm.controller.client.BaseController;
import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.AddressDTO;
import com.plansolve.farm.model.client.order.OrderDTO;
import com.plansolve.farm.model.client.order.OrderSimpleDTO;
import com.plansolve.farm.model.client.order.OrderStatisticsDTO;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.client.user.UserDescDTO;
import com.plansolve.farm.model.client.user.UserIdCardDTO;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.UserAppVersion;
import com.plansolve.farm.repository.user.UserAppVersionRepository;
import com.plansolve.farm.service.client.*;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/6
 * @Description: 用户登录后所能进行的操作
 **/
@RestController
@RequestMapping(value = "/farm/user")
public class UserMainController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(UserMainController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private MachineryService machineryService;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserAppVersionRepository versionRepository;
    @PostMapping(value = "/version")
    public void saveVersion(String version){
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        UserAppVersion appVersion = new UserAppVersion();
        appVersion.setIdUser(user.getIdUser());
        appVersion.setVersion(version);
        versionRepository.save(appVersion);
    }

    /**
     * 获取用户详细信息
     *
     * @return
     */
    @PostMapping(value = "/getUser")
    public Result getUser() {
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
        UserDTO userDTO = userService.findUser(user.getIdUser(), true);
        return ResultUtil.success(userDTO);
    }

    /**
     * 用户退出登录
     *
     * @return
     */
    @PostMapping(value = "/logout")
    public Result logout() {
        // 清楚session中所有信息
        AppHttpUtil.getSession().invalidate();
        return ResultUtil.success(null);
    }

    /**
     * 用户销户
     *
     * @return
     */
    @PostMapping(value = "/delete")
    public Result deleteUser() {
        // 检查是否经过验证码验证
        HttpSession session = AppHttpUtil.getSession();
        String mobile = (String) session.getAttribute(SysConstant.VALIDATION_MOBILE);
        Date expireDate = (Date) session.getAttribute(SysConstant.VALIDATION_EXPIRE_TIME);
        if (mobile == null || mobile.isEmpty() || expireDate == null) {
            logger.error("未进行短信验证");
            throw new PermissionException("[您未进行短信验证]");
        } else if (expireDate.before(new Date())) {
            logger.error("短信验证超过5分钟时效，需重新验证");
            throw new ParamErrorException("[请重新进行短信验证]");
        } else {
            User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
            userService.deleteUser(user.getIdUser());
            return logout();
        }
    }

    /**
     * 用户提交验证信息
     *
     * @param userIdCardDTO 用户验证信息（现提交即审核成功）
     * @return
     */
    @PostMapping(value = "/certificate")
    public Result certificate(@Valid UserIdCardDTO userIdCardDTO) {
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
        userService.certificate(user.getIdUser(), userIdCardDTO);
        return ResultUtil.success(null);
    }

    /**
     * 用户获取验证信息
     *
     * @return
     */
    @PostMapping(value = "/getCertificate")
    public Result getCertificate() {
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);

        user = userService.findUser(user.getIdUser());
        session.setAttribute(SysConstant.CURRENT_USER, user);
        UserIdCardDTO userIdCardDTO = userService.getCertificate(user.getIdUser());
        userIdCardDTO.setState(user.getUserState());
        if (userIdCardDTO.getIdCardNo() == null) userIdCardDTO.setIdCardNo("");
        if (userIdCardDTO.getRealname() == null) userIdCardDTO.setRealname("");
        return ResultUtil.success(userIdCardDTO);
    }

    /**
     * 用户更改头像
     *
     * @param picture 头像图片
     * @return 头像图片名称
     */
    @PostMapping(value = "/changeAvatar")
    public Result changeAvatar(MultipartFile picture) throws IOException {
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        user = userService.changeAvatar(user.getIdUser(), picture);
        return ResultUtil.success(user.getAvatar());
    }

    /**
     * 用户更改
     *
     * @param userDescDTO
     * @param address
     * @return
     */
    @PostMapping(value = "/changeUserInfo")
    public Result changeUserInfo(UserDescDTO userDescDTO, @RequestParam(required = false)AddressDTO address) {
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        userService.changeUserDesc(user.getIdUser(), userDescDTO, address);
        return ResultUtil.success(null);
    }

    /**
     * 根据用户手机号码获取用户信息
     *
     * @param mobile
     * @return
     */
    @PostMapping(value = "/getUserByMobile")
    public Result getUserByMobile(String mobile) {
        User user = userService.findByMobile(mobile);
        UserDTO userDTO = userService.loadDTO(user, false);
        return ResultUtil.success(userDTO);
    }

    /**
     * 查询指定用户是否拥有指定类型的农机
     *
     * @param mobile
     * @param machineryType
     * @return
     */
    @PostMapping(value = "/checkUserMachineryType")
    public Result checkUserMachineryType(String mobile, String machineryType) {
        User user = userService.findByMobile(mobile);
        Boolean userResult = machineryService.checkSingleUserMachineryType(user, machineryType);
        if (userResult) {
            return ResultUtil.success(true);
        } else {
            // 查询是否是社长，若是，查询该社社员是否拥有该农机
            boolean proprieter = cooperationService.proprieter(user);
            if (proprieter) {
                List<User> members = cooperationService.members(user.getIdCooperation());
                if (members != null && members.size() > 0) {
                    for (User member : members) {
                        if (machineryService.checkSingleUserMachineryType(member, machineryType)) {
                            return ResultUtil.success(true);
                        }
                    }
                }
            }
            return ResultUtil.success(false);
        }
    }

    /**
     * 发送意见反馈
     *
     * @param feedback
     * @return
     */
    @PostMapping(value = "/sendFeedback")
    public Result sendFeedback(String feedback) {
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        feedbackService.save(feedback, user);
        return ResultUtil.success(null);
    }

    /**
     * 个人成长--历史下单
     * 用户所有已完成的订单
     *
     * @return
     */
    @PostMapping(value = "/farmer/getFinishedOrders")
    public Result getOrderWithFarmerFinished() {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<UserOrder> orders = orderService.getOrderWithFarmerFinished(user);
        List<OrderDTO> orderDTOS = orderService.loadDTOs(orders);
        return ResultUtil.success(orderDTOS);
    }

    /**
     * 个人成长--历史接单
     * 获取用户所有已完成的接单任务
     *
     * @return
     */
    @PostMapping(value = "/operator/getUserStatisticalMsg")
    public Result getUserStatisticalMsg() {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<UserOrder> orders = orderService.getStatisticalMsg(user);
        List<OrderDTO> orderDTOS = orderService.loadDTOs(orders);
        return ResultUtil.success(orderDTOS);
    }

    /**
     * 作业统计--我的统计
     *
     * @param date
     * @return
     */
    @PostMapping(value = "/StatisticalMsg")
    public Result getUserOrdersStatisticalMsg(String date) {
        if (date != null && date.isEmpty() == false) {
            User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
            List<OrderStatisticsDTO> dtos = orderService.getUserOrdersStatisticalMsg(user, date);
            return ResultUtil.success(dtos);
        } else {
            throw new NullParamException("[统计年份不可为空]");
        }
    }

    /**
     * 作业统计--我的统计--子
     *
     * @param date
     * @return
     */
    @PostMapping(value = "/StatisticalMsg/date")
    public Result getUserOrdersDateStatisticalMsg(Date date) {
        if (date != null) {
            User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
            List<UserOrder> orders = orderService.getStatisticalMsg(user, date);
            List<OrderSimpleDTO> orderSimpleDTOS = orderService.loadSimpleDTOs(orders);
            return ResultUtil.success(orderSimpleDTOS);
        } else {
            throw new NullParamException("[统计年份不可为空]");
        }
    }

    /**
     * 作业统计-合作社统计
     *
     * @param month
     * @return
     */
    @PostMapping("/cooperation/statisticsCooperativeOrder")
    public Result statisticsCooperativeOrder(String month) {
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.statisticsCooperativeOrder(user, month);
    }

    /**
     * 作业统计-合作社统计（按日期查询）
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @PostMapping("/cooperation/statisticsCooperativeOrderByDate")
    public Result statisticsCooperativeOrders(String startTime, String endTime) {
        User user = (User) AppHttpUtil.getSession().getAttribute(SysConstant.CURRENT_USER);
        return cooperationService.statisticsCooperativeOrders(user, startTime, endTime);
    }

}

