package cn.com.modernmedia.newtag.mainprocess;

import android.content.Context;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.SubscribeOrderList;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.MainProcessParseCallBack;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 新接口缓存流程
 * 
 * @author zhuqiao
 * 
 */
public class TagMainProcessCache extends BaseTagMainProcess {
	private FetchApiType apiType = FetchApiType.USE_CACHE_ONLY;

	public TagMainProcessCache(Context context,
			MainProcessParseCallBack callBack) {
		super(context, callBack);
	}

	public void setApiType(FetchApiType apiType) {
		this.apiType = apiType;
	}

	@Override
	public void onStart(Object... objs) {
		showLoad(true);
		mState.isEnd = false;
		mState.isSuccess = false;
		getAppInfo(apiType);
	}

	@Override
	protected void doAfterFecthAppInfo(TagInfoList appInfo, boolean success) {
		if (success) {
			PrintHelper.print("cache:appifo");
			super.doAfterFecthAppInfo(appInfo, success);
			getAdvList(apiType);

			TagProcessManage.getInstance(mContext).showPushArticleActivity(
					mContext, "", 0);
		} else {
			toEnd(false);
		}
	}

	@Override
	protected void clearCacheWhenUpdatetimeChange() {
		// 屏蔽父类方法，缓存逻辑不清除缓存
	}

	@Override
	protected void doAfterFetchAdvList(AdvList advList, boolean success) {
		super.doAfterFetchAdvList(advList, success);
		checkAfterFetchAdvList(apiType);
	}

	@Override
	protected void doAfterFecthShiye(TagArticleList articleList, boolean success) {
		super.doAfterFecthShiye(articleList, success);

		getCatList(apiType);
	}

	@Override
	protected void doAfterFecthCatList(TagInfoList catList, boolean success,
			FetchApiType type) {
		if (success) {
			PrintHelper.print("cache:catlist");
			super.doAfterFecthCatList(catList, success, apiType);
		} else {
			toEnd(false);
		}
	}

	@Override
	protected void doAfterFetchSubscribeList(SubscribeOrderList subscribeList,
			boolean success) {
		super.doAfterFetchSubscribeList(subscribeList, success);
		if (success) {
			PrintHelper.print("cache:SubscribeOrderList");
		}
	}

	@Override
	protected void toEnd(boolean success) {
		mState.isEnd = true;
		mState.isSuccess = success;
		showLoad(false);
		if (callBack != null)
			callBack.afterFetchCache(success);
	}

	/**
	 * 是否显示loading
	 */
	protected void showLoad(boolean show) {
		if (ConstData.getAppId() == 20)
			return;
		if (show) {
			if (hasFilledData) {
				Tools.showLoading(mContext, true);
			} else {
				ModernMediaTools.showLoadView(mContext, 1);
			}
		} else {
			ModernMediaTools.dismissLoad(mContext);
		}
	}

	/**
	 * 显示错误页面
	 */
	protected void showError() {
		if (ConstData.getAppId() == 20)
			return;
		showLoad(false);
		if (!hasFilledData) {
			Tools.showLoading(mContext, false);
			ModernMediaTools.showLoadView(mContext, 2);
		}
	}

}
