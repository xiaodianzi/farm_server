package com.plansolve.farm.model.enums.state;

/**
 * @Author: 高一平
 * @Date: 2019/2/25
 * @Description:
 **/
public enum TransactionApplicationStateEnum implements StateEnum {
    CREATE("create","创建申请"),
    FROZEN("frozen","冻结申请"),
    CHECK_PASS("check_pass","初审通过"),
    CHECK_FAIL("check_fail","初审不通过"),
    RECHECK_PASS("recheck_pass","复核通过"),
    RECHECK_FAIL("recheck_fail","复核不通过"),
    REMITTANCE("remittance","付款完成");

    private String state; // 申请进度

    private String message; // 相关状态详情

    TransactionApplicationStateEnum(String state, String message) {
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
