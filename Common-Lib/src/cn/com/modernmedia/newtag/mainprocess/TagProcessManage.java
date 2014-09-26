package cn.com.modernmedia.newtag.mainprocess;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmedia.util.PageTransfer;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 新接口主页流程管理器
 * 
 * @author zhuqiao
 * 
 */
public class TagProcessManage {
	private Context mContext;
	private String pushTagName, pushArticleId;
	private boolean hasParse = false;
	private boolean firstFetchHttp = true;
	private boolean hasFetchCache = false;

	public TagProcessManage(Context context) {
		mContext = context;
	}

	public interface MainProcessParseCallBack {
		public void afterFecthCache();

		public void afterParse(String tagName, String id);

		public void afterFecthData();

		public void afterFecthHttp();
	}

	private MainProcessParseCallBack callBack = new MainProcessParseCallBack() {

		@Override
		public void afterFecthCache() {
			if (hasFetchCache)
				return;
			hasFetchCache = true;
			if (Tools.checkNetWork(mContext)) {
				if (ConstData.getAppId() != 20)
					Tools.showToast(mContext, R.string.fecthing_http);
				fetchFromHttp();
			} else {
				Tools.showToast(mContext, R.string.net_error);
			}

		}

		@Override
		public void afterFecthHttp() {
			if (firstFetchHttp) {
				firstFetchHttp = false;
				if (ConstData.getAppId() != 20)
					Tools.showToast(mContext, R.string.fecthing_ok);
				Tools.showLoading(mContext, false);
			}
		}

		@Override
		public void afterParse(String tagName, String id) {
			pushTagName = tagName;
			pushArticleId = id;
			if (TextUtils.isEmpty(AppValue.appInfo.getTagName())) {
				// TODO 未打开应用
				if (ConstData.getAppId() != 20)
					Tools.showToast(mContext, R.string.fecthing_http);
				fetchFromHttp();
			} else if (!TextUtils.isEmpty(pushTagName)
					&& !TextUtils.isEmpty(pushArticleId)) {
				// TODO 进入文章页
				TransferArticle tr = new TransferArticle(ParseUtil.stoi(id),
						tagName, "", -1, ArticleType.Default);
				PageTransfer.gotoArticleActivity(mContext, tr);
			}
		}

		@Override
		public void afterFecthData() {
			if (hasParse)
				return;
			if (!TextUtils.isEmpty(pushTagName)
					&& !TextUtils.isEmpty(pushArticleId)) {
				// TODO 进入文章页
				TransferArticle tr = new TransferArticle(
						ParseUtil.stoi(pushArticleId), pushTagName, "", -1,
						ArticleType.Default);
				PageTransfer.gotoArticleActivity(mContext, tr);
				hasParse = true;
			}
		}

	};

	/**
	 * 进入首页判断intent
	 * 
	 * @param intent
	 */
	public void checkIntent(Intent intent) {
		if (intent != null && intent.getExtras() != null) {
			String from = intent.getExtras().getString(
					GenericConstant.FROM_ACTIVITY);
			if (!TextUtils.isEmpty(from)
					&& from.equals(GenericConstant.FROM_ACTIVITY_VALUE)) {
				// 从首页进来
				fecthFromCache();
			} else {
				// 从push消息进来
				parsePush(intent);
			}
		} else {
			fecthFromCache();
		}
	}

	/**
	 * 加载缓存数据
	 */
	private void fecthFromCache() {
		AppValue.mainProcess = new TagMainProcessCache(mContext, callBack);
		AppValue.mainProcess.checkVersion();
	}

	/**
	 * 加载服务器数据
	 */
	private void fetchFromHttp() {
		AppValue.mainProcess = new TagMainProcessNormal(mContext, callBack);
		AppValue.mainProcess.checkVersion();
	}

	/**
	 * 推送
	 */
	private void parsePush(Intent intent) {
		AppValue.mainProcess = new TagMainProcessParse(mContext, callBack);
		((TagMainProcessParse) AppValue.mainProcess).parsePushMsg(intent);
	}

	public void reLoadData() {
		TagBaseMainProcess process = AppValue.mainProcess;
		if (process == null)
			return;
		switch (process.errorType) {
		case 1:
			process.getAppInfo();
			break;
		case 2:
			process.getCatList();
			break;
		case 3:
			process.getCatIndex(process.currTagInfo, null);
			break;
		case 4:
			process.getChild(process.currTagInfo);
			break;
		case 5:
			process.getSubscribeList();
			break;
		case 6:
			process.getAdvList();
			break;
		case 7:
			process.getArticleList(process.currTagInfo, null);
			break;
		default:
			break;
		}
	}
}
