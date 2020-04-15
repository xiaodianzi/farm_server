package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 操作已删除对象所抛异常
 **/

public class DeletedStateErrorException extends RuntimeException {

    private Integer code = ResultEnum.DELETED_ERROR.getCode();

    public DeletedStateErrorException(String message) {
        super(ResultEnum.DELETED_ERROR.getMessage() + message);
    }

    public Integer getCode() {
        return code;
    }

}
