package cn.com.modernmedia.businessweek;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.com.modernmedia.api.GetTagInfoOperate.TAG_TYPE;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.column.book.BookColumnActivity;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.model.Entry;

/**
 * 特刊专区
 * 
 * @author Eva.
 * 
 */
public class SpecialTabView extends BaseView {
	public static String TEKAN_TAGNAME = "special";
	private Context context;
	private RadioGroup special_rg;
	private ViewPager mViewpager;
	private GridView allGrid, vipGrid;
	private SpecialGridAdapter allAdapter, vipAdapter;
	private List<TagInfo> allDatas = new ArrayList<TagInfoList.TagInfo>(),
			vipDatas = new ArrayList<TagInfoList.TagInfo>();
	private List<View> grids = new ArrayList<View>();
	private IndexSpecialAdapter viewPagerAdapter;

	public SpecialTabView(Context context) {
		super(context);
		this.context = context;
		initView();
		initData();
	}

	private void initData() {
		// String parentTagName, String tagName, String group,
		// String top, TAG_TYPE type, FetchApiType fetchType,
		// FetchEntryListener listener
		OperateController.getInstance(context).getTagInfo("", TEKAN_TAGNAME,
				"", "", TAG_TYPE.SPECIAL, FetchApiType.USE_HTTP_FIRST,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagInfoList) {
							allDatas.clear();
							vipDatas.clear();
							TagInfoList list = (TagInfoList) entry;
							for (TagInfo i : list.getList()) {
								// if (i.getIsPay() == 0)
								allDatas.add(i);
								// else
								if (i.getIsPay() == 1)
									vipDatas.add(i);
							}
							handler.sendEmptyMessage(1);
						}

					}
				});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				viewPagerAdapter.notifyDataSetChanged();
				break;
			case 1:
				allAdapter.notifyDataSetChanged();
				vipAdapter.notifyDataSetChanged();
				break;
			}
		}
	};

	private void initView() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.main_special_view, null);
		addView(view);
		special_rg = (RadioGroup) view.findViewById(R.id.index_special_rg);
		mViewpager = (ViewPager) view.findViewById(R.id.index_special_vp);

		allGrid = new GridView(context);
		allGrid.setNumColumns(3);
		allGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		allGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent i = new Intent(context, BookColumnActivity.class);
				i.putExtra("is_tekan", 1);
				i.putExtra("book_deatail", allDatas.get(position));
				context.startActivity(i);
			}
		});
		vipGrid = new GridView(context);
		vipGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		vipGrid.setNumColumns(3);
		vipGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent i = new Intent(context, BookColumnActivity.class);
				i.putExtra("is_tekan", 1);
				i.putExtra("book_deatail", vipDatas.get(position));
				context.startActivity(i);
			}
		});
		allAdapter = new SpecialGridAdapter(context, allDatas);
		vipAdapter = new SpecialGridAdapter(context, vipDatas);
		allGrid.setAdapter(allAdapter);
		vipGrid.setAdapter(vipAdapter);

		grids.clear();
		grids.add(allGrid);
		grids.add(vipGrid);
		viewPagerAdapter = new IndexSpecialAdapter(context, grids);
		mViewpager.setAdapter(viewPagerAdapter);
		handler.sendEmptyMessage(0);

		special_rg.check(R.id.index_special_all);
		special_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.index_special_all) {
					mViewpager.setCurrentItem(0);
				} else if (checkedId == R.id.index_special_vip) {
					mViewpager.setCurrentItem(1);

				}
			}
		});
		mViewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 1)
					special_rg.check(R.id.index_special_vip);
				else
					special_rg.check(R.id.index_special_all);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	protected void reLoad() {

	}

	/**
	 * ViewPager Adapter
	 * 
	 * @author Eva.
	 * 
	 */
	public class IndexSpecialAdapter extends PagerAdapter {

		private List<View> mList = null;
		private Context mContext;

		public IndexSpecialAdapter(Context context, List<View> list) {
			mContext = context;
			mList = list;
		}

		@Override
		public int getCount() {
			if (mList != null) {
				return mList.size();
			}
			return 0;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mList.get(position));
			return mList.get(position);
		}
	}

}
