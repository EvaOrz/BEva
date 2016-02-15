package cn.com.modernmedia.views;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.views.adapter.SelectChildColumnAdapter;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 选择子栏目
 * 
 * @author zhuqiao
 * 
 */
@SuppressLint("InflateParams")
public class SelectChildColumnActivity extends BaseSelectColumnActivity {
	private Context mContext;
	private View pagerFrame;
	private ViewPager viewPager;
	private ListView listView;
	private SelectChildColumnAdapter adapter;
 
	private TagInfoList tagInfoList;

	private String parent;
	private int single;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_child_column_activity);
		mContext = this;
		if (getIntent() != null && getIntent().getExtras() != null) {
			parent = getIntent().getExtras().getString("PARENT");
			single = getIntent().getExtras().getInt("SINGLE", 0);
		}
		init();
	}

	private void init() {
		if (single == 0) {
			// TODO 从选择栏目进入
			tagInfoList = AppValue.tempColumnList.getEnSubscriptMap(parent);
		} else {
			// TODO 从子栏目head进入
			AppValue.tempColumnList = AppValue.ensubscriptColumnList.copy();
			tagInfoList = AppValue.tempColumnList.getEnSubscriptMap(parent);
		}

		pagerFrame = findViewById(R.id.select_child_column_pager_frame);
		viewPager = (ViewPager) findViewById(R.id.select_child_column_pager);
		listView = (ListView) findViewById(R.id.select_child_column_list);
		adapter = new SelectChildColumnAdapter(this);
		listView.setAdapter(adapter);
		viewPager.getLayoutParams().width = ViewsApplication.width * 3 / 4;
		viewPager.getLayoutParams().height = viewPager.getLayoutParams().width * 278 / 480;
		viewPager.setOffscreenPageLimit(tagInfoList.getParentList().size());
		viewPager.setPageMargin(ViewsApplication.width / 16);
		viewPager.setAdapter(new GalleryPagerAdapter());
		int index = 0;
		if (!TextUtils.isEmpty(parent)) {
			index = tagInfoList.getParentList().indexOf(parent);
		}
		index = index < 0 ? 0 : index;
		viewPager.setCurrentItem(index, false);
		adapter.setData(getList(index));
		pagerFrame.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return viewPager.dispatchTouchEvent(event);
			}
		});
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				adapter.clear();
				adapter.setData(getList(position));
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (pagerFrame != null)
					pagerFrame.invalidate();
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		findViewById(R.id.select_child_column_sure).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						addColumns(parent);
					}
				});
	}

	/**
	 * 获取当前tag下子栏目
	 * 
	 * @param position
	 * @return
	 */
	private List<TagInfo> getList(int position) {
		String parent = "";
		if (tagInfoList.getParentList().size() > position)
			parent = tagInfoList.getParentList().get(position);
		if (TextUtils.isEmpty(parent))
			return null;
		if (ParseUtil.mapContainsKey(tagInfoList.getChildMap(), parent)) {
			return tagInfoList.getChildMap().get(parent);
		}
		return null;
	}

	private class GalleryPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return tagInfoList.getParentList().size();
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
			View view = LayoutInflater.from(mContext).inflate(
					R.layout.select_child_column_pager_item, null);
			ImageView imageView = (ImageView) view
					.findViewById(R.id.select_child_column_pager_item_img);
			TextView name = (TextView) view
					.findViewById(R.id.select_child_column_pager_item_name);

			V.setImage(imageView, "placeholder");
			imageView.setScaleType(ScaleType.CENTER);
			TagInfo tagInfo = TagInfoListDb.getInstance(mContext)
					.getTagInfoByName(
							tagInfoList.getParentList().get(position), "",
							false);
			V.downSubscriptPicture(tagInfo.getTagName(), imageView);
			name.setText(tagInfo.getColumnProperty().getCname());

			container.addView(view);
			return view;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (single != 0 && interceptKeyBack(parent)) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public String getActivityName() {
		return SelectChildColumnActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
