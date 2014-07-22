package cn.com.modernmedia.businessweek.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.modernmedia.businessweek.AboutActivity;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.adapter.ColumnListAdapter;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Cat;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.WebViewPop;
import cn.com.modernmediaslate.model.Entry;

/**
 * 栏目view
 * 
 * @author ZhuQiao
 * 
 */
public class ColumnView extends LinearLayout implements FetchEntryListener {
	private Context mContext;
	private ListView mListView;
	private ColumnListAdapter adapter;
	private View footerView;
	private TextView loginText;

	public ColumnView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		this.addView(LayoutInflater.from(mContext).inflate(R.layout.column,
				null));
		this.setBackgroundColor(Color.BLACK);
		initFooter();
		mListView = (ListView) findViewById(R.id.column_list);
		adapter = new ColumnListAdapter(mContext);
		mListView.addFooterView(footerView);
		mListView.setAdapter(adapter);
	}

	private void initFooter() {
		footerView = LayoutInflater.from(mContext).inflate(
				R.layout.column_footview, null);
		View loginView = footerView.findViewById(R.id.column_footview_login);
		((Button) loginView.findViewById(R.id.cloumn_item_color))
				.setBackgroundColor(Color.BLUE);
		((LinearLayout) loginView.findViewById(R.id.cloumn_margin))
				.getLayoutParams().height = 10;
		loginView.findViewById(R.id.cloumn_margin).setBackgroundColor(
				Color.BLACK);
		loginView.setVisibility(View.GONE);
		loginText = (TextView) loginView.findViewById(R.id.cloumn_item_name);
		loginText.setText(R.string.login);
		loginView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity) mContext).gotoLoginActivity();
			}
		});

		// 关于
		View aboutView = footerView.findViewById(R.id.column_footview_about);
		((Button) aboutView.findViewById(R.id.cloumn_item_color))
				.setBackgroundColor(Color.BLUE);
		((TextView) aboutView.findViewById(R.id.cloumn_item_name))
				.setText(R.string.about);
		((LinearLayout) aboutView.findViewById(R.id.cloumn_margin))
				.getLayoutParams().height = 10;
		aboutView.findViewById(R.id.cloumn_margin).setBackgroundColor(
				Color.BLACK);
		aboutView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, AboutActivity.class);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(R.anim.down_in,
						R.anim.hold);
			}
		});

		// 应用推荐
		View recommendView = footerView
				.findViewById(R.id.column_footview_recommend);
		((Button) recommendView.findViewById(R.id.cloumn_item_color))
				.setBackgroundColor(Color.BLUE);
		((TextView) recommendView.findViewById(R.id.cloumn_item_name))
				.setText(R.string.app_recommend);
		((LinearLayout) recommendView.findViewById(R.id.cloumn_margin))
				.getLayoutParams().height = 10;
		recommendView.findViewById(R.id.cloumn_margin).setBackgroundColor(
				Color.BLACK);
		recommendView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new WebViewPop(mContext,
						"http://www.bbwc.cn/install/honored_android/index2.html");
			}
		});
	}

	@Override
	public void setData(Entry entry) {
		if (entry instanceof Cat) {
			Cat cat = (Cat) entry;
			if (ParseUtil.listNotNull(cat.getList())) {
				adapter.clear();
				adapter.setData(cat.getList());
			}
		}
	}

	public void setAdapterPosition(int position) {
		adapter.setSelectPosition(position);
		adapter.notifyDataSetChanged();
		mListView.setSelection(position);
	}

	/**
	 * 登录完成之后改变文字Fֵ
	 * 
	 * @param text
	 */
	public void afterLogin(String text) {
		loginText.setText(text);
	}

}
