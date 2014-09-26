package cn.com.modernmedia;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.newtag.mainprocess.MainProcessObservable;
import cn.com.modernmedia.newtag.mainprocess.MainProcessObservable.ObserverItem;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.PageTransfer;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.MainHorizontalScrollView;
import cn.com.modernmediaslate.SlateApplication;

import com.parse.ParseAnalytics;

/**
 * 公共首页
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonMainActivity extends BaseFragmentActivity implements
		Observer {
	private long lastClickTime = 0;
	protected int pushArticleId = -1;
	protected MainHorizontalScrollView scrollView;
	private List<NotifyAdapterListener> listenerList = new ArrayList<NotifyAdapterListener>();
	private TagProcessManage manage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CommonApplication.mainProcessObservable.addObserver(this,
				getActivityName());
		init();
		startMainProcess(getIntent(), true);
	}

	protected void startMainProcess(Intent intent, boolean track) {
		if (track)
			ParseAnalytics.trackAppOpened(intent);
		if (intent == null)
			return;
		manage = new TagProcessManage(this);
		manage.checkIntent(intent);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		startMainProcess(intent, false);
	}

	/**
	 * 初始化资源
	 */
	protected abstract void init();

	@Override
	public void update(Observable observable, Object data) {
		if (!(data instanceof ObserverItem))
			return;
		ObserverItem item = (ObserverItem) data;
		switch (item.flag) {
		case MainProcessObservable.SET_DATA_TO_COLUMN:
			setDataForColumn();
			break;
		case MainProcessObservable.SET_DATA_TO_INDEX:
			if (item.entry instanceof TagArticleList)
				setDataForIndex((TagArticleList) item.entry);
			break;
		case MainProcessObservable.SHOW_CHILD_CAT:
			if (item.entry instanceof TagInfoList) {
				showChildCat((TagInfoList) item.entry);
			}
			break;
		case MainProcessObservable.SHOW_INDEX_PAGER:
			showIndexPager();
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化栏目列表
	 */
	protected abstract void setDataForColumn();

	/**
	 * 初始化index
	 * 
	 * @param entry
	 */
	protected abstract void setDataForIndex(TagArticleList articleList);

	/**
	 * 子栏目
	 * 
	 * @param childInfoList
	 */
	protected abstract void showChildCat(TagInfoList childInfoList);

	/**
	 * 显示首页滑屏view
	 */
	protected abstract void showIndexPager();

	public abstract MainHorizontalScrollView getScrollView();

	/**
	 * 如果继承的应用包含用户模块，需要重载此方法
	 * 
	 * @return
	 */
	public String getUid() {
		return SlateApplication.UN_UPLOAD_UID;
	}

	public String getToken() {
		return "";
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
	 * 通知更新栏目列表选中状态
	 * 
	 * @param tagName
	 */
	public void notifyColumnAdapter(String tagName) {
		for (NotifyAdapterListener listener : listenerList) {
			listener.nofitySelectItem(tagName);
		}
	}

	/**
	 * 跳转到某个栏目首页
	 * 
	 * @param tagName
	 *            栏目名称
	 * @param isUri
	 *            是否来自uri;如果是，当找不到的时候需要添加这个栏目，否则，显示第一个栏目
	 */
	public void clickItemIfPager(String tagName, boolean isUri) {
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
		if (manage != null)
			manage.reLoadData();
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
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
