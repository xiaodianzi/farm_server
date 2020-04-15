package com.plansolve.farm.model.database;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/4/1
 * @Description:
 **/

@Data
@Entity
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idMessage; // 主键（数据库自增）

    private String mobile; // 发送手机号码

    private String detail; // 发送内容

    private Boolean isSuccess; // 是否成功

    private Date createTime; // 发送时间

}
