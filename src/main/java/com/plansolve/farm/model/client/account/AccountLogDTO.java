package com.plansolve.farm.model.client.account;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 高一平
 * @Date: 2019/4/9
 * @Description: id，io，amount，time
 **/
@Data
public class AccountLogDTO {

    private Long idAccountLog;

    private String time;

    private Integer io;

    private String changeType;

    private BigDecimal amount;

}
