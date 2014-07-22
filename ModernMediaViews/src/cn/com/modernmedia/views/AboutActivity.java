package cn.com.modernmedia.views;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.views.model.ColumnParm;
import cn.com.modernmedia.views.model.IndexNavParm;
import cn.com.modernmedia.views.model.IndexNavParm.IndexNavTitleParm;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;

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
		setContentView(R.layout.about);
		initRes();
		findViewById(R.id.about_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 初始化资源
	 */
	private void initRes() {
		ColumnParm columnParm = ParseProperties.getInstance(this).parseColumn();
		IndexNavParm parm = ParseProperties.getInstance(this).parseAboutNav();
		V.setImage((ImageView) findViewById(R.id.about_image),
				columnParm.getAbout());
		V.setImage((ImageView) findViewById(R.id.about_back),
				parm.getNav_column());
		V.setImage(findViewById(R.id.about_bar_divider), parm.getNav_shadow());
		V.setImage(findViewById(R.id.about_bar), parm.getNav_bg());
		TextView title = (TextView) findViewById(R.id.about_title);
		IndexNavTitleParm titleParm = parm.getTitleParm();
		if (titleParm.getTitle_top_padding() != 0) {
			title.setPadding(0, titleParm.getTitle_top_padding(), 0, 0);
		}
		if (titleParm.getTitle_textsize() != 0) {
			title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
					titleParm.getTitle_textsize());
		}
		if (!TextUtils.isEmpty(titleParm.getTitle_color())) {
			title.setTextColor(Color.parseColor(titleParm.getTitle_color()));
		}
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
