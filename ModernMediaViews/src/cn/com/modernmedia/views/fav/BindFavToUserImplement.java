package cn.com.modernmedia.views.fav;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.listener.BindFavToUserListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.unit.FavObservable;

public class BindFavToUserImplement implements BindFavToUserListener {
	private Context mContext;
	private NewFavDb favDb;

	public BindFavToUserImplement(Context context) {
		mContext = context;
		favDb = NewFavDb.getInstance(mContext);
	}

	@Override
	public void addFav(ArticleItem item, String uid) {
		favDb.addFav(item, uid, false);
		checkUpdateUser(item, uid);
	}

	@Override
	public void deleteFav(ArticleItem item, String uid) {
		favDb.deleteFav(item.getArticleId(), uid);
		checkUpdateUser(item, uid);
	}

	private void checkUpdateUser(ArticleItem fav, String uid) {
		if (!TextUtils.isEmpty(uid)
				&& !uid.equals(SlateApplication.UN_UPLOAD_UID)) {
			// 已登录
			SlateApplication.favObservable.setData(FavObservable.UPDATE);
		} else {
			// 未登录不做处理
		}
	}
}
