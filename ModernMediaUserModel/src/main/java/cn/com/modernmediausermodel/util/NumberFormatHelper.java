package cn.com.modernmediausermodel.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import cn.com.modernmediaslate.unit.ParseUtil;

public class NumberFormatHelper {
	private static NumberFormatHelper instance;
	private NumberFormat format;

	private NumberFormatHelper() {
		format = DecimalFormat.getInstance();
		format.setMaximumFractionDigits(1);
		format.setMinimumFractionDigits(1);
	}

	public static NumberFormatHelper getInstance() {
		if (instance == null)
			instance = new NumberFormatHelper();
		return instance;
	}

	public String formatStringValue(String value) {
		try {
			double result = ParseUtil.stod(value);
			return format.format(result);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return "0.00";
	}

	public String formatDoubleValue(double value) {
		return format.format(value);
	}
}
