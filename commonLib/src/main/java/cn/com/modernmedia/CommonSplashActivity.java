package cn.com.modernmedia;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.Down;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.SplashCallback;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.FavObservable;
import cn.com.modernmediaslate.unit.ParseUtil;

import com.parse.ParseAnalytics;
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
	protected AdvList advList;
	private Uri fromHtmlArticleUri;// 网页跳转文章页面url参数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ParseAnalytics.trackAppOpened(getIntent());
		TagProcessManage.getInstance(this).onStart(getIntent(),
				new SplashCallback() {

					@Override
					public void onParseMsgAnalyed(boolean isPush,
							String pushArticleUrl, int level,
							boolean isAppRunning) {
						CommonSplashActivity.this.onParseMsgAnalyed(isPush,
								pushArticleUrl, level, isAppRunning);
					}

				});
		/**
		 * 网页跳转测试用
		 */
		Intent i_getvalue = getIntent();
		String action = i_getvalue.getAction();
		if (Intent.ACTION_VIEW.equals(action)) {
			Uri uri = i_getvalue.getData();
			fromHtmlArticleUri = uri;
		}
	}

	/**
	 * parse消息分析结束
	 */
	private void onParseMsgAnalyed(boolean isPush, String pushArticleUrl,
			int level, boolean isAppRunning) {
		if (!isPush || !isAppRunning) {
			// NOTE 不是push或者应用不在运行中,那么照常运行
			// NOTE 如果是push但是应用不在运行,那么之后的cache流程会去验证是否需要弹出push文章
			init();
			TagProcessManage.getInstance(this).fetchFromHttp();
			return;
		}

		if (TextUtils.isEmpty(pushArticleUrl)) {
			// NOTE 如果应用运行中并且不需要显示push文章,那么关闭splash页面
			finish();
			return;
		}

		// NOTE 应用运行中并且需要显示push文章
		setContentView(R.layout.layout_splash_transparent);
		TagProcessManage.getInstance(this).showPushArticleActivity(this,
				pushArticleUrl, level);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(SlateApplication.mConfig.getUmeng_key())) {
			MobclickAgent.onResume(this);
		}
		if (fromHtmlArticleUri != null)
			gotoMainActivity();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!TextUtils.isEmpty(SlateApplication.mConfig.getUmeng_key())) {
			MobclickAgent.onPause(this);
		}
	}

	private void init() {
		setContentViewById();

		CommonApplication.clear();
		down();
		getAdvList();
		// 旧收藏数据迁移
		if (!DataHelper.getDbChanged(mContext)) {
			NewFavDb.getInstance(this).dataTransfer();
			DataHelper.setDbChanged(mContext);
			// 迁移服务器上的数据
			SlateApplication.favObservable.setData(FavObservable.DATA_CHANGE);
		}
	}

	protected abstract void setContentViewById();

	private void initAdv() {
		if (advList == null
				|| !ParseUtil.mapContainsKey(advList.getAdvMap(),
						AdvList.RU_BAN)) {// 取缓存
			checkHasAdv(null, null);
		} else {// 线上拿
			List<AdvItem> list = advList.getAdvMap().get(AdvList.RU_BAN);
			if (ParseUtil.listNotNull(list)) {
				for (AdvItem item : list) {
					if (checkPicAdvValid(item))
						return;
				}
			}
			checkHasAdv(null, null);
		}
	}

	/**
	 * 判断图片广告是否有效
	 * 
	 * @param item
	 * @return
	 */
	private boolean checkPicAdvValid(AdvItem item) {
		if (!advIsValid(item))
			return false;
		if (item.getShowType() != 0)// 不是图片广告
			return false;

		ArrayList<String> picList = new ArrayList<String>();
		for (AdvSource pic : item.getSourceList()) {
			// NOTE 当所有图片都下载成功时，才进入入版广告页
			if (CommonApplication.finalBitmap.getBitmapFromDiskCache(pic
					.getUrl()) == null) {
				return false;
			}
			picList.add(pic.getUrl());
		}
		checkHasAdv(picList, item);
		return true;
	}

	/**
	 * 广告是否有效
	 * 
	 * @param item
	 * @return
	 */
	protected boolean advIsValid(AdvItem item) {
		// 广告是否过期
		if (AdvTools.advIsExpired(item.getStartTime(), item.getEndTime()))
			return false;
		// 是否有资源列表
		if (!ParseUtil.listNotNull(item.getSourceList()))
			return false;
		return true;
	}

	protected void checkHasAdv(final ArrayList<String> picList,
			final AdvItem item) {
		if (ConstData.getInitialAppId() == 20) {
			if (ParseUtil.listNotNull(picList))
				gotoAdvActivity(picList, item);
			return;
		}
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (ParseUtil.listNotNull(picList))
					gotoAdvActivity(picList, item);
				else if (fromHtmlArticleUri != null)
					UriParse.clickSlate(CommonSplashActivity.this,
							fromHtmlArticleUri.toString(),
							new Entry[] { new ArticleItem() }, null,
							new Class<?>[0]);
				else
					gotoMainActivity();
			}
		}, ConstData.SPLASH_DELAY_TIME);
	}

	private void getAdvList() {
		OperateController.getInstance(mContext).getAdvList(
				FetchApiType.USE_CACHE_ONLY, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof AdvList) {
							advList = (AdvList) entry;
						}
						initAdv();
					}
				});
	}

	protected void gotoMainActivity() {
		if (CommonApplication.mainCls == null)
			finish();
		Intent intent = new Intent(mContext, CommonApplication.mainCls);
		intent.putExtra(GenericConstant.FROM_ACTIVITY,
				GenericConstant.FROM_ACTIVITY_VALUE);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.alpha_out, R.anim.hold);
	};

	protected void gotoAdvActivity(ArrayList<String> picList, AdvItem item) {
		Intent intent = new Intent(mContext, CommonAdvActivity.class);
		intent.putExtra(GenericConstant.FROM_ACTIVITY,
				GenericConstant.FROM_ACTIVITY_VALUE);
		intent.putExtra(GenericConstant.PIC_LIST, picList);
		intent.putExtra(GenericConstant.ADV_ITEM, item);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.alpha_out, R.anim.hold);
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

	@Override
	public void reLoadData() {
	}

}
