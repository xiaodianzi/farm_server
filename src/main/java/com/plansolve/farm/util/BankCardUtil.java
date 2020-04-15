package com.plansolve.farm.util;

/**
 * @Author: 高一平
 * @Date: 2019/3/4
 * @Description: 银行卡相关工具类
 **/
public class BankCardUtil {

    /**
     * 根据银行卡号获取该卡相关信息
     * {"cardType":"CC","bank":"CIB","key":"#","messages":[],"validated":true,"stat":"ok"}
     *
     * @param bankCardNo
     * @return
     */
    public static String validateBankCard(String bankCardNo) {
        String validateUrl = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=" + bankCardNo + "&cardBinCheck=true";
        String result = HttpUtil.HTTP_GET(validateUrl);
        return result;
    }

}
