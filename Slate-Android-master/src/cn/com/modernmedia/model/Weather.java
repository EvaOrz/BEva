package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 天气
 * 
 * @author ZhuQiao
 * 
 */
public class Weather extends Entry {
	private static final long serialVersionUID = 1L;
	private String cityid = "";
	private String city = "";// 城市中文名
	private String city_en = "";// 城市英文名
	private List<Forecast> forecastList = new ArrayList<Forecast>();

	public String getCityid() {
		return cityid;
	}

	public void setCityid(String cityid) {
		this.cityid = cityid;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity_en() {
		return city_en;
	}

	public void setCity_en(String city_en) {
		this.city_en = city_en;
	}

	public List<Forecast> getForecastList() {
		return forecastList;
	}

	public void setForecastList(List<Forecast> forecastList) {
		this.forecastList = forecastList;
	}

	public static class Forecast extends Entry {
		private static final long serialVersionUID = 1L;
		private String high = "";
		private String low = "";
		private String condition = "";
		private String day_of_week = "";
		private String icon = "";

		public String getHigh() {
			return high;
		}

		public void setHigh(String high) {
			this.high = high;
		}

		public String getLow() {
			return low;
		}

		public void setLow(String low) {
			this.low = low;
		}

		public String getCondition() {
			return condition;
		}

		public void setCondition(String condition) {
			this.condition = condition;
		}

		public String getDay_of_week() {
			return day_of_week;
		}

		public void setDay_of_week(String day_of_week) {
			this.day_of_week = day_of_week;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

	}
}
