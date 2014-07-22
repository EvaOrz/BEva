package cn.com.modernmedia.util;

/**
 * 数据类型转换
 * 
 * @author zhuqiao
 * 
 */
public class ParseUtil {
	/**
	 * string to int
	 * 
	 * @param str
	 */
	public static int stoi(String str) {
		int i = 0;
		try {
			i = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return i;
	}

	/**
	 * string to int
	 * 
	 * @param str
	 * @param default_i
	 *            出错时默认返�?
	 * @return
	 */
	public static int stoi(String str, int default_i) {
		int i = 0;
		try {
			i = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return default_i;
		}
		return i;
	}

	/**
	 * string to double
	 * 
	 * @param str
	 * @return
	 */
	public static double stod(String str) {
		double d = 0.00;
		try {
			d = Double.parseDouble(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * string to double
	 * 
	 * @param str
	 * @param defaule_d
	 *            出错时默认返�?
	 * @return
	 */
	public static double stod(String str, double defaule_d) {
		double d = 0.00;
		try {
			d = Double.parseDouble(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return defaule_d;
		}
		return d;
	}

	/**
	 * string to long
	 * 
	 * @param str
	 * @return
	 */
	public static long stol(String str) {
		long l = 0;
		try {
			l = Long.parseLong(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return l;
	}

	/**
	 * string to long
	 * 
	 * @param default_l
	 * @return
	 */
	public static long stol(String str, long default_l) {
		long l = 0;
		try {
			l = Long.parseLong(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return default_l;
		}
		return l;
	}

	public static String parseString(String res, Object... args) {
		return String.format(res, args);
	}
}
