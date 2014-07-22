package cn.com.modernmediasolo.api;

import cn.com.modernmedia.api.GetArticleOperate;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

/**
 * 独立栏目文章
 * 
 * @author ZhuQiao
 * 
 */
public class GetSoloArticleOperate extends GetArticleOperate {

	protected GetSoloArticleOperate(FavoriteItem detail) {
		super(detail);
	}

}
