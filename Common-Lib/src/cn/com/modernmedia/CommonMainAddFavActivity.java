package cn.com.modernmedia;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.listener.BindFavToUserListener;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleList;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

/**
 * 需要收藏功能的首页
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonMainAddFavActivity extends CommonMainActivity {
	private List<FavoriteItem> list;
	private FavDb favDb;
	private Context mContext;
	private BindFavToUserListener bindFavToUserListener;
	private AddFavCallBack callBack;

	public interface AddFavCallBack {
		public void callBack(boolean success, ArticleItem item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		favDb = FavDb.getInstance(this);
	}

	/**
	 * 获取文章列表
	 */
	private void getArticleList(final ArticleItem item) {
		showLoadingDialog(true);
		OperateController.getInstance(this).getArticleList(getIssue(),
				new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						if (entry instanceof ArticleList) {
							list = ((ArticleList) entry).getAllArticleList();
							if (ParseUtil.listNotNull(list))
								addFav(item);
							DataHelper.setArticleUpdateTime(mContext,
									getIssue().getArticleUpdateTime(),
									getIssue().getId());
						}
						showLoadingDialog(false);
					}
				});
	}

	/**
	 * 添加收藏
	 * 
	 * @param item
	 */
	public void addFav(ArticleItem item) {
		int articleId = ModernMediaTools.fecthSlateArticleId(item);
		if (favDb.containThisFav(articleId, getUid())) {
			if (bindFavToUserListener != null)
				bindFavToUserListener
						.deleteFav(getFavoriteItem(item), getUid());
			else
				favDb.deleteFav(articleId);
			CommonApplication.notifyFav();
			// TODO 刷新adapter状态，用于改变fav_img状态
			notifyRead();
			if (callBack != null)
				callBack.callBack(true, item);
			return;
		}
		if (!ParseUtil.listNotNull(list)) {
			getArticleList(item);
			return;
		}
		int pos = -1;
		int length = list.size();
		for (int i = 0; i < length; i++) {
			if (list.get(i).getId() == articleId) {
				pos = i;
				break;
			}
		}

		FavoriteItem favoriteItem = pos == -1 ? getFavoriteItem(item) : list
				.get(pos);

		if (bindFavToUserListener != null)
			bindFavToUserListener.addFav(favoriteItem, getUid());
		else
			favDb.addFav(favoriteItem, getUid(), false);
		CommonApplication.notifyFav();
		notifyRead();
		if (callBack != null)
			callBack.callBack(true, item);
	}

	public void setBindFavToUserListener(
			BindFavToUserListener bindFavToUserListener) {
		this.bindFavToUserListener = bindFavToUserListener;
	}

	/**
	 * 把ArticleItem转成FavoriteItem
	 * 
	 * @param item
	 * @return
	 */
	private FavoriteItem getFavoriteItem(ArticleItem item) {
		return item.convertToFavoriteItem(getIssue().getId());
	}

	public void setCallBack(AddFavCallBack callBack) {
		this.callBack = callBack;
	}

}