package cn.com.modernmedia.businessweek;

import android.content.Intent;
import android.os.Handler;
import cn.com.modernmedia.CommonAdvActivity;
import cn.com.modernmedia.util.ConstData;

/**
 * ���ҳ��
 * 
 * @author ZhuQiao
 * 
 */
public class AdvActivity extends CommonAdvActivity {
	/**
	 * ��ת����ҳ
	 */
	protected void gotoMainActivity() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(AdvActivity.this, MainActivity.class);
				intent.putExtra("FROM_ACTIVITY", "SPLASH");
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.alpha_out, R.anim.hold);
			}
		}, ConstData.SPLASH_DELAY_TIME);
	}

	@Override
	public void reLoadData() {

	}

}
