package cn.com.modernmedia.views.article;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.adapter.MyPagerAdapter.OnItemClickListener;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.Atlas;
import cn.com.modernmedia.model.Atlas.AtlasPicture;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.views.adapter.AtlasAdapter;
import cn.com.modernmedia.views.model.AtlasParm;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmedia.widget.CommonAtlasView;
import cn.com.modernmediaslate.model.Entry;

/**
 * 图集
 * 
 * @author ZhuQiao
 * 
 */
public abstract class BaseAtlasView extends CommonAtlasView {
	protected Context mContext;
	protected AtlasViewPager pager;
	protected TextView title, desc;
	protected LinearLayout dotLl;
	private List<ImageView> dots = new ArrayList<ImageView>();
	private List<AtlasPicture> list;
	private AtlasAdapter adapter;
	protected AtlasParm atlasParm;
	private NotifyArticleDesListener listener = new NotifyArticleDesListener() {

		@Override
		public void updateDes(int position) {
			if (list == null || list.size() < position + 1)
				return;
			title.setText(list.get(position).getTitle());
			desc.setText(list.get(position).getDesc());
			for (int i = 0; i < dots.size(); i++) {
				if (i == position % list.size()) {
					V.setImage(dots.get(i), atlasParm.getImage_dot_active());
				} else {
					V.setImage(dots.get(i), atlasParm.getImage_dot());
				}
			}
		}

		@Override
		public void updatePage(int state) {
		}
	};

	public BaseAtlasView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public BaseAtlasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	protected void init() {
		atlasParm = ParseProperties.getInstance(mContext).parseAtlas();
	}

	protected void setValuesForWidget(Atlas atlas) {
		list = atlas.getList();
		if (ParseUtil.listNotNull(list)) {
			adapter = new AtlasAdapter(mContext, atlasParm);
			adapter.setData(list);
			adapter.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(View view, int position) {
					UriParse.clickSlate(mContext, list.get(position).getLink(),
							new Entry[] { new ArticleItem() },
							BaseAtlasView.this);
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
				V.setImage(iv, atlasParm.getImage_dot_active());
			} else {
				V.setImage(iv, atlasParm.getImage_dot());
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
