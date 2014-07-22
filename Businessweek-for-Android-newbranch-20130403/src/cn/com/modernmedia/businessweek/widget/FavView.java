package cn.com.modernmedia.businessweek.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.adapter.FavAdadper;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.listener.FavNotifykListener;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.model.Favorite;

/**
 * Ê×Ò³ÊÕ²ØÒ³
 * 
 * @author ZhuQiao
 * 
 */
public class FavView extends LinearLayout implements FetchEntryListener {
	private Context mContext;
	private LinearLayout linearLayout;
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
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		this.addView(LayoutInflater.from(mContext).inflate(R.layout.fav, null),
				lp);
		linearLayout = (LinearLayout) findViewById(R.id.fav_list);
		adapter = new FavAdadper(mContext);
		setData(null);
	}

	@Override
	public void setData(Entry entry) {
		adapter.clear();
		Favorite favorite = db.getAllFav();
		if (favorite != null) {
			if (favorite != null && favorite.getList() != null) {
				adapter.setData(favorite.getList());
			}
			linearLayout.removeAllViews();
			for (int i = 0; i < adapter.getCount(); i++) {
				linearLayout.addView(adapter.getView(i, null, null));
			}
		}
	}

}
