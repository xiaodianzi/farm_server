package com.plansolve.farm.repository.account;

import com.plansolve.farm.model.database.account.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2019/3/28
 * @Description:
 **/
public interface BankRepository extends JpaRepository<Bank, Integer> {

    public Bank findByAcronym(String acronym);

}
