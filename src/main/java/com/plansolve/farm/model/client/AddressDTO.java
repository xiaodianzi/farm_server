package com.plansolve.farm.model.client;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: 高一平
 * @Date: 2018/6/8
 * @Description: 地址传输对象
 **/
@Data
public class AddressDTO implements Serializable {

    @NotBlank(message = "不能为空[省]")
    private String province; // 省

    @NotBlank(message = "不能为空[市]")
    private String city; // 市

    @NotBlank(message = "不能为空[区县]")
    private String county; // 区县

    /*@NotBlank(message = "不能为空[乡镇]")*/
    private String town; // 乡镇

    @NotNull(message = "不能为空[纬度]")
    private Double latitude; // 纬度

    @NotNull(message = "不能为空[经度]")
    private Double longitude; // 经度

    private String detail; // 详情
}
