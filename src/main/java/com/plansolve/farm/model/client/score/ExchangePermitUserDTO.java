package com.plansolve.farm.model.client.score;

import lombok.Data;
import javax.persistence.Column;
import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2019/3/28
 * @Description:
 */
@Data
public class ExchangePermitUserDTO implements Serializable {

    @Column(nullable = false)
    private Long idExchangePermitUser; //主键（数据库自增）

    @Column(nullable = false)
    private Long idUser; //关联用户id

    @Column(nullable = false)
    private String provider;//提供商

    @Column(nullable = false)
    private String mobile;//手机号

    @Column(nullable = false)
    private String location;//地点

    private String remark; //备注

    @Column(nullable = false)
    private String createTime; //授权时间

}
