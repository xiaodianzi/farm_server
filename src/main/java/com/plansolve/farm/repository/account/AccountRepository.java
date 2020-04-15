package com.plansolve.farm.repository.account;

import com.plansolve.farm.model.database.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2019/2/19
 * @Description:
 **/
public interface AccountRepository extends JpaRepository<Account, Long> {

    public Account findByIdUser(Long idUser);

}
