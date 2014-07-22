package cn.com.modernmedia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Down;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;

/**
 * 进版首页
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonSplashActivity extends BaseActivity {
	private Context mContext;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentViewById();
		mContext = this;
		CommonApplication.clear();
		down();
		init();
	}

	protected abstract void setContentViewById();

	protected void gotoMainActivity() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(mContext, getMainActivity());
				intent.putExtra(GenericConstant.FROM_ACTIVITY,
						GenericConstant.FROM_ACTIVITY_VALUE);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.alpha_out_1s,
						R.anim.alpha_in_1s);
			}
		}, ConstData.SPLASH_DELAY_TIME);
	};

	protected void gotoAdvActivity() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(mContext, getAdvActivity());
				intent.putExtra(GenericConstant.FROM_ACTIVITY,
						GenericConstant.FROM_ACTIVITY_VALUE);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.alpha_out_1s,
						R.anim.alpha_in_1s);
			}
		}, ConstData.SPLASH_DELAY_TIME);
	};

	/**
	 * 统计装机量
	 */
	private void down() {
		if (DataHelper.getDown(this))
			return;
		OperateController.getInstance(this).getDown(new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof Down) {
					Down down = (Down) entry;
					if (down.isSuccess()) {
						DataHelper.setDown(mContext);
					}
				}
			}
		});
	}

	private void init() {
		String url = DataHelper.getAdvPic(this);
		String startTime = DataHelper.getStartTime(this);
		String endTime = DataHelper.getEndTime(this);
		boolean hasAdv = false;
		if (TextUtils.isEmpty(url) || TextUtils.isEmpty(startTime)
				|| TextUtils.isEmpty(endTime)) {
		} else {
			long currentTime = System.currentTimeMillis() / 1000;
			if (currentTime > ParseUtil.stol(startTime)
					&& currentTime < ParseUtil.stol(endTime)) {
				Bitmap bitmap = FileManager.getImageFromFile(url);
				if (bitmap != null) {
					hasAdv = true;
				}
			}
		}
		checkHasAdv(hasAdv);
	}

	protected void checkHasAdv(boolean hasAdv) {
		if (hasAdv)
			gotoAdvActivity();
		else
			gotoMainActivity();
	}

	protected abstract Class<?> getMainActivity();

	protected abstract Class<?> getAdvActivity();

	@Override
	public void reLoadData() {
	}

}
