package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/11/14
 * @Description:
 **/
public class WeChatSellException extends RuntimeException {

    private Integer code;

    public WeChatSellException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public WeChatSellException(ResultEnum resultEnum, String message) {
        super(resultEnum.getMessage() +  "[" + message + "]");
        this.code = resultEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
