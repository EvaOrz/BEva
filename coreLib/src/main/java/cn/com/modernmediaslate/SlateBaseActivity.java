package cn.com.modernmediaslate;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

/**
 * 所有activity的父类
 * 
 * @author ZhuQiao
 * 
 */
public abstract class SlateBaseActivity extends Activity {
	private final int TOAST_LENGTH = 800;
	private Handler handler = new Handler();
	private Dialog dialog;
	private String flurry = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		flurry = SlateApplication.mConfig.getFlurry_api_key();
		addActivityToList();
	}

	public void showToast(final int resId) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(SlateBaseActivity.this, resId, TOAST_LENGTH)
						.show();

			}
		});
	}

	public void showToast(final String res) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(SlateBaseActivity.this, res, TOAST_LENGTH)
						.show();
			}
		});
	}

	/**
	 * 悬浮的dialog
	 * 
	 * @param flag
	 */
	public void showLoadingDialog(boolean flag) {
		if (flag) {
			if (dialog == null) {
				dialog = new Dialog(this, R.style.NobackDialog);
				dialog.setContentView(R.layout.processbar);
				dialog.setCancelable(true);
			}
			try {
				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				if (dialog != null && dialog.isShowing()) {
					dialog.cancel();
					dialog = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addActivityToList() {
		SlateApplication.addActivity(getActivityName(), getActivity());
	}

	public void removeActivityFromList() {
		SlateApplication.removeActivity(getActivityName());
	}

	public abstract String getActivityName();

	public abstract Activity getActivity();

	@Override
	protected void onStart() {
		super.onStart();
		if (this != null && !TextUtils.isEmpty(flurry))
			FlurryAgent.onStartSession(this, flurry);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (this != null && !TextUtils.isEmpty(flurry))
			FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		removeActivityFromList();
	}
}
