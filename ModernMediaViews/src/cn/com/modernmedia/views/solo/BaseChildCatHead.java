package cn.com.modernmedia.views.solo;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.index.IndexViewPagerItem;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.model.Entry;

/**
 * 独立栏目/子栏目导航栏基类
 * 
 * @author user
 * 
 */
public class BaseChildCatHead {
	public static final int DURATION = 250;
	protected Context mContext;
	protected View view;
	protected int height;
	private IndexViewPagerItem indexViewPagerItem;
	protected ViewGroup frame;
	private IndexListType type;
	/**
	 * 由于可能是独立栏目，并且绑定在列表上，导致每次切换的时候都还原，所以设置成静态变量
	 */
	public static int selectPosition = -1;

	/**
	 * 列表类型
	 * 
	 * @author user
	 * 
	 */
	public enum IndexListType {
		Normal, Child, Solo;
	}

	public BaseChildCatHead(Context context,
			IndexViewPagerItem indexViewPagerItem) {
		mContext = context;
		this.indexViewPagerItem = indexViewPagerItem;
	}

	/**
	 * 设置子栏目信息
	 * 
	 * @param parentId
	 */
	public void setChildValues(int parentId) {
		if (ParseUtil.mapContainsKey(DataHelper.childMap, parentId)) {
			initToolBar(DataHelper.childMap.get(parentId));
		}
		type = IndexListType.Child;
	}

	/**
	 * 设置独立栏目信息
	 * 
	 * @param list
	 */
	public void setSoloValues(int parentId) {
		List<SoloColumnChild> list = ModernMediaTools.getSoloChild(parentId);
		if (ParseUtil.listNotNull(list)) {
			initToolBar(list);
		}
		type = IndexListType.Solo;
	}

	/**
	 * 建造toolbar
	 * 
	 * @param list
	 */
	protected void initToolBar(final List<? extends Entry> list) {
		if (!ParseUtil.listNotNull(list))
			return;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Entry entry = list.get(0);
				if (entry instanceof CatItem)
					setSelectedItemForChild(((CatItem) entry).getParentId());
				else if (entry instanceof SoloColumnChild)
					setSelectedItemForSolo((SoloColumnChild) entry);
			}
		}, DURATION);
	}

	protected void clickItem(final Entry entry, final int position) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				click(entry, position);
			}
		}, DURATION);
	}

	private void click(Entry entry, int position) {
		if (mContext instanceof ViewsMainActivity) {
			((ViewsMainActivity) mContext).getScrollView().IndexClick();
			// 如果点击的是当前显示的cat,不重新 获取数据
			if (selectPosition == position) {
				return;
			}
			int parentId = -1;
			SoloColumnChild child = null;
			if (entry instanceof CatItem) {
				CatItem item = (CatItem) entry;
				DataHelper.setChildId(mContext, item.getParentId(),
						item.getId());
				V.setIndexTitle(mContext, item);
				parentId = item.getParentId();
			} else if (entry instanceof SoloColumnChild) {
				V.setIndexTitle(mContext, entry);
				child = (SoloColumnChild) entry;
			}
			// TODO 独立栏目只走catClickListener
			if (type != IndexListType.Solo && indexViewPagerItem != null) {
				indexViewPagerItem.setHasSetData(false);
				indexViewPagerItem.fetchData();
			} else if (ViewsApplication.catClickListener != null) {
				ViewsApplication.catClickListener.onClick(position, parentId,
						child);
			}
			selectPosition = position;
			if (entry instanceof CatItem)
				setSelectedItemForChild(parentId);
			else if (entry instanceof SoloColumnChild)
				setSelectedItemForSolo(child);
		}
	}

	/**
	 * 设置子栏目选中状态
	 * 
	 * @param parentId
	 */
	protected void setSelectedItemForChild(int parentId) {
		if (frame == null || frame.getChildCount() == 0)
			return;
		int savedCatId = DataHelper.getChildId(mContext, parentId + "");
		for (int i = 0; i < frame.getChildCount(); i++) {
			final int position = i;
			View child = frame.getChildAt(i);
			if (savedCatId == -1) {
				if (i == 0) {
					setStatusBySelect(child, true, 0);
				} else {
					setStatusBySelect(child, false, -1);
				}
			} else if (child.getTag() instanceof CatItem) {
				if (((CatItem) child.getTag()).getId() == savedCatId) {
					setStatusBySelect(child, true, position);
				} else {
					setStatusBySelect(child, false, -1);
				}
			}
		}
	}

	/**
	 * 设置独立栏目选中状态
	 * 
	 * @param soloColumnChild
	 */
	protected void setSelectedItemForSolo(SoloColumnChild soloColumnChild) {
		if (frame == null || frame.getChildCount() == 0)
			return;
		for (int i = 0; i < frame.getChildCount(); i++) {
			final int position = i;
			View child = frame.getChildAt(i);
			if (child.getTag() instanceof SoloColumnChild) {
				if (((SoloColumnChild) child.getTag()).getName().equals(
						soloColumnChild.getName())) {
					setStatusBySelect(child, true, position);
				} else {
					setStatusBySelect(child, false, -1);
				}
			}
		}
	}

	public void setView(View view) {
		this.view = view;
	}

	public View fetchView() {
		return view;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * 设置选中状态
	 * 
	 * @param view
	 * @param select
	 * @param index
	 *            选中的位置
	 */
	public void setStatusBySelect(View view, boolean select, int index) {
	}

}
