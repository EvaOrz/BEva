package cn.com.modernmedia;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.mainprocess.MainProcessManage;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.PageTransfer;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.MainHorizontalScrollView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

import com.parse.ParseAnalytics;

/**
 * 公共首页
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonMainActivity extends BaseFragmentActivity {
	private long lastClickTime = 0;
	protected int pushArticleId = -1;
	protected MainHorizontalScrollView scrollView;
	private List<NotifyAdapterListener> listenerList = new ArrayList<NotifyAdapterListener>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		initParse();
	}

	protected void initParse() {
		ParseAnalytics.trackAppOpened(getIntent());
		CommonApplication.manage = new MainProcessManage(this);
		CommonApplication.manage.checkIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getExtras() != null) {
			if (CommonApplication.manage == null)
				CommonApplication.manage = new MainProcessManage(this);
			CommonApplication.manage.parsePush(intent);
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		Builder builder = new Builder(this);
		builder.setMessage(R.string.new_issue);
		builder.setPositiveButton(R.string.vew_later,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 稍后查看
						CommonApplication.manage.getProcess().viewLater(id);
					}
				}).setNegativeButton(R.string.view_now,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CommonApplication.manage.getProcess().viewNow(id);
					}
				});
		AlertDialog dialog = builder.create();
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					CommonApplication.manage.getProcess().viewLater(id);
					dialog.cancel();
					return true;
				}
				return false;
			}
		});
		return dialog;
	}

	/**
	 * 初始化资源
	 */
	protected abstract void init();

	/**
	 * 初始化栏目列表
	 * 
	 * @param entry
	 */
	public abstract void setDataForColumn(Entry entry);

	/**
	 * 初始化index
	 * 
	 * @param entry
	 */
	public abstract void setDataForIndex(Entry entry);

	/**
	 * 显示独立栏目
	 * 
	 * @param parentId
	 */
	public void showSoloChildCat(int parentId) {
	}

	/**
	 * 独立栏目请求完文章后赋值
	 * 
	 * @param list
	 */
	public void setSoloArticleList(List<FavoriteItem> list) {
	}

	/**
	 * 显示首页滑屏view
	 */
	public void showIndexPager() {
	}

	public abstract MainHorizontalScrollView getScrollView();

	protected Class<?> getArticleActivity() {
		return null;
	}

	/**
	 * 如果继承的应用包含用户模块，需要重载此方法
	 * 
	 * @return
	 */
	protected String getUid() {
		return ConstData.UN_UPLOAD_UID;
	}

	/**
	 * indexview 显示loading
	 * 
	 * @param flag
	 *            0.不显示，1.显示loading，2.显示error
	 */
	public void checkIndexLoading(int flag) {
		if (getIndexView() != null) {
			if (flag == 0)
				getIndexView().disProcess();
			else if (flag == 1)
				getIndexView().showLoading();
			else if (flag == 2)
				getIndexView().showError();
		}
	}

	public abstract BaseView getIndexView();

	/**
	 * 设置首页title
	 * 
	 * @param name
	 */
	public abstract void setIndexTitle(String name);

	protected void notifyRead() {
		for (NotifyAdapterListener listener : listenerList) {
			listener.notifyReaded();
		}
	};

	public void gotoArticleActivity(TransferArticle transferArticle) {
		if (getArticleActivity() == null) {
			return;
		}
		PageTransfer.gotoArticleActivity(this, getArticleActivity(),
				transferArticle);
	}

	public void setPushArticleId(int pushArticleId) {
		this.pushArticleId = pushArticleId;
	}

	/**
	 * 给scrollview设置需要拦截的子scrollview
	 * 
	 * @param view
	 */
	public void setScrollView(View view) {
		scrollView.addNeedsScrollView(view);
	}

	/**
	 * 当切换栏目时，清除(栏目是viewpager时不考虑，因为scroll本身就不可滑动)
	 */
	public void clearScrollView() {
		scrollView.clearNeedsScrollView();
	}

	public void addListener(NotifyAdapterListener listener) {
		listenerList.add(listener);
	}

	/**
	 * 通知更新栏目列表选中状态״
	 * 
	 * @param catId
	 */
	public void notifyColumnAdapter(int catId) {
		for (NotifyAdapterListener listener : listenerList) {
			listener.nofitySelectItem(catId);
		}
	}

	public String getColumnId() {
		if (CommonApplication.manage == null)
			return "";
		return CommonApplication.manage.getProcess().getColumnId() + "";
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			long clickTime = System.currentTimeMillis() / 1000;
			if (clickTime - lastClickTime >= 3) {
				lastClickTime = clickTime;
				Toast.makeText(this, R.string.exit_app, ConstData.TOAST_LENGTH)
						.show();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void reLoadData() {
		if (CommonApplication.manage != null)
			CommonApplication.manage.reLoadData();
	}

	@Override
	protected void exitApp() {
		super.exitApp();
		CommonApplication.exit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == PageTransfer.REQUEST_CODE) {
				notifyRead();

				// TODO
				// 如果点击往期进入文章页，进入之前改变currentIssueId,从文章页退出，还原为最新当前index的issueId;(现在改成:只要从文章页返回，就替换)
				if (CommonApplication.issue != null) {
					CommonApplication.currentIssueId = CommonApplication.issue
							.getId();
				}

				if (pushArticleId != -1) {
					pushArticleId = -1;
					CommonApplication.manage.fetchFromHttpAfterParse();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
