package com.plansolve.farm.model.bo.user;

import lombok.Data;

import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/4/30
 * @Description: 农田业务对象
 **/
@Data
public class FarmlandBO {

    private Long idFarmland; // 主键
    private Long idUser; // 农田所属用户

    private String farmlandState; // 农田状态
    private String farmlandName; // 农田名字
    private Float farmlandAcreage; // 农田大小（亩）
    private String farmlandDetail; // 农田描述
    private String pictures; // 农田详情图片

    private Double latitude; // 纬度
    private Double longitude; // 经度
    private String province; // 省
    private String city; // 市
    private String county; // 区县
    private String addressDetail; // 详情

    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间

}
