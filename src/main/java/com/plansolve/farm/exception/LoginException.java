package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description:
 **/

public class LoginException extends RuntimeException {

    private Integer code = ResultEnum.LOGIN_ERROR.getCode();

    public LoginException(String message) {
        super(ResultEnum.LOGIN_ERROR.getMessage() + message);
    }

    public Integer getCode() {
        return code;
    }

}
