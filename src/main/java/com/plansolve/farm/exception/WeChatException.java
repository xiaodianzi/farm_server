package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 参数值不符合规则所抛异常
 **/

public class WeChatException extends RuntimeException {

    private Integer code = ResultEnum.WE_CHAT_ERROR.getCode();

    public WeChatException(String message) {
        super(ResultEnum.WE_CHAT_ERROR.getMessage() + message);
    }

    public Integer getCode() {
        return code;
    }

}
