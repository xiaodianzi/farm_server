package com.plansolve.farm.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 *
 * @author 高一平
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static String[] parsePatterns = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * @return 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    /**
     * @param pattern 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     * @return 得到当前日期字符串
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * @param date    日期
     * @param pattern 格式可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     * @return 得到日期字符串 默认格式（yyyy-MM-dd）
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    /**
     * @param date 日期
     * @return 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * @return 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * @return 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * @return 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * @param date 指定日期
     * @return 得到指定年份字符串 格式（yyyy）
     */
    public static String getYear(Date date) {
        return formatDate(date, "yyyy");
    }

    /**
     * @return 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * @param date 日期
     * @return 得到日期月份字符串 格式（MM）
     */
    public static String getMonth(Date date) {
        return formatDate(date, "MM");
    }

    /**
     * @return 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * @param date 日期
     * @return 得到日期日数字符串 格式（dd）
     */
    public static String getDay(Date date) {
        return formatDate(date, "dd");
    }

    /**
     * @return 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * @param date 日期
     * @return 得到日期星期字符串 格式（E）星期几
     */
    public static String getWeek(Date date) {
        return formatDate(date, "E");
    }

    /**
     * 日期型字符串转化为日期
     *
     * @param str { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     *            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
     *            "yyyy.MM.dd","yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     * @return 日期
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     *
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 1000);
    }

    /**
     * 获取过去的秒
     *
     * @param date
     * @return
     */
    public static long pastSeconds(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / 1000;
    }

    /**
     * 转换为时间（天,时:分:秒.毫秒）
     *
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param before
     * @param after
     * @return
     */
    public static double getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
    }

    /**
     * @param date
     * @author 作者：高一平
     * @Title: getDayBegin
     * @Description: 获取某天零点
     * @return: Date
     */
    public static Date getDayBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param date
     * @author 高一平
     * @Title: getDayEnd
     * @Description: 获取某天末点
     * @return: Date
     */
    public static Date getDayEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * @param date
     * @Title: getDayCenter
     * @Description: 获取某天午时
     * @author: 高一平
     * @date: 2017年11月24日 下午5:43:41
     */
    public static Date getDayCenter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param date
     * @Title: getMonthStart
     * @Description: 获取某月月初
     * @author: 高一平
     * @date: 2018年8月10日 下午5:43:41
     */
    public static Date getMonthStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param date
     * @Title: getMonthEnd
     * @Description: 获取某月月末
     * @author: 高一平
     * @date: 2018年8月10日 下午5:43:41
     */
    public static Date getMonthEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param date
     * @Title: getYearStart
     * @Description: 获取某年年初
     * @author: 高一平
     * @date: 2018年8月10日 下午5:43:41
     */
    public static Date getYearStart(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(date));
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param date
     * @Title: getYearEnd
     * @Description: 获取某年年末
     * @author: 高一平
     * @date: 2018年8月10日 下午5:43:41
     */
    public static Date getYearEnd(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(date));
        calendar.set(Calendar.MONTH, 12);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获取过去或未来的日期（年）
     *
     * @param date
     * @param num  正数为几年后，负数为几年前
     * @return
     */
    public static Date getDate_PastOrFuture_Year(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + num);
        Date time = calendar.getTime();
        return time;
    }

    public static Date getDate_PastOrFuture_Month(Date date, int num) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);//设置日历时间
        c.add(Calendar.MONTH, num);
        Date newDate = c.getTime();
        return newDate;
    }

    /**
     * @param date
     * @param num  正数为几天后，负数为几天前
     * @Title: getDate_PastOrFuture_Day
     * @Description: 获取过去或未来的日期（天）
     * @author: 高一平
     * @date: 2017年11月29日 下午4:13:44
     */
    public static Date getDate_PastOrFuture_Day(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + num);
        Date time = calendar.getTime();
        return time;
    }

    /**
     * 获取过去或未来的日期（小时）
     *
     * @param date
     * @param num  正数为几小时后，负数为几小时前
     * @return
     */
    public static Date getDate_PastOrFuture_Hours(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + num);
        Date time = calendar.getTime();
        return time;
    }

    /**
     * @param date
     * @param num
     * @Title: getDate_PastOrFuture_Minute
     * @Description: 获取过去或未来的日期（分钟）
     * @author: 高一平
     * @date: 2017年12月1日 下午1:23:47
     */
    public static Date getDate_PastOrFuture_Minute(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + num);
        Date time = calendar.getTime();
        return time;
    }

    /**
     * 获取当天时间剩余的秒数
     *
     * @param currentDate
     * @return
     */
    public static Integer getRemainSecondsOneDay(Date currentDate) {
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault());
        long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);
        return (int) seconds;
    }

    /**
     * 获取本周的开始时间
     *
     * @return
     */
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfweek == 1) {
            dayOfweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayOfweek);
        return getDayBegin(cal.getTime());
    }

    public static void main(String[] args) {
        Date dateAfterMonth = getDate_PastOrFuture_Month(new Date(), 3);
        System.out.println(dateAfterMonth);
    }

}
