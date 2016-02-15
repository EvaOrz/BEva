package cn.com.modernmedia.newtag.mainprocess;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import cn.com.modernmedia.R;
import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.MainProcessParseCallBack;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.TimeCollectUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * http入版流程
 * 
 * @author zhuqiao
 * 
 */
public class TagMainProcessHttp extends BaseTagMainProcess {
	private static final int FETCH_APP_INFO_DELAY = 300;

	private FetchApiType apiType = FetchApiType.USE_HTTP_FIRST;
	/**
	 * 获取appinfo丢失次数
	 */
	private int fetchAppInfoMiss;

	private Handler handler = new Handler();

	public TagMainProcessHttp(Context context, MainProcessParseCallBack callBack) {
		super(context, callBack);
	}

	@Override
	public void onStart(Object... objs) {
		fetchAppInfoMiss = 0;
		if (Tools.checkNetWork(mContext)) {
			getAppInfo(FetchApiType.USE_HTTP_ONLY);
		} else {
			// NOTE 结束流程
			Tools.showToast(mContext, R.string.net_error);
			toEnd(false);
		}
	}

	@Override
	protected void doAfterFecthAppInfo(TagInfoList appInfo, boolean success) {
		if (success) {
			super.doAfterFecthAppInfo(appInfo, success);

			// 下载广告
			if (TextUtils.equals(DataHelper.getAdvUpdateTime(mContext),
					AppValue.appInfo.getAdvUpdateTime())
					&& ConstData.IS_DEBUG != 8) {
				// 广告更新时间未变化
				getAdvList(FetchApiType.USE_CACHE_FIRST);
			} else {
				getAdvList(FetchApiType.USE_HTTP_ONLY);
			}
		} else if (fetchAppInfoMiss < 3) {
			fetchAppInfoMiss++;
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					getAppInfo(FetchApiType.USE_HTTP_ONLY);
				}
			}, FETCH_APP_INFO_DELAY);
		} else {
			// NOTE 请求appinfo失败，结束流程
			PrintHelper.print("=======get appinfo from http failed!======");
			toEnd(false);
		}
	}

	@Override
	protected void doAfterFetchAdvList(AdvList advList, boolean success) {
		super.doAfterFetchAdvList(advList, success);
		if (success) {
			DataHelper.setAdvUpdateTime(mContext,
					AppValue.appInfo.getAdvUpdateTime());
		}
		// NOTE 不管广告是否获取成功，都继续往下请求
		if (TextUtils.equals(DataHelper.getAppUpdateTime(mContext),
				AppValue.appInfo.getUpdatetime())) {
			// 应用更新时间没有变化
			apiType = FetchApiType.USE_CACHE_FIRST;
		} else {
			DataHelper.setAppUpdateTime(mContext,
					AppValue.appInfo.getUpdatetime());
			apiType = FetchApiType.USE_HTTP_ONLY;
		}

		checkAfterFetchAdvList(apiType);
	}

	@Override
	protected void doAfterFecthShiye(TagArticleList articleList, boolean success) {
		super.doAfterFecthShiye(articleList, success);

		// NOTE 不管视野是否获取成功，都继续往下请求(不然栏目列表之类的会没有地方去线上请求)
		getCatList(apiType);
	}

	@Override
	protected void doAfterFecthCatList(TagInfoList catList, boolean success,
			FetchApiType type) {
		if (success)
			super.doAfterFecthCatList(catList, success, apiType);
		else
			toEnd(false);
	}

	@Override
	protected void fetchFirstCatData() {
		if (ConstData.getInitialAppId() == 20)
			checkFirstItem();
	}

	private void checkFirstItem() {
		List<TagInfo> catItems = new ArrayList<TagInfo>();
		catItems.addAll(AppValue.ensubscriptColumnList.getColumnTagList(false,
				false));
		catItems.addAll(AppValue.ensubscriptColumnList.getColumnTagList(true,
				false));
		if (ConstData.getInitialAppId() == 20 && catItems.size() > 0)
			catItems.remove(0);
		for (TagInfo tagInfo : catItems) {
			if (tagInfo.getHasSubscribe() == 1) {
				// TODO 获取第一个已订阅栏目
				if (!tagInfo.showChildren()) {
					getArticleList(tagInfo);
					return;
				}
			}
		}
	}

	private void getArticleList(final TagInfo tagInfo) {
		if (tagInfo == null) {
			fetchFirstCatEnd(false);
			return;
		}
		TimeCollectUtil.getInstance().addCollectUrl(
				UrlMaker.getArticlesByTag(tagInfo, "", ""));
		mController.getTagArticles(tagInfo, "", "", null,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagArticleList) {
							TagArticleList articleList = (TagArticleList) entry;
							if (ParseUtil.listNotNull(articleList
									.getArticleList())) {
								getCatIndex(tagInfo);
								return;
							}
						}
						fetchFirstCatEnd(false);
					}
				});
	}

	/**
	 * 获取首页
	 */
	private void getCatIndex(TagInfo tagInfo) {
		TimeCollectUtil.getInstance().addCollectUrl(
				UrlMaker.getTagCatIndex(tagInfo, "", ""));
		mController.getTagIndex(tagInfo, "", "", null,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagArticleList) {
							TagArticleList articleList = (TagArticleList) entry;
							if (!articleList.getMap().isEmpty()) {
								fetchFirstCatEnd(true);
								return;
							}
						}
						fetchFirstCatEnd(false);
					}
				});
	}

	private void fetchFirstCatEnd(boolean success) {

	}

	@Override
	protected void toEnd(boolean success) {
		mState.isEnd = true;
		mState.isSuccess = success;
		if (callBack != null) {
			callBack.afterFetchHttp(success);
		}
	}

}
