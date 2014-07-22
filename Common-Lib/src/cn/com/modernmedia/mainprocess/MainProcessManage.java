package cn.com.modernmedia.mainprocess;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmedia.util.ModernMediaTools;

/**
 * 主页流程管理器
 * 
 * @author user
 * 
 */
public class MainProcessManage {
	private Context mContext;
	private BaseMainProcess process;
	private MainProcessAbstractFactory factory;
	private boolean isFirstFetchHttpSuccess = false;// 是否第一次从服务器上请求成功

	/**
	 * 流程状态
	 */
	protected ProcessType mType;

	/**
	 * 流程种类
	 * 
	 * @author user
	 * 
	 */
	public enum ProcessType {
		Cache/** 缓存 **/
		, Normal/** 普通流程 **/
		, Last/** 往期 **/
		, Parse/** 推送 **/
		;
	}

	/**
	 * 解析推送之后的流程
	 * 
	 * @author user
	 * 
	 */
	public enum ParseType {
		LoadCache/** 请求缓存 **/
		, LoadHttp/** 请求服务器 **/
		, GotoArticle/** 跳转至文章页 **/
		, LoadOnlyCache/** 只请求缓存里的期数据 **/
		;
	}

	public interface FetchDataCallBack {
		public void afterFetch(ProcessType type);

		public void afterParse(ParseType type);
	}

	public FetchDataCallBack fetchCallBack = new FetchDataCallBack() {

		@Override
		public void afterFetch(ProcessType type) {
			ModernMediaTools.showLoadView(mContext, 0);
			if (type == ProcessType.Cache) {
				fetchFromHttp();

				if (ModernMediaTools.checkNetWork(mContext)) {
					showFecthingToast(0);
				} else {
					ModernMediaTools.showToast(mContext, R.string.net_error);
				}
			} else if (type == ProcessType.Normal && !isFirstFetchHttpSuccess) {
				isFirstFetchHttpSuccess = true;
				showFecthingToast(1);
			}
		}

		@Override
		public void afterParse(ParseType type) {
			if (type == ParseType.LoadCache) {
				// TODO 走缓存流程
				startLoadCache();
			} else if (type == ParseType.LoadHttp) {
				// TODO 获取服务器数据(为了不显示新一期dialog)
				fetchFromHttpAfterParse();
			} else if (type == ParseType.LoadOnlyCache) {
				// TOOD 加载缓存中的issue
				fetchFromHttpById(DataHelper.getIssueId(mContext));
			}
		}
	};

	public MainProcessManage(Context context) {
		mContext = context;
		factory = new MainProcessFactory();
	}

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
				startLoadCache();
			} else {
				// 从push消息进来
				parsePush(intent);
			}
		} else {
			startLoadCache();
		}
	}

	/**
	 * 加载缓存
	 */
	public void startLoadCache() {
		mType = ProcessType.Cache;
		process = factory.createCacheProcess(mContext, fetchCallBack);
		((MainProcessCache) process).getIssueById();
	}

	/**
	 * 加载服务器数据
	 */
	public void fetchFromHttp() {
		mType = ProcessType.Normal;
		process = factory.createNormalProcess(mContext, fetchCallBack);
		((MainProcessNormal) process).checkVersion();
	}

	/**
	 * 加载某一期服务器数据
	 */
	private void fetchFromHttpById(int issueId) {
		mType = ProcessType.Normal;
		process = factory.createNormalProcess(mContext, fetchCallBack);
		((MainProcessNormal) process).setFromPreIssue(true);
		((MainProcessNormal) process).getIssue(issueId + "", false);
	}

	/**
	 * 解析推送
	 */
	public void parsePush(Intent intent) {
		mType = ProcessType.Parse;
		process = factory.createParseProcess(mContext, fetchCallBack, intent);
		((MainProcessParse) process).parsePushMsg(intent);
	}

	/**
	 * 推送进文章返回获取首页数据
	 */
	public void fetchFromHttpAfterParse() {
		mType = ProcessType.Normal;
		process = factory.createNormalProcess(mContext, fetchCallBack);
		((MainProcessNormal) process).setFromParse(true);
		((MainProcessNormal) process).checkVersion();
	}

	/**
	 * 获取往期首页
	 */
	public void fetchPreIssueIndex(int issueId) {
		fetchFromHttpById(issueId);
	}

	public BaseMainProcess getProcess() {
		if (process == null)
			startLoadCache();
		return process;
	}

	public ProcessType getType() {
		return mType;
	}

	public int reLoadData() {
		if (BaseMainProcess.isFirstIn)
			ModernMediaTools.showLoadView(mContext, 1);
		else
			((CommonMainActivity) mContext).checkIndexLoading(1);
		switch (process.getErrorType()) {
		case 1:
			process.getIssue("", false);
			break;
		case 2:
			process.getCatList();
			break;
		case 3:
			process.getIndex(true);
			break;
		case 4:
			process.getCatIndex(process.getColumnId(), true);
			break;
		case 5:
			process.getSoloCatList();
			break;
		case 6:
			process.getSoloArticleList(process.getColumnId(), false, "0", "0",
					true);
			break;
		case 7:
			process.getAdvList(false);
			break;
		default:
			break;
		}
		return -1;
	}

	public int getErrorType() {
		return process.getErrorType();
	}

	/**
	 * 获取当前cat所在catList的位置
	 * 
	 * @return
	 */
	public int getCatPosition(String catId) {
		return ModernMediaTools.getCatPosition(catId, process.getCat());
	}

	/**
	 * 是否正在使用缓存
	 * 
	 * @return
	 */
	public boolean isUsingCache() {
		return process instanceof MainProcessCache;
	}

	/**
	 * 获取网络时根据状态提示信息
	 * 
	 * @param flag
	 *            0:fetching;1.fecthOk;2.fetchError
	 */
	public void showFecthingToast(int flag) {
		if (!ModernMediaTools.checkNetWork(mContext))
			return;
		switch (flag) {
		case 0:
			ModernMediaTools.showToast(mContext, R.string.fecthing_http);
			break;
		case 1:
			ModernMediaTools.showToast(mContext, R.string.fecthing_ok);
			break;
		case 2:
			// 请求错误会统一提示
			break;
		default:
			break;
		}
	}
}
