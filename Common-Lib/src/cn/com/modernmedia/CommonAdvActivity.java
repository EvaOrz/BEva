package cn.com.modernmedia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.GenericConstant;

/**
 * 广告页面
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonAdvActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adv);
		Bitmap bitmap = FileManager
				.getImageFromFile(DataHelper.getAdvPic(this));
		((ImageView) findViewById(R.id.adv_image)).setImageBitmap(bitmap);
		gotoMainActivity();
	}

	/**
	 * 显示完入版广告后进入首页
	 */
	/**
	 * 显示完入版广告后进入首页
	 */
	private void gotoMainActivity() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				switchActivity();
			}
		}, ConstData.SPLASH_DELAY_TIME);
	};

	private void switchActivity() {
		Intent intent = new Intent(CommonAdvActivity.this, getMainActivity());
		intent.putExtra(GenericConstant.FROM_ACTIVITY,
				GenericConstant.FROM_ACTIVITY_VALUE);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.alpha_out_1s, R.anim.alpha_in_1s);
	}

	protected abstract Class<?> getMainActivity();

	@Override
	public void reLoadData() {
	}

}
