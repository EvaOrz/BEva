package cn.com.modernmedia;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.com.modernmedia.Fragment.BaseFragment;
import cn.com.modernmedia.util.ConstData;

import com.flurry.android.FlurryAgent;

/**
 * FragmentActivity����
 * 
 * @author ZhuQiao
 * 
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
	private Context mContext;
	private RelativeLayout process_layout;
	private ProgressBar loading;
	private ImageView error;
	private String flurryApiKey = "";
	private Handler handler = new Handler();
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActivityToList();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		flurryApiKey = ConstData.getFlurryApiKey();
		CommonApplication app = (CommonApplication) getApplication();
		if (CommonApplication.width == 0) {
			app.initScreenInfo();
		}
		if (TextUtils.isEmpty(CommonApplication.CHANNEL)) {
			app.initChannel();
		}
		if (ConstData.getAppId() == 1) {
			CommonApplication.getLocalLanguage();
		}
		deleteAllFragments(getFragmentTags());
	}

	/**
	 * �����Ҫprogressbar,��Ҫ�̳д˷���
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
	 * ��ʾloadingͼ��
	 */
	protected void showLoading() {
		if (process_layout == null) {
			Toast.makeText(this, "δ��ʼ��process!!", ConstData.TOAST_LENGTH)
					.show();
			return;
		}
		process_layout.setVisibility(View.VISIBLE);
		loading.setVisibility(View.VISIBLE);
		error.setVisibility(View.GONE);
	}

	/**
	 * ��ʾ������ʾ
	 */
	protected void showError() {
		if (process_layout == null) {
			Toast.makeText(this, "δ��ʼ��process!!", ConstData.TOAST_LENGTH)
					.show();
			return;
		}
		process_layout.setVisibility(View.VISIBLE);
		loading.setVisibility(View.GONE);
		error.setVisibility(View.VISIBLE);
	}

	/**
	 * ����process_layout
	 */
	protected void disProcess() {
		if (process_layout == null) {
			Toast.makeText(this, "δ��ʼ��process!!", ConstData.TOAST_LENGTH)
					.show();
			return;
		}
		process_layout.setVisibility(View.GONE);
		loading.setVisibility(View.GONE);
		error.setVisibility(View.GONE);
	}

	public void showToast(final int resId) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(BaseFragmentActivity.this, resId,
						ConstData.TOAST_LENGTH).show();
			}
		});
	}

	public void showToast(final String res) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(BaseFragmentActivity.this, res,
						ConstData.TOAST_LENGTH).show();
			}
		});
	}

	/**
	 * ������dialog
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
				dialog.cancel();
				dialog = null;
			} catch (Exception e) {
			}
		}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		removeActivityFromList();
	}

	/**
	 * ��ҳ�����ʱ���»�ȡ����
	 */
	public abstract void reLoadData();

	protected void showFragmentIfNeeded(Fragment fragment, String tag,
			int fragmentRes, String[] tags) {
		if (!(mContext instanceof BaseFragmentActivity))
			return;
		showFragment(fragment, true);
		FragmentManager fragmentManager = ((BaseFragmentActivity) mContext)
				.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		if (fragmentManager.findFragmentByTag(tag) == null) {
			transaction.add(fragmentRes, fragment, tag);
		} else {
			transaction.show(fragment);
			if (fragment instanceof BaseFragment) {
				((BaseFragment) fragment).refresh();
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
		if (fragment instanceof BaseFragment) {
			((BaseFragment) fragment).showView(show);
		}
	}

	public abstract String[] getFragmentTags();

	/**
	 * �����ݱ�ϵͳ���գ���framgentû������ʱ������fragment���ز�������,�����ڽ���activityʱ��ͳһ���������fragment
	 * ,���³�ʼ��
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
		CommonApplication.addActivity(getActivityName(), getActivity());
	};

	public void removeActivityFromList() {
		CommonApplication.removeActivity(getActivityName());
	}

	public abstract String getActivityName();

	public abstract Activity getActivity();
}
