package cn.com.modernmedia.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.R;
import cn.com.modernmediaslate.model.Entry;

public class PageTransfer {
	public static final int REQUEST_CODE = 100;// 从文章页返回，刷新，设置已阅读文章

	/**
	 * 往期进入文章页
	 * 
	 * @param artcleId
	 * @param catId
	 * @param articleType
	 * @param issue
	 */
	public static void gotoArticleActivity(Context context, Class<?> cls,
			TransferArticle transferArticle) {
		initGotoArticleActivity(context, cls, transferArticle);
	}

	/**
	 * 跳转至文章页
	 * 
	 * @param artcleId
	 * @param isIndex
	 *            true:首页；false:收藏
	 */
	private static void initGotoArticleActivity(Context context, Class<?> cls,
			TransferArticle transferArticle) {
		Intent intent = new Intent(context, cls);
		if (TextUtils.isEmpty(transferArticle.getUid())) {
			transferArticle.setUid(ConstData.UN_UPLOAD_UID);
		}
		intent.putExtra(GenericConstant.TRANSFE_RARTICLE, transferArticle);
		((Activity) context).startActivityForResult(intent, REQUEST_CODE);
		((Activity) context).overridePendingTransition(R.anim.right_in,
				R.anim.zoom_out);
	}

	public static class TransferArticle extends Entry {
		private static final long serialVersionUID = 1L;
		private int artcleId;
		private int catId;
		private int advId;
		private ArticleType articleType;
		private String uid = "";
		private String floderName = "";

		public TransferArticle() {
		}

		public TransferArticle(ArticleType articleType, String uid) {
			this.articleType = articleType;
			this.uid = uid;
		}

		public TransferArticle(int artcleId, int catId, int advId,
				ArticleType articleType) {
			this.artcleId = artcleId;
			this.catId = catId;
			this.articleType = articleType;
			this.advId = advId;
		}

		public TransferArticle(int artcleId, int catId,
				ArticleType articleType, String uid, String floderName) {
			this.artcleId = artcleId;
			this.catId = catId;
			this.articleType = articleType;
			this.uid = uid;
			this.floderName = floderName;
		}

		public int getArtcleId() {
			return artcleId;
		}

		public void setArtcleId(int artcleId) {
			this.artcleId = artcleId;
		}

		public int getCatId() {
			return catId;
		}

		public void setCatId(int catId) {
			this.catId = catId;
		}

		public ArticleType getArticleType() {
			return articleType;
		}

		public void setArticleType(ArticleType articleType) {
			this.articleType = articleType;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getFloderName() {
			return floderName;
		}

		public void setFloderName(String floderName) {
			this.floderName = floderName;
		}

		public int getAdvId() {
			return advId;
		}

		public void setAdvId(int advId) {
			this.advId = advId;
		}

	}
}
