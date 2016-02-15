package cn.com.modernmedia.views.column.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.SubscribeOrderList.SubscribeColumn;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.UserSubscribeListDb;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.index.TagIndexListView;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.LoginActivity;

/**
 * 独立标签详情页面（带订阅）
 * 
 * @author lusiyuan
 *
 */
public class BookColumnActivity extends BaseActivity implements OnClickListener {
	private TextView title, book;
	private TagInfo tagInfo;
	private LinearLayout container;
	private TagIndexListView indexListView;
	// private TagArticleList datas;
	private OperateController operateController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_column_detail_for_book);
		operateController = OperateController.getInstance(this);
		tagInfo = (TagInfo) getIntent().getSerializableExtra("book_deatail");
		if (tagInfo != null)
			LogHelper.showSubcribeColumn(this, tagInfo.getTagName());// flurry
		initView();
		initData();
	}

	private void initData() {
		if (tagInfo != null) {
			title.setText(tagInfo.getColumnProperty().getCname());
			showLoadingDialog(true);
			operateController.getTagArticles(tagInfo, "", "", null,
					new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							showLoadingDialog(false);
							if (entry instanceof TagArticleList) {
								indexListView.setData((TagArticleList) entry,
										null);// 塞数据
								container.removeAllViews();
								container.addView(indexListView.fetchView());
							}

						}
					});
		}

	}

	private void initView() {
		book = (TextView) findViewById(R.id.detail_book);
		book.setOnClickListener(this);
		findViewById(R.id.detail_back).setOnClickListener(this);

		title = (TextView) findViewById(R.id.detail_title);
		indexListView = new TagIndexListView(this);
		container = (LinearLayout) findViewById(R.id.detail_list);
	}

	/**
	 * 单个订阅操作
	 */
	private void book() {
		LogHelper.subcribeColumn(this, tagInfo.getTagName());// flurry
		if (SlateDataHelper.getUserLoginInfo(this) != null) {
			SubscribeColumn cs = new SubscribeColumn(tagInfo.getTagName(), "",
					0);
			OperateController.getInstance(this).saveSubscribeColumnSingle(
					Tools.getUid(this), SlateDataHelper.getToken(this), cs,
					new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							// 更新数据库
							AppValue.bookColumnList.getList().add(tagInfo);
							UserSubscribeListDb.getInstance(
									BookColumnActivity.this).clearTable(
									Tools.getUid(BookColumnActivity.this));
							UserSubscribeListDb.getInstance(
									BookColumnActivity.this).addEntry(
									AppValue.bookColumnList);
							// 通知主页面更新topMenu
							SlateApplication.loginStatusChange = true;
							book.setText("已订阅");
						}
					});
		} else {
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
		}
	}

	@Override
	public void reLoadData() {

	}

	@Override
	public String getActivityName() {
		return BookColumnActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return BookColumnActivity.this;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.detail_book) {// 订阅
			book();
		} else if (v.getId() == R.id.detail_back) {
			finish();
		}
	}

}
