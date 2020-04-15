package com.plansolve.farm.model.client.order;

import lombok.Data;

/**
 * @Author: 高一平
 * @Date: 2018/8/14
 * @Description:
 **/
@Data
public class OperatorDTO {

    private String username;

    private String mobile;

    private Float arce;

    private Integer identity;

    private String teamName;

    private Boolean isReported;

    public Boolean getReported() {
        return isReported;
    }

    public void setReported(Boolean reported) {
        isReported = reported;
    }

}
