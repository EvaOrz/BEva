package cn.com.modernmedia.views.column;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Cat;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.model.SoloColumn;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ConstData.APP_TYPE;
import cn.com.modernmedia.util.ImageScaleType;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.AboutActivity;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.adapter.BaseColumnAdapter;
import cn.com.modernmedia.views.adapter.BusColumnAdapter;
import cn.com.modernmedia.views.adapter.WeeklyColumnAdapter;
import cn.com.modernmedia.views.index.IndexView;
import cn.com.modernmedia.views.model.ColumnParm;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.WebViewPop;
import cn.com.modernmediaslate.model.Entry;

/**
 * 栏目列表页(logo+listview)
 * 
 * @author user
 * 
 */
public class ColumnView extends BaseView implements FetchEntryListener {
	private Context mContext;
	private View contain;
	private ImageView logo, top, divider;
	private ListView listView;
	private BaseColumnAdapter adapter;
	private ColumnParm parm;

	/**
	 * 确定栏目页列表的footer的某项是否选中，从而改变该项的背景或者字体颜色等
	 * 
	 * @author jiancong
	 * 
	 */
	public interface ColumnFooterItemIsSeletedListener {
		/**
		 * 
		 * @param isSelected
		 *            true表示其item处于选中状态,false则未选中
		 */
		public void doIsSelected(boolean isSelected);
	}

	public ColumnView(Context context) {
		this(context, null);
	}

	public ColumnView(Context context, AttributeSet attr) {
		super(context, attr);
		mContext = context;
		init();
	}

