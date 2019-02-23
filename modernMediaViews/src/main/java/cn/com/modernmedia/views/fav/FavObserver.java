package cn.com.modernmedia.views.fav;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.FavObservable;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 收藏模块观察者
 * 
 * @author zhuqiao
 * 
 */
public class FavObserver implements Observer {
	private Context mContext;

	public FavObserver(Context context) {
		mContext = context;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof Integer) {
			int flag = (Integer) arg1;
			switch (flag) {
			case FavObservable.UPDATE:
				updateFav();
				break;
			case FavObservable.AFTER_LOGIN:
				List<ArticleItem> list = NewFavDb.getInstance(mContext)
						.getUserUnUpdateFav(Tools.getUid(mContext));
				if (ParseUtil.listNotNull(list)) {
					updateFav();
				} else {
					getFav(true);
				}
				break;
			case FavObservable.DATA_CHANGE: // 数据迁移时，调用get接口，从而使旧服务器的数据迁移到新服务器上
				getFav(false);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 同步fav
	 * 
	 * @param context
	 */
	private void updateFav() {
		User user = SlateDataHelper.getUserLoginInfo(mContext);
		if (user == null)
			return;
		List<ArticleItem> list = NewFavDb.getInstance(mContext)
				.getUserUnUpdateFav(user.getUid());
		OperateController.getInstance(mContext).updateFav(user.getUid(),
				ConstData.getInitialAppId(), list, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						NewFavDb.getInstance(mContext).fetchDataFromHttp(entry,
								Tools.getUid(mContext));
					}
				});
	}

	/**
	 * 获取收藏
	 * 
	 * @param context
	 * @param isSaveData
	 *            是否存取得的数据
	 */
	private void getFav(final boolean isSaveData) {
		User user = SlateDataHelper.getUserLoginInfo(mContext);
		if (user != null) {
			OperateController.getInstance(mContext).getFav(user.getUid(),
					new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							if (isSaveData)
								NewFavDb.getInstance(mContext)
										.fetchDataFromHttp(entry,
												Tools.getUid(mContext));
						}
					});
		}
	}
}
