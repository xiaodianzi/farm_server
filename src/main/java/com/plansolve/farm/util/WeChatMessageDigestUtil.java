package com.plansolve.farm.util;

import com.plansolve.farm.exception.WeChatException;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * @Author: 高一平
 * @Date: 2018/10/15
 * @Description:
 **/
public class WeChatMessageDigestUtil {

    private static final WeChatMessageDigestUtil _instance = new WeChatMessageDigestUtil();

    private MessageDigest digest;

    private WeChatMessageDigestUtil() {
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (Exception e) {
            throw new WeChatException("微信signature初始化错误，error={}" + e.getMessage());
        }
    }

    public static WeChatMessageDigestUtil getInstance() {
        return _instance;
    }

    public static String byte2hex(byte[] b) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < b.length; i++) {
            tmp = (Integer.toHexString(b[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    public String encipher(String strSrc) {
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        digest.update(bt);
        strDes = byte2hex(digest.digest()); //to HexString
        return strDes;
    }

}
