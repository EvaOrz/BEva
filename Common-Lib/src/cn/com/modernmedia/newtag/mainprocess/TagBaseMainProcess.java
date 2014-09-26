package cn.com.modernmedia.newtag.mainprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.api.GetTagInfoOperate.TAG_TYPE;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.LastestArticleId;
import cn.com.modernmedia.model.SubscribeOrderList;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagArticleListDb;
import cn.com.modernmedia.newtag.db.TagIndexDb;
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.newtag.mainprocess.MainProcessObservable.ObserverItem;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.MainProcessParseCallBack;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.EnsubscriptHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.UpdateManager;
import cn.com.modernmedia.util.UpdateManager.CheckVersionListener;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 主页流程基类
 * 
 * @author zhuqiao
 * 
 */
public class TagBaseMainProcess {
	protected Context mContext;
	protected OperateController mController;
	protected MainProcessParseCallBack callBack;

	public static boolean isFirstIn = true;
	/**
	 * 1.应用信息;2.栏目列表;3.栏目首页;4.子栏目列表;5.获取订阅列表;6.获取广告;7.获取文章列表
	 */
	protected int errorType;
	protected TagInfo currTagInfo;

	public TagBaseMainProcess(Context context, MainProcessParseCallBack callBack) {
		mContext = context;
		mController = OperateController.getInstance(mContext);
		this.callBack = callBack;
	}

	/**
	 * 更新版本
	 */
	public void checkVersion() {
		showLoad(true);
		if (ConstData.getInitialAppId() == 1
				&& CommonApplication.CHANNEL.equals("googleplay")) {
			getAdvList();
			return;
		}
		UpdateManager manager = new UpdateManager(mContext,
				new CheckVersionListener() {

					@Override
					public void checkEnd() {
					}
				});
		manager.checkVersion();
		getAdvList();
	}

	/**
	 * 获取广告列表
	 */
	public void getAdvList() {
		errorType = 6;
	}

