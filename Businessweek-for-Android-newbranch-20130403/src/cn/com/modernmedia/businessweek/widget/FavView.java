package cn.com.modernmedia.businessweek.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ListView;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.adapter.FavAdadper;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.listener.FavNotifykListener;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;

/**
 * 首页收藏页
 * 
 * @author ZhuQiao
 * 
 */
public class FavView extends BaseView implements FetchEntryListener {
	private Context mContext;
	private ListView listView;
	private FavAdadper adapter;
	private FavDb db;

	public FavView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		db = FavDb.getInstance(mContext);
		init();
	}

	private void init() {
		MyApplication.setFavListener(new FavNotifykListener() {

			@Override
			public void refreshFav() {
				setData(null);
			}

		});
//		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
//				LayoutParams.FILL_PARENT);
//		this.addView(LayoutInflater.from(mContext).inflate(R.layout.fav, null),
//				lp);
//		listView = (ListView) findViewById(R.id.fav_list);
//		adapter = new FavAdadper(mContext);
//		listView.setAdapter(adapter);
//		((MainActivity) mContext).doAfterLogin();
	}

	@Override
	public void setData(Entry entry) {
		adapter.clear();
		User user = UserDataHelper.getUserLoginInfo(mContext);
		String uid = user == null ? ConstData.UN_UPLOAD_UID : user.getUid();
		List<FavoriteItem> list = db.getUserFav(uid, true);
		if (ParseUtil.listNotNull(list)) {
			adapter.setData(list);
		}
	}

	@Override
	protected void reLoad() {
	}
}
