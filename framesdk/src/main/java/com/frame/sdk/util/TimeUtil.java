package com.frame.sdk.util;

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间处理类
 */
public class TimeUtil {

    /**
     * 将Date类型的时间转换为 一般形式 字符串 1985-12-05 12:00:00
     */
    public static String date2Str1(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS",
                Locale.CHINA);
        return formater.format(date);
    }

    /**
     * 将Date类型的时间转换为 一般形式 字符串 1985-12-05 12:00
     */
    public static String date2Str2(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                Locale.CHINA);
        return formater.format(date);
    }

    /**
     * 将Date类型的时间转换为 一般形式 字符串 1985-12-05
     */
    public static String date2Str3(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd",
                Locale.CHINA);
        return formater.format(date);
    }

    /**
     * 将Date类型的时间转换为 一般形式 字符串 03-25 15:30
     */
    public static String date2Str4(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat("MM-dd HH:mm",
                Locale.CHINA);
        return formater.format(date);
    }

    public static String date2Year(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy", Locale.CHINA);
        return formater.format(date);
    }

    public static String date2Month(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat("MM", Locale.CHINA);
        return formater.format(date);
    }

    public static String date2Day(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat("dd", Locale.CHINA);
        return formater.format(date);
    }

    /**
     * 将字符串的时间转换为Date类型
     *
     * @param timeStr 1985-12-05 12:00:00
     * @return 失败返回null
     */
    public static Date str2Date1(String timeStr) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS",
                Locale.CHINA);
        try {
            return formater.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符串的时间转换为Date类型
     *
     * @param timeStr 1985-12-05 12:00
     * @return 失败返回null
     */
    public static Date str2Date2(String timeStr) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                Locale.CHINA);
        try {
            return formater.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符串的时间转换为Date类型
     *
     * @param timeStr 1985-12-05
     * @return 失败返回null
     */
    public static Date str2Date3(String timeStr) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd",
                Locale.CHINA);
        try {
            return formater.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符串的时间转换为Date类型
     *
     * @param timeStr 03-25 15:30
     * @return 失败返回null
     */
    public static Date str2Date4(String timeStr) {
        SimpleDateFormat formater = new SimpleDateFormat("MM-dd HH:mm",
                Locale.CHINA);
        try {
            return formater.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查时间字符串是否符合正确的时间格式。eg: MM/dd/yyyy下：12/33/2014是错误的
     *
     * @param dateDtring        eg:12/12/2014
     * @param dateFormatPattern eg:MM/dd/yyyy
     * @return 是否正确
     */
    public static boolean validDate(String dateDtring, String dateFormatPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern);
        sdf.setLenient(false);
        try {
            sdf.parse(dateDtring);
        } catch (ParseException e) {
            // e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 距离现在的时间 毫秒
     *
     * @param date
     * @return
     */
    public static long howFarInMillis(Date date) {
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        long nowMillis = now.getTime();
        long targetMillis = date.getTime();
        return Math.abs(nowMillis - targetMillis);
    }

    /**
     * 距离现在的时间 秒
     *
     * @param date
     * @return
     */
    public static long howFarInSecond(Date date) {
        return howFarInMillis(date) / 1000;
    }

    /**
     * 距离现在的时间 分钟
     *
     * @param date
     * @return
     */
    public static int howFarInMinute(Date date) {
        return (int) (howFarInSecond(date) / 60);
    }

    /**
     * 距离现在的时间 小时
     *
     * @param date
     * @return
     */
    public static int howFarInHour(Date date) {
        return (int) (howFarInMinute(date) / 60);
    }

    /**
     * 距离现在的时间 天
     *
     * @param date
     * @return
     */
    public static int howFarInDay(Date date) {
        return (int) (howFarInHour(date) / 24);
    }


    /**
     * 聊天界面显示的时间格式
     *
     * @param context
     * @param date
     * @param displayTime 根据该值判断是否显示时间
     *                    扩充 needShowToday 是否显示   今天  XX:XX
     * @return
     */
    public static String date2MessageStr(Context context, Date date, boolean displayTime, boolean needShowToday) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar now = Calendar.getInstance();
        String time = "";
        int sendYear = calendar.get(Calendar.YEAR);
        int nowYear = now.get(Calendar.YEAR);

        int sendDayWeek = calendar.get(Calendar.DAY_OF_WEEK);    // 星期几
        int nowDayWeek = now.get(Calendar.DAY_OF_WEEK);

        DateTime senDateTime = new DateTime(date);
        DateTime nowDateTime = new DateTime(now.getTime());

        // 相差的天数，这个算法可以解决跨年问题。这里相差天数的定义是以跨过晚上十二点为界的
        int differenceDay = Days.daysBetween(senDateTime.withTimeAtStartOfDay(), nowDateTime.withTimeAtStartOfDay()).getDays();
        Log.i("EIMUtil", "senDateTime:  " + senDateTime.toString() + "nowDateTime: " + nowDateTime.toString() + "differenceDay:" + differenceDay);

        // 如果是星期天，+7变为8方便计算
        if (nowDayWeek == 1) {
            nowDayWeek += 7;
        }
        boolean isToday = false;
        // 优先逻辑为今天，昨天，星期，才是年
        if (differenceDay <= 0) {    // 今天
            time = "今天";
            isToday = true;
        } else if (differenceDay == 1) {    // 昨天
            // 显示格式 昨天
            time = "昨天";
        } else if (nowDayWeek - 2 > 0 && (differenceDay) <= (nowDayWeek - 2)) {
            //显示格式 周一
            time = day2Week(sendDayWeek);
        } else if (sendYear == nowYear) {    // 如果是同一年，不显示年份
            //显示格式 1月 31
            sdf = new SimpleDateFormat("MM月dd日", Locale.SIMPLIFIED_CHINESE);
            time = sdf.format(date);
        } else {
            // 显示格式2014年1月31日
            sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.SIMPLIFIED_CHINESE);
            time = sdf.format(date);

        }

        if (displayTime) {
            SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
            if (isToday && !needShowToday) {
                time = timeformat.format(date);
            } else {
                time = time + " " + timeformat.format(date);
            }
        }

        return time;
    }

    /**
     * 消息列表界面时间格式，时间近的精确到时间，远的精确到日期
     *
     * @param context
     * @param date
     * @return
     */
    public static String setSessionTime(Context context, Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm", Locale.TRADITIONAL_CHINESE);
        SimpleDateFormat anotherDayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.TRADITIONAL_CHINESE);
        int am = calendar.get(Calendar.HOUR_OF_DAY);

        Calendar now = Calendar.getInstance();
        String time = "";
        int sendYear = calendar.get(Calendar.YEAR);
        int nowYear = now.get(Calendar.YEAR);

        int sendDayWeek = calendar.get(Calendar.DAY_OF_WEEK);    // 星期几
        int nowDayWeek = now.get(Calendar.DAY_OF_WEEK);

        DateTime senDateTime = new DateTime(date);
        DateTime nowDateTime = new DateTime(now.getTime());

        // 相差的天数，这个算法可以解决跨年问题。这里相差天数的定义是以跨过晚上十二点为界的
        int differenceDay = Days.daysBetween(senDateTime.withTimeAtStartOfDay(), nowDateTime.withTimeAtStartOfDay()).getDays();
        Log.i("EIMUtil", "senDateTime:  " + senDateTime.toString() + "nowDateTime: " + nowDateTime.toString() + "differenceDay:" + differenceDay);

        // 如果是星期天，+7变为8方便计算
        if (nowDayWeek == 1) {
            nowDayWeek += 7;
        }

        // 优先逻辑为今天，昨天，星期，才是年
//		if (differenceDay <= 0) {	// 今天
//			time = hour2Str(am) + timeformat.format(date);
//		} else if(differenceDay == 1){	// 昨天
//			// 显示格式 昨天
//			time = "昨天 " + hour2Str(am);
//		} else if (nowDayWeek - 2 > 0 && (differenceDay) <= (nowDayWeek - 2)) {
//			//显示格式 周一
//			time = day2Week(sendDayWeek) + hour2Str(am);
//		} else if (sendYear == nowYear) {	// 如果是同一年，不显示年份
//			//显示格式 1月 31
//			sdf = new SimpleDateFormat("MM月dd日",Locale.SIMPLIFIED_CHINESE);
//			time = sdf.format(date);
//		} else {
//			// 显示格式2014年1月31日
//			sdf = new SimpleDateFormat("yyyy年MM月dd日",Locale.SIMPLIFIED_CHINESE);
//			time = sdf.format(date);
//		}

        //时间逻辑为 若为今天，则显示正常时间，如 今天 8：:3
        //若不是今天，则显示日期 如 2016-3-20 4:30
        if (differenceDay <= 0) {    // 今天
            time = "今天" + hour2Str(am) + " " + timeformat.format(date);
        } else {
            time = anotherDayFormat.format(date);
        }
        return time;
    }

    private static String day2Week(int dayweek) {
        String str = null;
        if (dayweek == Calendar.MONDAY) {
            str = "周一 ";
        } else if (dayweek == Calendar.TUESDAY) {
            str = "周二 ";
        } else if (dayweek == Calendar.WEDNESDAY) {
            str = "周三 ";
        } else if (dayweek == Calendar.THURSDAY) {
            str = "周四 ";
        } else if (dayweek == Calendar.FRIDAY) {
            str = "周五 ";
        } else if (dayweek == Calendar.SATURDAY) {
            str = "周六 ";
        } else {
            str = "周日 ";
        }
        return str;
    }


    private static String hour2Str(int hour) {
        String str = null;
        if (hour >= 0 && hour < 6) {
            str = "凌晨";
        } else if (hour >= 6 && hour < 12) {
            str = "早上";
        } else if (hour >= 12 && hour < 18) {
            str = "下午";
        } else {
            str = "晚上";
        }
        return str;
    }

    public static String getMMSS(long time) {
        long mm = time / 1000;
        long m = mm / 60;
        long s = mm % 60;
        return m + ":" + s;
    }
}
