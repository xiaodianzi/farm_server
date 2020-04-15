package com.plansolve.farm.model.enums;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户状态码
 **/

public enum CropCodeEnum {
    RICE(1, "水稻", ""),
    WHEAT(2, "小麦", ""),
    CORN(3, "玉米", ""),
    SOYBEAN(4, "黄豆", ""),
    MUNG_BEAN(5, "绿豆", ""),
    POTATO(6, "土豆", ""),
    SORGHUM(7, "高粱", "");

    private Integer code; // 作物编码
    private String name; // 作物名称
    private String img; // 图标地址

    CropCodeEnum(Integer code, String name, String img) {
        this.code = code;
        this.name = name;
        this.img = img;
    }

    public Integer getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
    public String getImg() { return img; }

    public static CropCodeEnum getByName(String name) {
        for (CropCodeEnum each: CropCodeEnum.class.getEnumConstants()) {
            if (name.equals(each.getName())) {
                return each;
            }
        }
        return null;
    }
}
