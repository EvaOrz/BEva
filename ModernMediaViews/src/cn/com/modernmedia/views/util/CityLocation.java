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

//	private static final int RELOAD_GPS_MINUTE = 10;

//	private Context mContext;
//	private LocationManager mLocationManager;
//	private String mBestProvider;
//	private Location mLocation;

	public CityLocation(Context context) {
//		mContext = context;
//		mLocationManager = (LocationManager) mContext
//				.getSystemService(Context.LOCATION_SERVICE);
//		if (TextUtils.isEmpty(mBestProvider)) {
//			mBestProvider = LocationManager.GPS_PROVIDER;
//		}
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

	/**
	 * 开始更新
	 */
//	public void startUpdates() {
		// 10 minutes
//		mLocationManager.requestLocationUpdates(mBestProvider,
//				RELOAD_GPS_MINUTE * 60 * 1000, 0, this);
//	}

	/**
	 * 停止更新
	 */
//	public void stopUpdates() {
//		mLocationManager.removeUpdates(this);
//	}

//	private Location getCurrentLocation() {
//		if (mLocation != null) {
//			return mLocation;
//		}
//		return mLocationManager.getLastKnownLocation(mBestProvider);
//	}

//	public String getCityName() {
//		String cityName = "";
//		Location localLocation = getCurrentLocation();
//		Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
//		List<Address> addresses;
//		if (localLocation != null) {
//			try {
//				PrintHelper.print("LOCAL LOCATION : ["
//						+ localLocation.getLatitude() + ","
//						+ localLocation.getLongitude() + "]");
//				addresses = gcd.getFromLocation(localLocation.getLatitude(),
//						localLocation.getLongitude(), 1);
//				if (addresses.size() > 0) {
//					cityName = addresses.get(0).getLocality();
//					PrintHelper.print("LOCAL CITY : [" + cityName + "]");
//				}
//			} catch (IOException ioe) {
//				PrintHelper.print(ioe.toString());
//			} catch (Exception e) {
//				PrintHelper.print(e.toString());
//			}
//		}
//		return cityName;
//	}

//	public int getNearestCityIndex() {
//		int nearestCity = WEEKLY_CITY_BEIJING;
//		// 当前位置
//		Location location = getCurrentLocation();
//		if (location != null) {
//			float minDistance = location
//					.distanceTo(getCityLocation(WEEKLY_CITY_BEIJING));
//			float distance = location
//					.distanceTo(getCityLocation(WEEKLY_CITY_SHANGHAI));
//			if (distance < minDistance) {
//				minDistance = distance;
//				nearestCity = WEEKLY_CITY_SHANGHAI;
//			}
//			distance = location
//					.distanceTo(getCityLocation(WEEKLY_CITY_GUANGZHOU));
//			if (distance < minDistance) {
//				minDistance = distance;
//				nearestCity = WEEKLY_CITY_GUANGZHOU;
//			}
//			distance = location.distanceTo(getCityLocation(WEEKLY_CITY_GLOBAL));
//			if (distance < minDistance) {
//				nearestCity = WEEKLY_CITY_GLOBAL;
//			}
//		}
//		return nearestCity;
//	}

	public float[] getCityLocation(int weeklyCityIndex) {
//		Location location = new Location(mBestProvider);
		float[] res = new float[2];
		switch (weeklyCityIndex) {
		case WEEKLY_CITY_BEIJING:
//			location.setLatitude(LATITUDE_BEIJING);
//			location.setLongitude(LONGITUDE_BEIJING);
			res[0] = LATITUDE_BEIJING;
			res[1] = LONGITUDE_BEIJING;
			break;
		case WEEKLY_CITY_SHANGHAI:
//			location.setLatitude(LATITUDE_SHANGHAI);
//			location.setLongitude(LONGITUDE_SHANGHAI);
			res[0] = LATITUDE_SHANGHAI;
			res[1] = LONGITUDE_SHANGHAI;
			break;
		case WEEKLY_CITY_GUANGZHOU:
//			location.setLatitude(LATITUDE_GUANGZHOU);
//			location.setLongitude(LONGITUDE_GUANGZHOU);
			res[0] = LATITUDE_GUANGZHOU;
			res[1] = LONGITUDE_GUANGZHOU;
			break;
		case WEEKLY_CITY_GLOBAL:
//			location.setLatitude(LATITUDE_HONGKONG);
//			location.setLongitude(LONGITUDE_HONGKONG);
			res[0] = LATITUDE_HONGKONG;
			res[1] = LONGITUDE_HONGKONG;
			break;
		case WEEKLY_CITY_LOCAL:
//			location = getCurrentLocation();
			break;
		}
		return res;
	}

//	@Override
//	public void onLocationChanged(Location location) {
//		mLocation = location;
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//	}
}
