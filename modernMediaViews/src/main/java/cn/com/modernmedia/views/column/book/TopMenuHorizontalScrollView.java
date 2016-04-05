package cn.com.modernmedia.views.column.book;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.ConstData;
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

	private LinearLayout view;
	private Context mContext;
	private HorizontalScrollView scrollView;
	private LinearLayout layout;
	private ImageView columnView, addView;
	private List<View> checkSelectView = new ArrayList<View>();// 记录栏目列表
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
		view = (LinearLayout) LayoutInflater.from(mContext).inflate(
				R.layout.top_menu, null);
		addView(view);
		if (ConstData.getAppId() == 20) {// 设置iweekly背景颜色
			view.setBackgroundColor(Color.parseColor("#323232"));
		}
		scrollView = (HorizontalScrollView) view
				.findViewById(R.id.book_horizantal_scrollview);
		addView = (ImageView) view.findViewById(R.id.subscribe_add);
		addView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, BookActivity.class);
				if (mContext instanceof Activity)
					((Activity) mContext).startActivityForResult(i,
							ViewsMainActivity.BOOK_ACTIVITY);
			}
		});
		columnView = (ImageView) view.findViewById(R.id.subscribe_column);
		columnView.setImageResource(V.getId("top_menu_colunm"));
		columnView.measure(0, 0);
		columnView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((CommonMainActivity) mContext).getScrollView().clickButton(
						true);
			}
		});

	}

	/**
	 * 侧边栏按钮
	 * 
	 * @return
	 */
	public View getTopMenuColumnViewButton() {
		return columnView;
	}

	public View getTopMenuAddViewButton() {
		return addView;
	}

	public void setData(List<TagInfo> tagInfos) {
		if (tagInfos == null || tagInfos.size() < 1)
			return;
		scrollView.removeAllViews();
		checkSelectView.clear();

		layout = new LinearLayout(mContext);
		for (int i = 0; i < tagInfos.size(); i++) {
			if (!tagInfos.get(i).getTagName().equals("cat_191")) { // 去除视野
				final int position = i;
				final TagInfo tagInfo = tagInfos.get(i);

				TextView textChild = new TextView(mContext);
				textChild.setText(tagInfo.getColumnProperty().getCname());
				textChild.setTextColor(Color.BLACK);

				LinearLayout cLayout = new LinearLayout(mContext);
				/**
				 * noMenuBar = 1：top——menu不显示栏目信息
				 */
				if (tagInfo.getColumnProperty().getNoMenuBar() == 1) {
					cLayout.setLayoutParams(new LayoutParams(0, 0));
				} else {
					cLayout.setLayoutParams(new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.MATCH_PARENT));
					cLayout.setGravity(Gravity.CENTER_VERTICAL);
					cLayout.setPadding(20, 0, 0, 0);
				}
				cLayout.setTag(tagInfo);
				cLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						clickItem(tagInfo, position);
					}
				});
				if (ConstData.getAppId() == 20) {
					ImageView imgChild = new ImageView(mContext);
					imgChild.setLayoutParams(new LayoutParams(60, 60));
					V.setImageForWeeklyCat(mContext, tagInfo, imgChild);
					textChild.setPadding(10, 0, 20, 0);
					textChild.setTextColor(Color.WHITE);
					cLayout.addView(imgChild);
				} else {
					textChild.setGravity(Gravity.CENTER_VERTICAL);
					cLayout.setPadding(10, 20, 0, 20);
					textChild.setPadding(15, 0, 15, 0);
					textChild.setLayoutParams(new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.MATCH_PARENT));
				}
				cLayout.addView(textChild);
				layout.addView(cLayout);
				checkSelectView.add(cLayout);
			}
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
		if (mContext instanceof CommonMainActivity) {
			// 如果点击的是当前显示的cat,不重新 获取数据
			if (selectPosition == position) {
				return;
			}
			selectPosition = position;
			V.setIndexTitle(mContext, tagInfo);// 换title
			((CommonMainActivity) mContext).clickItemIfPager(
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
		if (mContext instanceof CommonMainActivity)
			screenHalf = ((CommonMainActivity) mContext).getWindowManager()
					.getDefaultDisplay().getWidth()
					/ 2 - 44 * 2 - 20;
		for (int i = 0; i < layout.getChildCount(); i++) {

			View child = layout.getChildAt(i);
			if (child.getTag() instanceof TagInfo) {

				// 选中状态
				if (((TagInfo) child.getTag()).getTagName().equals(currTagName)) {
					// 商周设置为 栏目颜色
					if (DataHelper.columnColorMap.containsKey(currTagName)
							&& ConstData.getAppId() == 1) {
						int color = DataHelper.columnColorMap.get(currTagName);
						TextView t = (TextView) ((LinearLayout) child)
								.getChildAt(0);
						t.setBackgroundColor(color);
						t.setTextColor(Color.WHITE);
					} else
						V.setViewBack(child, "#323232");

					int scrollX = scrollView.getScrollX();
					int left = child.getLeft();
					int right = child.getRight();
					int leftScreen = left - scrollX + (right - left) / 2;
					scrollView.smoothScrollBy((leftScreen - screenHalf), 0);
				} else {
					if (ConstData.getAppId() == 1) {
						TextView t = (TextView) ((LinearLayout) child)
								.getChildAt(0);
						V.setViewBack(t, "#fff0f0f0");
						t.setTextColor(Color.BLACK);
					} else
						V.setViewBack(child, "#191919");

				}
			}
		}
	}
}
