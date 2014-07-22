package cn.com.modernmedia.views.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.model.Cat;
import cn.com.modernmedia.model.SoloColumn.SoloColumnItem;
import cn.com.modernmedia.views.index.IndexViewPagerItem;
import cn.com.modernmediaslate.model.Entry;

/**
 * 可滑动的首页适配器
 * 
 * @author user
 * 
 */
public class IndexViewPagerAdapter extends MyPagerAdapter<SoloColumnItem> {
	private Context mContext;
	private Cat cat;
	private int currentPosition = -1;
	// 保存已经获得的列表数据
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Entry> map = new HashMap<Integer, Entry>();
	private IndexViewPagerItem currentIndexViewPagerItem;

	public IndexViewPagerAdapter(Context context, Cat cat,
			List<SoloColumnItem> list) {
		super(context, list);
		mContext = context;
		this.cat = cat;
		map.clear();
	}

	@Override
	public View fetchView(SoloColumnItem t) {
		return new IndexViewPagerItem(mContext, cat, t, this).fetchView();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (object instanceof View) {
			if (((View) object).getTag() instanceof IndexViewPagerItem) {
				((IndexViewPagerItem) ((View) object).getTag()).destory();
			}
		}
		super.destroyItem(container, position, object);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		if (currentPosition == position)
			return;
		currentPosition = position;
		if (object instanceof View
				&& ((View) object).getTag() instanceof IndexViewPagerItem) {
			currentIndexViewPagerItem = (IndexViewPagerItem) ((View) object)
					.getTag();
			currentIndexViewPagerItem.fetchData();
		}
	}

	public void addEntryToMap(int catId, Entry entry) {
		if (map.containsKey(catId)) {
			map.remove(catId);
		}
		map.put(catId, entry);
	}

	public Entry getEntryFromMap(int catId) {
		if (map.containsKey(catId)) {
			return map.get(catId);
		}
		return null;
	}

	public List<View> getSelfScrollViews() {
		if (currentIndexViewPagerItem == null)
			return null;
		return currentIndexViewPagerItem.getHeadView();
	}

}
