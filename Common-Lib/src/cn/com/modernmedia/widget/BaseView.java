package cn.com.modernmedia.widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.com.modernmedia.BaseFragmentActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.Fragment.BaseFragment;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmediaslate.model.Entry;

/**
 * 自定义view的父类
 * 
 * @author ZhuQiao
 * 
 */
public abstract class BaseView extends RelativeLayout {
	private Context mContext;
	private RelativeLayout process_layout;
	private ProgressBar loading;
	private ImageView error;

	public BaseView(Context context) {
		this(context, null);
	}

	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public void initProcess() {
		process_layout = (RelativeLayout) findViewById(R.id.process_layout);
		loading = (ProgressBar) findViewById(R.id.loading);
		error = (ImageView) findViewById(R.id.error);
		process_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (error.getVisibility() == View.VISIBLE) {
					reLoad();
				}
			}
		});
	}

	/**
	 * 显示loading图标
	 */
	public void showLoading() {
		if (process_layout == null) {
			Toast.makeText(mContext, "未初始化process!!", ConstData.TOAST_LENGTH)
					.show();
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
			Toast.makeText(mContext, "未初始化process!!", ConstData.TOAST_LENGTH)
					.show();
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
			Toast.makeText(mContext, "未初始化process!!", ConstData.TOAST_LENGTH)
					.show();
			return;
		}
		process_layout.setVisibility(View.GONE);
		loading.setVisibility(View.GONE);
		error.setVisibility(View.GONE);
	}

	protected abstract void reLoad();

	/**
	 * 所有点击事件都需要请求这个方法
	 * 
	 * @param entries
	 *            [0] ArticleItem;[1]TransferArticle(如果没有uri，
	 *            那么取传来的这个TransferArticle的ArticleType
	 *            ；否则，根据uri判断ArticleType);[2]issue...
	 * @param cls
	 *            [0]为特定的文章页
	 */
	public void clickSlate(Entry[] entries, Class<?>... cls) {
		UriParse.clickSlate(mContext, entries, cls);
	}

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
}
