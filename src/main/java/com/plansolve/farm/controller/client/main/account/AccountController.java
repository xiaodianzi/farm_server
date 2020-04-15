package com.plansolve.farm.controller.client.main.account;

import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.account.AccountDTO;
import com.plansolve.farm.model.client.account.AccountLogDTO;
import com.plansolve.farm.model.client.account.BankCardDTO;
import com.plansolve.farm.model.database.account.Account;
import com.plansolve.farm.model.database.account.BankCard;
import com.plansolve.farm.model.database.log.AccountLog;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.UserInfo;
import com.plansolve.farm.model.enums.type.AccountLogTypeEnum;
import com.plansolve.farm.service.client.AccountService;
import com.plansolve.farm.service.client.BankCardService;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.service.common.CaptchaService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.EncryptUtil;
import com.plansolve.farm.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/26
 * @Description:
 **/

@Slf4j
@RestController
@RequestMapping(value = "/farm/user/account")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private UserService userService;

    /**
     * 获取用户账户
     *
     * @return
     */
    @PostMapping(value = "/user")
    public Result getUserAccount() {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        Account account = accountService.findAccount(user.getIdUser());
        AccountDTO dto = accountService.loadDTO(account);
        List<BankCard> bankCards = bankCardService.findUserBankCard(user.getIdUser());
        if (bankCards != null && bankCards.size() > 0) {
            dto.setHaveBankCard(true);
            List<BankCardDTO> dtos = bankCardService.loadDTOS(bankCards);
            dto.setBankCards(dtos);
        } else {
            dto.setHaveBankCard(false);
        }
        return ResultUtil.success(dto);
    }

    /**
     * 初始化提现密码
     *
     * @param password 提现密码
     * @return
     */
    @PostMapping(value = "/initWithdrawPassword")
    public Result initWithdrawPassword(String password) {
        if (StringUtils.isNotBlank(password) && password.length() == 6) {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            accountService.setWithdrawPassword(user.getIdUser(), password);
            return ResultUtil.success(null);
        } else {
            log.error("初始化密码不正确");
            throw new ParamErrorException("[初始化密码不正确]");
        }
    }

    /**
     * 设置提现密码（手机验证码验证）
     *
     * @param captcha
     * @param password
     * @return
     */
    @PostMapping(value = "/setWithdrawPassword")
    public Result setWithdrawPassword(String captcha, String password, String idCardNo) {
        if (captcha == null || captcha.isEmpty()) {
            log.error("验证码为空");
            throw new ParamErrorException("[验证码不能为空]");
        } else {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            if (captchaService.checkCaptcha(user.getMobile(), captcha)) {
                UserInfo userInfo = userService.findUserInfo(user.getIdUser());
                if (userInfo == null) {
                    throw new PermissionException("该用户未审核");
                } else {
                    if (userInfo.getIdCardNo().trim().equals(idCardNo.trim())) {
                        accountService.setWithdrawPassword(user.getIdUser(), password);
                        return ResultUtil.success(null);
                    } else {
                        throw new ParamErrorException("[身份证号码校验失败]");
                    }
                }
            } else {
                log.error("[用户手机验证码不匹配]");
                throw new ParamErrorException("[手机验证码校验失败]");
            }
        }
    }

    /**
     * 修改提现密码
     *
     * @param originalPassword 原始密码
     * @param password         新密码
     * @return
     */
    @PostMapping(value = "/resetWithdrawPassword")
    public Result resetWithdrawPassword(String originalPassword, String password) {
        if (StringUtils.isNotBlank(originalPassword) && StringUtils.isNotBlank(password)) {
            User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
            Account account = accountService.findAccount(user.getIdUser());
            String pwd = account.getWithdrawPassword();
            if (StringUtils.isNotBlank(pwd)) {
                Boolean flag = EncryptUtil.comparator(originalPassword, pwd);
                if (flag && password.length() == 6) {
                    accountService.setWithdrawPassword(user.getIdUser(), password);
                    return ResultUtil.success(null);
                } else {
                    log.error("重置密码不正确");
                    throw new ParamErrorException("[重置密码不正确]");
                }
            } else {
                log.error("原始密码不存在");
                throw new ParamErrorException("[原始密码不存在]");
            }
        } else {
            log.error("参数错误");
            throw new ParamErrorException("");
        }
    }

    /**
     * 获取账户明细
     *
     * @return
     */
    @PostMapping(value = "/logs")
    public Result getLogs(@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "20") Integer pageSize) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<AccountLog> accountLogs = accountService.getAccountLogs(user, pageNo, pageSize);
        List<AccountLogDTO> dtos = accountService.loadDTOS(accountLogs);
        return ResultUtil.success(dtos);
    }

    /**
     * 农机手订单收入明细
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/income/order/logs")
    public Result getOrderIncomeLogs(@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "20") Integer pageSize) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<AccountLog> accountLogs = accountService.getAccountLogsByType(user, AccountLogTypeEnum.INCOME.getType(), pageNo, pageSize);
        List<AccountLogDTO> dtos = accountService.loadDTOS(accountLogs);
        return ResultUtil.success(dtos);
    }

}
