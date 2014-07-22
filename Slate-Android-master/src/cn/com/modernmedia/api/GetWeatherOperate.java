package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmedia.model.Weather;
import cn.com.modernmedia.model.Weather.Forecast;

/**
 * 获取天气预报
 * 
 * @author ZhuQiao
 * 
 */
public class GetWeatherOperate extends BaseOperate {
	private String url = "";
	private Weather weather;

	public GetWeatherOperate(double longitude, double latitude) {
		url = UrlMaker.getWeather(longitude, latitude);
		weather = new Weather();
	}

	public Weather getWeather() {
		return weather;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		weather.setCityid(jsonObject.optString("cityid", ""));
		weather.setCity(jsonObject.optString("city", ""));
		weather.setCity_en(jsonObject.optString("city_en", ""));
		JSONArray array = jsonObject.optJSONArray("forecasts");
		if (isNull(array))
			return;
		int length = array.length();
		JSONObject obj;
		Forecast forecast;
		for (int i = 0; i < length; i++) {
			obj = array.optJSONObject(i);
			if (isNull(obj))
				continue;
			forecast = new Forecast();
			forecast.setHigh(obj.optString("high", ""));
			forecast.setLow(obj.optString("low", ""));
			forecast.setCondition(obj.optString("condition", ""));
			forecast.setDay_of_week(obj.optString("day_of_week", ""));
			forecast.setIcon(obj.optString("icon", ""));
			weather.getForecastList().add(forecast);
		}
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return "";
	}

}
