package com.plansolve.farm.util;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: 高一平
 * @Date: 2017/6/20
 * @Description: 字符串相关工具
 **/
public class StringUtil {
    public static final String EMPTY_STRING = "";
    public static final String ENCODING = "utf-8";

    public StringUtil() {
    }

    /**
     * 判断字符串是否为数字构成
     *
     * @param param
     * @return
     */
    public static boolean isNumeric(String param) {
        Pattern numPattern = Pattern.compile("[0-9]*");
        Matcher isNum = numPattern.matcher(param);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 单个字符串是否为空
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(value.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 多个字符串是否为空
     *
     * @param values
     * @return
     */
    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }

    /**
     * 移除字符串中的数字
     *
     * @param string
     * @return
     */
    public static String removeNumbers(String string) {
        string = string.replace("0", EMPTY_STRING)
                .replace("1", EMPTY_STRING)
                .replace("2", EMPTY_STRING)
                .replace("3", EMPTY_STRING)
                .replace("4", EMPTY_STRING)
                .replace("5", EMPTY_STRING)
                .replace("6", EMPTY_STRING)
                .replace("7", EMPTY_STRING)
                .replace("8", EMPTY_STRING)
                .replace("9", EMPTY_STRING);
        return string;
    }

    /**
     * @param str
     * @param string
     * @author 作者：高一平
     * @Title: checkStrExist
     * @Description: 判断string中是否存在字符串str
     * @return: boolean
     */
    public static boolean checkStrExist(String str, String string) {
        boolean flag;
        if (string.indexOf(str) >= 0) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * @param encoding 字符集
     * @param string
     * @return Boolean
     * @throws UnsupportedEncodingException
     * @Title judgeEncoding
     * @Description: 判断是否是对应字符集的字符串
     * @author 作者：高一平
     */
    public static Boolean judgeEncoding(String encoding, String string) throws UnsupportedEncodingException {
        Boolean flag;
        if (string.equals(new String(string.getBytes(encoding), encoding)) == true) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * @param string
     * @return String
     * @throws UnsupportedEncodingException
     * @Title getEncoding
     * @Description 判断指定字符串的字符集
     * @author 作者：高一平
     */
    public static String getEncoding(String string) throws UnsupportedEncodingException {
        String[] encodings = {"utf-8", "UTF-8", "iso8859-1", "ISO8859-1", "GB2312", "GBK"};
        for (String encoding : encodings) {
            if (judgeEncoding(encoding, string) == true) {
                return encoding;
            }
        }
        return "";
    }

    /**
     * @param string
     * @return String
     * @throws UnsupportedEncodingException
     * @Title getStringByEncoding
     * @Description 获取指定字符集的字符串
     * @author 作者：高一平
     */
    public static String getStringByEncoding(String string) throws UnsupportedEncodingException {
        // 判断是否是系统设定的字符集
        if (judgeEncoding(ENCODING, string) == true) {
            // 是，原字符串输出
            return string;
        } else {
            // 不是，判断是什么字符集
            String encoding = getEncoding(string);
            // 重新使用指定字符集编码
            string = new String(string.getBytes(encoding), ENCODING);
            return string;
        }
    }

    /**
     * @param str
     * @param len
     * @return String
     * @Title suspensionStr
     * @Description 截取字符, 如果超过长度, 截取并加省略号
     * @author 作者：高一平
     */
    static public String suspensionStr(String str, int len) {
        try {
            str = str.substring(0, len) + "...";
        } catch (Exception e) {
            return str;
        }
        return str;
    }

    /************************************************返回固定长度的字符串*****************************************/
    // 返回最小固定长度的字符串，位数不足使用填充符——后缀
    public static String suffixStr(String str, Integer len, String symbol) {
        if (symbol == null || symbol.isEmpty()) symbol = " ";
        StringBuffer strBuf = new StringBuffer();
        if (str.length() > len) {
            return str;
        } else {
            strBuf.append(str);
            for (int i = 0; i < (len - str.length()); i++) {
                strBuf.append(symbol);
            }
            return strBuf.toString();
        }
    }

    // 返回最小固定长度的字符串，若位数不足使用填充符——前缀
    public static String prefixStr(String str, Integer len, String symbol) {
        if (symbol == null || symbol.isEmpty()) symbol = " ";
        StringBuffer strBuf = new StringBuffer();
        if (str.length() > len) {
            return str;
        } else {
            for (int i = 0; i < (len - str.length()); i++) {
                strBuf.append(symbol);
            }
            strBuf.append(str);
            return strBuf.toString();
        }

    }

    /**
     * 字符串转换为二进制码
     *
     * @param str
     * @return
     */
    public static String toBinary(String str) {
        //把字符串转成字符数组
        char[] strChar = str.toCharArray();
        String result = "";
        for (int i = 0; i < strChar.length; i++) {
            //toBinaryString(int i)返回变量的二进制表示的字符串
            //toHexString(int i) 八进制
            //toOctalString(int i) 十六进制
            result += Integer.toBinaryString(strChar[i]) + " ";
        }
        return result;
    }

    public static String toString(String binary) {
        String[] tempStr = binary.split(" ");
        char[] tempChar = new char[tempStr.length];
        for (int i = 0; i < tempStr.length; i++) {
            tempChar[i] = BinstrToChar(tempStr[i]);
        }
        return String.valueOf(tempChar);
    }

    //将二进制转换成字符
    public static char BinstrToChar(String binStr) {
        int[] temp = BinstrToIntArray(binStr);
        int sum = 0;
        for (int i = 0; i < temp.length; i++) {
            sum += temp[temp.length - 1 - i] << i;
        }
        return (char) sum;
    }

    //将二进制字符串转换成int数组
    public static int[] BinstrToIntArray(String binStr) {
        char[] temp = binStr.toCharArray();
        int[] result = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            result[i] = temp[i] - 48;
        }
        return result;
    }

    /************************************************返回固定长度的字符串*****************************************/


    public static boolean hasLength(String str) {
        return str != null && str.length() > 0;
    }

}