package com.plansolve.farm.controller.client.main;

import com.plansolve.farm.exception.NullParamException;
import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.account.ApplicationDetailDTO;
import com.plansolve.farm.model.client.account.TransactionApplicationDTO;
import com.plansolve.farm.model.database.account.Account;
import com.plansolve.farm.model.database.account.ApplicationDetail;
import com.plansolve.farm.model.database.account.BankCard;
import com.plansolve.farm.model.database.account.TransactionApplication;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.type.TransactionApplicationTypeEnum;
import com.plansolve.farm.service.client.AccountService;
import com.plansolve.farm.service.client.BankCardService;
import com.plansolve.farm.service.client.TransactionApplicationService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.BigDecimalUtil;
import com.plansolve.farm.util.EncryptUtil;
import com.plansolve.farm.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/11
 * @Description:
 **/
@RestController
@RequestMapping(value = "/farm/user/pay")
@Slf4j
public class UserPayController {

    @Autowired
    private TransactionApplicationService applicationService;
    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private AccountService accountService;

    /**
     * 用户申请提现
     *
     * @param money
     * @param idBankCard
     * @param password
     * @return
     */
    @PostMapping(value = "/withdraw")
    public Result withdraw(BigDecimal money, Long idBankCard, String password) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        if (idBankCard == null || idBankCard < 0) {
            log.error("银行卡ID错误");
            throw new ParamErrorException("[银行卡ID错误]");
        }
        BankCard bankCard = bankCardService.findBankCard(idBankCard);
        if (bankCard == null) {
            log.error("银行卡错误");
            throw new ParamErrorException("[该用户没有此银行卡]");
        }
        if (money == null || BigDecimalUtil.lessThanAndEquals(money, new BigDecimal(0))) {
            log.error("提现金额错误");
            throw new ParamErrorException("[提现金额错误]");
        }

        if (password == null || password.trim().isEmpty()) {
            log.error("密码为空");
            throw new NullParamException("[密码不可为空]");
        }
        // 校验用户提现密码
        Account account = accountService.findAccount(user.getIdUser());
        if (!account.getPasswordSetting()) {
            log.error("该用户未设置提现密码]");
            throw new NullParamException("[该用户未设置提现密码]");
        }
        if (EncryptUtil.comparator(password, account.getWithdrawPassword())){
            TransactionApplication application = applicationService.create(user, TransactionApplicationTypeEnum.WITHDRAW.getType(), null, bankCard.getIdBankCard(), money);
            applicationService.withdrawCheck(application.getIdTransactionApplication());
            return ResultUtil.success(null);
        } else {
            log.error("密码不正确");
            throw new ParamErrorException("[密码错误]");
        }
    }

    /**
     * 用户查询自己的申请
     *
     * @return
     */
    @PostMapping(value = "/withdraw/list")
    public Result withdrawList() {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<TransactionApplication> applications = applicationService.findUserTransactionApplications(user, TransactionApplicationTypeEnum.WITHDRAW.getType());
        List<TransactionApplicationDTO> dtos = applicationService.loadDTOS(applications);
        return ResultUtil.success(dtos);
    }

    /**
     * 获取申请详情
     *
     * @param transactionApplicationNo
     * @return
     */
    @PostMapping(value = "/application/details")
    public Result applicationDetails(String transactionApplicationNo) {
        List<ApplicationDetail> details = applicationService.findApplicationDetails(transactionApplicationNo);
        List<ApplicationDetailDTO> dtos = applicationService.loadDetailDTOS(details);
        return ResultUtil.success(dtos);
    }

}
