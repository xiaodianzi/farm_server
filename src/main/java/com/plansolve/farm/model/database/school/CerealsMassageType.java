package com.plansolve.farm.model.database.school;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: 高一平
 * @Date: 2019/5/15
 * @Description:
 **/

@Entity
@Data
public class CerealsMassageType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idCerealsMassageType; // 主键（数据库自增）

    @Column(nullable = false)
    private String crop;

    private String picture; // 新闻相关图片

}
