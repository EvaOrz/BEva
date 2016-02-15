package cn.com.modernmedia.api;

import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;

/**
 * 获取栏目首页列表
 * 
 * @author zhuqiao
 * 
 */
public class GetTagIndexOperate extends GetTagArticlesOperate {

	public GetTagIndexOperate(TagInfo info, String top, String limited,
			TagArticleList _articleList) {
		super(info, top, limited, _articleList);
		isCatIndex = true;
		url = UrlMaker.getTagCatIndex(info, top, limited);
	}

	@Override
	protected String getUrl() {
		return url;
	}

}
