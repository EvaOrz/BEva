package cn.com.modernmediausermodel.help;

import java.util.List;

import android.content.Context;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediausermodel.api.UserModelInterface;
import cn.com.modernmediausermodel.listener.RequestListener;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;

public class UserHelper {
	public static void addFav() {

	}

	/**
	 * 同步fav
	 * 
	 * @param context
	 */
	public static void updateFav(final Context context) {
		User user = UserDataHelper.getUserLoginInfo(context);
		if (user != null) {
			List<FavoriteItem> list = FavDb.getInstance(context)
					.getUserUnUpdateFav(user.getUid(), false);
			UserModelInterface.getInstance(context).updateFav(user.getUid(),
					ConstData.getInitialAppId() + "", list,
					new RequestListener() {

						@Override
						public void onSuccess(Entry entry) {
							FavDb.getInstance(context).fetchDataFromHttp(entry);
						}

						@Override
						public void onFailed(Entry error) {
							CommonApplication.notifyFav();
						}
					});
		}
	}

	/**
	 * 获取收藏
	 * 
	 * @param context
	 */
	public static void getFav(final Context context) {
		User user = UserDataHelper.getUserLoginInfo(context);
		if (user != null) {
			UserModelInterface.getInstance(context).getFav(user.getUid(),
					new RequestListener() {

						@Override
						public void onSuccess(Entry entry) {
							FavDb.getInstance(context).fetchDataFromHttp(entry);
						}

						@Override
						public void onFailed(Entry error) {
							CommonApplication.notifyFav();
						}
					});
		}
	}
}
