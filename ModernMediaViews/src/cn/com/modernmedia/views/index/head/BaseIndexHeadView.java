package cn.com.modernmedia.views.index.head;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.common.ShareHelper;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 首页焦点图
 * 
 * @author user
 * 
 */
public abstract class BaseIndexHeadView extends BaseView implements
		NotifyArticleDesListener {
	private static final int CHANGE = 1;
	private static final int CHANGE_DELAY = 5000;

	protected Context mContext;
	protected IndexHeadCircularViewPager viewPager;
	private List<ImageView> dots = new ArrayList<ImageView>();
	protected List<ArticleItem> mList = new ArrayList<ArticleItem>();
	private boolean isAuto;
	private boolean shouldScroll;// 如果只有一张图，那么不监听滑动
	protected Template template;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == CHANGE) {
				viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
				startChange();
			}
		}

	};

	public BaseIndexHeadView(Context context, Template template) {
		super(context);
		mContext = context;
		this.template = template;
		init();
		initProperties();
	}

	/**
	 * 初始化资源
	 */
	protected abstract void init();

	private void initProperties() {
		if (viewPager != null) {
			viewPager.setListener(this);
			viewPager.setTemplate(template);
		}
	}

	@Override
	public void updateDes(int position) {
		if (mList != null && mList.size() > position && mList.size() > 1) {
			ArticleItem item = mList.get(position);
			updateTitle(item);
			if (position == 0) {
				position = dots.size() - 1;
			} else if (position == mList.size() - 1) {
				position = 0;
			} else {
				position--;
			}
			for (int i = 0; i < dots.size(); i++) {
				if (i == position) {
					dots.get(i).setImageResource(R.drawable.dot_active);
				} else {
					dots.get(i).setImageResource(R.drawable.dot);
				}
			}
			LogHelper.lodAndroidShowHeadline(position);
			AdvTools.requestImpression(item);
		}
	}

	@Override
	public void updatePage(int state) {
	}

	/**
	 * 独立栏目
	 * 
	 * @param soloHeadIndex
	 */
	public void setSoloData(List<ArticleItem> soloHeadIndex) {
		setDataToGallery(soloHeadIndex);
	}

	/**
	 * 获取当前list;当是独立栏目时，如果从服务器上返会的list。size为0，而当前headview不为空，那么不删除headview
	 * 
	 * @return
	 */
	public List<ArticleItem> getDataList() {
		return mList;
	}

	public void setData(List<ArticleItem> list) {
		setDataToGallery(list);
	}

	/**
	 * 
	 * @param list
	 * @param isSolo
	 *            true:更新完的数据，追加在开始添加；false:覆盖本来数据
	 */
	protected void setDataToGallery(final List<ArticleItem> list) {
		if (list == null || viewPager == null)
			return;
		viewPager.setArticleType(ArticleType.Default);
		mList.clear();
		mList.addAll(list);
		if (!ParseUtil.listNotNull(mList))
			return;
		if (mContext instanceof CommonMainActivity) {
			if (mList.size() == 1) {
				shouldScroll = false;
			} else {
				((CommonMainActivity) mContext).setScrollView(this);
				shouldScroll = true;
				startRefresh();
			}
		}
		updateTitle(mList.get(0));
		// addOutline(list.get(0).getOutline(), parm.getItem_outline_img());
		viewPager.setDataForPager(mList);
		initDot(mList, dots);
	}

	protected void addOutline(String outline, String outlineImg) {
	}

	/**
	 * 分享
	 */
	protected void doShare() {
		if (mContext instanceof ViewsMainActivity) {
			int position = viewPager.getCurrentItem();
			if (mList != null && mList.size() > position) {
				ShareHelper.shareByDefault(mContext, mList.get(position));
			}
		}
	}

	private void startChange() {
		if (mList == null || mList.size() < 2 || !isAuto
				|| ConstData.getAppId() == 20) {
			return;
		}
		Message msg = handler.obtainMessage();
		if (handler.hasMessages(CHANGE)) {
			handler.removeMessages(CHANGE);
		}
		msg.what = 1;
		handler.sendMessageDelayed(msg, CHANGE_DELAY);
	}

	/**
	 * 开始自动切换
	 */
	public void startRefresh() {
		if (viewPager == null)
			return;
		isAuto = true;
		startChange();
	}

	/**
	 * 停止后台自动切换
	 */
	public void stopRefresh() {
		if (handler.hasMessages(1)) {
			handler.removeMessages(1);
		}
		isAuto = false;
	}

	protected void updateTitle(ArticleItem item) {
	}

	protected void initDot(List<ArticleItem> itemList, List<ImageView> dots) {
	}

	public View getViewPager() {
		return viewPager;
	}

	public boolean isShouldScroll() {
		return shouldScroll;
	}

	public void dismissHead() {
		if (viewPager != null) {
			setPadding(0, -viewPager.getLayoutParams().height, 0, 0);
			invalidate();
		}
	}

	@Override
	protected void reLoad() {
	}
}
