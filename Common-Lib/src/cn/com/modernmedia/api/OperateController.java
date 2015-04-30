package cn.com.modernmedia.api;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import cn.com.modernmedia.api.GetTagInfoOperate.TAG_TYPE;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.SubscribeOrderList.SubscribeColumn;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.TagDataHelper;
import cn.com.modernmediaslate.listener.DataCallBack;
import cn.com.modernmediaslate.model.Entry;

/**
 * 接口控制
 * 
 * @author ZhuQiao
 * 
 */
public class OperateController {
	private static OperateController instance;
	private static Context mContext;

	private Handler mHandler = new Handler();

	private OperateController(Context context) {
		mContext = context;
	}

	public static synchronized OperateController getInstance(Context context) {
		mContext = context;
		if (instance == null)
			instance = new OperateController(context);
		return instance;
	}

	private static interface AfterCallBack {
		public void afterCallBack(Entry entry, boolean fromHttp);
	}

	private void doRequest(BaseOperate operate, Entry entry, boolean useCache,
			FetchEntryListener listener) {
		doRequest(operate, entry, useCache, listener, null);
	}

	private void doRequest(BaseOperate operate, final Entry entry,
			boolean useCache, final FetchEntryListener listener,
			final AfterCallBack afterCallBack) {
		operate.asyncRequest(mContext, useCache, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? entry : null, listener, fromHttp,
						afterCallBack);
			}
		});
	}

	private void doPostRequest(BaseOperate operate, Entry entry,
			boolean useCache, FetchEntryListener listener) {
		doPostRequest(operate, entry, useCache, listener, null);
	}

	private void doPostRequest(BaseOperate operate, final Entry entry,
			boolean useCache, final FetchEntryListener listener,
			final AfterCallBack afterCallBack) {
		operate.asyncRequestByPost(mContext, useCache, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? entry : null, listener, fromHttp,
						afterCallBack);
			}
		});
	}

	/**
	 * 返回给ui层
	 * 
	 * @param entry
	 * @param listener
	 * @param fromHttp
	 * @param afterCallBack
	 */
	private void sendMessage(final Entry entry,
			final FetchEntryListener listener, final boolean fromHttp,
			final AfterCallBack afterCallBack) {
		synchronized (mHandler) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					listener.setData(entry);
					if (afterCallBack != null)
						afterCallBack.afterCallBack(entry, fromHttp);
				}
			});
		}
	}

	/**
	 * 获取统计装机量是否成功
	 * 
	 * @param listener
	 */
	public void getDown(FetchEntryListener listener) {
		DownOperate operate = new DownOperate(mContext);
		doRequest(operate, operate.getDown(), false, listener);
	}

	/**
	 * 判断版本号
	 * 
	 * @param listener
	 */
	public void checkVersion(FetchEntryListener listener) {
		CheckVersionOperate operate = new CheckVersionOperate();
		doRequest(operate, operate.getVersion(), false, listener);
	}

	/**
	 * 获取天气预报
	 * 
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 */
	public void getWeather(double longitude, double latitude,
			FetchEntryListener listener) {
		GetWeatherOperate operate = new GetWeatherOperate(longitude, latitude);
		doRequest(operate, operate.getWeather(), false, listener);
	}

	/**
	 * 获取最新未读文章id
	 * 
	 * @param listener
	 */
	public void getLastestArticleIds(String tagName, boolean useLocalData,
			FetchEntryListener listener) {
		GetLastestArticleIdOperate operate = new GetLastestArticleIdOperate(
				mContext, tagName);
		doRequest(operate, operate.getmLastestArticleId(), useLocalData,
				listener);
	}

	/**
	 * 获取广告列表
	 * 
	 * @param useLocal
	 * @param listener
	 */
	public void getAdvList(boolean useLocal, FetchEntryListener listener) {
		GetAdvListOperate operate = new GetAdvListOperate();
		doRequest(operate, operate.getAdvList(), useLocal, listener);
	}

	/**
	 * 获取iWeekly入版广告
	 * 
	 * @param listener
	 */
	// public void getWeeklyInApp(boolean useLocalData, FetchEntryListener
	// listener) {
	// GetInAppAdvOperate operate = new GetInAppAdvOperate();
	// doRequest(operate, operate.getInAppAdv(), useLocalData, listener);
	// }

	/**
	 * 获取应用信息
	 * 
	 * @param listener
	 */
	public void getAppInfo(FetchEntryListener listener) {
		getTagInfo("", "", "1", "", TAG_TYPE.APP_INFO, listener);
	}

	/**
	 * 获取tag信息 默认取缓存，如果应用更新时间变了，会统一清缓存(下拉刷新不使用缓存)
	 * 
	 * @param listener
	 */
	public void getTagInfo(String tagName, boolean useCache,
			FetchEntryListener listener) {
		GetTagInfoOperate operate = new GetTagInfoOperate("", tagName, "", "",
				TAG_TYPE.TAG_INFO);
		doRequest(operate, operate.getTagInfoList(), useCache, listener);
	}

	/**
	 * 获取子栏目信息 默认取缓存，如果应用更新时间变了，会统一清缓存
	 * 
	 * @param listener
	 */
	public void getChildTagInfo(String parentTagName,
			FetchEntryListener listener) {
		GetTagInfoOperate operate = new GetTagInfoOperate(parentTagName, "",
				"", "", TAG_TYPE.CHILD_CAT);
		doRequest(operate, operate.getTagInfoList(), true, listener);
	}

	/**
	 * 
	 * @param parentTagName
	 *            父类标签
	 * @param tagName
	 *            标签
	 * @param group
	 *            分组
	 * @param top
	 * @param type
	 *            是否获取child,tree或者其他
	 * @param listener
	 */
	public void getTagInfo(String parentTagName, String tagName, String group,
			String top, TAG_TYPE type, FetchEntryListener listener) {
		GetTagInfoOperate operate = new GetTagInfoOperate(parentTagName,
				tagName, group, top, type);
		doRequest(operate, operate.getTagInfoList(), false, listener);
	}

	/**
	 * 获取往期列表
	 * 
	 * @param top
	 * @param listener
	 */
	public void getLastIssueList(String top, FetchEntryListener listener) {
		GetLastIssueListOperate operate = new GetLastIssueListOperate(top);
		doRequest(operate, operate.getTagInfoList(), false, listener);
	}

	/**
	 * 获取往期的子栏目
	 * 
	 * @param tagName
	 * @param listener
	 */
	public void getLastIssueCats(String tagName, FetchEntryListener listener) {
		GetTagInfoOperate operate = new GetLastIssueCatsOperate(tagName);
		doRequest(operate, operate.getTagInfoList(), false, listener);
	}

	/**
	 * 获取一个或者多个栏目的文章列表
	 * 
	 * @param tagName
	 *            tag统一标识，可以传多个tagname,用逗号隔开
	 * @param top
	 *            用于分页，取下一页$top=最后一个标签的offset
	 * @param bottom
	 *            用于分页，取上一页$bottom=第一个标签的offset
	 * @param listener
	 */
	public void getTagArticles(final TagInfo tagInfo, String top,
			String limited, TagArticleList articleList,
			FetchEntryListener listener) {
		boolean useCache = !TextUtils.equals(limited, "5")
				&& TextUtils.equals(
						tagInfo.getArticleupdatetime(),
						TagDataHelper.getCatArticleUpdateTime(mContext,
								tagInfo.getTagName()));
		getTagArticles(tagInfo, top, limited, articleList, useCache, listener);
	}

	public void getTagArticles(final TagInfo tagInfo, String top,
			String limited, TagArticleList articleList, boolean useCache,
			FetchEntryListener listener) {
		final GetTagArticlesOperate operate = new GetTagArticlesOperate(
				tagInfo, top, limited, articleList);
		doRequest(operate, operate.getArticleList(), useCache, listener,
				new AfterCallBack() {

					@Override
					public void afterCallBack(Entry entry, boolean fromHttp) {
						if (entry != null && fromHttp) {
							TagDataHelper.setCatArticleUpdateTime(mContext,
									tagInfo.getTagName(),
									tagInfo.getArticleupdatetime());
						}
					}
				});
	}

	/**
	 * 获取往期栏目的文章列表
	 * 
	 * @param tagName
	 *            tag统一标识，可以传多个tagname,用逗号隔开
	 * @param top
	 *            用于分页，取下一页$top=最后一个标签的offset
	 * @param publishTime
	 *            发布时间
	 * 
	 * @param listener
	 */
	public void getLastIssueArticles(String lastIssueTag, String tagName,
			String top, String publishTime, boolean useCache,
			FetchEntryListener listener) {
		GetLastIssueArticlesOperate operate = new GetLastIssueArticlesOperate(
				lastIssueTag, tagName, top, publishTime);
		doRequest(operate, operate.getArticleList(), useCache, listener);
	}

	/**
	 * 获取栏目首页数据
	 * 
	 * @param tagInfo
	 * @param listener
	 */
	public void getTagIndex(final TagInfo tagInfo, String top, String limited,
			TagArticleList articleList, FetchEntryListener listener) {
		GetTagIndexOperate operate = new GetTagIndexOperate(tagInfo, top,
				limited, articleList);
		boolean useCache = !TextUtils.equals(limited, "5")
				&& TextUtils.equals(
						tagInfo.getColoumnupdatetime(),
						TagDataHelper.getCatIndexUpdateTime(mContext,
								tagInfo.getTagName()));
		doRequest(operate, operate.getArticleList(), useCache, listener,
				new AfterCallBack() {

					@Override
					public void afterCallBack(Entry entry, boolean fromHttp) {
						if (entry != null && fromHttp) {
							TagDataHelper.setCatIndexUpdateTime(mContext,
									tagInfo.getTagName(),
									tagInfo.getColoumnupdatetime());
						}
					}
				});
	}

	/**
	 * 取出用户栏目订阅列表
	 * 
	 * @param uid
	 * @param listene
	 */
	public void getSubscribeOrderList(String uid, String token,
			FetchEntryListener listener) {
		GetUserSubscribeListOpertate operate = new GetUserSubscribeListOpertate(
				uid, token);
		doRequest(operate, operate.getSubscribeOrderList(), false, listener);
	}

	/**
	 * 更新用户订阅的栏目列表
	 * 
	 * @param list
	 *            用户订阅的所有栏目列表
	 * @param listener
	 */
	public void saveSubscribeColumnList(String uid, String token,
			List<SubscribeColumn> list, FetchEntryListener listener) {
		SaveUserSubscribeListOpertate operate = new SaveUserSubscribeListOpertate(
				uid, token, list);
		doPostRequest(operate, operate.getError(), false, listener);
	}

	/**
	 * 获取服务器数据
	 * 
	 * @param uid
	 * @param listener
	 */
	public void getFav(String uid, FetchEntryListener listener) {
		final UserGetFavOperate operate = new UserGetFavOperate(uid);
		// doPostRequest(operate, operate.getFavorite(), false, listener);
		doRequest(operate, operate.getFavorite(), false, listener);
	}

	/**
	 * 同步收藏
	 * 
	 */
	public void updateFav(String uid, int appid, List<ArticleItem> list,
			FetchEntryListener listener) {
		if (list == null || list.size() == 0)
			sendMessage(null, listener, false, null);
		final UserUpdateFavOperate operate = new UserUpdateFavOperate(uid,
				appid, list);
		doPostRequest(operate, operate.getFavorite(), false, listener);
	}

	/**
	 * 获取文章详情
	 * 
	 * @param articleId
	 * @param listener
	 */
	public void getArticleDetails(int articleId, FetchEntryListener listener) {
		GetArticleDetailsOperate operate = new GetArticleDetailsOperate(
				articleId);
		doRequest(operate, operate.getArticleList(), false, listener);
	}
}
