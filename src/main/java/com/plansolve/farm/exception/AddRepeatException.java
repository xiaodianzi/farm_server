package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 操作已删除对象所抛异常
 **/

public class AddRepeatException extends RuntimeException {

    private Integer code = ResultEnum.ADD_REPEAT_ERROR.getCode();

    public AddRepeatException(String message) {
        super(ResultEnum.ADD_REPEAT_ERROR.getMessage());
    }

    public Integer getCode() {
        return code;
    }

}
