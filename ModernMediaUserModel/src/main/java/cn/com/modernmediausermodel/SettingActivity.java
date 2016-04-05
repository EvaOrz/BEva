package cn.com.modernmediausermodel;

import java.io.File;

import net.tsz.afinal.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.com.modernmedia.pay.PayActivity;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.widget.EvaSwitchBar;
import cn.com.modernmedia.widget.EvaSwitchBar.OnChangeListener;
import cn.com.modernmedia.widget.WebViewPop;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Active;

/**
 * 设置
 * 
 * @author ZhuQiao
 * 
 */
public class SettingActivity extends Activity implements OnClickListener,
		OnChangeListener {
	private Context mContext;
	private Active active;
	private TextView bookStatus;// 订阅显示
	private TextView version;// 版本信息
	private boolean canBook = true;// 是否可以订阅
	private EvaSwitchBar autoLoop;// head自动循环播放开关
	private EvaSwitchBar wifiVedio;// WiFi下视频自动播放开关
	private boolean index_head_auto_loop = true, wifi_auto_play_vedio = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initstatus();
		initView();
		initActiveList();
	}

	private void initstatus() {
		index_head_auto_loop = DataHelper.getIndexHeadAutoLoop(this);
		wifi_auto_play_vedio = DataHelper.getWiFiAutoPlayVedio(this);
	}

	private void initView() {
		findViewById(R.id.setting_back).setOnClickListener(this);
		bookStatus = (TextView) findViewById(R.id.setting_book_single);
		bookStatus.setOnClickListener(this);
		initBookStatus();
		findViewById(R.id.setting_get_book_single).setOnClickListener(this);
		findViewById(R.id.setting_auto_loop).setOnClickListener(this);
		findViewById(R.id.setting_wifi_auto_vedio).setOnClickListener(this);
		findViewById(R.id.settings_recommend).setOnClickListener(this);
		findViewById(R.id.nomal_question).setOnClickListener(this);
		version = (TextView) findViewById(R.id.setting_version);
		initVersion();
		autoLoop = (EvaSwitchBar) findViewById(R.id.auto_loop_switch);
		autoLoop.setChecked(index_head_auto_loop);
		wifiVedio = (EvaSwitchBar) findViewById(R.id.wifi_auto_vedio_switch);
		wifiVedio.setChecked(wifi_auto_play_vedio);
		autoLoop.setOnChangeListener(this);
		wifiVedio.setOnChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.setting_book_single) {// 订阅
			if (canBook) {
				setupBookPreference();
			}
		} else if (v.getId() == R.id.setting_get_book_single) // 获赠订阅
			setupGetBookPreference();
		else if (v.getId() == R.id.setting_auto_loop) {// 首页焦点图自动轮播

		} else if (v.getId() == R.id.setting_wifi_auto_vedio) {// Wi-Fi下自动播放视频

		} else if (v.getId() == R.id.settings_recommend) // 向朋友推荐本应用
			setupRecommendPreference();
		else if (v.getId() == R.id.nomal_question) {// 常见问题

		} else if (v.getId() == R.id.setting_back) {
			finish();
		}
	}

	/**
	 * 初始化活动列表页面
	 */
	private void initActiveList() {
		UserOperateController.getInstance(this).getActiveList(
				SlateDataHelper.getUid(this), SlateDataHelper.getToken(this),
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						active = (Active) entry;
					}
				});
	}

	/**
	 * 订阅
	 */
	private void setupBookPreference() {
		if (SlateDataHelper.getUserLoginInfo(SettingActivity.this) != null) {// 已登录
			if (!SlateDataHelper.getIssueLevel(SettingActivity.this)
					.equals("1")) {
				Intent i = new Intent(getApplicationContext(),
						PayActivity.class);
				startActivity(i);
			}
		} else {
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(i);
		}
	}

	/**
	 * 初始化订阅状态
	 */
	private void initBookStatus() {
		if (SlateDataHelper.getIssueLevel(SettingActivity.this).equals("1")) {// 付费用户，显示已订阅
			long endtime = SlateDataHelper.getEndTime(this);
			if (endtime == 0) {
				bookStatus.setText(R.string.book_already_no_time);// 已订阅（不显示到期时间）
			} else
				bookStatus.setText(String.format(
						this.getString(R.string.book_already),
						Utils.strToDate(SlateDataHelper.getEndTime(this))));// 显示到期时间
			canBook = false;
		} else {
			bookStatus.setText(R.string.book);
			canBook = true;
		}
	}

	/**
	 * 获赠订阅
	 */
	private void setupGetBookPreference() {
		if (SlateDataHelper.getUserLoginInfo(SettingActivity.this) != null) {
			if (active != null) {
				WebViewPop pop = new WebViewPop(SettingActivity.this,
						active.getUrl());
			}
		} else {
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(i);
		}
	}

	/**
	 * 推荐给朋友
	 */
	private void setupRecommendPreference() {
		// TODO send email
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_SUBJECT,
				getString(R.string.preference_recommend_subject));
		intent.putExtra(Intent.EXTRA_TEXT,
				Html.fromHtml(getString(R.string.preference_recommend_text)));
		startActivity(intent);
	}

	/**
	 * 初始化程序名称和版本信息
	 */
	private void initVersion() {
		PackageManager packageManager = getPackageManager();
		try {
			final PackageInfo info = packageManager.getPackageInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			String versionText = info.applicationInfo.loadLabel(packageManager)
					+ " " + info.versionName;
			if (ConstData.IS_DEBUG != 0) {
				versionText = versionText + "（" + "测试版" + "）";
			}
			version.setText(versionText);
		} catch (PackageManager.NameNotFoundException ignored) {
		}
	}

	/**
	 * 清除缓存
	 */
	private void clearApplicationData() {
		try {
			File dir = mContext.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	/**
	 * 更新订阅状态用
	 */
	@Override
	protected void onResume() {
		super.onResume();
		initBookStatus();
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.hold, R.anim.down_out);
	}

	@Override
	public void onChange(EvaSwitchBar buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.auto_loop_switch) {
			DataHelper.setIndexHeadAutoLoop(SettingActivity.this, isChecked);
		} else if (buttonView.getId() == R.id.wifi_auto_vedio_switch) {
			DataHelper.setWiFiAutoPlayVedio(SettingActivity.this, isChecked);
		}

	}

}
