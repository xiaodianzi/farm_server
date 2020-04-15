package com.plansolve.farm.model.database.score;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Andrew
 * @Date: 2019/5/10
 * @Description:
 */
@Entity
@Data
public class ExchangePermitUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idExchangePermitUser; //主键（数据库自增）

    @Column(nullable = false, updatable = false)
    private Long idUser; //关联用户id

    @Column(nullable = false)
    private boolean valid; //是否有效

    private String provider;//提供商

    private String location;//地点

    private String remark; //备注

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createTime; //创建日期

}
