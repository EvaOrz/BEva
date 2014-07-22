package cn.com.modernmedia.views.solo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.ParseUtil;

/**
 * 子栏目首页
 * 
 * @author user
 * 
 */
public class ChildIndexView extends BaseSoloIndexView {
	private Context mContext;
	private List<CatItem> list;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, ChildCatItem> map = new HashMap<Integer, ChildCatItem>();

	public ChildIndexView(Context context) {
		super(context);
		mContext = context;
		map.clear();
		init(1);
	}

	@Override
	public void setData(int parentId) {
		super.setData(parentId);
		if (ParseUtil.mapContainsKey(DataHelper.childMap, parentId)) {
			list = DataHelper.childMap.get(parentId);
			viewPager.setValue(list.size());
			catHead.setChildValues(parentId);
		} else {
			list = new ArrayList<CatItem>();
			frameLayout.setVisibility(View.GONE);
		}
		ChildPagerAdapter adapter = new ChildPagerAdapter();
		viewPager.setAdapter(adapter);
		if (ParseUtil.listNotNull(list)) {
			int savedCatId = DataHelper.getChildId(mContext, parentId + "");
			if (savedCatId != -1) {
				for (int i = 0; i < list.size(); i++) {
					CatItem item = list.get(i);
					if (item.getId() == savedCatId) {
						onClick(i, parentId, null);
						break;
					}
				}
			}
		}
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
		if (list != null && list.size() > position) {
			CatItem item = list.get(position);
			if (catHead != null) {
				catHead.clickItem(item, position);
			}
		}
	}

	private class ChildPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			CatItem item = list.get(position);
			ChildCatItem childView = new ChildCatItem(mContext, null, parentId);
			childView.setData(item.getId() + "");
			container.addView(childView);
			map.put(position, childView);
			return childView;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			super.setPrimaryItem(container, position, object);
			View view = viewPager.getChildAt(position);
			if (view instanceof ChildCatItem) {
				currentHeadView = ((ChildCatItem) view).getHeadView();
				currentHeadSize = ((ChildCatItem) view).getChildSize();
			}
		}

	}
}
