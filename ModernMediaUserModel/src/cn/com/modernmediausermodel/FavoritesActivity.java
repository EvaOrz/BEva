package cn.com.modernmediausermodel;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediausermodel.adapter.FavAdadper;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;

/**
 * 收藏页
 * 
 * @author user
 * 
 */
public class FavoritesActivity extends BaseActivity {
	public static final String ISSUE_KEY = "issue";

	private ImageView back;
	private ListView listView;
	private FavAdadper adapter;
	private FavDb db;
	private Issue issue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_favorites);
		if (getIntent() != null && getIntent().getExtras() != null) {
			if (getIntent().getExtras().getSerializable(ISSUE_KEY) instanceof Issue)
				issue = (Issue) getIntent().getExtras().getSerializable(
						ISSUE_KEY);
		}
		db = FavDb.getInstance(this);
		init();
	}

	private void init() {
		back = (ImageView) findViewById(R.id.favorites_button_back);
		listView = (ListView) findViewById(R.id.favorites_list);
		adapter = new FavAdadper(this);
		adapter.setIssue(issue);
		listView.setAdapter(adapter);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
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
