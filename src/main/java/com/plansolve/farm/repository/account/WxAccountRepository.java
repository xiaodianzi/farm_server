package com.plansolve.farm.repository.account;

import com.plansolve.farm.model.database.account.WxAccount;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 高一平
 * @Date: 2019/4/11
 * @Description:
 **/
public interface WxAccountRepository extends JpaRepository<WxAccount, Long> {

    public WxAccount findByIdUserAndAndOpenId(Long idUser, String openId);

}
