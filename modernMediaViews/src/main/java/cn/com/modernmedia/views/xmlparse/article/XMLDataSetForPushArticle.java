package cn.com.modernmedia.views.xmlparse.article;

import android.content.Context;
import android.view.View;

import java.util.HashMap;
import java.util.List;

import cn.com.modernmedia.views.PushArticleActivity;

/**
 * iweekly push文章设置数据
 *
 * @author jiancong
 */
public class XMLDataSetForPushArticle extends XMLDataSetForArticle {

    public XMLDataSetForPushArticle(Context context, HashMap<String, View> map, List<View> clickViewList, List<View> ninePatchViewList) {
        super(context, map, clickViewList, ninePatchViewList);
    }

    @Override
    public void setData() {
        super.setData();
    }

    @Override
    protected void fav() {
        ((PushArticleActivity) mContext).addFav();
    }

    @Override
    protected void back() {
        ((PushArticleActivity) mContext).back();
    }

    @Override
    protected void share() {
        ((PushArticleActivity) mContext).showShare();
    }

}
