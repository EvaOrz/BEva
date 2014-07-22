package cn.com.modernmedia.businessweek;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import cn.com.modernmedia.businessweek.widget.AtlasView;
import cn.com.modernmedia.widget.ArticleDetailItem;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediasolo.CommonSoloArticleActivity;
import cn.com.modernmediausermodel.help.BindFavToUserImplement;

/**
 * 文章详情页
 * 
 * @author ZhuQiao
 * 
 */
public class ArticleActivity extends CommonSoloArticleActivity {
	/**
	 * 在ViewPager中，它除了加载当前页，还会加载当前页的左右页（无论它们实际可不可见）。除了当前页的View算是可见的，
	 * 其左右页的View算是可见的（无论它们实际可不可见）
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(-1);
	}

	@Override
	protected void init() {
		super.init();
		findViewById(R.id.default_article_toolbar).setBackgroundResource(
				R.drawable.navbar);
		backBtn.setBackgroundResource(R.drawable.nav_back);
		favBtn.setBackgroundResource(R.drawable.nav_fav);
		fontBtn.setBackgroundResource(R.drawable.nav_font_size);
		shareBtn.setBackgroundResource(R.drawable.nav_actionsheet);

		setBindFavToUserListener(new BindFavToUserImplement(this));
	}

	@Override
	protected void changeFavBtn(boolean isFavEd) {
		if (isFavEd)
			favBtn.setBackgroundResource(R.drawable.nav_faved);
		else
			favBtn.setBackgroundResource(R.drawable.nav_fav);
	}

	@Override
	protected void hideFont(boolean hide) {
		if (hide)
			fontBtn.setVisibility(View.GONE);
		else
			fontBtn.setVisibility(View.VISIBLE);
	}

	@Override
	protected View fetchView(FavoriteItem detail) {
		View view;
		int type = detail.getProperty().getType();
		if (type == 2) {// 图集
			view = new AtlasView(this);
			((AtlasView) view).setData(detail, getIssue(),
					mBundle.getArticleType() == ArticleType.Solo);
		} else {
			view = new ArticleDetailItem(this) {
				
				@Override
				public void showGallery(List<String> urlList) {
				}
				
				@Override
				public int getBackGroundRes() {
					return R.drawable.webview_bg;
				}
			};
			((ArticleDetailItem) view).setData(detail);
			((ArticleDetailItem) view).changeFont();
		}
		return view;
	}

	@Override
	protected AtlasViewPager getAtlasViewPager(Object object) {
		return ((AtlasView) object).getAtlasViewPager();
	}

	@Override
	public String getActivityName() {
		return ArticleActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
