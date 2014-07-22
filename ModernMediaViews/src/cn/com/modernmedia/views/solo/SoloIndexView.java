package cn.com.modernmedia.views.solo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.index.IndexViewPagerItem;

/**
 * 独立栏目首页
 * 
 * @author user
 * 
 */
public class SoloIndexView extends BaseSoloIndexView {
	private Context mContext;
	private List<SoloColumnChild> childColumnList;// 子栏目列表
	@SuppressLint("UseSparseArrays")
	private Map<Integer, SoloCatItem> map = new HashMap<Integer, SoloCatItem>();
	private IndexViewPagerItem indexViewPagerItem;// 使用它里面的soloHelper操作
	private SoloCatItem currentCatItem;

	public SoloIndexView(Context context) {
		this(context, null);
	}

	public SoloIndexView(Context context, IndexViewPagerItem indexViewPagerItem) {
		super(context);
		mContext = context;
		this.indexViewPagerItem = indexViewPagerItem;
		map.clear();
		init(10);
	}

	@Override
	public void setData(int parentId) {
		super.setData(parentId);
		if (CommonApplication.manage != null)
			childColumnList = ModernMediaTools.getSoloChild(parentId);
		if (ParseUtil.listNotNull(childColumnList)
				&& childColumnList.size() > 1) {
			catHead.setSoloValues(parentId);
		} else {
			frameLayout.setVisibility(View.GONE);
		}
		ChildPagerAdapter adapter = new ChildPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setValue(childColumnList.size());
	}

	@Override
	public void onClick(int position, int parentId, SoloColumnChild soloChild) {
		super.onClick(position, parentId, soloChild);
		if (!map.isEmpty()) {
			for (int key : map.keySet()) {
				map.get(key).onClick(position, parentId, soloChild);
			}
		}
	}

	@Override
	public void updateDes(int position) {
		super.updateDes(position);
		if (childColumnList != null && childColumnList.size() > position) {
			SoloColumnChild child = childColumnList.get(position);
			if (catHead != null)
				catHead.clickItem(child, position);
		}
		reStoreRefresh();
	}

	@Override
	public void updatePage(int state) {
		if (state == ViewPager.SCROLL_STATE_DRAGGING) {
			reStoreRefresh();
		}
	}

	public void reStoreRefresh() {
		int position = viewPager.getCurrentItem();
		if (ParseUtil.mapContainsKey(map, position)) {
			map.get(position).reStoreRefresh();
		}
	}

	public SoloCatItem getCurrentCatItem() {
		return currentCatItem;
	}

	private class ChildPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return childColumnList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			SoloCatItem childView = new SoloCatItem(mContext, parentId,
					indexViewPagerItem);
			if (ParseUtil.listNotNull(childColumnList)
					&& childColumnList.size() > position)
				childView.setData(childColumnList.get(position));
			container.addView(childView.fetchView());
			map.put(position, childView);
			return childView.fetchView();
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			super.setPrimaryItem(container, position, object);
			if (map.containsKey(position)) {
				currentCatItem = map.get(position);
				currentHeadView = currentCatItem.getHeadView();
				currentHeadSize = currentCatItem.getChildSize();
			}
		}
	}

}
