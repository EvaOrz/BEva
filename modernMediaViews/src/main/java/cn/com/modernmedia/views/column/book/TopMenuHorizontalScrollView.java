package cn.com.modernmedia.views.column.book;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.util.V;

/**
 * top_menu view
 * 
 * @author lusiyuan
 *
 */
public class TopMenuHorizontalScrollView extends RelativeLayout {

	private RelativeLayout view;
	private Context mContext;
	private HorizontalScrollView scrollView;
	private LinearLayout layout;
	private List<TextView> checkSelectView = new ArrayList<TextView>();// 记录栏目列表
	/**
	 * 由于可能是独立栏目，并且绑定在列表上，导致每次切换的时候都还原，所以设置成静态变量
	 */
	public static int selectPosition = -1;

	// private ImageView add;

	public TopMenuHorizontalScrollView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public TopMenuHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	private void init() {
		view = (RelativeLayout) LayoutInflater.from(mContext).inflate(
				R.layout.top_menu, null);
		addView(view);
		scrollView = (HorizontalScrollView) view
				.findViewById(R.id.book_horizantal_scrollview);
		view.findViewById(R.id.subscribe_add).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent i = new Intent(mContext, BookActivity.class);
						if (mContext instanceof Activity)
							((Activity) mContext).startActivityForResult(i,
									ViewsMainActivity.BOOK_ACTIVITY);
					}
				});

	}

	public void setData(List<TagInfo> tagInfos) {
		if (tagInfos == null || tagInfos.size() < 1)
			return;
		scrollView.removeAllViews();
		checkSelectView.clear();

		layout = new LinearLayout(mContext);
		layout.setPadding(0, 10, 0, 10);
		for (int i = 0; i < tagInfos.size(); i++) {
			final int position = i;
			final TagInfo tagInfo = tagInfos.get(i);
			TextView child = new TextView(mContext);
			child.setText(tagInfo.getColumnProperty().getCname());
			child.setPadding(25, 10, 25, 10);
			child.setTextColor(Color.BLACK);
			child.setTag(tagInfo);
			child.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					clickItem(tagInfo, position);
				}
			});

			layout.addView(child);
			checkSelectView.add(child);
		}
		scrollView.addView(layout);
		setSelectedItemForChild(tagInfos.get(0).getTagName());
	}

	protected void clickItem(final TagInfo tagInfo, final int position) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				click(tagInfo, position);
			}
		}, 250);
	}

	private void click(TagInfo tagInfo, int position) {
		if (mContext instanceof ViewsMainActivity) {
			// ((ViewsMainActivity) mContext).getScrollView().IndexClick();
			// 如果点击的是当前显示的cat,不重新 获取数据
			if (selectPosition == position) {
				return;
			}
			selectPosition = position;
			V.setIndexTitle(mContext, tagInfo);// 换title
			((ViewsMainActivity) mContext).clickItemIfPager(
					tagInfo.getTagName(), false);// 换内容
			setSelectedItemForChild(tagInfo.getTagName());
		}
	}

	/**
	 * 设置子栏目选中状态
	 * 
	 * @param tagInfo
	 */
	@SuppressLint("NewApi")
	// int[] location = new int[2];
	// child.getLocationOnScreen(location);
	// int x = location[0];
	public void setSelectedItemForChild(String currTagName) {
		if (layout == null || layout.getChildCount() == 0)
			return;
		int screenHalf = 0;// 屏幕宽度的一半
		if (mContext instanceof ViewsMainActivity)
			screenHalf = ((ViewsMainActivity) mContext).getWindowManager()
					.getDefaultDisplay().getWidth() / 2;
		for (int i = 0; i < layout.getChildCount(); i++) {

			View child = layout.getChildAt(i);
			if (child.getTag() instanceof TagInfo) {

				if (((TagInfo) child.getTag()).getTagName().equals(currTagName)) {
					if (DataHelper.columnColorMap.containsKey(currTagName)) {
						int color = DataHelper.columnColorMap.get(currTagName);
						child.setBackgroundColor(color);
					}
					((TextView) child).setTextColor(Color.WHITE);

					int scrollX = scrollView.getScrollX() - 60;
					int left = child.getLeft();
					int leftScreen = left - scrollX;
					scrollView.smoothScrollBy((leftScreen - screenHalf), 0);

				} else {
					V.setViewBack(child, "#ffffff");
					((TextView) child).setTextColor(Color.BLACK);
				}
			}
		}
	}
}
