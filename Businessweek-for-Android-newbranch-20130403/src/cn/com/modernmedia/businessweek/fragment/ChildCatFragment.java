package cn.com.modernmedia.businessweek.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.Fragment.BaseFragment;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.widget.ChildCatItem;
import cn.com.modernmedia.businessweek.widget.IndexHeadView;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.widget.AtlasViewPager;

/**
 * 子栏目Fragment
 * 
 * @author ZhuQiao
 * 
 */
public class ChildCatFragment extends BaseFragment implements
		NotifyArticleDesListener {
	private static final int DURATION = 250;
	private Context mContext;
	private AtlasViewPager viewPager;
	private LinearLayout toolbar;
	private ImageView tag;
	private List<CatItem> list;
	private Issue issue;
	private int parentId = -1;
	private float startX;
	private int bg_width;
	private boolean isClick = true;
	private IndexHeadView currentHeadView;
	private int currentHeadSize;
	private View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.child_view, container, false);
		viewPager = (AtlasViewPager) view.findViewById(R.id.child_viewpager);
		viewPager.setListener(this);
		viewPager.setOffscreenPageLimit(3);
		toolbar = (LinearLayout) view.findViewById(R.id.child_toolbar);
		tag = (ImageView) view.findViewById(R.id.child_tag_bg);
		if (mContext instanceof MainActivity) {
			((MainActivity) mContext).setScrollView(1, viewPager);
		}
		refresh();
		return view;
	}

	public void setData(Issue issue, int parentId) {
		this.issue = issue;
		this.parentId = parentId;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void refresh() {
		if (issue == null || parentId == -1)
			return;
		toolbar.removeAllViews();
		tag.setVisibility(View.GONE);
		if (!DataHelper.childMap.isEmpty()
				&& DataHelper.childMap.containsKey(parentId)) {
			list = DataHelper.childMap.get(parentId);
			viewPager.setValue(list.size());
			initToolBar();
		} else {
			list = new ArrayList<CatItem>();
		}
		ChildPagerAdapter adapter = new ChildPagerAdapter();
		viewPager.setAdapter(adapter);
	}

	@Override
	public void updateDes(int position) {
		if (list != null && list.size() > position) {
			if (!isClick)
				startAnim(position);
		}
	}

	@Override
	public void updatePage(int state) {
	}

	private void initToolBar() {
		android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
				MyApplication.width / list.size(),
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		bg_width = MyApplication.width / (list.size() + 1);
		lp.topMargin = 5;
		lp.bottomMargin = 5;
		for (int i = 0; i < list.size(); i++) {
			final CatItem item = list.get(i);
			TextView textView = new TextView(mContext);
			textView.setTextColor(Color.BLACK);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			textView.setText(item.getTagname());
			textView.setGravity(Gravity.CENTER);
			textView.setTag(i);
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final int position = (Integer) v.getTag();
					isClick = true;
					startAnim(position);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							viewPager.setCurrentItem(position, false);
						}
					}, DURATION);

				}
			});
			toolbar.addView(textView, lp);
		}
		tag.getLayoutParams().width = bg_width;
		startAnim(0);
	}

	private void startAnim(final int index) {
		float toX = index * MyApplication.width / list.size()
				+ MyApplication.width / (list.size() * 2) - bg_width / 2;
		TranslateAnimation ta = new TranslateAnimation(Animation.ABSOLUTE,
				startX, Animation.ABSOLUTE, toX, Animation.ABSOLUTE, 0,
				Animation.ABSOLUTE, 0);
		ta.setDuration(DURATION);
		ta.setFillAfter(true);
		ta.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				if (tag.getVisibility() != View.VISIBLE)
					return;
				for (int i = 0; i < toolbar.getChildCount(); i++) {
					View view = toolbar.getChildAt(i);
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
				if (tag.getVisibility() != View.VISIBLE) {
					tag.setVisibility(View.VISIBLE);
					for (int i = 0; i < toolbar.getChildCount(); i++) {
						View view = toolbar.getChildAt(i);
						if (view instanceof TextView) {
							if (i == index) {
								((TextView) view).setTextColor(Color.WHITE);
							} else {
								((TextView) view).setTextColor(Color.BLACK);
							}
						}
					}
				}
				isClick = false;
			}
		});
		tag.startAnimation(ta);
		startX = toX;
	}

	public IndexHeadView getHeadView() {
		return currentHeadView;
	}

	public int getChildSize() {
		return currentHeadSize;
	}

	public void setIntercept(boolean intercept) {
		viewPager.setIntercept(intercept);
	}

	private class ChildPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			CatItem item = list.get(position);
			ChildCatItem childView = new ChildCatItem(mContext);
			childView.setData(issue, item.getId() + "");
			container.addView(childView);
			return childView;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			super.setPrimaryItem(container, position, object);
			View view = viewPager.getChildAt(position);
			if (view instanceof ChildCatItem) {
				currentHeadView = ((ChildCatItem) view).getHeadView();
				currentHeadSize = ((ChildCatItem) view).getChildSize();
			}
		}

	}

	@Override
	public void showView(boolean show) {
		if (view != null)
			view.setVisibility(show ? View.VISIBLE : View.GONE);
	}

}
