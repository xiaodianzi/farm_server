package com.plansolve.farm.model.database.cooperation;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description: 合作社小队
 **/

@Data
@Entity
public class Team implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idTeam; // 主键（数据库自增）

    @Column(nullable = false, updatable = false)
    private Integer idCooperation; // 所属合作社

    @Column(nullable = false, length = 16)
    private String teamName; // 小队名称

    private Long idUser; // 队长

}
