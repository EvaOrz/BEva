package cn.com.modernmedia.views.fav;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.listener.FavNotifykListener;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.adapter.FavAdapter;
import cn.com.modernmedia.views.model.FavParm;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

/**
 * 收藏列表view
 * 
 * @author user
 * 
 */
public class FavView extends BaseView implements FetchEntryListener {
	private Context mContext;
	private View titleBar;
	private ImageView top, divider;
	private ListView listView;
	private FavAdapter adapter;
	private FavDb db;
	private FavParm parm;

	public FavView(Context context) {
		this(context, null);
	}

	public FavView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		db = FavDb.getInstance(mContext);
		init();
	}

	private void init() {
		CommonApplication.setFavListener(new FavNotifykListener() {

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
		top = (ImageView) findViewById(R.id.fav_top_image);
		divider = (ImageView) findViewById(R.id.fav_head_shadow);

		initRes();
		adapter = new FavAdapter(mContext, parm);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				position = position - listView.getHeaderViewsCount();
				if (position < 0 || adapter.getCount() <= position)
					return;
				FavoriteItem item = adapter.getItem(position);
				if (mContext instanceof CommonMainActivity) {
					LogHelper.logOpenArticleFromFavoriteArticleList(mContext,
							item.getId() + "", item.getCatid() + "");
					((CommonMainActivity) mContext)
							.gotoArticleActivity(new TransferArticle(item
									.getId(), item.getCatid(), -1,
									ArticleType.Fav));
				}
			}
		});
		setData(null);
	}

	/**
	 * 初始化资源
	 */
	private void initRes() {
		parm = ParseProperties.getInstance(mContext).parseFav();
		titleBar = findViewById(R.id.fav_titlebar);
		titleBar.getLayoutParams().height = CommonApplication.width * 88 / 640;
		V.setImage(titleBar, parm.getBar_background());
		V.setImage(findViewById(R.id.fav_contain), parm.getBackground());
		if (!TextUtils.isEmpty(parm.getTitle_color())) {
			((TextView) findViewById(R.id.fav_title)).setTextColor(Color
					.parseColor(parm.getTitle_color()));
		}
		if (!TextUtils.isEmpty(parm.getDivider())) {
			V.setListviewDivider(mContext, listView, parm.getDivider());
		}
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
		if (!TextUtils.isEmpty(parm.getDivider())) {
			V.setListviewDivider(mContext, listView, parm.getDivider());
		}
	}

	@Override
	public void setData(Entry entry) {
		adapter.clear();
		List<FavoriteItem> list = db.getUserFav(ConstData.UN_UPLOAD_UID, true);
		if (ParseUtil.listNotNull(list)) {
			adapter.setData(list);
		}
	}

	@Override
	protected void reLoad() {
	}

}
