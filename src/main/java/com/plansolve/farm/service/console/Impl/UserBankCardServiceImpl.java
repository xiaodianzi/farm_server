package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.model.database.account.BankCard;
import com.plansolve.farm.repository.account.BankCardRepository;
import com.plansolve.farm.service.console.UserBankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 高一平
 * @Date: 2019/4/2
 * @Description:
 **/

@Service
public class UserBankCardServiceImpl implements UserBankCardService {

    @Autowired
    private BankCardRepository bankCardRepository;

    /**
     * 查询银行卡
     *
     * @param idBankCard
     * @return
     */
    @Override
    public BankCard findBankCard(Long idBankCard) {
        return bankCardRepository.findByIdBankCard(idBankCard);
    }
}
