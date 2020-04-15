package com.plansolve.farm.model.database.log;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/6/21
 * @Description:
 **/
@Data
@Entity
public class UserErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idUserErrorLog;

    private Long idUser;

    private String mobile;

    private String pageName;

    private String title;

    private String content;

    private Date createTime;

}
