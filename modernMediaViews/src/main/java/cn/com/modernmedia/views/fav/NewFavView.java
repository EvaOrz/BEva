package cn.com.modernmedia.views.fav;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.listener.FavNotifyListener;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.adapter.FavAdapter;
import cn.com.modernmedia.views.model.TemplateFav;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 收藏列表view
 * 
 * @author user
 * 
 */
public class NewFavView extends BaseView implements FetchEntryListener {
	private Context mContext;
	private RelativeLayout navBar;
	private ListView listView;
	private FavAdapter adapter;
	private TemplateFav template;

	public NewFavView(Context context) {
		this(context, null);
	}

	public NewFavView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		CommonApplication.setFavListener(new FavNotifyListener() {

			@Override
			public void refreshFav() {
				setData(null);
			}

		});
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		addView(LayoutInflater.from(mContext).inflate(R.layout.fav_view, null),
				lp);
		listView = (ListView) findViewById(R.id.fav_list);
		navBar = (RelativeLayout) findViewById(R.id.fav_nav_bar);
		initRes();
		adapter = new FavAdapter(mContext, template);
		listView.setAdapter(adapter);
		setData(null);
	}

	/**
	 * 初始化资源
	 */
	private void initRes() {
		template = ParseProperties.getInstance(mContext).parseFav();
		XMLParse xmlParse = new XMLParse(mContext, null);
		View view = xmlParse
				.inflate(template.getNavBar().getData(), null, null);
		xmlParse.setDataForFavNavbar();
		navBar.addView(view);
		// 设置整个view的背景色
		V.setImage(this, template.getBackground());
	}

	@Override
	public void setData(Entry entry) {
		adapter.clear();
		List<ArticleItem> list = NewFavDb.getInstance(mContext).getUserFav(
				Tools.getUid(mContext));
		if (ParseUtil.listNotNull(list)) {
			adapter.setData(list);
		}
	}

	@Override
	public void reLoad() {
		setData(null);
	}

}
