package cn.com.modernmedia;

import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleList;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.solo.db.SoloArticleListDb;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

/**
 * 独立栏目文章页
 * 
 * @author user
 * 
 */
public abstract class CommonSoloArticleActivity extends CommonArticleActivity {
	/**
	 * 获取独立栏目文章列表(由于获取栏目列表的时候已经获取完了文章，所以文章直接从缓存中取)
	 */
	@Override
	protected void getSoloArticleList(int catId, int articleId) {
		showLoading();
		SoloArticleListDb db = SoloArticleListDb.getInstance(this);
		if (!db.containThisItem(articleId)) {
			getSoloAList(catId);
			return;
		}
		ArticleList articleList = db.getArticleListByOffset(catId, "0", "0",
				true, false);
		if (articleList instanceof ArticleList) {
			list = articleList.getAllArticleList();
			if (ParseUtil.listNotNull(list)) {
				getPosition(list, true);
				disProcess();
			} else {
				// TODO 可能是从首页slate进入
				getSoloAList(catId);
			}
		} else {
			showError();
		}
	}

	@Override
	protected void getSoloArticleById(Issue issue, String catId,
			String articleId) {
		FavoriteItem detail = new FavoriteItem();
		detail.setCatid(ParseUtil.stoi(catId));
		detail.setId(ParseUtil.stoi(articleId));
		OperateController.getInstance(this).getSoloArticleById(detail,
				new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						afterGetArticleById(entry);
					}
				});
	}

	/**
	 * 从服务器获取独立栏目文章列表
	 * 
	 * @param catId
	 */
	private void getSoloAList(int catId) {
		OperateController.getInstance(this).getSoloArticleList(catId + "", "0",
				"0", true, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof ArticleList) {
							list = ((ArticleList) entry).getAllArticleList();
							if (ParseUtil.listNotNull(list)) {
								getPosition(list, true);
								disProcess();
							} else {
								showError();
							}
						}
					}
				});
	}
}
