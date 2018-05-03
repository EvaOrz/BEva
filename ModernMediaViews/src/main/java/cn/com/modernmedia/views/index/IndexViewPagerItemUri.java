package cn.com.modernmedia.views.index;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.adapter.IndexViewPagerAdapter;
import cn.com.modernmedia.views.listener.LoadCallBack;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 可滑动的首页view item（通过uri新增的栏目）
 * 
 * @author zhuqiao
 *
 */
public class IndexViewPagerItemUri extends IndexViewPagerItem {

	public IndexViewPagerItemUri(Context context, TagInfo tagInfo,
			IndexViewPagerAdapter adapter) {
		super(context, tagInfo, adapter);
	}

	@Override
	protected boolean checkShowCatHead() {
		if (TextUtils.isEmpty(tagInfo.getParent())
				|| tagInfo.getParent().startsWith("app")) {
			// TODO 一级栏目
			return tagInfo.getHaveChildren() == 1;
		}
		// TODO 二级栏目默认返回true
		return true;
	}

	@Override
	public void fetchData(String top, boolean isRefresh, boolean isLoadMore,
			LoadCallBack callBack, TagArticleList tagArticleList) {
		if (tagInfo == null)
			return;
		if (hasSetData)
			return;
		if (tagInfo.getHaveChildren() == 1) {
			getChildren();
		} else {
			getArticleList(tagInfo, top, isRefresh, isLoadMore, callBack,
					tagArticleList);
		}
	}

	@Override
	protected void getChildren() {
		childInfoList = new TagInfoList();
		Map<String, List<TagInfo>> map = AppValue.uriTagInfoList.getChildMap();
		if (ParseUtil.mapContainsKey(map, tagInfo.getTagName())) {
			for (TagInfo info : map.get(tagInfo.getTagName())) {
				childInfoList.getList().add(info);
			}
		}
		if (ParseUtil.listNotNull(childInfoList.getList())) {
			getCatIndex(childInfoList.getList().get(0));
		}
	}

	@Override
	protected String getApiTagName() {
		return tagInfo.getTagName();
	}

}
