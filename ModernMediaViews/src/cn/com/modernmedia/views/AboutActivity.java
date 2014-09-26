package cn.com.modernmedia.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.views.model.TemplateAbout;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.xmlparse.XMLParse;

/**
 * 关于页面
 * 
 * @author user
 * 
 */
public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TemplateAbout about = ParseProperties.getInstance(this).parseAbout();
		XMLParse xmlParse = new XMLParse(this, null);
		View view = xmlParse.inflate(about.getData(), null, "");
		xmlParse.setDataForAbout();
		setContentView(view);
	}

	@Override
	public void reLoadData() {
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.hold, R.anim.down_out);
	}

	@Override
	public String getActivityName() {
		return AboutActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
