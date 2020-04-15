package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 操作已冻结对象所抛异常
 **/

public class FrozenStateErrorException extends RuntimeException {

    private Integer code = ResultEnum.FROZEN_ERROR.getCode();

    public FrozenStateErrorException(String message) {
        super(ResultEnum.FROZEN_ERROR.getMessage() + message);
    }

    public Integer getCode() {
        return code;
    }

}
