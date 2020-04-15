package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 参数值不符合规则所抛异常
 **/

public class ServerBusyException extends RuntimeException {

    private Integer code = ResultEnum.SERVER_BUSY_ERROR.getCode();

    public ServerBusyException(String message) {
        super(ResultEnum.SERVER_BUSY_ERROR.getMessage() + message);
    }

    public Integer getCode() {
        return code;
    }

}
