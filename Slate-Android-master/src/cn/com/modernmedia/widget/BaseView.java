package cn.com.modernmedia.widget;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.com.modernmedia.BaseFragmentActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.Fragment.BaseFragment;
import cn.com.modernmedia.listener.SlateListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.util.UriParse;

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
	private SlateListener listener;

	public BaseView(Context context) {
		super(context);
		mContext = context;
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

	public void setListener(SlateListener listener) {
		this.listener = listener;
	}

	protected void clickSlate(ArticleItem item) {
		if (listener == null)
			return;
		String link = "";
		int type = item.getAdv().getAdvProperty().getIsadv();
		if (type == 1) {// 广告
			link = item.getAdv().getColumnAdv().getLink();
		} else {
			link = item.getSlateLink();
		}
		if (TextUtils.isEmpty(link)) {
			listener.linkNull(item);
		} else if (link.toLowerCase().startsWith("http://")) {
			Uri uri = Uri.parse(link);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			listener.httpLink(item, intent);
		} else if (link.toLowerCase().startsWith("slate://")) {
			List<String> list = UriParse.parser(link);
			if (list.size() > 1) {
				if (list.get(0).equalsIgnoreCase("video")) {
					String path = list.get(1).replace(".m3u8", ".mp4");//
					if (path.toLowerCase().endsWith(".mp4")) {
						listener.video(item, path);
					}
				} else if (list.get(0).equalsIgnoreCase("article")) {
					if (list.size() > 3) {
						listener.articleLink(item,
								ParseUtil.stoi(list.get(3), -1));
					}
				}
			}
		}
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

	protected void deleteAllFragments(String[] tags) {
		if (!(mContext instanceof BaseFragmentActivity))
			return;
		FragmentManager fragmentManager = ((BaseFragmentActivity) mContext)
				.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		for (String hideTag : tags) {
			Fragment fragmentNeedHide = fragmentManager
					.findFragmentByTag(hideTag);
			if (fragmentNeedHide != null) {
				showFragment(fragmentNeedHide, false);
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
}
