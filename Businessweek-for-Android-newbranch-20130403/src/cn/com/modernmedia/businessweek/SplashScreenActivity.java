package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.graphics.Color;
import cn.com.modernmedia.CommonSplashActivity;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.util.DataHelper;

/**
 * 应用启动页
 * 
 * @author ZhuQiao
 * 
 */
public class SplashScreenActivity extends CommonSplashActivity {

	@Override
	protected void setContentViewById() {
		setContentView(R.layout.splash_screen);
		findViewById(R.id.splash_view).setBackgroundColor(Color.BLACK);
		reSetFavDb();
	}

	private void reSetFavDb() {
		if (!DataHelper.getAddColumn(this)) {
			FavDb db = FavDb.getInstance(this);
			db.addColumn();
			DataHelper.setAddColumn(this);
		}
	}

	@Override
	public String getActivityName() {
		return SplashScreenActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
