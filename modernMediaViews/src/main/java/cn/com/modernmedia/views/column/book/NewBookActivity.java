package cn.com.modernmedia.views.column.book;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.EnsubscriptHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.views.R;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 订阅页面
 * 
 * @author lusiyuan
 *
 */
public class NewBookActivity extends BaseActivity implements OnClickListener {
	private List<TagInfo> allTags = new ArrayList<TagInfo>();
	private List<TagInfo> myTags = new ArrayList<TagInfo>();
	private ScrollView scroll;
	private DragGridView bookedGridView, bookingGridView;
	private GridViewAdapter allAdapter, myAdapter;
	private TextView order;// 删除排序
	
	public boolean ischeck = false;// 删除状态

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book);
		LogHelper.openSubcribeColumn(this);// 提交log-->flurry
		initView();
	}

	@Override
	protected void onResume() {
		initData();
		super.onResume();
	}

	public void initData() {
		if (SlateApplication.loginStatusChange) {// 登陆状态改变
			showLoadingDialog(true);
			OperateController.getInstance(this).getSubscribeOrderList(
					Tools.getUid(this), SlateDataHelper.getToken(this),
					FetchApiType.USE_HTTP_FIRST, new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							showLoadingDialog(false);
							EnsubscriptHelper
									.addEnsubscriptColumn(NewBookActivity.this);
							initTags();
						}
					});
		} else
			initTags();
	}

	private void initTags() {
		allTags.clear();
		myTags.clear();
		for (int i = 0; i < AppValue.ensubscriptColumnList.getList().size(); i++) {
			allTags.add(AppValue.ensubscriptColumnList.getList().get(i));
		}
		for (int i = 0; i < AppValue.bookColumnList.getList().size(); i++) {
			myTags.add(AppValue.bookColumnList.getList().get(i));
		}
		allTags.removeAll(myTags);
		// 更新ui数据
		// handler.sendEmptyMessage(0);
	}

	private void initView() {
		findViewById(R.id.book_back).setOnClickListener(this);
		order = (TextView) findViewById(R.id.orderordelete);
		order.setOnClickListener(this);

		scroll = (ScrollView) findViewById(R.id.book_scrollview);
		bookedGridView = (DragGridView) findViewById(R.id.booked_gridview);
		bookingGridView = (DragGridView) findViewById(R.id.booking_gridview);

		myAdapter = new GridViewAdapter(this, myTags, GridViewAdapter.MY);
		bookedGridView.setAdapter(myAdapter);
		allAdapter = new GridViewAdapter(this, allTags, GridViewAdapter.ALL);
		bookingGridView.setAdapter(allAdapter);

		bookingGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (ischeck) {
					TagInfo t = allTags.get(position);
					t.setCheck(true);
					myTags.add(t);
					allTags.remove(position);
					myAdapter.notifyDataSetChanged();
					allAdapter.notifyDataSetChanged();
				} else {// 非编辑状态，点击跳转栏目详情页面
					Intent i = new Intent(NewBookActivity.this,
							BookColumnActivity.class);
					i.putExtra("book_deatail", allTags.get(position));
					startActivity(i);
				}

			}
		});
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void reLoadData() {

	}

	@Override
	public String getActivityName() {
		return null;
	}

	@Override
	public Activity getActivity() {
		return null;
	}

}
