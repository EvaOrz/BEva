package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmedia.widget.CheckFooterListView;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.db.CardListByArtilceIdDb;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.widget.BaseCardListView;

/**
 * 来自同一篇文章的卡片列表显示类
 * 
 * @author jiancong
 * 
 */
public class ArticleCardListActivity extends SlateBaseActivity implements
		OnClickListener {
	public final static String KEY_ARTICLE_ID = "articleId";
	// public final static String KEY_ISSUE_ID = "issueId";
	private CheckFooterListView listView;
	private UserOperateController controller;
	private BaseCardListView cardListHelp;
	private String articleId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_cardlist);
		initDataFromBundle();
		init();
	}

	private void initDataFromBundle() {
		if (getIntent() != null && getIntent().getExtras() != null) {
			articleId = getIntent().getExtras().getString(KEY_ARTICLE_ID);
		}
		if (TextUtils.isEmpty(articleId))
			return;
	}

	private void init() {
		listView = (CheckFooterListView) findViewById(R.id.articlecard_list_view);
		findViewById(R.id.articlecard_back).setOnClickListener(this);
		controller = UserOperateController.getInstance(this);

		cardListHelp = new BaseCardListView(this, listView) {

			@Override
			public void setTipVisibility(boolean show) {
				int visible = show ? View.VISIBLE : View.GONE;
				findViewById(R.id.articlecard_no_tip).setVisibility(visible);
			}

			@Override
			public Card getCardFromDb(String timeLine) {
				return CardListByArtilceIdDb.getInstance(
						ArticleCardListActivity.this).getCard(articleId);
			}

			@Override
			public void getCardList(String timelineId, boolean isGetMore,
					boolean isPull) {
				super.getCardList(timelineId, isGetMore, isPull);
				myGetCardList(isPull);
			}

		};
		cardListHelp.initListView(false);
		cardListHelp.getCardList("0", false, false);
		listView.removeFooter();
	}

	private void myGetCardList(final boolean isPull) {
		controller.getCardListByArticleId(articleId, Tools.getUid(this),
				new UserFetchEntryListener() {
					@Override
					public void setData(Entry entry) {
						cardListHelp.afterGetCardList(entry,"", false, isPull);
					}
				});
	}

	@Override
	public String getActivityName() {
		return ArticleCardListActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.articlecard_back) {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CardDetailActivity.REQUEST_CODE_SELF) {
			// TODO 从写笔记页或者笔记详情页返回
			cardListHelp.getCardList("0", false, false);
		}
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
