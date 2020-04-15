package com.plansolve.farm.model.client;

import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @Author: Andrew
 * @Date: 2018/6/12
 * @Description: 创建合作社输入参数
 */
public class CooperationDTO {

    private String name; // 姓名

    @Size(min = 11, max = 11, message = "请输入正确的手机号码")
    private String mobile;

    private MultipartFile avatar; // 用户头像地址

    @NotBlank(message = "合作社名称不能为空")
    private String cooperationName; // 合作社名称

    private AddressDTO addressDTO; //地址

    private String business_license; // 合作社营业执照号

    private MultipartFile license_pic; // 营业执照照片

    private String license_url; // 营业执照图片路径

    private String cooperationNum; // 合作社编号

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public MultipartFile getAvatar() {
        return avatar;
    }

    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }

    public String getCooperationName() {
        return cooperationName;
    }

    public void setCooperationName(String cooperationName) {
        this.cooperationName = cooperationName;
    }

    public AddressDTO getAddressDTO() {
        return addressDTO;
    }

    public void setAddressDTO(AddressDTO addressDTO) {
        this.addressDTO = addressDTO;
    }

    public String getBusiness_license() {
        return business_license;
    }

    public void setBusiness_license(String business_license) {
        this.business_license = business_license;
    }

    public MultipartFile getLicense_pic() {
        return license_pic;
    }

    public void setLicense_pic(MultipartFile license_pic) {
        this.license_pic = license_pic;
    }

    public String getCooperationNum() {
        return cooperationNum;
    }

    public void setCooperationNum(String cooperationNum) {
        this.cooperationNum = cooperationNum;
    }

    public String getLicense_url() {
        return license_url;
    }

    public void setLicense_url(String license_url) {
        this.license_url = license_url;
    }

    @Override
    public String toString() {
        return "CooperationDTO{" +
                "name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", avatar=" + avatar +
                ", cooperationName='" + cooperationName + '\'' +
                ", addressDTO=" + addressDTO +
                ", business_license='" + business_license + '\'' +
                ", license_pic=" + license_pic +
                ", license_url='" + license_url + '\'' +
                ", cooperationNum='" + cooperationNum + '\'' +
                '}';
    }
}
