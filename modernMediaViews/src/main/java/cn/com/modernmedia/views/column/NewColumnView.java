package cn.com.modernmedia.views.column;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.views.AboutActivity;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.model.TemplateColumn;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.views.xmlparse.column.FunctionColumn;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.WebViewPop;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 栏目列表页
 * 
 * @author zhuqiao
 * 
 */
public class NewColumnView extends BaseView implements FetchEntryListener {
	private Context mContext;
	private TemplateColumn template;

	private View addColumnFrame;
	private RelativeLayout headFrame;
	private ListView listView;
	private ColumnAdapter adapter;
	private LinearLayout defaultFooter;
	private TextView tip;

	private XMLParse headParse;

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

	public NewColumnView(Context context) {
		this(context, null);
	}

	public NewColumnView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	@SuppressLint("InflateParams")
	private void init() {
		template = ParseProperties.getInstance(mContext).parseColumn();
		addView(LayoutInflater.from(mContext).inflate(R.layout.new_column_view,
				null));
		headFrame = (RelativeLayout) findViewById(R.id.column_head);
		listView = (ListView) findViewById(R.id.column_list);
		addColumnFrame = findViewById(R.id.add_column_frame);
		tip = (TextView) findViewById(R.id.column_tip);
		// iweekly ||verycity
		if (ConstData.getAppId() == 20 || ConstData.getAppId() == 37) {
			((TextView) findViewById(R.id.add_column_text))
					.setTextColor(Color.WHITE);
		}

		V.setViewBack(findViewById(R.id.column_contain),
				template.getBackground());
		initHead();
		initFooter();
		initAdapter();
	}

	/**
	 * 初始化headview
	 */
	private void initHead() {
		headParse = new XMLParse(mContext, null);
		View headView = headParse.inflate(template.getHead().getData(), null,
				"");
		if (template.getHead().getHold() == 1) {
			headFrame.removeAllViews();
			headFrame.addView(headView);
		} else {
			headView.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.FILL_PARENT,
					AbsListView.LayoutParams.WRAP_CONTENT));
			listView.addHeaderView(headView);
		}

		addColumnFrame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((CommonMainActivity) mContext).gotoSelectColumnActivity();
			}
		});
	}

	/**
	 * 初始化adapter
	 */
	private void initAdapter() {
		adapter = new ColumnAdapter(mContext, template);
		listView.setAdapter(adapter);
	}

	@Override
	public void setData(Entry entry) {
		adapter.clear();
		TagInfoList tagInfoList = null;
		if (entry instanceof TagInfoList) {
			tagInfoList = (TagInfoList) entry;
			if (ParseUtil.listNotNull(tagInfoList.getList())) {
				// TODO 添加默认栏目

				// 栏目数据
				// List<TagInfo> currAppTagList = tagInfoList.getColumnTagList(
				// false, false);
				adapter.setData(tagInfoList.getList());
				adapter.setThisAppItemCount(tagInfoList.getList().size());
			}
		}
		if (adapter.getCount() > 0) {
			listView.setSelection(0);
		}
		if (mContext instanceof ViewsMainActivity) {
			if (ViewsApplication.itemIsSelectedListener != null)
				ViewsApplication.itemIsSelectedListener.doIsSelected(false);
		}
		if (AppValue.appInfo.getHaveSubscribe() == 1
				&& SlateApplication.mConfig.getHas_subscribe() == 1) {
			addColumnFrame.setVisibility(View.VISIBLE);
			// TODO 添加订阅栏目，暂时注释
			// if (tagInfoList != null) {
			// adapter.setData(tagInfoList.getColumnTagList(true, true));
			// }
		} else {
			addColumnFrame.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置list的footer
	 * 
	 * @param footerView
	 */
	public void setViewFooter(View footerView) {
		if (footerView != null) {
			listView.removeFooterView(defaultFooter);
			listView.addFooterView(footerView);
			listView.addFooterView(defaultFooter);
		}
	}

	/**
	 * 初始化footerview
	 */
	private void initFooter() {
		defaultFooter = new LinearLayout(mContext);
		defaultFooter.setOrientation(LinearLayout.VERTICAL);
		int paddingTop = 0;
		if (ConstData.getAppId() != 32 && ConstData.getAppId() != 33) {
			paddingTop = mContext.getResources().getDimensionPixelSize(
					R.dimen.dp10);
		}
		defaultFooter.setPadding(0, paddingTop, 0, 0);
		if (!TextUtils.isEmpty(template.getAbout())) {
			View aboutView = getFooterItem(1);
			aboutView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, AboutActivity.class);
					mContext.startActivity(intent);
					((Activity) mContext).overridePendingTransition(
							R.anim.down_in, R.anim.hold);
				}
			});
			defaultFooter.addView(aboutView);
		}
		if (!TextUtils.isEmpty(template.getRecommend())) {
			View recommendView = getFooterItem(2);
			recommendView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new WebViewPop(mContext, template.getRecommend(), false);
				}
			});
			defaultFooter.addView(recommendView);
		}
		defaultFooter.addView(getFooterItem(4));
		listView.addFooterView(defaultFooter);
	}

	private View getFooterItem(int id) {
		ColumnAdapter adapter = new ColumnAdapter(mContext, template);
		TagInfo info = new TagInfo();
		info.setAdapter_id(id);
		List<TagInfo> list = new ArrayList<TagInfo>();
		list.add(info);
		adapter.setData(list);
		return adapter.getView(0, null, null);
	}

	/**
	 * 设置页面宽度
	 * 
	 * @param width
	 */
	public void setViewWidth(int width) {
		if (template.getHead().getNeed_calculate_height() == 0)
			return;
		if (headParse != null
				&& headParse.getMap().containsKey(FunctionColumn.COLUMN_LOG)) {
			View view = headParse.getMap().get(FunctionColumn.COLUMN_LOG);
			view.getLayoutParams().height = (view.getLayoutParams().height * width)
					/ CommonApplication.width;
			// 不需要重新设置view宽度，在MainHorizontalScrollView里已经把columnview的宽度整体设置成可显示的大小了
		}
	}

	public TextView getTip() {
		return tip;
	}

	@Override
	protected void reLoad() {
	}

}
