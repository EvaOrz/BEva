package cn.com.modernmedia.listener;

import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

/**
 * 绑定到用户(只有继承了用户模块才会使用)
 * 
 * @author ZhuQiao
 * 
 */
public interface BindFavToUserListener {
	public void addFav(FavoriteItem item, String uid);

	public void deleteFav(FavoriteItem item, String uid);
}
