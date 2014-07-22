package cn.com.modernmedia;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Down;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.util.PrintHelper;

/**
 * 进版首页
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonSplashActivity extends BaseActivity {
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentViewById();
		mContext = this;
		CommonApplication.clear();
		initScreenInfo();
		down();
		init();
	}

	protected abstract void setContentViewById();

	protected abstract void gotoMainActivity();

	protected abstract void gotoAdvActivity();

	/**
	 * 获取屏幕信息
	 */
	private void initScreenInfo() {
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		CommonApplication.width = dm.widthPixels;
		CommonApplication.height = dm.heightPixels;
		PrintHelper.print("width:" + dm.widthPixels + "height:"
				+ dm.heightPixels);
	}

	/**
	 * 统计装机量
	 */
	private void down() {
		if (DataHelper.getDown(this))
			return;
		OperateController controller = new OperateController(this);
		controller.getDown(new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry != null && entry instanceof Down) {
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
					// adv_pic.setImageBitmap(bitmap);
					hasAdv = true;
					gotoAdvActivity();
				}
			}
		}
		if (!hasAdv)
			gotoMainActivity();
	}

	@Override
	public void reLoadData() {

	}

}
