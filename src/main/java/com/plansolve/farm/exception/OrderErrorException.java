package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 操作已删除对象所抛异常
 **/

public class OrderErrorException extends RuntimeException {

    private Integer code = ResultEnum.ORDER_ERROR.getCode();

    public OrderErrorException(String message) {
        super(ResultEnum.ORDER_ERROR.getMessage() + message);
    }

    public Integer getCode() {
        return code;
    }

}
