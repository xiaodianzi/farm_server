package com.plansolve.farm.util;

import java.math.BigDecimal;

/**
 * @Author: 高一平
 * @Date: 2019/3/19
 * @Description:
 **/
public class BigDecimalUtil {

    /**
     * a是否等于b
     * @param a
     * @param b
     * @return
     */
    public static Boolean equals(BigDecimal a, BigDecimal b){
        if(a.compareTo(b) == 0){
            return true;
        } else {
            return false;
        }
    }

    /**
     * a是否大于b
     * @param a
     * @param b
     * @return
     */
    public static Boolean moreThan(BigDecimal a, BigDecimal b){
        if(a.compareTo(b) == 1){
            return true;
        } else {
            return false;
        }
    }

    /**
     * a是否大于等于b
     * @param a
     * @param b
     * @return
     */
    public static Boolean moreThanAndEquals(BigDecimal a, BigDecimal b){
        if(a.compareTo(b) > -1){
            return true;
        } else {
            return false;
        }
    }

    /**
     * a是否小于b
     * @param a
     * @param b
     * @return
     */
    public static Boolean lessThan(BigDecimal a, BigDecimal b){
        if(a.compareTo(b) == -1){
            return true;
        } else {
            return false;
        }
    }

    /**
     * a是否大于等于b
     * @param a
     * @param b
     * @return
     */
    public static Boolean lessThanAndEquals(BigDecimal a, BigDecimal b){
        if(a.compareTo(b) < 1){
            return true;
        } else {
            return false;
        }
    }

}
