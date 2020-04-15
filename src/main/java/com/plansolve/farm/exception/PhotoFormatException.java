package com.plansolve.farm.exception;

import com.plansolve.farm.model.enums.code.ResultEnum;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description:
 **/

public class PhotoFormatException extends RuntimeException {

    private Integer code = ResultEnum.PHOTO_FORMAT_ERROR.getCode();

    public PhotoFormatException(String message) {
        super(ResultEnum.NOT_EXIST.getMessage() + message);
    }

    public Integer getCode() {
        return code;
    }

}
