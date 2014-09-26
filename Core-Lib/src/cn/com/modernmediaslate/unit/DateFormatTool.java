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

}
