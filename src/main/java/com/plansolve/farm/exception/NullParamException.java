package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description:
 **/

public class NullParamException extends RuntimeException {

    private Integer code = ResultEnum.PARAM_ERROR.getCode();

    public NullParamException(String message) {
        super(message);
    }

    public Integer getCode() {
        return code;
    }

}
