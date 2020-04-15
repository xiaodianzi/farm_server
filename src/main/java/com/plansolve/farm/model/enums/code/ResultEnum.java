package com.plansolve.farm.model.enums.code;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 返回数据状态码
 **/

public enum ResultEnum implements CodeEnum {
    SUCCESS(200, "成功"),
    NOT_OPEN(202, "暂未开通"),
    UNKNOWN_ERROR(-1, "未知错误"),
    PARAM_ERROR(400, "请求参数错误"),
    PARAM_INTEGRITY_ERROR(401, "该参数违反数据完整性原则"),
    LOGIN_ERROR(402, "登录失效"),
    PERMISSION_ERROR(403, "该用户不具有此操作权限"),
    FROZEN_ERROR(404, "已冻结"),
    DELETED_ERROR(405, "已删除"),
    ENCODEING_ERROR(500, "字符集编码异常"),
    ADDRESS_CONVERT_ERROR(501, "字符集编码异常"),
    MASSAGE_SEND_ERROR(600, "短信发送失败"),
    FilE_ERROR(601, "文件处理出错"),
    JDPUSH_ERROR(602, "推送失败"),
    PHOTO_FORMAT_ERROR(603, "图片无法识别"),
    SERVER_BUSY_ERROR(604, "服务器忙"),
    GET_ORDER_FAIL(700, "抢单失败"),
    ORDER_ERROR(701, "订单出错"),
    ORDER_CANCEL_ERROR(702, "订单取消"),
    ORDER_NOT_EXIST_ERROR(703, "该订单不存在"),
    ADD_REPEAT_ERROR(704, "重复创建错误"),
    NOT_EXIST(800, "查询的信息不存在"),
    WE_CHAT_ERROR(900, "微信公众号出错"),
    WE_CHAT_PAY_ERROR(901, "微信支付失败");

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}