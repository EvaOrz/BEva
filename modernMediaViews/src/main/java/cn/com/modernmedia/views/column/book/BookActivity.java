package cn.com.modernmedia.views.column.book;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.SubscribeOrderList.SubscribeColumn;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.UserSubscribeListDb;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.EnsubscriptHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.views.R;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.LoginActivity;

/**
 * 栏目订阅页面
 * 
 * @author lusiyuan
 * 
 */
@SuppressLint("ResourceAsColor")
public class BookActivity extends BaseActivity implements OnClickListener {
	private List<TagInfo> allTags = new ArrayList<TagInfo>();
	private List<TagInfo> myTags = new ArrayList<TagInfo>();
	private DragGridView bookedGridView, bookingGridView;
	private GridViewAdapter allAdapter, myAdapter;
	private TextView order;// 删除排序
	private ImageView iweeklyTitle, back;// iweekly图片
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
									.addEnsubscriptColumn(BookActivity.this);
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
		handler.sendEmptyMessage(0);

	}

	private void initView() {
		order = (TextView) findViewById(R.id.orderordelete);
		order.setOnClickListener(this);
		back = (ImageView) findViewById(R.id.book_back);
		back.setOnClickListener(this);

		iweeklyTitle = (ImageView) findViewById(R.id.iweekly_book_title_img);
		if (ConstData.getAppId() == 20) {
			findViewById(R.id.select_column_title_frame).setBackgroundColor(
					getResources().getColor(R.color.column_head));
			back.setImageResource(R.drawable.book_back_weekly);
			findViewById(R.id.book_weekly_nav_bar).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.book_nav_bar).setVisibility(View.VISIBLE);
		}
		bookedGridView = (DragGridView) findViewById(R.id.booked_gridview);
		bookingGridView = (DragGridView) findViewById(R.id.booking_gridview);

		myAdapter = new GridViewAdapter(this, myTags, GridViewAdapter.MY);
		bookedGridView.setAdapter(myAdapter);
		allAdapter = new GridViewAdapter(this, allTags, GridViewAdapter.ALL);
		bookingGridView.setAdapter(allAdapter);

		// bookedGridView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		//
		// }
		// });

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
					Intent i = new Intent(BookActivity.this,
							BookColumnActivity.class);
					i.putExtra("book_deatail", allTags.get(position));
					startActivity(i);
				}

			}
		});
	}

	/**
	 * 删除某条订阅
	 * 
	 * @param position
	 */
	public void deleteBook(int position) {
		if (myTags.get(position).getIsFix() != 1) // 非固定栏目
			if (ischeck) {// 编辑状态，可删除排序
				TagInfo t = myTags.get(position);
				t.setCheck(false);
				allTags.add(t);
				myTags.remove(position);
				myAdapter.notifyDataSetChanged();
				allAdapter.notifyDataSetChanged();
			}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			myAdapter.notifyDataSetChanged();
			allAdapter.notifyDataSetChanged();
		};
	};

	/**
	 * 批量订阅操作
	 */
	private void book() {
		String log = "";
		List<SubscribeColumn> cs = new ArrayList<SubscribeColumn>();
		for (TagInfo tt : AppValue.ensubscriptColumnList.getList()) {// 遍历可订阅列表
			if (tt.getIsFix() != 1) {// 3.1.4 固定栏目要删除
				SubscribeColumn sub = new SubscribeColumn(tt.getTagName(),
						null, 1);
				for (TagInfo t : myTags) {// 遍历已订阅列表
					if (TextUtils.equals(tt.getTagName(), t.getTagName())) {// 如果已订阅
						sub.setIsDelete(0);
						log += t.getTagName() + ",";
					}
				}
				cs.add(sub);
			}
		}

		LogHelper.subcribeColumn(this, log);// flurry
		if (SlateDataHelper.getUserLoginInfo(this) != null) {
			showLoadingDialog(true);

			OperateController.getInstance(this).saveSubscribeColumnList(
					Tools.getUid(this), SlateDataHelper.getToken(this), cs,
					new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							initCheckItemStatus();

							// 批量订阅成功，更新临时订阅列表
							AppValue.bookColumnList.getList().clear();
							AppValue.bookColumnList.getList().addAll(myTags);
							// 批量订阅成功，更新数据库
							UserSubscribeListDb
									.getInstance(BookActivity.this)
									.clearTable(Tools.getUid(BookActivity.this));
							UserSubscribeListDb.getInstance(BookActivity.this)
									.addEntry(AppValue.bookColumnList);
							// 通知主页面更新topMenu
							SlateApplication.loginStatusChange = true;
							// 订阅完成，退出订阅页面
							finish();
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
		return BookActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.book_back) {
			initCheckItemStatus();
			finish();
		} else if (v.getId() == R.id.orderordelete) {
			isLogined();
		}
	}

	/**
	 * 取消itemcheck状态
	 */
	private void initCheckItemStatus() {
		// 取消check状态
		for (int i = 0; i < myTags.size(); i++) {
			myTags.get(i).setCheck(false);
		}
	}

	/**
	 * 如果已登录，则排序状态 未登录，则登陆
	 */
	public void isLogined() {
		if (SlateDataHelper.getUserLoginInfo(this) == null) {
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
		} else {
			if (ischeck) {// 提交订阅
				book();
			} else {
				order.setText("完成");
				for (int i = 0; i < myTags.size(); i++) {
					if (myTags.get(i).getIsFix() != 1)
						myTags.get(i).setCheck(true);
				}
				myAdapter.notifyDataSetChanged();
			}
			ischeck = !ischeck;
		}
	}
}
