package com.plansolve.farm.model.console;

import lombok.Data;

/**
 * @Author: 高一平
 * @Date: 2019/4/2
 * @Description:
 **/
@Data
public class MessageDTO {

    private Long idMessage; // 主键（数据库自增）

    private String mobile; // 发送手机号码

    private String detail; // 发送内容

    private Boolean isSuccess; // 是否成功

    private String createTime; // 发送时间

}
