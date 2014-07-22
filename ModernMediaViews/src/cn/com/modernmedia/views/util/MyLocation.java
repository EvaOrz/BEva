package cn.com.modernmedia.views.util;

import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * 定位
 * 使用百度定位SDK3.1版本，api参考http://developer.baidu.com/map/geosdk-android-classv3.1
 * .htm
 * 
 * @author jiancong
 * 
 */
public class MyLocation {
	private static final String TAG = "MyLocation";
	private static final String SERVICE_NAME = "com.baidu.location.service_v2.9";
	private static final String COOR_TYPE_GC = "gcj02"; // 国测局经纬度坐标系,默认为百度经纬坐标系(bd09ll)
	private static final int SCAN_TIME = 900; // 定时定位的时间间隔(单位ms)
	private LocationClient mLocationClient;
	private Context mContext;
	private boolean isNeedAddrInfo = true;
	private int priority = LocationClientOption.NetWorkFirst;
	private FetchLocationListener mListener;

	private BDLocationListener mLocationListener = new BDLocationListener() {

		@Override
		public void onReceivePoi(BDLocation arg0) {
		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;

			mLocationClient.stop();
			mLocationClient.unRegisterLocationListener(mLocationListener);
			mLocationClient = null;

			String msg = "";
			switch (location.getLocType()) {
			case 61:
				msg = "GPS定位结果:";
				break;
			case 62:
				msg = "扫描整合定位依据失败。此时定位结果无效";
				break;
			case 63:
				msg = "网络异常，没有成功向服务器发起请求。此时定位结果无效";
				break;
			case 65:
				msg = " 定位缓存的结果:";
				break;
			case 66:
				msg = "离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果";
				break;
			case 67:
				msg = "离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果";
				break;
			case 68:
				msg = "网络连接失败时，查找本地离线定位时对应的返回结果";
				break;
			case 161:
				msg = "网络定位结果:";
				break;
			default:
				msg = "服务端定位失败";
				break;
			}
			if (isNeedAddrInfo) {
				Log.d(TAG, msg + location.getAddrStr());
				mListener.fetchAddress(location.getCity());
			}
			mListener.fetchCoordinate(location.getLongitude(),
					location.getLatitude());
		}
	};

	/**
	 * 获取当前位置结果接口
	 * 
	 * @author jiancong
	 * 
	 */
	public interface FetchLocationListener {
		/**
		 * 获取的地址返回
		 * 
		 * @param address
		 */
		public abstract void fetchAddress(String address);

		/**
		 * 经纬度的返回
		 * 
		 * @param longitude
		 * @param latitude
		 */
		public abstract void fetchCoordinate(double longitude, double latitude);
	}

	public MyLocation(Context context) {
		this.mContext = context;
		mLocationClient = new LocationClient(mContext);
		mLocationClient.registerLocationListener(mLocationListener);
		setLocationOption();
	}

	/**
	 * 开始获取
	 */
	public void startGetLocation() {
		mLocationClient.start();
	}

	/**
	 * 停止获取
	 */
	public void stopGetLocation() {
		mLocationClient.stop();
	}

	/**
	 * 设置参数
	 */
	public void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		// 设置是否打开gps，使用gps前提是用户硬件打开gps。默认是不打开gps的
		// if (priority == LocationClientOption.GpsFirst) {
		// option.setOpenGps(true);
		// }
		// 输出坐标系
		option.setCoorType(COOR_TYPE_GC);
		option.setServiceName(SERVICE_NAME);
		// 是否需要地址信息，默认不需要
		if (isNeedAddrInfo) {
			option.setAddrType("all");
		}
		// 设置定时定位的时间间隔(单位ms),
		// 当不设此项，或者所设的整数值小于1000（ms）时，采用一次定位模式;反之，则会多次请求（位置变化时，得到新结果）
		option.setScanSpan(SCAN_TIME);
		// 不需要POI信息(Point of Interest)
		option.setPoiExtraInfo(false);
		// 启用缓存
		option.disableCache(true);
		// 设置优先级(GPS优先时，若GPS不可用时，则使用网络地位)
		option.setPriority(priority);
		mLocationClient.setLocOption(option);
	}

	protected boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public FetchLocationListener getmListener() {
		return mListener;
	}

	public void setmListener(FetchLocationListener mListener) {
		this.mListener = mListener;
	}

	public boolean isNeedAddrInfo() {
		return isNeedAddrInfo;
	}

	public void setNeedAddrInfo(boolean isNeedAddrInfo) {
		this.isNeedAddrInfo = isNeedAddrInfo;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
