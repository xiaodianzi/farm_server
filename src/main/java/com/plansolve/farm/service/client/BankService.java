package com.plansolve.farm.service.client;

import com.plansolve.farm.model.database.account.Bank;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/28
 * @Description:
 **/
public interface BankService {

    /**
     * 查询所有银行信息
     *
     * @return
     */
    public List<Bank> findAll();

    /**
     * 根据缩写查询银行信息
     *
     * @param acronym
     * @return
     */
    public Bank findBankByAcronym(String acronym);

}
