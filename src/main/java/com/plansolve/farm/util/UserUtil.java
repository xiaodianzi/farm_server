package com.plansolve.farm.util;

import com.plansolve.farm.exception.DeletedStateErrorException;
import com.plansolve.farm.exception.FrozenStateErrorException;
import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.state.UserStateEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: 高一平
 * @Date: 2018/7/25
 * @Description:
 **/
@Slf4j
public class UserUtil {

    /**
     * 确认用户是否存在
     *
     * @param user
     */
    public static Boolean checkUserExist(User user) {
        if (user == null) {
            log.error("该手机号码不存在");
            throw new ParamErrorException("[该手机号码未注册]");
        } else if (user.getUserState().equals(UserStateEnum.DELETED)) {
            log.error("该用户已被注销");
            throw new DeletedStateErrorException("[该用户已被注销]");
        } else if (user.getUserState().equals(UserStateEnum.FROZEN)) {
            log.error("该用户已被冻结");
            throw new FrozenStateErrorException("[该用户已被冻结]");
        } else {
            return true;
        }
    }

    public static Boolean checkUserState(User user){
        if (user.getUserState().equals(UserStateEnum.NORMOL.getState())) {
            return true;
        } else {
            if (user.getUserState().equals(UserStateEnum.PENDING.getState())) {
                throw new PermissionException("[该用户还未提交审核信息，不能进行此操作]");
            } else if (user.getUserState().equals(UserStateEnum.AUDIT.getState())) {
                throw new PermissionException("[该用户还在审核中，不能进行此操作]");
            } else if (user.getUserState().equals(UserStateEnum.FROZEN.getState())) {
                throw new FrozenStateErrorException("[该用户已被冻结，不能进行此操作]");
            } else {
                throw new PermissionException("[用户状态异常]");
            }
        }
    }

}
