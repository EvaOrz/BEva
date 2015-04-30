package cn.com.modernmedia.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.api.GetTagArticlesOperate;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.common.ShareHelper;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagArticleListDb;
import cn.com.modernmedia.newtag.db.TagIndexDb;
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.views.article.BaseAtlasView;
import cn.com.modernmedia.views.fav.BindFavToUserImplement;
import cn.com.modernmedia.views.model.TemplateAriticle;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.views.xmlparse.article.XMLDataSetForArticle;
import cn.com.modernmedia.widget.ArticleDetailItem;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmedia.widget.CommonAtlasView;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediausermodel.LoginActivity;
import cn.com.modernmediausermodel.util.UserPageTransfer;

/**
 * 文章页
 * 
 * @author JianCong
 * 
 */
public class ArticleActivity extends CommonArticleActivity {
	private TemplateAriticle template;
	private XMLDataSetForArticle dataSet;

	public static boolean fix_buf_article;// iweekly推送会出错？

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(-1);
		if (fix_buf_article) {
			fix_buf_article = false;
			finish();
		}
	}

	@Override
	protected void init() {
		super.init();
		initRes();
		if (SlateApplication.mConfig.getIs_navbar_bg_change() == 0
				|| transferArticle == null || dataSet.getNavBar() == null)
			return;
		if (transferArticle.getArticleType() == ArticleType.Default) {
			if (transferArticle.getTagName() != null
					&& ParseUtil.mapContainsKey(DataHelper.columnColorMap,
							transferArticle.getTagName())) {
				dataSet.getNavBar().setBackgroundColor(
						DataHelper.columnColorMap.get(transferArticle
								.getTagName()));
			}
		} else if (transferArticle.getArticleType() == ArticleType.Fav) {
			dataSet.getNavBar().setBackgroundColor(Color.RED);
		}
	}

	/**
	 * 初始化资源
	 */
	private void initRes() {
		template = ParseProperties.getInstance(this).parseArticle();
		RelativeLayout navBar = (RelativeLayout) findViewById(R.id.default_article_toolbar);
		XMLParse parse = new XMLParse(this, null);
		View view = parse.inflate(template.getNavBar().getData(), null, "");
		navBar.addView(view);
		dataSet = parse.getDataSetForArticle();
		dataSet.setData();
		if (template.getHas_user() == 1) {
			setBindFavToUserListener(new BindFavToUserImplement(this));
		}
		// 导航栏的顶部和文章页的顶部对齐
		if (template.getIsAlignToNav() == 0) {
			addRule();
		}
	}

	@Override
	protected void checkShouldInsertSubscribe(TagArticleList dArticleList) {
		TagInfo tagInfo = TagInfoListDb.getInstance(this).getTagInfoByName(
				transferArticle.getTagName(), "", true);
		if (V.checkShouldInsertSubscribeArticle(this,
				transferArticle.getTagName())) {
			// TODO 如果是iweekly新闻栏目，那么获取所有订阅栏目的前5篇文章
			Entry subscribeTopEntry = TagArticleListDb.getInstance(this)
					.getEntry(
							new GetTagArticlesOperate(tagInfo, "", "5", null),
							"", "", false, TagIndexDb.SUBSCRIBE_TOP_ARTICLE);
			if (subscribeTopEntry instanceof TagArticleList
					&& ParseUtil
							.listNotNull(((TagArticleList) subscribeTopEntry)
									.getArticleList())) {
				reBuildArticleList(dArticleList,
						(TagArticleList) subscribeTopEntry);
			} else {
				getSubscribeTopArticle(dArticleList, tagInfo);
			}
		} else {
			reBuildArticleList(dArticleList, null);
		}
	}

	/**
	 * 获取所有订阅栏目前5篇文章
	 * 
	 * @param articleList
	 */
	private void getSubscribeTopArticle(final TagArticleList dArticleList,
			TagInfo tagInfo) {
		String mergeNames = AppValue.ensubscriptColumnList
				.getSubscriptTagMergeName();
		if (TextUtils.isEmpty(mergeNames)) {
			reBuildArticleList(dArticleList, null);
			return;
		} else {
			showLoading();
			OperateController.getInstance(this).getTagArticles(tagInfo, "",
					"5", null, new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							if (entry instanceof TagArticleList) {
								disProcess();
								reBuildArticleList(dArticleList,
										(TagArticleList) entry);
							} else {
								showError();
							}
						}
					});
		}
	}

	/**
	 * 重新组装文章列表
	 * 
	 * @param dArticleList
	 *            初始的列表
	 * @param sArticleList
	 *            订阅top5列表
	 */
	private void reBuildArticleList(TagArticleList dArticleList,
			TagArticleList sArticleList) {
		dArticleList.insertSubscribeArticle(this, sArticleList, false);
		list = dArticleList.getArticleList();
		if (ParseUtil.listNotNull(list)) {
			getPosition();
		}
	}

	@Override
	protected void changeFavBtn(boolean isFaved) {
		dataSet.changeFavBtn(isFaved);
	}

	@Override
	protected void hideIfAdv(boolean hide) {
		dataSet.hideIfAdv(hide);
	}

	@Override
	protected void hideFont(boolean hide) {
		dataSet.hideFont(hide);
	}

	/**
	 * 点击分享按钮
	 */
	public void showShare() {
		int position = viewPager.getCurrentItem();
		ArticleItem item = getArticleByPosition(position);
		int index = -1;
		if (currView instanceof CommonAtlasView) {
			index = ((CommonAtlasView) currView).getCurrentIndex();
		}
		if (item != null) {
			if (ConstData.getAppId() == 20) {
				ShareHelper.shareByWeekly(this, getCurrentArticleDetail(),
						V.getId("share_bottom"));
			} else {
				ShareHelper.shareByDefault(this, item.convertToShare(index));
			}
		}
	}

	@Override
	protected View fetchView(final ArticleItem detail) {
		View view = null;
		int type = detail.getProperty().getType();
		if (type == 2) {// 图集
			BaseAtlasView atlasView = new BaseAtlasView(this,
					detail.getAppid() == ConstData.getInitialAppId());
			atlasView.setData(detail);
			view = atlasView;
		} else {
			boolean bgIsTransparent = (template.getBgIsTransparent() == 1);
			view = new ArticleDetailItem(this, bgIsTransparent) {

				@Override
				public void setBackGroundRes(ImageView imageView) {
					V.setImage(imageView, "head_placeholder");
				}

				@Override
				public void doScroll(int l, int t, int oldl, int oldt) {
				}

				@Override
				public void myGotoWriteNewCardActivity(ArticleItem item) {
					if (template.getHas_user() == 1) {
						checkLogin(item, LoginActivity.class);
					}
				}

				@Override
				public void showGallery(List<String> urlList, String currentUrl) {
					if (ParseUtil.listNotNull(urlList)
							&& ViewsApplication.articleGalleryCls != null) {
						Intent intent = new Intent(ArticleActivity.this,
								ViewsApplication.articleGalleryCls);
						Bundle bundle = new Bundle();
						bundle.putStringArrayList("URL_LIST",
								(ArrayList<String>) urlList);
						bundle.putString(
								"TITLE",
								detail.getTitle() == null ? "" : detail
										.getTitle());
						bundle.putString("DESC", detail.getDesc() == null ? ""
								: detail.getDesc());
						int index = urlList.indexOf(currentUrl);
						if (index < 0 || index >= urlList.size())
							index = 0;
						bundle.putInt("INDEX", index);
						intent.putExtras(bundle);
						startActivity(intent);
					}
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
	protected void changedNavBar(int pos) {
	}

	@Override
	public String getActivityName() {
		return ArticleActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == UserPageTransfer.TO_LOGIN_BY_ARTICLE_CARD) {
				UserPageTransfer.gotoArticleCardActivity(this,
						getCurrArticleId() + "");
			}
		}
	}
}
