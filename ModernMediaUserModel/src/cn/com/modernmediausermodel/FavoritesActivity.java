package cn.com.modernmediausermodel;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.PageTransfer;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediausermodel.adapter.FavAdadper;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserDataHelper;

/**
 * 收藏页
 * 
 * @author user
 * 
 */
public class FavoritesActivity extends BaseActivity {
	public static final String ISSUE_KEY = "issue";

	private Context mContext;
	private ImageView back;
	protected ListView listView;
	protected FavAdadper adapter;
	private FavDb db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_favorites);
		mContext = this;
		db = FavDb.getInstance(this);
		init();
	}

	private void init() {
		back = (ImageView) findViewById(R.id.favorites_button_back);
		listView = (ListView) findViewById(R.id.favorites_list);
		adapter = new FavAdadper(this);
		listView.setAdapter(adapter);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setResult(RESULT_OK);
				finish();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (adapter.getCount() > position) {
					FavoriteItem detail = adapter.getItem(position);
					LogHelper.logOpenArticleFromFavoriteArticleList(mContext,
							detail.getId() + "", detail.getCatid() + "");
					// 取得当前用户ID。若未取得，默认为‘0’
					User user = UserDataHelper.getUserLoginInfo(mContext);
					String uid = user == null ? ConstData.UN_UPLOAD_UID : user
							.getUid();
					TransferArticle article = new TransferArticle(detail
							.getId(), detail.getCatid(), ArticleType.Fav, uid,
							null);
					gotoArticleActivity(article);
				}
			}
		});
	}

	private void gotoArticleActivity(TransferArticle transferArticle) {
		if (UserConstData.getArticleClass() == null) {
			return;
		}
		PageTransfer.gotoArticleActivity(this, UserConstData.getArticleClass(),
				transferArticle);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 每次进入都重新取得数据
		getData();
	}

	/**
	 * 取得收藏夹数据
	 */
	public void getData() {
		adapter.clear();
		User user = UserDataHelper.getUserLoginInfo(this);
		String uid = user == null ? ConstData.UN_UPLOAD_UID : user.getUid();
		List<FavoriteItem> list = db.getUserFav(uid, true);
		if (ParseUtil.listNotNull(list)) {
			adapter.setData(list);
		}
	}

	@Override
	public void reLoadData() {
	}

	@Override
	public String getActivityName() {
		return FavoritesActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
