package cn.com.modernmedia.businessweek;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.util.ConstData;

/**
 * πÿ”⁄“≥√Ê
 * 
 * @author ZhuQiao
 * 
 */
public class AboutActivity extends BaseActivity {
	private Button back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		back = (Button) findViewById(R.id.about_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ConstData.IS_DEBUG != 0) {
					Intent intent = new Intent(AboutActivity.this,
							TestActivity.class);
					startActivity(intent);
				} else {
					finish();
				}
			}
		});
	}

	@Override
	public void reLoadData() {
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.hold, R.anim.down_out);
	}

}
