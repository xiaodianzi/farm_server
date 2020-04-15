package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 操作已删除对象所抛异常
 **/

public class OrderCancelErrorException extends RuntimeException {

    private Integer code = ResultEnum.ORDER_CANCEL_ERROR.getCode();

    public OrderCancelErrorException(String message) {
        super(ResultEnum.ORDER_CANCEL_ERROR.getMessage() + message);
    }

    public Integer getCode() {
        return code;
    }

}
