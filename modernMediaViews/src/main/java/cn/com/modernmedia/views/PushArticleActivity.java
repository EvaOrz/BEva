package cn.com.modernmedia.views;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import cn.com.modernmedia.common.ShareHelper;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;
import cn.com.modernmedia.newtag.mainprocess.TagMainProcessParse;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.widget.ArticleDetailItem;
import cn.com.modernmediaslate.unit.TimeCollectUtil;

/**
 * 显示从parse来的文章
 * 
 * @author jiancong
 * 
 */
public class PushArticleActivity extends ArticleActivity {
	private FrameLayout contentView;

	private ArticleDetailItem articleDetailItem;
	private ArticleItem articleItem = new ArticleItem();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		transferArticle = null;// 阻止CommonArticleActivity往下执行
		initDataFromBundle();
		setContentView(R.layout.push_article_activity);

		TimeCollectUtil.getInstance().savePageTime(
				TimeCollectUtil.EVENT_OPEN_PUSH, true);
		init();
	}

	/**
	 * 初始化从上一个页面传递过来的数据
	 */
	private void initDataFromBundle() {
		if (getIntent() == null || getIntent().getExtras() == null) {
			return;
		}
		String url = getIntent().getExtras().getString(
				TagMainProcessParse.KEY_PUSH_ARTICLE_URL);
		if (TextUtils.isEmpty(url))
			return;

		// NOTE 组装articleItem
		// 文章url
		PhonePageList phonePageList = new PhonePageList();
		phonePageList.setUrl(url);
		articleItem.getPageUrlList().add(phonePageList);
		// 文章类型
		articleItem.getProperty().setType(1);
	}

	@Override
	protected void init() {
		contentView = (FrameLayout) findViewById(R.id.push_article_content);
		RelativeLayout navBar = (RelativeLayout) findViewById(R.id.push_article_toolbar);
		template = ParseProperties.getInstance(this).parseArticle();
		XMLParse parse = new XMLParse(this, null);
		View view = parse.inflate(template.getNavBar().getData(), null, "");
		navBar.addView(view);
		parse.getDataSetForPushArticle().setData();

		// 导航栏的顶部和文章页的顶部对齐
		if (template.getIsAlignToNav() == 0) {
			LayoutParams lp = (LayoutParams) contentView.getLayoutParams();
			lp.topMargin = getResources().getDimensionPixelSize(
					R.dimen.article_bar_height);
		}

		View detailView = fetchView(articleItem);
		if (detailView instanceof ArticleDetailItem) {
			articleDetailItem = (ArticleDetailItem) detailView;
			if (articleDetailItem.getWebView() != null)
				articleDetailItem.getWebView().setPushArticle(true);
			contentView.addView(articleDetailItem, new LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					(FrameLayout.LayoutParams.MATCH_PARENT)));
		}

		// TODO
		TimeCollectUtil.getInstance().savePageTime("OpenPush", false);
	}

	/**
	 * 点击分享按钮
	 */
	@Override
	public void showShare() {
		if (articleItem == null)
			return;
		if (ConstData.getAppId() == 20) {
			ShareHelper.shareByWeekly(this, articleItem,
					V.getId("share_bottom"));
		} else {
			ShareHelper.shareByDefault(this, articleItem);
		}
	}

	@Override
	public void reLoadData() {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		super.finish();
		TagProcessManage.getInstance(this).onPushArticleActivityFinished();
		if (articleDetailItem != null && articleDetailItem.getWebView() != null)
			articleDetailItem.getWebView().pop();
		overridePendingTransition(R.anim.hold, R.anim.down_out);
	}

	@Override
	public String getActivityName() {
		return PushArticleActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}
}
