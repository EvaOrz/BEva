package cn.com.modernmedia.businessweek.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.adapter.MyPagerAdapter.OnItemClickListener;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.adapter.AtlasAdapter;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.Atlas;
import cn.com.modernmedia.model.Atlas.AtlasPicture;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediasolo.widget.SoloAltasView;

/**
 * 图集
 * 
 * @author ZhuQiao
 * 
 */
public class AtlasView extends SoloAltasView {
	private Context mContext;
	private AtlasViewPager pager;
	private TextView title, desc;
	private LinearLayout dotLl;
	private List<ImageView> dots = new ArrayList<ImageView>();
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

	protected void setValuesForWidget(Atlas atlas) {
		list = atlas.getList();
		if (ParseUtil.listNotNull(list)) {
			adapter = new AtlasAdapter(mContext);
			adapter.setData(list);
			adapter.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(View view, int position) {
					UriParse.clickSlate(mContext, list.get(position).getLink(),
							new Entry[] { new ArticleItem() }, AtlasView.this);
				}
			});
			pager.setAdapter(adapter);
			pager.setValue(list.size());
			pager.setListener(listener);
			initDot(list);
		}
	}

	/**
	 * 初始化dot
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
		super.reLoad();
	}

	@Override
	public AtlasViewPager getAtlasViewPager() {
		return pager;
	}

}