	/**
	 * 获取应用信息
	 */
	public void getAppInfo() {
		errorType = 1;
		mController.getAppInfo(new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof TagInfoList) {
					TagInfoList appInfo = (TagInfoList) entry;
					if (ParseUtil.listNotNull(appInfo.getList())) {
						doAfterFecthAppInfo(appInfo, true);
						return;
					}
				}
				doAfterFecthAppInfo(null, false);
			}
		});
	}

	/**
	 * 获取完应用信息
	 * 
	 * @param appInfo
	 * @param success
	 *            是否成功
	 */
	protected void doAfterFecthAppInfo(TagInfoList appInfo, boolean success) {
		if (success) {
			AppValue.appInfo = appInfo.getList().get(0).getAppProperty();
			AppValue.appInfo.setTagName(appInfo.getList().get(0).getTagName());
			if (!TextUtils.equals(DataHelper.getAppUpdateTime(mContext),
					AppValue.appInfo.getUpdatetime())) {
				clearCacheWhenUpdatetimeChange();
			}
			getCatList();
		}
	}

	/**
	 * 如果应用更新时间变了，那么清楚特定的缓存
	 */
	protected void clearCacheWhenUpdatetimeChange() {
		TagIndexDb.getInstance(mContext).clearSubscribeTopArticle();
		TagArticleListDb.getInstance(mContext).clearSubscribeTopArticle();
		TagInfoListDb.getInstance(mContext).clearTable("", "", "",
				TAG_TYPE.TAG_INFO);
		TagInfoListDb.getInstance(mContext).clearTable("", "", "",
				TAG_TYPE.CHILD_CAT);
	}

	/**
	 * 获取栏目列表
	 */
	protected void getCatList() {
		errorType = 2;
		mController.getTagInfo(AppValue.appInfo.getTagName(), "", "3", "",
				TAG_TYPE.TREE_CAT, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagInfoList) {
							TagInfoList catList = (TagInfoList) entry;
							if (ParseUtil.listNotNull(catList.getList())) {
								doAfterFecthCatList(catList, true);
								return;
							}
						}
						doAfterFecthCatList(null, false);
					}
				});
	}

	/**
	 * 获取完栏目列表
	 * 
	 * @param catList
	 * @param success
	 */
	protected void doAfterFecthCatList(TagInfoList catList, boolean success) {
		if (success) {
			AppValue.defaultColumnList = catList;
			// iweekly或者支持订阅的应用需先获取iweekly的最新文章
			if (ConstData.getInitialAppId() == 20) {
				getLastestArticle();
			} else {
				getSubscribeList();
			}
		}
	}

	/**
	 * 获取用户订阅列表
	 */
	public void getSubscribeList() {
		String uid = ((CommonMainActivity) mContext).getUid();
		String token = ((CommonMainActivity) mContext).getToken();
		if (TextUtils.equals(uid, SlateApplication.UN_UPLOAD_UID)
				|| AppValue.appInfo.getHaveSubscribe() == 0
				|| SlateApplication.mConfig.getHas_subscribe() == 0) {
			// TODO 未登录或者不支持订阅
			doAfterFetchSubscribeList(new SubscribeOrderList(), true, uid,
					token);
		} else {
			getSubscribeOrderList(uid, token);
		}
	}

	protected void getSubscribeOrderList(final String uid, final String token) {
		errorType = 5;
		mController.getSubscribeOrderList(uid, token, new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof SubscribeOrderList) {
					doAfterFetchSubscribeList((SubscribeOrderList) entry, true,
							uid, token);
				} else {
					doAfterFetchSubscribeList(null, false, uid, token);
				}
			}
		});
	}

	/**
	 * 获取完订阅列表
	 * 
	 * @param catList
	 *            完整栏目列表
	 * @param subscribeList
	 *            订阅栏目列表
	 */
	protected void doAfterFetchSubscribeList(SubscribeOrderList subscribeList,
			boolean success, String uid, String token) {
		EnsubscriptHelper.addEnsubscriptColumn(mContext, uid, token);
		CommonApplication.mainProcessObservable
				.notifyProcessChange(new ObserverItem(
						MainProcessObservable.SET_DATA_TO_COLUMN, new Entry()));
		if (CommonApplication.mConfig.getIs_index_pager() == 1) {
			// TODO 首页滑屏
			CommonApplication.mainProcessObservable
					.notifyProcessChange(new ObserverItem(
							MainProcessObservable.SHOW_INDEX_PAGER, new Entry()));
			isFirstIn = false;
			if (callBack != null)
				callBack.afterFecthData();
			toEnd(true);
			return;
		}
		checkFirstItem();
	}

	/**
	 * 获取iweekly最新未读文章
	 */
	protected void getLastestArticle() {
		OperateController.getInstance(mContext).getLastestArticleIds("", false,
				new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						if (entry instanceof LastestArticleId) {
							CommonApplication.lastestArticleId = (LastestArticleId) entry;
						}
						getSubscribeList();
					}
				});
	}

	public void checkFirstItem() {
		List<TagInfo> catItems = new ArrayList<TagInfo>();
		catItems.addAll(AppValue.ensubscriptColumnList.getColumnTagList(false,
				false));
		catItems.addAll(AppValue.ensubscriptColumnList.getColumnTagList(true,
				false));
		for (TagInfo tagInfo : catItems) {
			if (tagInfo.getHasSubscribe() == 1) {
				// TODO 获取第一个已订阅栏目
				if (tagInfo.showChildren()) {
					// TODO 包含子栏目
					getChild(tagInfo);
				} else {
					// TODO 不包含子栏目
					getArticleList(tagInfo, null);
				}
				return;
			}
		}
		toEnd(false);
	}

	/**
	 * 获取子栏目列表
	 * 
	 * @param tagInfo
	 */
	public void getChild(TagInfo tagInfo) {
		((CommonMainActivity) mContext).notifyColumnAdapter(tagInfo
				.getTagName());
		currTagInfo = tagInfo;
		((CommonMainActivity) mContext).setIndexTitle(currTagInfo
				.getColumnProperty().getCname());
		TagInfoList childInfoList = new TagInfoList();
		if (tagInfo.isUriTag()) {
			Map<String, List<TagInfo>> map = AppValue.uriTagInfoList
					.getChildMap();
			if (ParseUtil.mapContainsKey(map, tagInfo.getTagName())) {
				for (TagInfo _tagInfo : map.get(tagInfo.getTagName())) {
					childInfoList.getList().add(_tagInfo);
				}
			}
		} else {
			childInfoList = AppValue.ensubscriptColumnList
					.getChildHasSubscriptTagInfoList(tagInfo.getTagName());
		}
		CommonApplication.mainProcessObservable
				.notifyProcessChange(new ObserverItem(
						MainProcessObservable.SHOW_CHILD_CAT, childInfoList));
		showLoad(false);
		toEnd(true);
	}

	/**
	 * 先请求文章列表(与栏目首页联动);文章列表的数据库缓存会在接口里实现
	 * 
	 * @param tagInfo
	 * @param articleList
	 */
	public void getArticleList(final TagInfo tagInfo,
			final TagArticleList articleList) {
		if (tagInfo == null)
			return;
		if (ConstData.getAppId() == 20
				&& tagInfo.getTagName().equals("cat_191")) {
			getCatIndex(tagInfo, null);
			return;
		}
		((CommonMainActivity) mContext).notifyColumnAdapter(tagInfo
				.getTagName());
		errorType = 7;
		currTagInfo = tagInfo;
		AppValue.currColumn = currTagInfo;
		mController.getTagArticles(tagInfo, "", "", null,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagArticleList) {
							TagArticleList articleList = (TagArticleList) entry;
							if (ParseUtil.listNotNull(articleList
									.getArticleList())) {
								getCatIndex(tagInfo, null);
								return;
							}
						}
						doAfterFecthArticleList(null, false);
					}
				});
	}

	/**
	 * 获取首页
	 */
	public void getCatIndex(TagInfo tagInfo, TagArticleList articleList) {
		if (tagInfo == null)
			return;
		errorType = 3;
		currTagInfo = tagInfo;
		AppValue.currColumn = currTagInfo;
		mController.getTagIndex(tagInfo, "", "", articleList,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagArticleList) {
							TagArticleList articleList = (TagArticleList) entry;
							if (!articleList.getMap().isEmpty()) {
								doAfterFecthArticleList(articleList, true);
								return;
							}
						}
						doAfterFecthArticleList(null, false);
					}
				});
	}

	/**
	 * 获取完文章列表
	 * 
	 * @param articleList
	 * @param success
	 */
	protected void doAfterFecthArticleList(TagArticleList articleList,
			boolean success) {
		if (success) {
			isFirstIn = false;
			((CommonMainActivity) mContext).setIndexTitle(articleList
					.getProperty().getCname());
			CommonApplication.mainProcessObservable
					.notifyProcessChange(new ObserverItem(
							MainProcessObservable.SET_DATA_TO_INDEX,
							articleList));
			if (callBack != null) {
				callBack.afterFecthData();
			}
		}
		toEnd(success);
	}

	protected void toEnd(boolean success) {
	}

	private void showMainProcess(int flag) {
		if (ConstData.getAppId() != 20) {
			ModernMediaTools.showLoadView(mContext, flag);
		}
	}

	protected void showLoad(boolean show) {
		if (show) {
			// TODO 如果缓存请求成功，那么显示dialog的loading
			if (!isFirstIn) {
				Tools.showLoading(mContext, show);
			} else {
				showMainProcess(1);
			}
		} else {
			ModernMediaTools.dismissLoad(mContext);
		}
	}

	protected void showError() {
		showLoad(false);
		if (isFirstIn) {
			Tools.showLoading(mContext, false);
			showMainProcess(2);
		} else {
			((CommonMainActivity) mContext).checkIndexLoading(2);
		}
	}

	public TagInfo getCurrTagInfo() {
		return currTagInfo;
	}

	/**
	 * 退出应用时还原变量
	 */
	public static void clear() {
		isFirstIn = true;
	}
}
