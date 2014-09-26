package cn.com.modernmedia.views.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.index.IndexViewPagerItem;
import cn.com.modernmedia.views.index.IndexViewPagerItemUri;
import cn.com.modernmediaslate.model.Entry;

/**
 * 可滑动的首页适配器
 * 
 * @author user
 * 
 */
public class IndexViewPagerAdapter extends MyPagerAdapter<TagInfo> {
	private Context mContext;
	private int currentPosition = -1;
	// 保存已经获得的列表数据
	@SuppressLint("UseSparseArrays")
	private Map<String, Entry> indexMap = new HashMap<String, Entry>();
	private Map<String, Entry> articleMap = new HashMap<String, Entry>();
	private IndexViewPagerItem currentIndexViewPagerItem;

	public IndexViewPagerAdapter(Context context, List<TagInfo> tagList) {
		super(context, tagList);
		mContext = context;
		indexMap.clear();
		articleMap.clear();
	}

	@Override
	public View fetchView(TagInfo t) {
		if (t.isUriTag()) {
			return new IndexViewPagerItemUri(mContext, t, this).fetchView();
		}
		return new IndexViewPagerItem(mContext, t, this).fetchView();
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
			currentIndexViewPagerItem.fetchData("", false, false, null, null);
		}
	}

	/**
	 * 添加文章列表
	 * 
	 * @param tagName
	 * @param articleList
	 * @param more
	 */
	public void addArticleListToMap(String tagName, TagArticleList articleList,
			boolean more, boolean isIndex) {
		Map<String, Entry> map = isIndex ? indexMap : articleMap;
		if (!more) {
			if (map.containsKey(tagName)) {
				map.remove(tagName);
			}
		} else {
			if (map.containsKey(tagName)) {
				if (map.get(tagName) instanceof TagArticleList) {
					TagArticleList curr = (TagArticleList) map.get(tagName);
					for (int key : curr.getMap().keySet()) {
						if (articleList.hasData(key)) {
							curr.getMap().get(key)
									.addAll(articleList.getMap().get(key));
						}
					}
					curr.setEndOffset(articleList.getEndOffset());
					map.remove(tagName);
					map.put(tagName, curr);
					return;
				}
			}
		}
		map.put(tagName, articleList);
	}

	/**
	 * 添加子栏目列表
	 * 
	 * @param tagName
	 * @param tagInfoList
	 */
	public void addChildrenTopMap(String tagName, TagInfoList tagInfoList,
			boolean isIndex) {
		Map<String, Entry> map = isIndex ? indexMap : articleMap;
		if (map.containsKey(tagName)) {
			map.remove(tagName);
		}
		map.put(tagName, tagInfoList);
	}

	public Entry getEntryFromMap(String tagName, boolean isIndex) {
		Map<String, Entry> map = isIndex ? indexMap : articleMap;
		if (map.containsKey(tagName)) {
			return map.get(tagName);
		}
		return null;
	}

	public void removeEntryFromMap(String tagName, boolean isIndex) {
		Map<String, Entry> map = isIndex ? indexMap : articleMap;
		if (map.containsKey(tagName)) {
			map.remove(tagName);
		}
	}

	public List<View> getSelfScrollViews() {
		if (currentIndexViewPagerItem == null)
			return null;
		return currentIndexViewPagerItem.getHeadView();
	}

}
