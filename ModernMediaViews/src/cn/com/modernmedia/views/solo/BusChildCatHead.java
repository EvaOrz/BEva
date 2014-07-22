package cn.com.modernmedia.views.solo;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.index.IndexViewPagerItem;
import cn.com.modernmediaslate.model.Entry;

/**
 * 商周类型独立栏目/子栏目导航栏
 * 
 * @author user
 * 
 */
public class BusChildCatHead extends BaseChildCatHead {
	private FrameLayout view;

	protected LinearLayout soloImgLl, soloToolBg;
	protected ImageView tag;

	private float startX;
	private int bg_width, listSize;

	public BusChildCatHead(Context context,
			IndexViewPagerItem indexViewPagerItem) {
		super(context, indexViewPagerItem);
		init();
	}

	private void init() {
		height = mContext.getResources().getDimensionPixelSize(
				R.dimen.solo_titlebar_height);
		view = new FrameLayout(mContext);
		view.addView(
				LayoutInflater.from(mContext).inflate(
						R.layout.child_cat_head_bus, null), new LayoutParams(
						LayoutParams.FILL_PARENT, height));
		frame = (LinearLayout) view.findViewById(R.id.solo_toolbar);
		soloImgLl = (LinearLayout) view.findViewById(R.id.solo_img_ll);
		soloToolBg = (LinearLayout) view.findViewById(R.id.tool_bg_ll);
		tag = (ImageView) view.findViewById(R.id.solo_tag_bg);
		setView(view);
	}

	@Override
	protected void initToolBar(final List<? extends Entry> list) {
		android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
				CommonApplication.width / list.size(),
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		bg_width = CommonApplication.width / (list.size() + 1);
		lp.topMargin = 5;
		lp.bottomMargin = 5;
		if (ParseUtil.listNotNull(list))
			listSize = list.size();
		frame.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			final int position = i;
			final Entry entry = list.get(i);
			TextView textView = new TextView(mContext);
			textView.setTextColor(Color.BLACK);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			if (entry instanceof CatItem) {
				textView.setText(((CatItem) entry).getTagname());
			} else if (entry instanceof SoloColumnChild) {
				textView.setText(((SoloColumnChild) entry).getName());
			}
			textView.setGravity(Gravity.CENTER);
			textView.setTag(entry);
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					clickItem(entry, position);
				}
			});
			frame.addView(textView, lp);
		}
		tag.getLayoutParams().width = bg_width;
		super.initToolBar(list);
	}

	private void startAnim(final int index) {
		if (listSize <= 1)
			return;
		float toX = index * CommonApplication.width / listSize
				+ CommonApplication.width / (listSize * 2) - bg_width / 2;
		if (startX == toX)
			return;
		TranslateAnimation ta = new TranslateAnimation(Animation.ABSOLUTE,
				startX, Animation.ABSOLUTE, toX, Animation.ABSOLUTE, 0,
				Animation.ABSOLUTE, 0);
		ta.setDuration(DURATION);
		ta.setFillAfter(true);
		ta.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				for (int i = 0; i < frame.getChildCount(); i++) {
					View view = frame.getChildAt(i);
					if (view instanceof TextView) {
						if (i == index) {
							((TextView) view).setTextColor(Color.WHITE);
						} else {
							((TextView) view).setTextColor(Color.BLACK);
						}
					}
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		tag.startAnimation(ta);
		startX = toX;
	}

	@Override
	public void setStatusBySelect(View view, boolean select, int index) {
		if (select) {
			if (tag.getVisibility() != View.VISIBLE)
				tag.setVisibility(View.VISIBLE);
			startAnim(index);
		}
	}

}
