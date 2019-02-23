package cn.com.modernmedia.api;

import android.content.Context;

import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;

/**
 * 获取栏目首页列表
 *
 * @author zhuqiao
 */
public class GetTagIndexOperate extends GetTagArticlesOperate {

    public GetTagIndexOperate(Context c, TagInfo info, String top, String limited, TagArticleList _articleList) {
        super(c, info, top, limited, _articleList);
        isCatIndex = true;
        url = UrlMaker.getTagCatIndex(c, info, top, limited);
        System.out.println();
    }

    @Override
    protected String getUrl() {
        return url;
    }

}
