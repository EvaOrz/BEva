package cn.com.modernmedia.views;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import cn.com.modernmedia.CommonSoloArticleActivity;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.article.AtlasView;
import cn.com.modernmedia.views.article.BaseAtlasView;
import cn.com.modernmedia.views.model.ArticleParm;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.ArticleDetailItem;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediausermodel.DefaultLoginActivity;
import cn.com.modernmediausermodel.help.BindFavToUserImplement;

/**
 * 文章页
 * 
 * @author JianCong
 * 
 */
public class ArticleActivity extends CommonSoloArticleActivity {
	private ArticleParm articleParm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(-1);
	}

	@Override
	protected void init() {
		super.init();
		initRes();
	}

	/**
	 * 初始化资源
	 */
	private void initRes() {
		articleParm = ParseProperties.getInstance(this).parseArticle();
		View navBar = findViewById(R.id.default_article_toolbar);
		V.setImage(navBar, articleParm.getNav_bg());
		V.setImage(backBtn, articleParm.getNav_back());
		if (TextUtils.isEmpty(articleParm.getNav_font_size())) {
			fontBtn.setVisibility(View.GONE);
		} else {
			V.setImage(fontBtn, articleParm.getNav_font_size());
		}
		V.setImage(favBtn, articleParm.getNav_fav());
		V.setImage(shareBtn, articleParm.getNav_share());

		if (articleParm.getHas_user() == 1) {
			setBindFavToUserListener(new BindFavToUserImplement(this));
		}
		// 导航栏的顶部和文章页的顶部对齐
		if (articleParm.getIsAlignToNav() == 0) {
			addRule();
		}
	}

	@Override
	protected void changeFavBtn(boolean isFaved) {
		if (isFaved)
			V.setImage(favBtn, articleParm.getNav_faved());
		else
			V.setImage(favBtn, articleParm.getNav_fav());
	}

	@Override
	protected void hideIfAdv(boolean hide) {
		if (hide) {
			favBtn.setVisibility(View.GONE);
			shareBtn.setVisibility(View.GONE);
			fontBtn.setVisibility(View.GONE);
		} else {
			favBtn.setVisibility(View.VISIBLE);
			shareBtn.setVisibility(View.VISIBLE);
			// 有字体图片时，即表示字体大小可控， 按钮可见
			if (!TextUtils.isEmpty(articleParm.getNav_font_size())) {
				fontBtn.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void hideFont(boolean hide) {
		if (!TextUtils.isEmpty(articleParm.getNav_font_size())) {
			if (hide) {
				fontBtn.setVisibility(View.GONE);
			} else {
				fontBtn.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected View fetchView(FavoriteItem detail) {
		View view = null;
		int type = detail.getProperty().getType();
		if (type == 2) {// 图集
			AtlasView atlasView = new AtlasView(this, articleParm,
					mBundle.getArticleType());
			view = atlasView.fetchView();
			atlasView.setData(detail);
		} else {
			boolean bgIsTransparent = (articleParm.getBgIsTransparent() == 1);
			view = new ArticleDetailItem(this, bgIsTransparent) {

				@Override
				public void setBackGroundRes(ImageView imageView) {
					V.setImage(imageView, articleParm.getPlaceholder());
				}

				@Override
				public void myGotoWriteNewCardActivity(ArticleItem item) {
					if (articleParm.getHas_user() == 1) {
						checkLogin(item, DefaultLoginActivity.class);
					}
				}

				@Override
				public void showGallery(List<String> urlList, String currentUrl) {
				}

			};
			((ArticleDetailItem) view).setData(detail);
			((ArticleDetailItem) view).changeFont();
		}
		return view;
	}

	@Override
	protected AtlasViewPager getAtlasViewPager(Object object) {
		if (object instanceof BaseAtlasView) {
			return ((BaseAtlasView) object).getAtlasViewPager();
		}
		return null;
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
