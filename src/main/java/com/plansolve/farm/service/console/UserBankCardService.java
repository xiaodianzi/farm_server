package com.plansolve.farm.service.console;

import com.plansolve.farm.model.database.account.BankCard;

/**
 * @Author: 高一平
 * @Date: 2019/4/2
 * @Description:
 **/
public interface UserBankCardService {

    /**
     * 查询银行卡
     *
     * @param idBankCard
     * @return
     */
    public BankCard findBankCard(Long idBankCard);

}
