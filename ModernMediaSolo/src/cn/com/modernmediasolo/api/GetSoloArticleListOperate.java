package cn.com.modernmediasolo.api;

import java.util.List;

import android.content.Context;
import cn.com.modernmedia.api.GetArticleListOperate;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediasolo.db.SoloArticleListDb;

/**
 * 独立栏目文章列表
 * 
 * @author ZhuQiao
 * 
 */
public class GetSoloArticleListOperate extends GetArticleListOperate {
	private SoloArticleListDb db;
	private boolean mFromNet;

	public GetSoloArticleListOperate(Context context, String catId,
			String fromOffset, String toOffset, boolean fromNet,
			boolean fetchNew) {
		super(context, catId, fromOffset, toOffset, fetchNew);
		db = SoloArticleListDb.getInstance(context);
		// 是否是网络请求(false：读取数据库数据，不执行addToDb操作)
		mFromNet = true;
	}

	@Override
	public synchronized void addSoloArticleListDb(int catId,
			List<FavoriteItem> list) {
		if (mFromNet)
			db.addSoloArticle(catId, list);
	}
}
