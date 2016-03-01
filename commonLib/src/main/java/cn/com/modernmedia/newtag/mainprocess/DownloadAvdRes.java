package cn.com.modernmedia.newtag.mainprocess;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.breakpoint.BreakPointUtil;
import cn.com.modernmedia.breakpoint.DownloadPackageCallBack;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.BreakPoint;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 下载广告、splash资源
 * 
 * @author user
 * 
 */
public class DownloadAvdRes {
	private Context mContext;

	public DownloadAvdRes(Context c) {
		mContext = c;
	}

	/**
	 * 下载入版广告
	 */
	public void downloadRunBan() {
		if (CommonApplication.advList == null)
			return;

		Map<Integer, List<AdvItem>> advMap = CommonApplication.advList
				.getAdvMap();
		if (!ParseUtil.mapContainsKey(advMap, AdvList.RU_BAN))
			return;
		if (!ParseUtil.listNotNull(advMap.get(AdvList.RU_BAN)))
			return;

		for (AdvItem item : advMap.get(AdvList.RU_BAN)) {
			if (!ParseUtil.listNotNull(item.getSourceList()))
				continue;

			if (item.getShowType() == 0) {
				// 图片
				downloadRuBanForPic(item);
			} else if (item.getShowType() == 1) {
				// 网页
				downloadRuBanForZip(item);
			}
		}
	}

	/**
	 * 下载iweekly启动图
	 */
	public void downloadSplash(String url) {
		if (ConstData.getAppId() != 20 || TextUtils.isEmpty(url))
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
		downloadPackage(url, util);
	}

	/**
	 * 下载图片入版广告
	 */
	private void downloadRuBanForPic(AdvItem item) {
		for (AdvSource pic : item.getSourceList()) {
			CommonApplication.finalBitmap.display(pic.getUrl());
		}
	}

	/**
	 * 下载html入版广告
	 */
	private void downloadRuBanForZip(AdvItem item) {
		BreakPointUtil util = new BreakPointUtil(mContext,
				new DownloadPackageCallBack() {

					@Override
					public void onSuccess(String tagName, String folderName) {
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
		PrintHelper.print("======download ruban zip======");
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
