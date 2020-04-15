package com.plansolve.farm.model.console.account;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: 高一平
 * @Date: 2019/2/22
 * @Description:
 **/
@Data
public class AccountLogConsoleDTO implements Serializable {

    private Long idAccountLog;//主键id

    private String mobile;//用户手机

    private Long idUser;//账户id

    private String accountType;//账户类型

    private BigDecimal amount;//金额

    private String changeType;//资金明细

    private String changeTime;//时间

}
