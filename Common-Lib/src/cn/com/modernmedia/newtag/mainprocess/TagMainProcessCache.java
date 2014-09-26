package cn.com.modernmedia.newtag.mainprocess;

import android.content.Context;
import android.os.Handler;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.GetTagInfoOperate;
import cn.com.modernmedia.api.GetTagInfoOperate.TAG_TYPE;
import cn.com.modernmedia.api.GetUserSubscribeListOpertate;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.LastestArticleId;
import cn.com.modernmedia.model.SubscribeOrderList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.newtag.db.UserSubscribeListDb;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.MainProcessParseCallBack;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 新接口缓存流程
 * 
 * @author zhuqiao
 * 
 */
public class TagMainProcessCache extends TagBaseMainProcess {
	private Handler mHandler = new Handler();
	private String uid = "", token = "";

	public TagMainProcessCache(Context context,
			MainProcessParseCallBack callBack) {
		super(context, callBack);
	}

	@Override
	public void checkVersion() {
		getAdvList();
	}

	@Override
	public void getAdvList() {
		super.getAdvList();
		OperateController.getInstance(mContext).getAdvList(true,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof AdvList) {
							CommonApplication.advList = (AdvList) entry;
							getAppInfo();
						} else {
							end();
						}
					}
				});
	}

	@Override
	protected void clearCacheWhenUpdatetimeChange() {
	}

	private void sendMessage(final Entry entry,
			final FetchEntryListener listener) {
		synchronized (mHandler) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (listener != null)
						listener.setData(entry);
				}
			});
		}
	}

	/**
	 * 子线程信息db数据
	 * 
	 * @param flag
	 *            1.获取app信息;2.获取catlist;3.获取已订阅列表
	 * @param listener
	 */
	private void getEntryFromDb(final int flag,
			final FetchEntryListener listener) {
		Thread thread = new Thread() {

			@Override
			public void run() {
				Entry entry = null;
				if (flag == 1)
					entry = TagInfoListDb.getInstance(mContext).getEntry(
							new GetTagInfoOperate(), "", "", "1", "",
							TAG_TYPE.APP_INFO);
				else if (flag == 2)
					entry = TagInfoListDb.getInstance(mContext).getEntry(
							new GetTagInfoOperate(),
							AppValue.appInfo.getTagName(), "", "3", "",
							TAG_TYPE.TREE_CAT);
				else if (flag == 3)
					entry = UserSubscribeListDb.getInstance(mContext).getEntry(
							new GetUserSubscribeListOpertate(uid, token), uid);
				sendMessage(entry, listener);
			}

		};
		thread.start();
		thread = null;
	}

	@Override
	public void getAppInfo() {
		showLoad(true);
		getEntryFromDb(1, new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof TagInfoList) {
					PrintHelper.print("cache:appifo");
					TagInfoList appInfo = (TagInfoList) entry;
					if (ParseUtil.listNotNull(appInfo.getList())) {
						doAfterFecthAppInfo(appInfo, true);
						return;
					}
				}
				end();
			}
		});
	}

	@Override
	protected void getCatList() {
		getEntryFromDb(2, new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof TagInfoList) {
					PrintHelper.print("cache:catlist");
					TagInfoList catList = (TagInfoList) entry;
					if (ParseUtil.listNotNull(catList.getList())) {
						doAfterFecthCatList(catList, true);
						return;
					}
				}
				end();
			}
		});
	}

	/**
	 * 获取iweekly最新未读文章
	 */
	protected void getLastestArticle() {
		OperateController.getInstance(mContext).getLastestArticleIds("", true,
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

	@Override
	protected void getSubscribeOrderList(final String uid, final String token) {
		this.uid = uid;
		this.token = token;
		getEntryFromDb(3, new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof SubscribeOrderList) {
					PrintHelper.print("cache:SubscribeOrderList");
					doAfterFetchSubscribeList((SubscribeOrderList) entry, true,
							uid, token);
				} else {
					end();
				}
			}
		});
	}

	@Override
	protected void toEnd(boolean success) {
		isFirstIn = false;
		end();
	}

	private void end() {
		showLoad(false);
		if (callBack != null)
			callBack.afterFecthCache();
	}
}
