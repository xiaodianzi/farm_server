package com.plansolve.farm.model.client.user;

import com.plansolve.farm.model.client.AddressDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: 高一平
 * @Date: 2018/6/8
 * @Description: 农田传输对象
 **/
public class FarmlandDTO implements Serializable {

    private String farmlandNo; // 农田编号

    private AddressDTO address; // 农田地址

    @NotBlank(message = "农田名称不能为空")
    private String farmlandName; // 农田名字

    @NotNull(message = "农田大小不能为空")
    private Float farmlandAcreage; // 农田大小（亩）

    private String farmlandState; // 农田状态

    private String pictures; // 农田详情图片

    private String detail;

    public String getFarmlandNo() {
        return farmlandNo;
    }

    public void setFarmlandNo(String farmlandNo) {
        this.farmlandNo = farmlandNo;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public String getFarmlandName() {
        return farmlandName;
    }

    public void setFarmlandName(String farmlandName) {
        this.farmlandName = farmlandName;
    }

    public Float getFarmlandAcreage() {
        return farmlandAcreage;
    }

    public void setFarmlandAcreage(Float farmlandAcreage) {
        this.farmlandAcreage = farmlandAcreage;
    }

    public String getFarmlandState() {
        return farmlandState;
    }

    public void setFarmlandState(String farmlandState) {
        this.farmlandState = farmlandState;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "FarmlandDTO{" +
                "farmlandNo='" + farmlandNo + '\'' +
                ", address=" + address +
                ", farmlandName='" + farmlandName + '\'' +
                ", farmlandAcreage=" + farmlandAcreage +
                ", farmlandState='" + farmlandState + '\'' +
                ", pictures='" + pictures + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
