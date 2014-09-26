package cn.com.modernmedia.views.xmlparse.fav;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.PageTransfer;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.adapter.FavAdapter;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.xmlparse.BaseXMLDataSet;
import cn.com.modernmedia.views.xmlparse.FunctionXML;
import cn.com.modernmedia.views.xmlparse.article.FunctionArticle;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 收藏设置数据
 * 
 * @author zhuqiao
 * 
 */
public class XMLDataSetForFav extends BaseXMLDataSet {
	private FavAdapter adapter;

	public XMLDataSetForFav(Context context, HashMap<String, View> map,
			List<View> clickViewList, List<View> ninePatchViewList,
			FavAdapter adapter) {
		super(context, map, clickViewList, ninePatchViewList);
		this.adapter = adapter;
	}

	public XMLDataSetForFav(Context context, HashMap<String, View> map,
			List<View> clickViewList) {
		super(context, map, clickViewList, null);
	}

	/**
	 * 导航栏设置数据
	 * 
	 */
	public void setDataForNavBar() {
		registerClick(null, null);
	}

	/**
	 * 收藏列表设置数据
	 * 
	 * @param articleItem
	 * @param position
	 */
	public void setData(ArticleItem articleItem, int position) {
		if (map == null || map.isEmpty() || articleItem == null
				|| adapter == null)
			return;
		title(articleItem, position);
		img(articleItem);
		desc(articleItem);
		video(articleItem);
		registerClick(articleItem, ArticleType.Fav);
	}

	/**
	 * 标题
	 * 
	 * @param articleItem
	 */
	private void title(ArticleItem articleItem, int position) {
		if (!map.containsKey(FunctionXML.TITLE))
			return;
		View view = map.get(FunctionXML.TITLE);
		if (!(view instanceof TextView))
			return;
		TextView title = (TextView) view;
		title.setText(articleItem.getTitle());
		boolean select = position == adapter.getSelectPosition();
		if (select) {
			if (title.getTag(R.id.select_color) instanceof String) {
				title.setTextColor(Color.parseColor(title.getTag(
						R.id.select_color).toString()));
			}
		} else {
			if (title.getTag(R.id.unselect_color) instanceof String) {
				title.setTextColor(Color.parseColor(title.getTag(
						R.id.unselect_color).toString()));
			}
		}
	}

	/**
	 * item图片
	 * 
	 * @param articleItem
	 */
	private void img(ArticleItem articleItem) {
		if (!map.containsKey(FunctionXML.IMAGE))
			return;
		View view = map.get(FunctionXML.IMAGE);
		if (!(view instanceof ImageView))
			return;
		ImageView image = (ImageView) view;
		if (ParseUtil.listNotNull(articleItem.getThumbList())) {
			V.setImage(image, articleItem.getThumbList().get(0).getUrl());
		}
	}

	/**
	 * 描述
	 */
	private void desc(ArticleItem item) {
		if (!map.containsKey(FunctionXML.DESC))
			return;
		View view = map.get(FunctionXML.DESC);
		if (!(view instanceof TextView))
			return;
		TextView desc = (TextView) view;
		desc.setText(item.getDesc());
	}

	@Override
	protected void onClick(View view, final ArticleItem item,
			final ArticleType articleType) {
		if (view.getTag(R.id.click) instanceof String) {
			if (TextUtils.equals(view.getTag(R.id.click).toString(),
					FunctionArticle.BACK)) {
				((Activity) mContext).finish();
				return;
			}
		}
		LogHelper.logOpenArticleFromFavoriteArticleList(mContext,
				item.getArticleId() + "", "");
		TransferArticle transferArticle = new TransferArticle(
				item.getArticleId(), "", "", ArticleType.Fav,
				UserTools.getUid(mContext), null);
		PageTransfer.gotoArticleActivity(mContext, transferArticle);
	}

}
