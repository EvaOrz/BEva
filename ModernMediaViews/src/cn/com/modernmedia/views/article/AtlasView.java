package cn.com.modernmedia.views.article;

import android.content.Context;
import android.view.View;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.views.model.ArticleParm;
import cn.com.modernmedia.views.model.AtlasParm;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

/**
 * 图集view
 * 
 * @author user
 * 
 */
public class AtlasView {
	private Context mContext;
	private BaseAtlasView atlasView;
	private ArticleParm parm;
	private ArticleType articleType;

	public AtlasView(Context context, ArticleParm parm, ArticleType articleType) {
		mContext = context;
		this.parm = parm;
		this.articleType = articleType;
		AtlasParm atlasParm = ParseProperties.getInstance(context).parseAtlas();
		if (V.ILADY.equals(atlasParm.getType())) {
			atlasView = new LadyAtlasView(context);
		} else {
			atlasView = new BusAtlasView(context);
		}
	}

	public void setData(FavoriteItem detail) {
		if (parm != null && atlasView != null) {
			atlasView.setData(detail, CommonApplication.issue,
					articleType == ArticleType.Solo);
		}
	}

	public View fetchView() {
		if (atlasView == null)
			return new View(mContext);
		return atlasView;
	}

}
