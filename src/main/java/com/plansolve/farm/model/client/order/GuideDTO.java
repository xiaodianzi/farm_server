package com.plansolve.farm.model.client.order;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: 高一平
 * @Date: 2018/6/20
 * @Description:
 **/
public class GuideDTO {

    @NotBlank(message = "领路人名称不能为空")
    private String guideName; // 引路人名称

    @NotBlank(message = "领路人联系方式不能为空")
    private String guideMobile; // 引路人联系方式

    @NotBlank(message = "集合地址不能为空")
    private String assemblyAddress; // 集合地址

    @NotNull(message = "集合地址（纬度）不能为空")
    private Double latitude; // 集合地址（纬度）

    @NotNull(message = "集合地址（经度）不能为空")
    private Double longitude; // 集合地址（经度）

    public String getGuideName() {
        return guideName;
    }

    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }

    public String getGuideMobile() {
        return guideMobile;
    }

    public void setGuideMobile(String guideMobile) {
        this.guideMobile = guideMobile;
    }

    public String getAssemblyAddress() {
        return assemblyAddress;
    }

    public void setAssemblyAddress(String assemblyAddress) {
        this.assemblyAddress = assemblyAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "GuideDTO{" +
                "guideName='" + guideName + '\'' +
                ", guideMobile='" + guideMobile + '\'' +
                ", assemblyAddress='" + assemblyAddress + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
