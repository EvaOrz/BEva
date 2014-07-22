package cn.com.modernmediasolo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.com.modernmedia.adapter.CheckScrollAdapter;
import cn.com.modernmedia.model.ArticleItem;

public class SoloAdapter extends CheckScrollAdapter<ArticleItem> {
	private List<Integer> descList = new ArrayList<Integer>();
	private List<ArticleItem> mItemList = new ArrayList<ArticleItem>();

	public SoloAdapter(Context context) {
		super(context);
		mItemList.clear();
	}

	/**
	 * 
	 * @param list
	 * @param addToFirst
	 *            是否添加在前面
	 */
	public void setData(List<ArticleItem> list, boolean addToFirst) {
		if (addToFirst) {
			mItemList.addAll(0, list);
			setData(mItemList);
		} else {
			mItemList.addAll(list);
			setData(list);
		}
	}

	private void setData(List<ArticleItem> list) {
		synchronized (list) {
			for (ArticleItem item : list) {
				add(item);
			}
		}
	}

	public void clearData() {
		mItemList.clear();
		descList.clear();
	}

}
