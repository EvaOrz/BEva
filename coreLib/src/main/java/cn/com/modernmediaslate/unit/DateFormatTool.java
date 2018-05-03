package cn.com.modernmediaslate.unit;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期格式化工具类
 *
 * @author zhuqiao
 */
public class DateFormatTool {

    /**
     * 格式化日期
     *
     * @param time    时间（毫秒）
     * @param pattern 日期格式
     * @return
     */
    public static String format(long time, String pattern) {
        return format(time, pattern, "");
    }

    /**
     * 格式化日期
     *
     * @param time     时间（毫秒）
     * @param pattern  日期格式
     * @param language 使用语言
     * @return
     */
    public static String format(long time, String pattern, String language) {
        if (TextUtils.isEmpty(pattern)) return "";
        pattern = pattern.replace("@n", "\n");
        try {
            SimpleDateFormat format;
            if (TextUtils.equals("english", language)) {
                format = new SimpleDateFormat(pattern, Locale.ENGLISH);
            } else {
                format = new SimpleDateFormat(pattern);
            }
            return format.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化日期,包含开始-结束日期
     *
     * @param startTime    开始时间（毫秒）
     * @param endTime      结束时间（毫秒）
     * @param startPattern 开始日期格式
     * @param endPattern   结束日期格式
     * @param connector    连接符
     * @return
     */
    public static String format2(long startTime, long endTime, String startPattern, String endPattern, String connector) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(startPattern);
            String start = format.format(new Date(startTime));
            format.applyPattern(endPattern);
            String end = format.format(new Date(endTime));
            return start + connector + end;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 毫秒数 -- 00:00时间格式转化
     *
     * @param time
     * @return
     */
    public static String getTime(long time) {
        StringBuffer sb = new StringBuffer();
        long timeInSeconds = time / 1000;
        int hours = (int) timeInSeconds / 3600;
        if (hours >= 10) {
            sb.append(hours);
            sb.append(":");

        } else if (hours > 0 && hours < 10) {
            sb.append(0).append(hours);
            sb.append(":");
        }

        long minutes = (int) ((timeInSeconds % 3600) / 60);
        if (minutes >= 10) {
            sb.append(minutes);
        } else if (minutes > 0 && minutes < 10) {
            sb.append(0).append(minutes);
        } else {
            sb.append("00");
        }
        sb.append(":");

        int seconds = (int) (timeInSeconds % 60);
        if (seconds >= 10) {
            sb.append(seconds);
        } else if (seconds > 0 && seconds < 10) {
            sb.append(0).append(seconds);
        } else {
            sb.append("00");
        }
        return sb.toString();
    }

    /**
     * 时间字符串 --》 毫秒值
     *
     * @param duration
     */
    public static int getDuration(String duration) {
        int result = 0;
        String[] tt = duration.split(":");
        if (tt.length == 1) {
            result = Integer.valueOf(tt[0]) * 1000;

        } else if (tt.length == 2) {
            result = Integer.valueOf(tt[0]) * 60 * 1000 + Integer.valueOf(tt[1]) * 1000;
        } else if (tt.length == 3) {
            result = Integer.valueOf(tt[0]) * 60 * 60 * 1000 + Integer.valueOf(tt[1]) * 60 * 1000 + Integer.valueOf(tt[3]) * 1000;
        }
        return result;
    }


    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     *
     * @param t 时间戳
     * @return
     */
    public static String getStandardDate(long t) {

        StringBuffer sb = new StringBuffer();

        long time = System.currentTimeMillis() - (t * 1000);
        long mill = (long) Math.ceil(time / 1000);//秒前

        long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

        if (day > 6) {// 超过一周，显示日期
            return getStringToDate(t);
        } else if (day - 1 > 0) {
            sb.append(day + "天");
        } else if (hour - 1 > 0) {
            if (hour >= 24) {
                sb.append("1天");
            } else {
                sb.append(hour + "小时");
            }
        } else if (minute - 1 > 0) {
            if (minute == 60) {
                sb.append("1小时");
            } else {
                sb.append(minute + "分钟");
            }
        } else if (mill - 1 > 0) {
            if (mill == 60) {
                sb.append("1分钟");
            } else {
                sb.append(mill + "秒");
            }
        } else {
            sb.append("刚刚");
        }
        if (!sb.toString().equals("刚刚")) {
            sb.append("前");
        }
        return sb.toString();
    }


    public static String getStringToDate(long timeStr) {
        long t = timeStr * 1000;
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        String start = format.format(new Date(t));
        return start;
    }

    public static String getStringToDateForMd(long timeStr) {
        long t = timeStr * 1000;
        SimpleDateFormat format = new SimpleDateFormat("MM.dd", Locale.getDefault());
        String start = format.format(new Date(t));
        return start;
    }

    /**
     * 是否是同一天
     *
     * @return
     */
    public static boolean isSameDay(long d1, long d2) {
        Date date = new Date(d1 * 1000), sameDate = new Date(d2 * 1000);
        if (null == date || null == sameDate) {
            return false;
        }
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(sameDate);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        if (nowCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) && nowCalendar.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH) && nowCalendar.get(Calendar.DATE) == dateCalendar.get(Calendar.DATE)) {
            return true;
        }
        return false;
    }


    public static String getYY(long timeStr) {
        long t = timeStr * 1000;
        SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.getDefault());
        String start = format.format(new Date(t));
        return start;
    }
}
