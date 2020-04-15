package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description:
 **/

public class NotExistException extends RuntimeException {

    private Integer code = ResultEnum.NOT_EXIST.getCode();

    public NotExistException(String message) {
        super(ResultEnum.NOT_EXIST.getMessage() + message);
    }

    public Integer getCode() {
        return code;
    }

}
