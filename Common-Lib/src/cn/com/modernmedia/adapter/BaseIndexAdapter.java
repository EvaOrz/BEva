package cn.com.modernmedia.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.model.ArticleItem;

public class BaseIndexAdapter extends CheckScrollAdapter<ArticleItem> {
	private BaseIndexAdapter me;
	private Context mContext;
	private List<Integer> descList = new ArrayList<Integer>();// 直接加载desc比较卡
	private FavDb favDb;
	private ReadDb readDb;

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

	public BaseIndexAdapter(Context context) {
		super(context);
		me = this;
		mContext = context;
		favDb = FavDb.getInstance(mContext);
		readDb = ReadDb.getInstance(mContext);
		if (context instanceof CommonMainActivity) {
			((CommonMainActivity) context).addListener(adapterListener);
		}
	}

	public void setData(List<ArticleItem> list) {
		descList.clear();
		isScroll = false;
		synchronized (list) {
			for (ArticleItem item : list) {
				add(item);
			}
		}
	}

	/**
	 * 是否阅读过
	 * 
	 * @param articleId
	 * @return
	 */
	protected boolean isReaded(int articleId) {
		return readDb.isReaded(articleId);
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
	protected void addDesc(int position) {
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
	protected boolean hasInitDesc(int position) {
		return descList.contains(position);
	}
}
