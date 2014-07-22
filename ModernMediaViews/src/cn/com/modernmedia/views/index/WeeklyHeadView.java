package cn.com.modernmedia.views.index;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.Weather;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.util.MyAnimate;
import cn.com.modernmedia.views.widget.WeatherView;
import cn.com.modernmedia.views.widget.WeeklyCircularViewPager;

/**
 * iWeekly类型的焦点图
 * 
 * @author user
 * 
 */
public class WeeklyHeadView extends IndexHeadView {
	private WeatherView weatherView;
	private Button animBtn;
	private int lineStartX, lineEndX;
	private RelativeLayout weatherReLayout;

	public WeeklyHeadView(Context context, IndexListParm parm) {
		super(context, parm);
	}

	@Override
	protected void init() {
		addView(LayoutInflater.from(mContext).inflate(
				R.layout.index_head_weekly, null));
		viewPager = (WeeklyCircularViewPager) findViewById(R.id.column_pager);
		((WeeklyCircularViewPager) viewPager).setSize(CommonApplication.width,
				parm.getHead_height());
		// TODO 其实没有，设置一个gone掉的view，兼容父类方法
		dotLl = (LinearLayout) findViewById(R.id.weekly_index_gallery_dot);
		title = (TextView) findViewById(R.id.column_pager_title_text);
		animBtn = (Button) findViewById(R.id.head_right_line);
		weatherReLayout = (RelativeLayout) findViewById(R.id.weather_view);
		weatherReLayout.setVisibility(View.GONE);
		weatherView = new WeatherView(mContext, weatherReLayout);
	}

	@Override
	protected void setDataToGallery(List<ArticleItem> list) {
		super.setDataToGallery(list);
		if (list.size() == 1) {
			MyAnimate.startTranslateAnimation(animBtn, 0,
					CommonApplication.width);
		}
	}

	@Override
	public void updateDes(int position) {
		super.updateDes(position);
		if (mList == null || mList.isEmpty())
			return;
		title.setText(mList.get(position).getTitle());
		if (mList.size() > 2) {
			int timeWidth = 0;
			if (position == 0) {
				timeWidth = mList.size() - 2;
			} else if (position == mList.size() - 1) {
				timeWidth = 1;
			} else {
				timeWidth = position;
			}
			lineEndX = Math.round(CommonApplication.width * timeWidth
					/ (mList.size() - 2));
			MyAnimate.startTranslateAnimation(animBtn, lineStartX, lineEndX);
			lineStartX = lineEndX;
		}
	}

	public void setValueForWeather(Weather weather) {
		weatherReLayout.setVisibility(View.GONE);
		weatherView.setData(weather);
	}

	public View getWeatherReLayout() {
		return weatherReLayout;
	}

}
