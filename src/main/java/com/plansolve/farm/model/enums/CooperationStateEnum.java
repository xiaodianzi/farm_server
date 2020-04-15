package com.plansolve.farm.model.enums;

/**
 * @Author: Andrew
 * @Date: 2018/6/12
 * @Description: 合作社状态码
 **/

public enum CooperationStateEnum {
    //合作社类型
    COMMON("common","普通类型"),

    //合作社状态
    NORMOL("normal","正常状态"), FAILURE("failure","已失效"), FROZEN("frozen","已冻结"),

    //社员状态
    APPLY("apply","已申请"),

    PERMITTED("permitted","已批准"),

    DELETED("deleted","已删除"),

    //作业状态
    INVITE("invite","已邀请"),

    CANCLED("cancled","已取消"),

    AGREED("agreed","已同意"),

    REFUSED("refused","已拒绝"),

    //社员角色
    PROPRIETER("proprieter", "社长"),

    CAPTAIN("captain","队长"),

    MEMBER("member","队员");

    private String state; // 用户状态

    private String message; // 相关状态详情

    CooperationStateEnum(String state, String message) {
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
