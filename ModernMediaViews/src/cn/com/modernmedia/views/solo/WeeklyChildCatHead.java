package cn.com.modernmedia.views.solo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.index.IndexViewPagerItem;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.model.Entry;

/**
 * iweekly类型独立栏目/子栏目导航栏
 * 
 * @author user
 * 
 */
public class WeeklyChildCatHead extends BaseChildCatHead {
	private HorizontalScrollView view;
	private LayoutInflater inflater;
	private int selectColor, color;

	public WeeklyChildCatHead(Context context,
			IndexViewPagerItem indexViewPagerItem) {
		super(context, indexViewPagerItem);
		init();
	}

	private void init() {
		height = mContext.getResources().getDimensionPixelSize(
				R.dimen.iweekly_column_index_head_icon_height);
		selectColor = mContext.getResources().getColor(R.color.index_head_bg);
		color = mContext.getResources().getColor(R.color.column_left_logo_bg);
		inflater = LayoutInflater.from(mContext);
		frame = new LinearLayout(mContext);
		((LinearLayout) frame).setOrientation(LinearLayout.HORIZONTAL);
		view = new HorizontalScrollView(mContext);
		view.setBackgroundColor(color);
		view.setHorizontalScrollBarEnabled(false);
		view.setVerticalScrollBarEnabled(false);
		view.addView(frame, new LayoutParams(LayoutParams.FILL_PARENT, height));
		setView(view);
	}

	@Override
	protected void initToolBar(List<? extends Entry> list) {
		frame.removeAllViews();
		if (!ParseUtil.listNotNull(list))
			return;
		for (int i = 0; i < list.size(); i++) {
			final Entry entry = list.get(i);
			final int position = i;
			View child = inflater.inflate(R.layout.child_cat_head_weekly, null);
			ImageView icon = (ImageView) child
					.findViewById(R.id.column_index_head_icon);
			TextView title = (TextView) child
					.findViewById(R.id.column_index_head_title);
			View divider = child
					.findViewById(R.id.column_index_head_end_divider);
			if (i == 0)
				child.findViewById(R.id.column_index_head_divider)
						.setVisibility(View.GONE);
			else if (i == list.size() - 1)
				divider.setVisibility(View.VISIBLE);
			if (entry instanceof CatItem) {
				CatItem item = (CatItem) entry;
				title.setText(((CatItem) entry).getTagname());
				V.setImageForWeeklyCat(item, icon, false);
			} else if (entry instanceof SoloColumnChild) {
				title.setText(((SoloColumnChild) entry).getName());
			}
			child.setTag(entry);
			child.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					clickItem(entry, position);
				}
			});
			frame.addView(child);
		}
		super.initToolBar(list);
	}

	@Override
	public void setStatusBySelect(View view, boolean select, int index) {
		view.setBackgroundColor(select ? selectColor : color);
	}

	/**
	 * 隐藏headview
	 */
	public void hideHead() {
		frame.removeAllViews();
	}

	/**
	 * 显示headview
	 */
	public void showHead() {
	}

}
