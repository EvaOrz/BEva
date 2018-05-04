package cn.com.modernmedia.views.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.index.IndexViewPagerItem;
import cn.com.modernmedia.views.index.IndexViewPagerItemUri;

/**
 * 可滑动的首页适配器
 * 
 * @author user
 * 
 */
public class IndexViewPagerAdapter extends MyPagerAdapter<TagInfo> {
	private Context mContext;
	private int currentPosition = -1;
	private IndexViewPagerItem currentIndexViewPagerItem;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, CheckNavHideListener> listenerMap = new HashMap<Integer, CheckNavHideListener>();

	public static interface CheckNavHideListener {
		public void onResume();

		public void onPause();
	}

	public IndexViewPagerAdapter(Context context, List<TagInfo> tagList) {
		super(context, tagList);
		mContext = context;
		listenerMap.clear();
	}

	@Override
	public View fetchView(TagInfo t) {
		if (t.isUriTag()) {
			return new IndexViewPagerItemUri(mContext, t, this).fetchView();
		}
		return new IndexViewPagerItem(mContext, t, this).fetchView();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Object obj = super.instantiateItem(container, position);
		if (obj instanceof View) {
			View view = (View) obj;
			if (view.getTag() instanceof CheckNavHideListener) {
				listenerMap.put(position,
						((CheckNavHideListener) view.getTag()));
			}
		}
		return obj;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (object instanceof View) {
			View view = (View) object;
			if (view.getTag() instanceof IndexViewPagerItem) {
				((IndexViewPagerItem) view.getTag()).destory();
				listenerMap.remove(position);
			}
		}
		super.destroyItem(container, position, object);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int loopPosition,
			int realPosition, Object object) {
		if (currentPosition == realPosition)
			return;
		currentPosition = realPosition;
		stopCheckNav();
		if (object instanceof View
				&& ((View) object).getTag() instanceof IndexViewPagerItem) {
			currentIndexViewPagerItem = (IndexViewPagerItem) ((View) object)
					.getTag();
			currentIndexViewPagerItem.fetchData("", false, false, null, null);
		}
	}

	private void stopCheckNav() {
		for (int key : listenerMap.keySet()) {
			CheckNavHideListener listener = listenerMap.get(key);
			if (listener == null)
				continue;
			if (key != currentPosition) {
				listener.onPause();
			} else {
				listener.onResume();
			}
		}
	}

	public List<View> getSelfScrollViews() {
		if (currentIndexViewPagerItem == null)
			return null;
		return currentIndexViewPagerItem.getHeadView();
	}

	public IndexViewPagerItem getCurrentPagerItem() {
		return currentIndexViewPagerItem;
	}

}
