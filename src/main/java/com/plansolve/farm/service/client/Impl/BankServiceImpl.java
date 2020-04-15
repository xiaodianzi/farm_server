package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.model.database.account.Bank;
import com.plansolve.farm.repository.account.BankRepository;
import com.plansolve.farm.service.client.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/28
 * @Description:
 **/
@Service
public class BankServiceImpl implements BankService {

    @Autowired
    private BankRepository bankRepository;

    /**
     * 查询所有银行信息
     *
     * @return
     */
    @Override
    public List<Bank> findAll() {
        return bankRepository.findAll();
    }

    /**
     * 根据缩写查询银行信息
     *
     * @param acronym
     * @return
     */
    @Override
    public Bank findBankByAcronym(String acronym) {
        return bankRepository.findByAcronym(acronym);
    }
}
