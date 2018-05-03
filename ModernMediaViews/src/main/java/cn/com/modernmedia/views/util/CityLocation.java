package cn.com.modernmedia.views.util;

import android.content.Context;

public class CityLocation  {
	public static final int WEEKLY_CITY_BEIJING = 0;
	public static final int WEEKLY_CITY_SHANGHAI = 1;
	public static final int WEEKLY_CITY_GUANGZHOU = 2;
	public static final int WEEKLY_CITY_GLOBAL = 3;
	public static final int WEEKLY_CITY_LOCAL = 4;

	public static final float LATITUDE_SHANGHAI = 31.28f;
	public static final float LONGITUDE_SHANGHAI = 121.48f;
	public static final float LATITUDE_BEIJING = 39.904f;
	public static final float LONGITUDE_BEIJING = 116.407f;
	public static final float LATITUDE_GUANGZHOU = 23.125f;
	public static final float LONGITUDE_GUANGZHOU = 113.299f;
	public static final float LATITUDE_HONGKONG = 22.20f;
	public static final float LONGITUDE_HONGKONG = 114.11f;

	public CityLocation(Context context) {
	}

	public int getCityByTemplate(String template) {
		if (template.equals("phone-beijing")) {
			return WEEKLY_CITY_BEIJING;
		} else if (template.equals("phone-shanghai")) {
			return WEEKLY_CITY_SHANGHAI;
		} else if (template.equals("phone-guangzhou")) {
			return WEEKLY_CITY_GUANGZHOU;
		} else if (template.equals("phone-xianggang")) {
			return WEEKLY_CITY_GLOBAL;
		} else {
			return -1;
		}
	}

	public float[] getCityLocation(int weeklyCityIndex) {
		float[] res = new float[2];
		switch (weeklyCityIndex) {
		case WEEKLY_CITY_BEIJING:
			res[0] = LATITUDE_BEIJING;
			res[1] = LONGITUDE_BEIJING;
			break;
		case WEEKLY_CITY_SHANGHAI:
			res[0] = LATITUDE_SHANGHAI;
			res[1] = LONGITUDE_SHANGHAI;
			break;
		case WEEKLY_CITY_GUANGZHOU:
			res[0] = LATITUDE_GUANGZHOU;
			res[1] = LONGITUDE_GUANGZHOU;
			break;
		case WEEKLY_CITY_GLOBAL:
			res[0] = LATITUDE_HONGKONG;
			res[1] = LONGITUDE_HONGKONG;
			break;
		case WEEKLY_CITY_LOCAL:
			break;
		}
		return res;
	}

}
