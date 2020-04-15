package com.plansolve.farm.model.database.account;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: 高一平
 * @Date: 2019/3/28
 * @Description:
 **/

@Entity
@Data
public class Bank implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idBank;

    @Column(updatable = false, nullable = false)
    private String bankName; // 银行名称

    @Column(updatable = false, nullable = false)
    private String acronym; // 银行缩写

    @Column(nullable = false)
    private String icon; // 图标路径

}
