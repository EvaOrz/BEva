package cn.com.modernmedia.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.R;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;

public class PageTransfer {
	public static final int REQUEST_CODE = 100;// 从文章页返回，刷新，设置已阅读文章

	/**
	 * 进入文章页
	 * 
	 * @param context
	 * @param transferArticle
	 */
	public static void gotoArticleActivity(Context context,
			TransferArticle transferArticle) {
		if (SlateApplication.articleCls != null)
			initGotoArticleActivity(context, transferArticle);
	}

	/**
	 * 跳转至文章页
	 */
	private static void initGotoArticleActivity(Context context,
			TransferArticle transferArticle) {
		Intent intent = new Intent(context, SlateApplication.articleCls);
		if (TextUtils.isEmpty(transferArticle.getUid())) {
			transferArticle.setUid(SlateApplication.UN_UPLOAD_UID);
		}
		intent.putExtra(GenericConstant.TRANSFE_RARTICLE, transferArticle);
		((Activity) context).startActivityForResult(intent, REQUEST_CODE);
		((Activity) context).overridePendingTransition(R.anim.right_in,
				R.anim.zoom_out);
	}

	public static class TransferArticle extends Entry {
		private static final long serialVersionUID = 1L;
		private int artcleId;
		private String tagName;
		private String parent;
		private int advId;
		private ArticleType articleType;
		private String uid = "";
		private String floderName = "";
		private String publishTime = ""; // 期发布时间

		public TransferArticle() {
		}

		public TransferArticle(ArticleType articleType, String uid) {
			this.articleType = articleType;
			this.uid = uid;
		}

		public TransferArticle(int artcleId, String tagName, String parent,
				int advId, ArticleType articleType) {
			this.artcleId = artcleId;
			this.tagName = tagName;
			this.parent = parent;
			this.articleType = articleType;
			this.advId = advId;
		}

		public TransferArticle(int artcleId, String tagName, String parent,
				ArticleType articleType, String uid, String publishTime,
				String floderName) {
			this.artcleId = artcleId;
			this.tagName = tagName;
			this.parent = parent;
			this.articleType = articleType;
			this.uid = uid;
			this.publishTime = publishTime;
			this.floderName = floderName;
		}

		public TransferArticle(int artcleId, String tagName, String parent,
				ArticleType articleType, String uid, String floderName) {
			this.artcleId = artcleId;
			this.tagName = tagName;
			this.parent = parent;
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

		public String getTagName() {
			return tagName;
		}

		public void setTagName(String tagName) {
			this.tagName = tagName;
		}

		public ArticleType getArticleType() {
			return articleType;
		}

		public void setArticleType(ArticleType articleType) {
			this.articleType = articleType;
		}

		public String getUid() {
			return TextUtils.isEmpty(uid) ? SlateApplication.UN_UPLOAD_UID
					: uid;
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

		public String getParent() {
			return parent;
		}

		public void setParent(String parent) {
			this.parent = parent;
		}

		public String getPublishTime() {
			return publishTime;
		}

		public void setPublishTime(String publishTime) {
			this.publishTime = publishTime;
		}

	}
}
