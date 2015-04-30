package cn.com.modernmedia.newtag.mainprocess;

import android.content.Context;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.model.Entry;

/**
 * 获取视野首页帮助类
 * 
 * @author zhuqiao
 *
 */
public abstract class FetchShiyeIndexHelper {
	private static final String SHIYE_TAG_NAME = "cat_191";

	private Context mContext;

	public FetchShiyeIndexHelper(Context context) {
		mContext = context;
	}

	/**
	 * 获取视野栏目首页数据
	 */
	public void getShiyeTagIndex(FetchApiType apiType) {
		// 组装视野tagInfo
		TagInfo tagInfo = new TagInfo();
		tagInfo.setTagName(SHIYE_TAG_NAME);
		tagInfo.setHaveChildren(0);
		tagInfo.setColoumnupdatetime(AppValue.appInfo.getUpdatetime());
		OperateController.getInstance(mContext).getTagIndex(tagInfo, "", "",
				null, apiType, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagArticleList) {
							TagArticleList articleList = (TagArticleList) entry;
							if (!articleList.getMap().isEmpty()) {
								doAfterFecthShiye(articleList, true);
								return;
							}
						}
						doAfterFecthShiye(null, false);
					}
				});
	}

	public abstract void doAfterFecthShiye(TagArticleList articleList,
			boolean success);
}
