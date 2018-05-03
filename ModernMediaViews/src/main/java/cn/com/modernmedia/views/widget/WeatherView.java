package cn.com.modernmedia.views.widget;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Weather;
import cn.com.modernmedia.model.Weather.Forecast;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.model.Entry;

/**
 * 天气预报view
 * 
 * @author ZhuQiao
 * 
 */
public class WeatherView implements FetchEntryListener, OnClickListener {
	private Context mContext;
	private RelativeLayout mWeatherView;
	private RelativeLayout[] mDayViews;
	private Button mOpenClose;
	private int mShowAllPosition;
	private int mShowOnePosition;
	private boolean isShowAll = false;
	private int toX;

	public WeatherView(Context context, RelativeLayout weatherView) {
		mContext = context;
		mWeatherView = weatherView;
		mDayViews = new RelativeLayout[4];
		mDayViews[0] = (RelativeLayout) mWeatherView
				.findViewById(R.id.weather_day1);
		mDayViews[1] = (RelativeLayout) mWeatherView
				.findViewById(R.id.weather_day2);
		mDayViews[2] = (RelativeLayout) mWeatherView
				.findViewById(R.id.weather_day3);
		mDayViews[3] = (RelativeLayout) mWeatherView
				.findViewById(R.id.weather_day4);
		mOpenClose = (Button) mWeatherView.findViewById(R.id.open_close);
		mOpenClose.setOnClickListener(this);
		mShowAllPosition = Math.round(mContext.getResources().getDimension(
				R.dimen.weather_view_width));
		mShowOnePosition = Math.round(mContext.getResources().getDimension(
				R.dimen.weather_day_width)
				+ mContext.getResources().getDimension(
						R.dimen.weather_day_margin_left));
	}

	@Override
	public void setData(Entry entry) {
		if (entry == null || !(entry instanceof Weather)) {
			return;
		}
		Weather weather = (Weather) entry;
		List<Forecast> list = weather.getForecastList();
		if (list == null || list.isEmpty())
			return;
		Drawable buttonImage = mContext.getResources().getDrawable(
				R.drawable.bg_weather_view_open);
		mOpenClose.setCompoundDrawablesWithIntrinsicBounds(buttonImage, null,
				null, null);
		initWeather();
		for (int i = 0; i < list.size(); i++) {
			if (i < 4)
				setDataForItem(list.get(i), mDayViews[i], i == 0);
		}
	}

	private void setDataForItem(Forecast forecast, RelativeLayout view,
			boolean isFirst) {
		ImageView icon = (ImageView) view.findViewById(R.id.weather_icon);
		TextView day = (TextView) view.findViewById(R.id.weather_day);
		TextView low = (TextView) view.findViewById(R.id.weather_low);
		TextView high = (TextView) view.findViewById(R.id.weather_high);
		int resId = V.ID_ERROR;
		try {
			resId = getWeatherIconResource(forecast.getIcon());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (resId != V.ID_ERROR && resId != V.ID_COLOR && resId != V.ID_HTTP)
			icon.setImageResource(resId);
		if (isFirst) {
			day.setText("TODAY");
		} else {
			day.setText(getWeekDay(forecast.getDay_of_week()));
		}
		low.setText(getWeatherText(forecast.getLow()));
		high.setText(getWeatherText(forecast.getHigh()));
	}

	private String getWeekDay(String dayStr) {
		if ("周一".equals(dayStr)) {
			return "MON";
		} else if ("周二".equals(dayStr)) {
			return "TUE";
		} else if ("周三".equals(dayStr)) {
			return "WED";
		} else if ("周四".equals(dayStr)) {
			return "THU";
		} else if ("周五".equals(dayStr)) {
			return "FRI";
		} else if ("周六".equals(dayStr)) {
			return "SAT";
		} else {
			return "SUN";
		}
	}

	private String getWeatherText(String weatherText) {
		if (weatherText.endsWith("℃")) {
			weatherText = weatherText.substring(0, weatherText.indexOf("℃"));
		}
		return weatherText + "˚";
	}

	/**
	 * 获取资源文件
	 * 
	 * @param iconFileName
	 * @return
	 */
	private int getWeatherIconResource(String iconFileName) throws Exception {
		String iconName = iconFileName.substring(
				iconFileName.lastIndexOf("/") + 1,
				iconFileName.lastIndexOf("."));

		if ("cn_overcast".equals(iconName)) {
			return V.getId("ico_weather_cloudy");
		} else if ("cn_heavyrain".equals(iconName)) {
			return V.getId("ico_weather_rain");
		}
		if (iconName.startsWith("cn_")) {
			iconName = iconName.replaceFirst("cn_", "");
		}
		String resourceId = "ico_weather_" + iconName;
		return V.getId(resourceId);
	}

	private void initWeather() {
		TranslateAnimation ta = new TranslateAnimation(mShowAllPosition,
				mShowAllPosition - mShowOnePosition, 0, 0);
		ta.setDuration(300);
		ta.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mWeatherView.setVisibility(View.VISIBLE);
				for (int i = 0; i < mDayViews.length; i++) {
					if (i > 0 && mDayViews[i] != null) {
						mDayViews[i].setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mWeatherView.clearAnimation();
				LayoutParams params = (LayoutParams) mWeatherView
						.getLayoutParams();
				params.rightMargin = -mShowAllPosition + mShowOnePosition;
				mWeatherView.setLayoutParams(params);
			}
		});
		mWeatherView.startAnimation(ta);
		isShowAll = false;
	}

	private boolean isAniming = false;

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.open_close) {
			if (isAniming)
				return;
			Drawable buttonImage;
			if (isShowAll) {
				isShowAll = false;
				toX = mShowAllPosition - mShowOnePosition;
				buttonImage = mContext.getResources().getDrawable(
						R.drawable.bg_weather_view_open);
			} else {
				isShowAll = true;
				toX = mShowOnePosition - mShowAllPosition;
				buttonImage = mContext.getResources().getDrawable(
						R.drawable.bg_weather_view_close);
			}
			TranslateAnimation ta = new TranslateAnimation(0, toX, 0, 0);
			ta.setDuration(300);
			ta.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					isAniming = true;
					if (isShowAll) {
						for (int i = 0; i < mDayViews.length; i++) {
							if (i > 0 && mDayViews[i] != null) {
								mDayViews[i].setVisibility(View.VISIBLE);
							}
						}
					}
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mWeatherView.clearAnimation();
					LayoutParams params = (LayoutParams) mWeatherView
							.getLayoutParams();
					params.rightMargin = params.rightMargin - toX;
					mWeatherView.setLayoutParams(params);
					if (!isShowAll) {
						for (int i = 0; i < mDayViews.length; i++) {
							if (i > 0 && mDayViews[i] != null) {
								mDayViews[i].setVisibility(View.GONE);
							}
						}
					}
					isAniming = false;
				}
			});
			mOpenClose.setCompoundDrawablesWithIntrinsicBounds(buttonImage,
					null, null, null);
			mWeatherView.startAnimation(ta);
		}
	}
}
