package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 发送短信失败所抛异常
 **/

public class MessageSendErrorException extends RuntimeException {

    private Integer code = ResultEnum.MASSAGE_SEND_ERROR.getCode();

    public MessageSendErrorException(String message) {
        super(ResultEnum.MASSAGE_SEND_ERROR.getMessage() + message);
    }

    public Integer getCode() {
        return code;
    }

}
