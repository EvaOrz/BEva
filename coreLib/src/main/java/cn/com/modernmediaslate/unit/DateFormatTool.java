package cn.com.modernmediaslate.unit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;

/**
 * 日期格式化工具类
 * 
 * @author zhuqiao
 * 
 */
public class DateFormatTool {

	/**
	 * 格式化日期
	 * 
	 * @param time
	 *            时间（毫秒）
	 * @param pattern
	 *            日期格式
	 * @return
	 */
	public static String format(long time, String pattern) {
		return format(time, pattern, "");
	}

	/**
	 * 格式化日期
	 * 
	 * @param time
	 *            时间（毫秒）
	 * @param pattern
	 *            日期格式
	 * @param language
	 *            使用语言
	 * @return
	 */
	public static String format(long time, String pattern, String language) {
		if (TextUtils.isEmpty(pattern))
			return "";
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
	 * @param startTime
	 *            开始时间（毫秒）
	 * @param endTime
	 *            结束时间（毫秒）
	 * @param startPattern
	 *            开始日期格式
	 * @param endPattern
	 *            结束日期格式
	 * @param connector
	 *            连接符
	 * @return
	 */
	public static String format2(long startTime, long endTime,
			String startPattern, String endPattern, String connector) {
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

}
