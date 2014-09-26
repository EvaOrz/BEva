package cn.com.modernmedia.views;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.adapter.SelectColumnAdapter;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 选择栏目
 * 
 * @author zhuqiao
 * 
 */
public class SelectColumnActivity extends BaseSelectColumnActivity implements
		OnClickListener {
	private ListView listView;
	private SelectColumnAdapter adapter;
	private TextView tabCurrAppColumns, tabOtherAppColumns;
	private boolean refresh = true;
	private int currTab = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_column_activity);
		init();
	}

	private void init() {
		listView = (ListView) findViewById(R.id.select_column_list);
		adapter = new SelectColumnAdapter(this);
		listView.setAdapter(adapter);
		AppValue.tempColumnList = AppValue.ensubscriptColumnList.copy();
		findViewById(R.id.select_column_tab_frame).getLayoutParams().width = ViewsApplication.width * 3 / 4;
		tabCurrAppColumns = (TextView) findViewById(R.id.manage_curr_app);
		tabOtherAppColumns = (TextView) findViewById(R.id.manage_other_app);

		findViewById(R.id.select_column_sure).setOnClickListener(this);
		findViewById(R.id.manage_curr_app).setOnClickListener(this);
		findViewById(R.id.manage_other_app).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.select_column_sure) {
			addColumns("");
		} else if (v.getId() == R.id.manage_curr_app) {
			if (currTab == 2) {
				currTab = 1;
				setTabText(tabCurrAppColumns, true);
				setTabText(tabOtherAppColumns, false);
				setDataToAdapter(true);
			}
		} else if (v.getId() == R.id.manage_other_app) {
			if (currTab == 1) {
				currTab = 2;
				setTabText(tabCurrAppColumns, false);
				setTabText(tabOtherAppColumns, true);
				setDataToAdapter(true);
			}
		}
	}

	private void setTabText(TextView textView, boolean select) {
		if (select) {
			textView.setBackgroundColor(Color.parseColor("#4691dc"));
			textView.setTextColor(Color.WHITE);
		} else {
			textView.setBackgroundColor(Color.parseColor("#fafafa"));
			textView.setTextColor(Color.parseColor("#333333"));
		}
	}

	/**
	 * 判断是否包含有可以订阅的子栏目
	 * 
	 * @param info
	 *            父栏目
	 * @return true，显示父栏目；false,不显示
	 */
	public boolean checkHasChildCanSubscribe(TagInfo info) {
		// TODO 如果是父栏目，那么遍历所有子栏目，只要有一个满足可以订阅，那么父栏目就可以显示
		if (!ParseUtil.mapContainsKey(AppValue.tempColumnList.getChildMap(),
				info.getTagName()))
			return false;
		List<TagInfo> list = AppValue.tempColumnList.getChildMap().get(
				info.getTagName());
		for (TagInfo child : list) {
			if (child.getEnablesubscribe() == 1
					&& child.getColumnProperty().getNoColumn() == 0
					&& child.getIsFix() == 0)
				return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (refresh) {
			setDataToAdapter(false);
		}
	}

	/**
	 * 给adapter设置数据
	 * 
	 * @param moveToTop
	 */
	private void setDataToAdapter(boolean moveToTop) {
		adapter.clear();
		adapter.setData(currTab == 1);
		if (moveToTop && adapter.getCount() > 0) {
			listView.setSelection(0);
		}
	}

	public void gotoSelectChildColumnActvity(TagInfo tagInfo) {
		Intent intent = new Intent(this, SelectChildColumnActivity.class);
		intent.putExtra("PARENT", tagInfo.getTagName());
		startActivityForResult(intent,
				ViewsMainActivity.SELECT_COLUMN_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ViewsMainActivity.SELECT_COLUMN_REQUEST_CODE) {
				refresh = false;
				setResult(RESULT_OK);
				finish();
			}
		}
	}

	@Override
	public String getActivityName() {
		return SelectColumnActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
