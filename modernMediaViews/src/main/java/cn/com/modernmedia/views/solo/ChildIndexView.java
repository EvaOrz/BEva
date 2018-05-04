package cn.com.modernmedia.views.solo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;

/**
 * 子栏目首页
 * 
 * @author user
 * 
 */
public class ChildIndexView extends BaseSoloIndexView {
	private Context mContext;
	private List<TagInfo> list;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, ChildCatItem> map = new HashMap<Integer, ChildCatItem>();

	public ChildIndexView(Context context) {
		super(context);
		mContext = context;
		map.clear();
		init(1);
	}

	@Override
	public void setData(TagInfoList childInfoList) {
		super.setData(childInfoList);
		list = childInfoList.getList();
		viewPager.setValue(list.size());
		ChildPagerAdapter adapter = new ChildPagerAdapter();
		viewPager.setAdapter(adapter);
	}

	@Override
	public void onClick(int position, TagInfo info) {
		super.onClick(position, info);
		if (!map.isEmpty()) {
			for (int key : map.keySet()) {
				map.get(key).onClick(position, info);
			}
		}
	}

	@Override
	public void updateDes(int position) {
		super.updateDes(position);
		if (list != null && list.size() > position) {
			TagInfo item = list.get(position);
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
			TagInfo item = list.get(position);
			ChildCatItem childView = new ChildCatItem(mContext, null, item,
					childInfoList, ChildIndexView.this);
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
