package cn.com.modernmediausermodel.help;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.listener.BindFavToUserListener;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

public class BindFavToUserImplement implements BindFavToUserListener {
	private Context mContext;
	private FavDb favDb;

	public BindFavToUserImplement(Context context) {
		mContext = context;
		favDb = FavDb.getInstance(mContext);
	}

	@Override
	public void addFav(FavoriteItem item, String uid) {
		favDb.addFav(item, uid, false);
		checkUpdateUser(item, uid);
	}

	@Override
	public void deleteFav(FavoriteItem item, String uid) {
		favDb.deleteFav(item.getId());
		checkUpdateUser(item, uid);
	}

	private void checkUpdateUser(FavoriteItem fav, String uid) {
		if (!TextUtils.isEmpty(uid) && !uid.equals(ConstData.UN_UPLOAD_UID)) {
			// 已登录
			UserHelper.updateFav(mContext);
		} else {
			// 未登录不做处理
		}
	}
}
