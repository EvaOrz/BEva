package cn.com.modernmedia.views.xmlparse;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.xmlparse.article.FunctionArticle;

/**
 * 关于数据设置
 * 
 * @author jiancong
 * 
 */
public class XMLDataSetForAbout extends BaseXMLDataSet {

	public XMLDataSetForAbout(Context context, HashMap<String, View> map,
			List<View> clickViewList, List<View> ninePatchViewList) {
		super(context, map, clickViewList, ninePatchViewList);
	}

	public void setData() {
		registerClick(null, null);
	}

	@Override
	protected void onClick(View view, ArticleItem item, ArticleType articleType) {
		if (view.getTag(R.id.click) instanceof String) {
			if (TextUtils.equals(view.getTag(R.id.click).toString(),
					FunctionArticle.BACK)) {
				((Activity) mContext).finish();
			}
		}
	}

}
