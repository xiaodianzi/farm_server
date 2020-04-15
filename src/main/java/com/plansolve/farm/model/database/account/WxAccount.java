package com.plansolve.farm.model.database.account;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/4/11
 * @Description: 用户微信钱包
 **/

@Entity
@Data
public class WxAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idWxAccount;

    @Column(nullable = false, updatable = false)
    private Long idUser; // 用户ID

    @Column(nullable = false, updatable = false)
    private String openId;

}
