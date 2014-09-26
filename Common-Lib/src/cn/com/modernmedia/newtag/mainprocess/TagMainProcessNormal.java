package cn.com.modernmedia.newtag.mainprocess;

import java.util.List;
import java.util.Map;

import android.content.Context;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.MainProcessParseCallBack;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmedia.util.UpdateManager;
import cn.com.modernmedia.util.UpdateManager.CheckVersionListener;
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
		if (success) {
			if (ConstData.getInitialAppId() == 1
					&& CommonApplication.CHANNEL.equals("googleplay")) {
				doAfterFecthAppInfo(appInfo);
				return;
			}
			UpdateManager manager = new UpdateManager(mContext,
					new CheckVersionListener() {

						@Override
						public void checkEnd() {
							doAfterFecthAppInfo(appInfo);
						}
					});
			manager.checkVersion(appInfo.getList().get(0).getAppProperty()
					.getVersion());
		} else {
			showError();
		}
	}

	private void doAfterFecthAppInfo(TagInfoList appInfo) {
		super.doAfterFecthAppInfo(appInfo, true);
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

}
