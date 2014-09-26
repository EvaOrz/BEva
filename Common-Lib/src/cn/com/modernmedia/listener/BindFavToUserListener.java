package cn.com.modernmedia.listener;

import cn.com.modernmedia.model.ArticleItem;


/**
 * 绑定到用户(只有继承了用户模块才会使用)
 * 
 * @author ZhuQiao
 * 
 */
public interface BindFavToUserListener {
	public void addFav(ArticleItem item, String uid);

	public void deleteFav(ArticleItem item, String uid);
}
