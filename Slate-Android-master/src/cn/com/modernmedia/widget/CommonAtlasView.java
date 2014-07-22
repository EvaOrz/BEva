package cn.com.modernmedia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.model.ArticleList.ArticleDetail;
import cn.com.modernmedia.util.LogHelper;

/**
 * Í¼¼¯
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonAtlasView extends BaseView {
	private Context mContext;

	public CommonAtlasView(Context context) {
		super(context);
		mContext = context;
	}

	public CommonAtlasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void reLoad() {

	}

	public abstract AtlasViewPager getAtlasViewPager();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return getAtlasViewPager().onTouchEvent(event);
	}

	protected void addLoadok(ArticleDetail detail) {
		if (mContext instanceof CommonArticleActivity) {
			((CommonArticleActivity) mContext).addLoadOkUrl(detail.getLink());
			if (((CommonArticleActivity) mContext).getCurrentUrl().equals(
					detail.getLink())) {
				ReadDb.getInstance(mContext).addReadArticle(
						detail.getArticleId());
				LogHelper.logAndroidShowArticle(mContext, detail.getCatId()
						+ "", detail.getArticleId() + "");
			}
		}
	}
}
