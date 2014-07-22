package cn.com.modernmedia.businessweek.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.adapter.AtlasAdapter;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.ArticleList.ArticleDetail;
import cn.com.modernmedia.model.Atlas;
import cn.com.modernmedia.model.Atlas.AtlasPicture;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmedia.widget.CommonAtlasView;

/**
 * Í¼¼¯
 * 
 * @author ZhuQiao
 * 
 */
public class AtlasView extends CommonAtlasView {
	private Context mContext;
	private AtlasViewPager pager;
	private TextView title, desc;
	private LinearLayout dotLl;
	private OperateController controller;
	private List<ImageView> dots = new ArrayList<ImageView>();
	private ArticleDetail detail;
	private Issue issue;
	private List<AtlasPicture> list;
	private AtlasAdapter adapter;
	private NotifyArticleDesListener listener = new NotifyArticleDesListener() {

		@Override
		public void updateDes(int position) {
			if (list == null || list.size() < position + 1)
				return;
			title.setText(list.get(position).getTitle());
			desc.setText(list.get(position).getDesc());
			for (int i = 0; i < dots.size(); i++) {
				if (i == position % list.size()) {
					dots.get(i).setBackgroundResource(R.drawable.dot_active);
				} else {
					dots.get(i).setBackgroundResource(R.drawable.dot);
				}
			}
		}

		@Override
		public void updatePage(int state) {
		}
	};

	public AtlasView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public AtlasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		controller = new OperateController(mContext);
		this.addView(LayoutInflater.from(mContext)
				.inflate(R.layout.atlas, null));
		title = (TextView) findViewById(R.id.atlas_title);
		desc = (TextView) findViewById(R.id.atlas_desc);
		this.setBackgroundColor(Color.WHITE);
		initProcess();
		pager = (AtlasViewPager) findViewById(R.id.atlas_gallery);
		pager.getLayoutParams().height = MyApplication.width * 234 / 320;
		dotLl = (LinearLayout) findViewById(R.id.atlas_gallery_dot);
	}

	public void setData(ArticleDetail detail, Issue issue) {
		if (detail == null || issue == null)
			return;
		this.detail = detail;
		this.issue = issue;
		addLoadok(detail);
		controller.getArticleById(issue, detail.getCatId() + "",
				detail.getArticleId() + "", new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						post(new Runnable() {

							@Override
							public void run() {
								if (entry != null && entry instanceof Atlas) {
									setValuesForWidget((Atlas) entry);
									disProcess();
								} else {
									showError();
								}
							}
						});
					}
				});
	}

	private void setValuesForWidget(Atlas atlas) {
		list = atlas.getList();
		if (list == null || list.isEmpty())
			return;
		adapter = new AtlasAdapter(mContext);
		adapter.setData(list);
		pager.setAdapter(adapter);
		pager.setValue(list.size());
		pager.setListener(listener);
		initDot(list);
	}

	/**
	 * ³õÊ¼»¯dot
	 * 
	 * @param list
	 */
	private void initDot(List<AtlasPicture> list) {
		dotLl.removeAllViews();
		dots.clear();
		if (list.size() == 1)
			return;
		ImageView iv;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = 5;
		for (int i = 0; i < list.size(); i++) {
			iv = new ImageView(mContext);
			if (i == 0) {
				iv.setBackgroundResource(R.drawable.dot_active);
			} else {
				iv.setBackgroundResource(R.drawable.dot);
			}
			dotLl.addView(iv, lp);
			dots.add(iv);
		}
	}

	@Override
	protected void reLoad() {
		setData(detail, issue);
	}

	@Override
	public AtlasViewPager getAtlasViewPager() {
		return pager;
	}

}