	private void init() {
		addView(LayoutInflater.from(mContext).inflate(R.layout.column_view,
				null));
		contain = findViewById(R.id.column_view);
		logo = (ImageView) findViewById(R.id.column_logo);
		top = (ImageView) findViewById(R.id.column_top_image);
		divider = (ImageView) findViewById(R.id.column_head_shadow);
		listView = (ListView) findViewById(R.id.column_list);
		initRes();
		if (parm.getType().equals(V.BUSINESS)) {
			adapter = new BusColumnAdapter(mContext, parm);
		} else if (parm.getType().equals(V.IWEEKLY)) {
			adapter = new WeeklyColumnAdapter(mContext, parm);
		} else {
			adapter = new BaseColumnAdapter(mContext, parm);
		}
		initFooter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				position = position - listView.getHeaderViewsCount();
				if (position < 0 || adapter.getCount() <= position)
					return;
				click(adapter.getItem(position), position);
			}
		});
	}

	/**
	 * 设置页面宽度
	 * 
	 * @param width
	 */
	public void setViewWidth(int width) {
		String size = parm.getLogo_size();
		ImageScaleType.setScaleType(logo, parm.getLogo_scale_type());
		if (!TextUtils.isEmpty(size)) {
			String[] arr = size.split(",");
			if (arr.length == 2) {
				int s_width = ParseUtil.stoi(arr[0], 1);
				int s_height = ParseUtil.stoi(arr[1], 0);
				logo.getLayoutParams().width = width;
				if (s_width == -1) {
					// TODO 宽占满屏幕，高不做等比缩放
					logo.getLayoutParams().height = s_height
							* CommonApplication.height / 1280;
				} else {
					logo.getLayoutParams().height = width * s_height / s_width;
				}
			}
		}
	}

	/**
	 * 设置list的footer
	 * 
	 * @param footerView
	 */
	public void setViewFooter(View footerView) {
		if (footerView != null) {
			listView.addFooterView(footerView);
		}
	}

	/**
	 * 初始化资源
	 */
	private void initRes() {
		parm = ParseProperties.getInstance(mContext).parseColumn();
		if (TextUtils.isEmpty(parm.getLogo())) {
			logo.setVisibility(View.GONE);
		} else {
			V.setImage(logo, parm.getLogo());
		}
		V.setViewBack(logo, parm.getLogo_bg());
		// 背景
		V.setImage(contain, parm.getColumn_bg());
		// 顶部img
		if (TextUtils.isEmpty(parm.getTop_image())) {
			top.setVisibility(View.GONE);
		} else {
			V.setImage(top, parm.getTop_image());
		}
		// head divider
		if (TextUtils.isEmpty(parm.getHead_divider())) {
			divider.setVisibility(View.GONE);
		} else {
			V.setImage(divider, parm.getHead_divider());
		}
		if (!parm.getType().equals(V.IWEEKLY)
				&& !TextUtils.isEmpty(parm.getDivider())) {
			V.setListviewDivider(mContext, listView, parm.getDivider());
		}
	}

	/**
	 * 初始化footerview
	 */
	private void initFooter() {
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		if (parm.getType().equals(V.BUSINESS)) {
			if (!TextUtils.isEmpty(parm.getAbout())) {
				View aboutView = getBusFooterItem(R.string.about);
				aboutView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,
								AboutActivity.class);
						mContext.startActivity(intent);
						((Activity) mContext).overridePendingTransition(
								R.anim.down_in, R.anim.hold);
					}
				});
				layout.addView(aboutView);
			}
			if (!TextUtils.isEmpty(parm.getRecommend())) {
				View recommendView = getBusFooterItem(R.string.app_recommend);
				recommendView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						new WebViewPop(mContext, parm.getRecommend());
					}
				});
				layout.addView(recommendView);
			}
		}
		listView.addFooterView(layout);
	}

	private View getBusFooterItem(int textRes) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.bus_column_item, null);
		View color = view.findViewById(R.id.bus_cloumn_item_color);
		if (parm.getShow_color() == 1) {
			color.setBackgroundColor(Color.BLUE);
		} else {
			color.setVisibility(View.GONE);
		}
		TextView name = (TextView) view.findViewById(R.id.bus_column_item_name);
		name.setText(textRes);
		if (!TextUtils.isEmpty(parm.getName_color())) {
			name.setTextColor(Color.parseColor(parm.getName_color()));
		}
		View margin = view.findViewById(R.id.bus_cloumn_margin);
		if (parm.getShow_margin() == 1) {
			margin.getLayoutParams().height = 10;
			margin.setBackgroundColor(Color.BLACK);
		} else {
			margin.setVisibility(View.GONE);
		}
		View contain = view.findViewById(R.id.bus_column_contain);
		V.setImage(contain, parm.getItem_bg());
		V.setImage(view.findViewById(R.id.bus_column_item_row), parm.getRow());
		return view;
	}

	@Override
	public void setData(Entry entry) {
		if (entry instanceof Cat) {
			Cat cat = (Cat) entry;
			if (ParseUtil.listNotNull(cat.getList())) {
				adapter.clear();
				adapter.setData(cat.getList());
			}
		} else if (entry instanceof SoloColumn) {
			SoloColumn column = (SoloColumn) entry;
			if (ParseUtil.listNotNull(column.getList())) {
				adapter.clear();
				adapter.setData(column.getList(), true);
			}
		}
		if (adapter.getCount() > 0) {
			listView.setSelection(0);
		}
		if (mContext instanceof ViewsMainActivity) {
			if (ViewsApplication.itemIsSelectedListener != null)
				ViewsApplication.itemIsSelectedListener.doIsSelected(false);
		}
	}

	public void setAdapterPosition(int position) {
		adapter.setSelectPosition(position);
		adapter.notifyDataSetChanged();
		listView.setSelection(position);
	}

	private void click(final CatItem item, final int position) {
		LogHelper.logOpenColumn(mContext, item.getId() + "");
		if (mContext instanceof CommonMainActivity) {
			BaseView view = ((CommonMainActivity) mContext).getIndexView();
			if (view instanceof IndexView)
				((IndexView) view).showIssueListView(false);
			if (ViewsApplication.itemIsSelectedListener != null)
				ViewsApplication.itemIsSelectedListener.doIsSelected(false);
			((CommonMainActivity) mContext).setIndexTitle(item.getCname());
			((CommonMainActivity) mContext).getScrollView().IndexClick();
			if (adapter.getSelectPosition() == position)
				return;
			adapter.setSelectPosition(position);
			adapter.notifyDataSetChanged();
			final int id = item.getId();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					if (CommonApplication.appType == APP_TYPE.IWEEKLY) {
						if (position == 0) {
							((ViewsMainActivity) mContext).finish();
						} else {
							clickItemIfPager(item);
						}
						return;
					}
					if (ConstData.isIndexPager()) {
						clickItemIfPager(item);
						return;
					}
					if (V.isSoloColumn(mContext, item)) {
						return;
					}
					if (V.isParentColumn(mContext, item)) {
						return;
					}
					if (id == 0) {
						if (CommonApplication.manage != null)
							CommonApplication.manage.getProcess()
									.getIndex(true);
					} else {
						if (CommonApplication.manage != null)
							CommonApplication.manage.getProcess().getCatIndex(
									id, true);
					}
				}
			}, 200);
		}
	}

	/**
	 * 首页滑屏，直接定位到具体的栏目
	 * 
	 * @param item
	 */
	private void clickItemIfPager(CatItem item) {
		((ViewsMainActivity) mContext).clickItemIfPager(item.getId());
	}

	/**
	 * iWeekly使用
	 * 
	 * @return
	 */
	public void setAdapter(BaseColumnAdapter adapter) {
		this.adapter = adapter;
		listView.setAdapter(adapter);
	}

	public ColumnParm getParm() {
		return parm;
	}

	@Override
	protected void reLoad() {
	}
}
