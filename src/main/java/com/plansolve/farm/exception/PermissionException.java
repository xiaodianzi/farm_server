package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 参数值不符合规则所抛异常
 **/

public class PermissionException extends RuntimeException {

    private Integer code = ResultEnum.PERMISSION_ERROR.getCode();

    public PermissionException(String message) {
        super(ResultEnum.PERMISSION_ERROR.getMessage() + message);
    }

    public Integer getCode() {
        return code;
    }

}
