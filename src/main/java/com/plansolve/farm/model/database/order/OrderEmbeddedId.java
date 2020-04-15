package com.plansolve.farm.model.database.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/

@Data
public class OrderEmbeddedId implements Serializable {

    private Long idUserOrder; // 主键（订单）

    private Long idUser; // 主键（用户）

}
