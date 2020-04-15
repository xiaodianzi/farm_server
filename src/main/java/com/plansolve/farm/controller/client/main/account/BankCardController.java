package com.plansolve.farm.controller.client.main.account;

import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.account.BankCardDTO;
import com.plansolve.farm.model.database.account.Bank;
import com.plansolve.farm.model.database.account.BankCard;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.BankCardService;
import com.plansolve.farm.service.client.BankService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/26
 * @Description:
 **/
@RestController
@RequestMapping(value = "/farm/user/account/bankcard")
public class BankCardController {

    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private BankService bankService;

    /**
     * 获取银行列表
     *
     * @return
     */
    @PostMapping(value = "/bankList")
    public Result bankList() {
        List<Bank> banks = bankService.findAll();
        return ResultUtil.success(banks);
    }

    /**
     * 获取用户银行卡信息
     *
     * @return
     */
    @PostMapping(value = "/list")
    public Result bankCardList() {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        List<BankCard> bankCards = bankCardService.findUserBankCard(user.getIdUser());
        List<BankCardDTO> dtos = bankCardService.loadDTOS(bankCards);
        return ResultUtil.success(dtos);
    }

    /**
     * 添加银行卡
     *
     * @param bankCard 银行卡信息
     * @return
     */
    @PostMapping(value = "/createBankcard")
    public Result create(@Valid BankCardDTO bankCard) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        BankCard card = bankCardService.create(bankCard, user);
        BankCardDTO dto = bankCardService.loadDTO(card);
        return ResultUtil.success(dto);
    }

    /**
     * 删除银行卡
     *
     * @param idBankCard 操作的银行卡号码
     * @return
     */
    @PostMapping(value = "/deleteBankcard")
    public Result delete(Long idBankCard) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        bankCardService.delete(idBankCard, user);
        return ResultUtil.success(null);
    }

}
