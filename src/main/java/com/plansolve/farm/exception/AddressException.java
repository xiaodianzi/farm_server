package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 参数值不符合规则所抛异常
 **/

public class AddressException extends RuntimeException {

    private Integer code = ResultEnum.ADDRESS_CONVERT_ERROR.getCode();

    public AddressException(String message) {
        super(ResultEnum.ADDRESS_CONVERT_ERROR.getMessage() + message);
    }

    public Integer getCode() {
        return code;
    }

}
