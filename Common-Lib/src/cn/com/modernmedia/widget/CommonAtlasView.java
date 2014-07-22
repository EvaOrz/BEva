package cn.com.modernmedia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Atlas;
import cn.com.modernmedia.model.Atlas.AtlasPicture;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediaslate.model.Favorite.Thumb;

/**
 * 图集
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonAtlasView extends BaseView {
	private Context mContext;
	private FavoriteItem detail;
	private Issue issue;
	private boolean mIsSolo;

	public CommonAtlasView(Context context) {
		super(context);
		mContext = context;
	}

	public CommonAtlasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public void setData(FavoriteItem detail, Issue issue, boolean isSolo) {
		if (detail == null || issue == null)
			return;
		mIsSolo = isSolo;
		this.detail = detail;
		this.issue = issue;
		addLoadok(detail);
		if (detail.isAdv()) {
			setValuesForWidget(convertFavoriteToAtlas(detail));
			disProcess();
		} else {
			if (!isSolo)
				getArticleById(detail);
			else
				getSoloArticleById(detail);
		}
	}

	/**
	 * 获取普通文章图集
	 * 
	 * @param item
	 */
	private void getArticleById(FavoriteItem item) {
		OperateController.getInstance(mContext).getArticleById(item,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						doAfterFetchArticle(entry);
					}
				});
	}

	/**
	 * 获取独立栏目文章图集
	 * 
	 * @param item
	 */
	protected void getSoloArticleById(FavoriteItem item) {
		OperateController.getInstance(mContext).getSoloArticleById(item,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						doAfterFetchArticle(entry);
					}
				});
	}

	/**
	 * 设置value
	 * 
	 * @param entry
	 */
	private void doAfterFetchArticle(Entry entry) {
		if (entry instanceof Atlas) {
			setValuesForWidget((Atlas) entry);
			disProcess();
		} else {
			showError();
		}
	}

	protected void setValuesForWidget(Atlas atlas) {
	}

	@Override
	protected void reLoad() {
		setData(detail, issue, mIsSolo);
	}

	public abstract AtlasViewPager getAtlasViewPager();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return getAtlasViewPager().onTouchEvent(event);
	}

	protected void addLoadok(FavoriteItem detail) {
		if (mContext instanceof CommonArticleActivity) {
			((CommonArticleActivity) mContext).addLoadOkUrl(detail.getLink());
			if (((CommonArticleActivity) mContext).getCurrentUrl().equals(
					detail.getLink())) {
				ReadDb.getInstance(mContext).addReadArticle(detail.getId());
				LogHelper.logAndroidShowArticle(mContext, detail.getCatid()
						+ "", detail.getId() + "");
				AdvTools.requestImpression(detail);
			}
		}
	}

	private Atlas convertFavoriteToAtlas(FavoriteItem item) {
		Atlas atlas = new Atlas();
		if (ParseUtil.listNotNull(item.getThumb())) {
			for (Thumb thumb : item.getThumb()) {
				AtlasPicture pic = new AtlasPicture();
				pic.setUrl(thumb.getUrl());
				pic.setTitle(pic.getTitle());
				pic.setDesc(thumb.getDesc());
				pic.setLink(thumb.getLink());
				pic.setWidth(thumb.getWidth());
				pic.setHeight(thumb.getHeight());
				atlas.getList().add(pic);
			}
		}
		return atlas;
	}

	public boolean ismIsSolo() {
		return mIsSolo;
	}

	public void gotoArticle(int articleId) {
		if (mContext instanceof CommonArticleActivity && articleId != -1) {
			((CommonArticleActivity) mContext).moveToArticle(articleId);
		}
	}

}
