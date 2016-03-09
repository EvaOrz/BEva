package cn.com.modernmedia.views.index.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.adapter.CheckScrollAdapter;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 首页adapter
 * 
 * @author zhuqiao
 * 
 */
public class BaseIndexAdapter extends CheckScrollAdapter<ArticleItem> {
	private BaseIndexAdapter me;
	protected Context mContext;
	private List<Integer> descList = new ArrayList<Integer>();// 直接加载desc比较卡
	private NewFavDb favDb;
	protected List<ArticleItem> mItemList = new ArrayList<ArticleItem>();
	private boolean isSoloAdapter = false; // 描述当前adapter是否用于独立栏目的，默认不是
	protected ArticleType articleType = ArticleType.Default;// 文章类型，为了给不能通过onItemClick跳转的adapter使用
	protected boolean convertViewIsNull;
	protected Template template = new Template();

	/**
	 * 通知刷新readed
	 */
	private NotifyAdapterListener adapterListener = new NotifyAdapterListener() {

		@Override
		public void notifyReaded() {
			me.notifyDataSetChanged();
		}

		@Override
		public void nofitySelectItem(Object args) {
		}

		@Override
		public void notifyChanged() {
		}
	};

	public BaseIndexAdapter(Context context, Template template) {
		super(context);
		me = this;
		mContext = context;
		this.template = template;
		favDb = NewFavDb.getInstance(mContext);
		if (context instanceof CommonMainActivity) {
			((CommonMainActivity) context).addListener(adapterListener);
		}
		mItemList.clear();
	}

	public void setData(List<ArticleItem> list) {
		descList.clear();
		isScroll = false;
		if (!ParseUtil.listNotNull(list))
			return;
		synchronized (list) {
			for (ArticleItem item : list) {
				if (item.isShowTitleBar()) {
					// TODO 单独添加titlebar
					ArticleItem titleItem = new ArticleItem();
					titleItem.setShowTitleBar(true);
					titleItem.setInputtime(item.getInputtime());
					titleItem.setGroupdisplaycolor(item.getGroupdisplaycolor());
					titleItem.setGroupdisplayname(item.getGroupdisplayname());
					titleItem.setGroupname(item.getGroupname());
					titleItem.getPosition().setStyle(0);
					add(titleItem);
				}
				// 3.0.0 6为广告模板
				if (item.isAdv() && item.getPosition().getStyle() == 6) {
					Log.e("发现广告", item.getTitle());
					if (!ParseUtil.mapContainsKey(template.getList().getMap(),
							6)) {
						// TODO 如果没有广告模板，那么把它作为普通文章
						item.setAdv(false);
					}
				}
				add(item);
			}
		}
	}

	/**
	 * 是否收藏过
	 * 
	 * @param articleId
	 * @param uid
	 * @return
	 */
	protected boolean isFaved(int articleId, String uid) {
		return favDb.containThisFav(articleId, uid);
	}

	/**
	 * 添加已加载过的item
	 * 
	 * @param position
	 */
	public void addDesc(int position) {
		try {
			if (!descList.contains(Integer.valueOf(position)))
				descList.add(position);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否加载过当前item
	 * 
	 * @param position
	 * @return
	 */
	public boolean hasInitDesc(int position) {
		return descList.contains(position);
	}

	public void clearData() {
		mItemList.clear();
		descList.clear();
	}

	public boolean isSoloAdapter() {
		return isSoloAdapter;
	}

	public void setSoloAdapter(boolean isSoloAdapter) {
		this.isSoloAdapter = isSoloAdapter;
	}

	public void setArticleType(ArticleType articleType) {
		this.articleType = articleType;
	}

	public boolean isScroll() {
		return isScroll;
	}

	public boolean isConvertViewIsNull() {
		return convertViewIsNull;
	}

	/**
	 * 下载图片
	 * 
	 * @param item
	 * @param imageView
	 */
	public void downImage(ArticleItem item, View image, boolean usePicture) {
		if (usePicture) {
			if (ParseUtil.listNotNull(item.getPicList()))
				CommonApplication.finalBitmap.display(image, item.getPicList()
						.get(0).getUrl());
		} else {
			if (ParseUtil.listNotNull(item.getThumbList()))
				CommonApplication.finalBitmap.display(image, item
						.getThumbList().get(0).getUrl());
		}
	}

}
