package com.plansolve.farm.util;

import com.plansolve.farm.model.enums.code.CodeEnum;
import com.plansolve.farm.model.enums.state.StateEnum;
import com.plansolve.farm.model.enums.type.TypeEnum;

/**
 * @Author: 高一平
 * @Date: 2018/8/2
 * @Description:
 **/
public class EnumUtil {

    public static <T extends CodeEnum> T getByCode(Integer code, Class<T> enumClass) {
        for (T each: enumClass.getEnumConstants()) {
            if (code.equals(each.getCode())) {
                return each;
            }
        }
        return null;
    }

    public static <T extends StateEnum> T getByState(String state, Class<T> enumClass) {
        for (T each: enumClass.getEnumConstants()) {
            if (state.equals(each.getState())) {
                return each;
            }
        }
        return null;
    }

    public static <T extends TypeEnum> T getByType(String type, Class<T> enumClass) {
        for (T each: enumClass.getEnumConstants()) {
            if (type.equals(each.getType())) {
                return each;
            }
        }
        return null;
    }
}
