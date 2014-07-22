package cn.com.modernmedia;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.SlateBaseActivity;

import com.flurry.android.FlurryAgent;

/**
 * 所有activity的父类
 * 
 * @author ZhuQiao
 * 
 */
public abstract class BaseActivity extends SlateBaseActivity {
	private RelativeLayout process_layout;
	private ProgressBar loading;
	private ImageView error;
	private String flurryApiKey = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		flurryApiKey = ConstData.getFlurryApiKey();
		CommonApplication app = (CommonApplication) getApplication();
		if (CommonApplication.width == 0) {
			app.initScreenInfo();
		}
		if (TextUtils.isEmpty(CommonApplication.CHANNEL)) {
			app.initChannel();
		}
	}

	/**
	 * 如果需要progressbar,需要继承此方法
	 */
	protected void initProcess() {
		process_layout = (RelativeLayout) findViewById(R.id.process_layout_activity);
		loading = (ProgressBar) findViewById(R.id.loading_activity);
		error = (ImageView) findViewById(R.id.error_activity);
		process_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (error.getVisibility() == View.VISIBLE) {
					reLoadData();
				}
			}
		});
	}

	/**
	 * 显示loading图标
	 */
	public void showLoading() {
		if (process_layout == null) {
			System.out.println("未初始化process!!");
			return;
		}
		process_layout.setVisibility(View.VISIBLE);
		loading.setVisibility(View.VISIBLE);
		error.setVisibility(View.GONE);
	}

	/**
	 * 显示错误提示
	 */
	public void showError() {
		if (process_layout == null) {
			System.out.println("未初始化process!!");
			return;
		}
		process_layout.setVisibility(View.VISIBLE);
		loading.setVisibility(View.GONE);
		error.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏process_layout
	 */
	public void disProcess() {
		if (process_layout == null) {
			System.out.println("未初始化process!!");
			return;
		}
		process_layout.setVisibility(View.GONE);
		loading.setVisibility(View.GONE);
		error.setVisibility(View.GONE);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (this != null && !TextUtils.isEmpty(flurryApiKey))
			FlurryAgent.onStartSession(this, flurryApiKey);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (this != null && !TextUtils.isEmpty(flurryApiKey))
			FlurryAgent.onEndSession(this);
	}

	/**
	 * 当页面出错时重新获取数据
	 */
	public abstract void reLoadData();

	/**
	 * 当页面被系统回收后是否需要重新进入应用
	 * 
	 * @param cls
	 *            从哪个页面进入应用
	 * @return 成功回收页面
	 */
	protected boolean onSystemDestory(Class<?> cls) {
		return onSystemDestoryToSplash(cls);
	}

	private boolean onSystemDestoryToSplash(Class<?> cls) {
		if (!CommonApplication.onSystemDestory)
			return false;
		System.exit(0);
		return true;
	}
}
