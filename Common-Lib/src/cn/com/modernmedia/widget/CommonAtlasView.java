package cn.com.modernmedia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.LogHelper;

/**
 * 图集
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

	public void setData(ArticleItem item) {
		if (item == null)
			return;
		addLoadok(item);
		setValuesForWidget(item);
		disProcess();
	}

	protected void setValuesForWidget(ArticleItem item) {
	}

	public abstract int getCurrentIndex();

	@Override
	protected void reLoad() {
	}

	public abstract AtlasViewPager getAtlasViewPager();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return getAtlasViewPager().onTouchEvent(event);
	}

	protected void addLoadok(ArticleItem item) {
		if (mContext instanceof CommonArticleActivity) {
			int id = item.getArticleId();
			((CommonArticleActivity) mContext).addLoadOkIds(id);
			if (((CommonArticleActivity) mContext).getCurrArticleId() == id) {
				ReadDb.getInstance(mContext).addReadArticle(id);
				LogHelper.logAndroidShowArticle(mContext, item.getTagName(), id
						+ "");
				AdvTools.requestImpression(item);
			}
		}
	}

	public void gotoArticle(int articleId) {
		if (mContext instanceof CommonArticleActivity && articleId != -1) {
			((CommonArticleActivity) mContext).moveToArticle(articleId);
		}
	}

}
