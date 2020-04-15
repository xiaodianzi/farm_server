package com.plansolve.farm.model.client;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2019/3/26
 * @Description:
 */
public class MachineryDictDTO implements Serializable {

    private String machineryCategory;//农机类别

    private List<FarmMachineryDict> machineryTypeList;

    public String getMachineryCategory() {
        return machineryCategory;
    }

    public void setMachineryCategory(String machineryCategory) {
        this.machineryCategory = machineryCategory;
    }

    public List<FarmMachineryDict> getMachineryTypeList() {
        return machineryTypeList;
    }

    public void setMachineryTypeList(List<FarmMachineryDict> machineryTypeList) {
        this.machineryTypeList = machineryTypeList;
    }

}
