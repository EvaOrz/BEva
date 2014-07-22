package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import cn.com.modernmediausermodel.DefaultModifyPwdActivity;

/**
 * 更新密码页
 * 
 * @author ZhuQiao
 * 
 */
public class ModifyPasswordActivity extends DefaultModifyPwdActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(-1);
		((ImageView) findViewById(R.id.modify_pwd_img))
				.setImageResource(R.drawable.img_ad_iphone);
	}

	@Override
	public String getActivityName() {
		return ModifyPasswordActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.zoom_in, R.anim.right_out);
	}

}
