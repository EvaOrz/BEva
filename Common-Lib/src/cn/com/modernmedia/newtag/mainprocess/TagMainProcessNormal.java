package cn.com.modernmedia.newtag.mainprocess;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.breakpoint.BreakPointUtil;
import cn.com.modernmedia.breakpoint.DownloadPackageCallBack;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.BreakPoint;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.MainProcessParseCallBack;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 新接口正常流程
 * 
 * @author zhuqiao
 * 
 */
public class TagMainProcessNormal extends TagBaseMainProcess {

	public TagMainProcessNormal(Context context,
			MainProcessParseCallBack callBack) {
		super(context, callBack);
	}

	@Override
	public void getAdvList() {
		super.getAdvList();
		OperateController.getInstance(mContext).getAdvList(false,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof AdvList) {
							CommonApplication.advList = (AdvList) entry;
							if (ConstData.getAppId() == 20)
								downloadRuBanForWeekly(CommonApplication.advList);
							else
								downloadRuBan(CommonApplication.advList);
							getAppInfo();
						} else {
							showError();
						}
					}
				});
	}

	@Override
	protected void doAfterFecthAppInfo(final TagInfoList appInfo,
			final boolean success) {
		super.doAfterFecthAppInfo(appInfo, success);
		if (success) {
			downloadSplash(appInfo.getList().get(0));
		} else {
			showError();
		}
	}

	@Override
	protected void doAfterFecthCatList(TagInfoList catList, boolean success) {
		super.doAfterFecthCatList(catList, success);
		if (!success) {
			showError();
		} else {
			if (CommonApplication.mConfig.getIs_index_pager() == 1) {
				showLoad(false);
			}
		}
	}

	@Override
	protected void doAfterFecthArticleList(TagArticleList articleList,
			boolean success) {
		super.doAfterFecthArticleList(articleList, success);
		if (!success) {
			showError();
		}
	}

	@Override
	protected void toEnd(boolean success) {
		if (success) {
			if (callBack != null)
				callBack.afterFecthHttp();
		}
	}

	/**
	 * 下载入版广告
	 * 
	 * @param advList
	 */
	protected void downloadRuBan(AdvList advList) {
		Map<Integer, List<AdvItem>> advMap = advList.getAdvMap();
		if (ParseUtil.mapContainsKey(advMap, AdvList.RU_BAN)) {
			if (ParseUtil.listNotNull(advMap.get(AdvList.RU_BAN))) {
				for (AdvItem item : advMap.get(AdvList.RU_BAN)) {
					for (AdvSource pic : item.getSourceList()) {
						PrintHelper.print("down_ruban===" + pic.getUrl());
						CommonApplication.finalBitmap.display(pic.getUrl());
					}
				}
			}
		}
	}

	private void downloadRuBanForWeekly(AdvList advList) {
		Map<Integer, List<AdvItem>> advMap = advList.getAdvMap();
		if (ParseUtil.mapContainsKey(advMap, AdvList.RU_BAN)) {
			if (ParseUtil.listNotNull(advMap.get(AdvList.RU_BAN))) {
				for (AdvItem item : advMap.get(AdvList.RU_BAN)) {
					if (ParseUtil.listNotNull(item.getSourceList()))
						downloadRuBanForWeekly(item);
				}
			}
		}
	}

	/**
	 * 下载入版广告
	 */
	private void downloadRuBanForWeekly(AdvItem item) {
		BreakPointUtil util = new BreakPointUtil(mContext,
				new DownloadPackageCallBack() {

					@Override
					public void onSuccess(String tagName, String folderName) {
						System.out.println("weekly success");
					}

					@Override
					public void onPause(String tagName) {
					}

					@Override
					public void onFailed(String tagName) {
					}

					@Override
					public void onProcess(String tagName, long complete,
							long total) {
					}
				});
		downloadPackage(item.getSourceList().get(0).getUrl(), util);
	}

	/**
	 * 下载iweekly启动图
	 */
	private void downloadSplash(TagInfo info) {
		if (ConstData.getAppId() != 20)
			return;
		PrintHelper.print("start down splash");
		BreakPointUtil util = new BreakPointUtil(mContext,
				new DownloadPackageCallBack() {

					@Override
					public void onSuccess(String tagName, String folderName) {
						System.out.println("success");
					}

					@Override
					public void onPause(String tagName) {
					}

					@Override
					public void onFailed(String tagName) {
					}

					@Override
					public void onProcess(String tagName, long complete,
							long total) {
					}
				});
		downloadPackage(info.getAppProperty().getSplash(), util);
	}

	/**
	 * iweekly入版广告下载、启动图
	 * 
	 * @param context
	 * @param url
	 * @param breakPointUtil
	 */
	private void downloadPackage(String url, BreakPointUtil breakPointUtil) {
		if (TextUtils.isEmpty(url)) {
			return;
		}
		BreakPoint breakPoint = new BreakPoint();
		if (FileManager.containThisPackageFolder(url)) {
			// TODO 如果包含解压包，则直接加载
			breakPoint.setStatus(BreakPointUtil.DONE);
		} else if (FileManager.containThisPackage(url)) {
			// TODO 如果包含zip包,执行断点下载
			breakPoint.setStatus(BreakPointUtil.BREAK);
		} else {
			breakPoint.setStatus(BreakPointUtil.NONE);
		}
		breakPoint.setTagName("1");// 无用
		breakPoint.setUrl(url);
		breakPointUtil.downLoad(breakPoint);
	}

}
