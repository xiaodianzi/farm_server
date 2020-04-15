package com.plansolve.farm.model.database.cooperation;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 合作社详情
 **/

@Data
@Entity
public class CooperationInfo implements Serializable {

    @Id
    @Column(updatable = false)
    private Integer idCooperation; // 主键

    @Column(nullable = false)
    private Integer historyOrderNum; // 历史订单数

    @Column(length = 64)
    private String business_license; // 合作社营业执照号

    private String license_pic; // 营业执照照片

}
