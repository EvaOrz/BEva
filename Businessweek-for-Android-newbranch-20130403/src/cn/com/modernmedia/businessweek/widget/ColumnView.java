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
import android.widget.TextView;
import cn.com.modernmedia.businessweek.AboutActivity;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.adapter.ColumnListAdapter;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.model.Cat;
import cn.com.modernmedia.model.Entry;

/**
 * À¸Ä¿view
 * 
 * @author ZhuQiao
 * 
 */
public class ColumnView extends LinearLayout implements FetchEntryListener {
	private Context mContext;
	private LinearLayout layout;
	private ColumnListAdapter adapter;
	private View footerView;

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
		layout = (LinearLayout) findViewById(R.id.column_list);
		adapter = new ColumnListAdapter(mContext, new NotifyAdapterListener() {

			@Override
			public void notifyReaded() {
			}

			@Override
			public void notifyChanged() {
				setValuesForWidget();
			}

			@Override
			public void nofitySelectItem(Object args) {
			}
		});
	}

	private void initFooter() {
		footerView = LayoutInflater.from(mContext).inflate(
				R.layout.column_item, null);
		((Button) footerView.findViewById(R.id.cloumn_item_color))
				.setBackgroundColor(Color.BLUE);
		((TextView) footerView.findViewById(R.id.cloumn_item_name))
				.setText(R.string.about);
		((LinearLayout) footerView.findViewById(R.id.cloumn_margin))
				.getLayoutParams().height = 10;
		footerView.findViewById(R.id.cloumn_margin).setBackgroundColor(
				Color.BLACK);
		footerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, AboutActivity.class);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(R.anim.down_in,
						R.anim.hold);
			}
		});
	}

	@Override
	public void setData(Entry entry) {
		if (entry != null && entry instanceof Cat) {
			Cat cat = (Cat) entry;
			if (cat.getList() != null && !cat.getList().isEmpty()) {
				adapter.clear();
				adapter.setData(cat.getList());
				setValuesForWidget();
			}
		}
	}

	public void setValuesForWidget() {
		layout.removeAllViews();
		for (int i = 0; i < adapter.getCount(); i++) {
			layout.addView(adapter.getView(i, null, null));
		}
		layout.addView(footerView);
	}

}
