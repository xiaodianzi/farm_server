package com.plansolve.farm.model.enums.state;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: SUCCESS—支付成功,REFUND—转入退款,NOTPAY—未支付,CLOSED—已关闭,REVOKED—已撤销（刷卡支付）,USERPAYING--用户支付中,PAYERROR--支付失败(其他原因，如银行返回失败)
 **/

public enum WxPayOrderStateEnum implements StateEnum {
    SUCCESS("SUCCESS","支付成功"),
    REFUND("REFUND","转入退款"),
    NOTPAY("NOTPAY","未支付"),
    CLOSED("CLOSED","已关闭"),
    REVOKED("REVOKED","已撤销（刷卡支付）"),
    USERPAYING("USERPAYING","用户支付中"),
    PAYERROR("PAYERROR","支付失败(其他原因，如银行返回失败)");

    private String state; // 账户状态

    private String message; // 相关状态详情

    WxPayOrderStateEnum(String state, String message) {
        this.state = state;
        this.message = message;
    }

    public String getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }
}
