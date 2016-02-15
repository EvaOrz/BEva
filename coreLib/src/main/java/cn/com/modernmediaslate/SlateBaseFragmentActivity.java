package cn.com.modernmediaslate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;
import cn.com.modernmediaslate.fragment.SlateBaseFragment;

import com.flurry.android.FlurryAgent;

/**
 * FragmentActivity父类
 * 
 * @author ZhuQiao
 * 
 */
public abstract class SlateBaseFragmentActivity extends FragmentActivity {
	private final int TOAST_LENGTH = 1000;
	private Context mContext;
	private Handler handler = new Handler();
	private Dialog dialog;
	private String flurry = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		flurry = SlateApplication.mConfig.getFlurry_api_key();
		addActivityToList();
		deleteAllFragments(getFragmentTags());
	}

	public void showToast(final int resId) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(SlateBaseFragmentActivity.this, resId,
						TOAST_LENGTH).show();
			}
		});
	}

	public void showToast(final String res) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(SlateBaseFragmentActivity.this, res,
						TOAST_LENGTH).show();
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
				if (dialog != null) {
					dialog.cancel();
					dialog = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void showFragmentIfNeeded(Fragment fragment, String tag,
			int fragmentRes, String[] tags) {
		if (!(mContext instanceof SlateBaseFragmentActivity))
			return;
		showFragment(fragment, true);
		FragmentManager fragmentManager = ((SlateBaseFragmentActivity) mContext)
				.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		if (fragmentManager.findFragmentByTag(tag) == null) {
			transaction.add(fragmentRes, fragment, tag);
		} else {
			transaction.show(fragment);
			if (fragment instanceof SlateBaseFragment) {
				((SlateBaseFragment) fragment).refresh();
			}
		}

		for (String hideTag : tags) {
			if (!hideTag.equals(tag)) {
				Fragment fragmentNeedHide = fragmentManager
						.findFragmentByTag(hideTag);
				if (fragmentNeedHide != null) {
					showFragment(fragmentNeedHide, false);
					transaction.remove(fragmentNeedHide);
				}
			}
		}
		try {
			// TODO catch Activity has been destroyed error
			transaction.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void showFragment(Fragment fragment, boolean show) {
		if (fragment instanceof SlateBaseFragment) {
			((SlateBaseFragment) fragment).showView(show);
		}
	}

	public abstract String[] getFragmentTags();

	/**
	 * 当数据被系统回收，而framgent没被回收时，导致fragment加载不出数据,所以在进入activity时，统一清空暂留的fragment
	 * ,重新初始化
	 * 
	 * @param tags
	 */
	protected void deleteAllFragments(String[] tags) {
		if (tags == null)
			return;
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		for (String hideTag : tags) {
			Fragment fragmentNeedHide = fragmentManager
					.findFragmentByTag(hideTag);
			if (fragmentNeedHide != null) {
				transaction.remove(fragmentNeedHide);
			}
		}
		try {
			// TODO catch Activity has been destroyed error
			transaction.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addActivityToList() {
		SlateApplication.addActivity(getActivityName(), getActivity());
	};

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
		exitApp();
	}

	/**
	 * 离开应用
	 */
	protected void exitApp() {
		removeActivityFromList();
	}
}
