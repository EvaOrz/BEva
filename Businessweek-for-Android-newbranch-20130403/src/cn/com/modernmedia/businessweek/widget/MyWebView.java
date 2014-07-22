package cn.com.modernmedia.businessweek.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import cn.com.modernmedia.businessweek.ArticleActivity;
import cn.com.modernmedia.widget.CommonWebView;

/**
 * Œƒ’¬œÍ«Èwebview
 * 
 * @author ZhuQiao
 * 
 */
public class MyWebView extends CommonWebView {
	private Context mContext;

	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	public void gotoArticle(int articleId) {
		if (mContext instanceof ArticleActivity && articleId != -1) {
			((ArticleActivity) mContext).moveToArticle(articleId);
		}
	}

	@Override
	public void showGallery(List<String> urlList) {
	}
}
