package cn.com.modernmedia;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.Down;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.FavObservable;
import cn.com.modernmediaslate.unit.ParseUtil;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * 进版首页
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonSplashActivity extends BaseActivity {
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(SlateApplication.mConfig.getUmeng_key())) {
			MobclickAgent.onResume(this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!TextUtils.isEmpty(SlateApplication.mConfig.getUmeng_key())) {
			MobclickAgent.onPause(this);
		}
	}

	protected void init() {
		setContentViewById();
		mContext = this;
		CommonApplication.clear();
		down();
		if (ConstData.getInitialAppId() == 20) {
			// TODO iweekly
			checkHasAdv(false, null, null, null);
		} else {
			getAdvList();
		}
		// 旧收藏数据迁移
		if (!DataHelper.getDbChanged(mContext)) {
			NewFavDb.getInstance(this).dataTransfer();
			DataHelper.setDbChanged(mContext);
			// 迁移服务器上的数据
			SlateApplication.favObservable.setData(FavObservable.DATA_CHANGE);
		}
	}

	protected abstract void setContentViewById();

	protected void gotoMainActivity() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (CommonApplication.mainCls == null)
					finish();
				Intent intent = new Intent(mContext, CommonApplication.mainCls);
				intent.putExtra(GenericConstant.FROM_ACTIVITY,
						GenericConstant.FROM_ACTIVITY_VALUE);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.alpha_out, R.anim.hold);
			}
		}, ConstData.SPLASH_DELAY_TIME);
	};

	protected void gotoAdvActivity(final ArrayList<String> picList,
			final String effects, final String impressionUrl) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(mContext, CommonAdvActivity.class);
				intent.putExtra(GenericConstant.FROM_ACTIVITY,
						GenericConstant.FROM_ACTIVITY_VALUE);
				intent.putExtra(GenericConstant.PIC_LIST, picList);
				intent.putExtra(GenericConstant.EFFECTS, effects);
				intent.putExtra(GenericConstant.IMPRESSION_URL, impressionUrl);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.alpha_out, R.anim.hold);
			}
		}, ConstData.SPLASH_DELAY_TIME);
	};

	/**
	 * 统计装机量
	 */
	private void down() {
		// 初始化友盟
		if (!TextUtils.isEmpty(SlateApplication.mConfig.getUmeng_key())) {
			AnalyticsConfig.setAppkey(SlateApplication.mConfig.getUmeng_key());
			// MobclickAgent.setDebugMode(true);
		}

		if (DataHelper.getDown(this))
			return;
		OperateController.getInstance(this).getDown(new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof Down) {
					Down down = (Down) entry;
					if (down.isSuccess()) {
						DataHelper.setDown(mContext);
					}
				}
			}
		});
	}

	private void init(AdvList advList) {
		if (advList == null
				|| !ParseUtil.mapContainsKey(advList.getAdvMap(),
						AdvList.RU_BAN)) {
			checkHasAdv(false, null, null, null);
		} else {
			ArrayList<String> picList = new ArrayList<String>();
			List<AdvItem> list = advList.getAdvMap().get(AdvList.RU_BAN);
			String effects = "";
			String impressionUrl = "";
			if (ParseUtil.listNotNull(list)) {
				for (AdvItem item : list) {
					// TODO 广告属于当前期并且广告没有过期,不考虑issueId
					if (!AdvTools.advIsExpired(item.getStartTime(),
							item.getEndTime())) {
						for (AdvSource pic : item.getSourceList()) {
							// TODO 当所有图片都下载成功时，才进入入版广告页
							if (CommonApplication.finalBitmap
									.getBitmapFromDiskCache(pic.getUrl()) == null) {
								continue;
							}
							picList.add(pic.getUrl());
						}
						effects = item.getEffects();
						impressionUrl = item.getTracker().getImpressionUrl();
					}
				}
			}
			checkHasAdv(!picList.isEmpty(), picList, effects, impressionUrl);
		}
	}

	protected void checkHasAdv(boolean hasAdv, ArrayList<String> picList,
			String effects, String impressionUrl) {
		if (hasAdv && ParseUtil.listNotNull(picList))
			gotoAdvActivity(picList, effects, impressionUrl);
		else
			gotoMainActivity();
	}

	private void getAdvList() {
		OperateController.getInstance(mContext).getAdvList(true,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof AdvList) {
							init((AdvList) entry);
						} else
							init(null);
					}
				});
	}

	@Override
	public void reLoadData() {
	}

}
