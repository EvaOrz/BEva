package cn.com.modernmedia.api;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.solo.api.GetSoloArticleListOperate;
import cn.com.modernmedia.solo.api.GetSoloArticleOperate;
import cn.com.modernmedia.solo.api.GetSoloCatIndexOperate;
import cn.com.modernmedia.solo.api.GetSoloColumnOperate;
import cn.com.modernmediaslate.listener.DataCallBack;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

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

	private void sendMessage(final Entry entry,
			final FetchEntryListener listener) {
		synchronized (mHandler) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					listener.setData(entry);
				}
			});
		}
	}

	/**
	 * 获取最新一期杂志信息
	 * 
	 * @param useLocal
	 *            是否查看的是新一期
	 * @param issueId
	 *            如果不为空，表示取某一期
	 * 
	 */
	public void getIssue(final FetchEntryListener listener, boolean useLocal,
			String issueId) {
		final GetIssueOperate operate = new GetIssueOperate(0, issueId);
		operate.asyncRequest(mContext, useLocal, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getIssue() : null, listener);
			}
		});
	}

	/**
	 * 获取期列表
	 * 
	 * @param page
	 * @param listener
	 */
	public void getIssueList(int page, final FetchEntryListener listener) {
		final GetIssueListOperate operate = new GetIssueListOperate(mContext,
				page);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getIssueList() : null, listener);
			}
		});
	}

	/**
	 * 获取最新一期的栏目列表
	 * 
	 * @param Issue
	 * 
	 */
	public void getCatList(Issue issue, final FetchEntryListener listener) {
		if (issue == null) {
			sendMessage(null, listener);
			return;
		}
		final GetCatListOperate operate = new GetCatListOperate(issue.getId());
		operate.asyncRequest(mContext, CommonApplication.columnUpdateTimeSame,
				new DataCallBack() {

					@Override
					public void callback(boolean success) {
						sendMessage(success ? operate.getCat() : null, listener);
					}
				});
	}

	/**
	 * 获取首页数据
	 * 
	 * @param Issue
	 */
	public void getIndex(Issue issue, final FetchEntryListener listener) {
		if (issue == null) {
			sendMessage(null, listener);
			return;
		}
		final GetIndexOperate operate = new GetIndexOperate(issue.getId() + "",
				issue.getColumnUpdateTime() + "");
		operate.asyncRequest(mContext, CommonApplication.columnUpdateTimeSame,
				new DataCallBack() {

					@Override
					public void callback(final boolean success) {
						sendMessage(success ? operate.getIndexArticle() : null,
								listener);
					}
				});
	}

	/**
	 * 获取除首页以外的栏目首页
	 * 
	 * @param issue
	 * @param columnId
	 * @param listener
	 * @param position
	 *            当前栏目再栏目列表里的位置
	 */
	public void getCartIndex(Issue issue, String columnId, int position,
			final FetchEntryListener listener) {
		if (issue == null || TextUtils.isEmpty(columnId)) {
			sendMessage(null, listener);
			return;
		}
		final GetCatIndexOperate operate = new GetCatIndexOperate(issue.getId()
				+ "", issue.getColumnUpdateTime() + "", columnId, position);
		operate.asyncRequest(mContext, CommonApplication.columnUpdateTimeSame,
				new DataCallBack() {

					@Override
					public void callback(boolean success) {
						sendMessage(success ? operate.getCatIndexArticle()
								: null, listener);
					}
				});
	}

	/**
	 * 获取文章列表
	 * 
	 * @param issue
	 * @param listener
	 * @param articleType
	 *            文章类型
	 */
	public void getArticleList(Issue issue, ArticleType articleType,
			final FetchEntryListener listener) {
		if (issue == null) {
			sendMessage(null, listener);
			return;
		}
		final GetArticleListOperate operate = new GetArticleListOperate(
				issue.getId() + "", issue.getArticleUpdateTime() + "",
				articleType);
		operate.asyncRequest(mContext, CommonApplication.articleUpdateTimeSame,
				new DataCallBack() {

					@Override
					public void callback(boolean success) {
						sendMessage(success ? operate.getArticleList() : null,
								listener);
					}
				});
	}

	/**
	 * 获取图集文章
	 * 
	 * @param item
	 * @param listener
	 */
	public void getArticleById(FavoriteItem item,
			final FetchEntryListener listener) {
		if (item == null) {
			sendMessage(null, listener);
			return;
		}
		final GetArticleOperate operate = new GetArticleOperate(
				item.getIssueid(), item.getCatid() + "", item.getId() + "",
				item.getUpdateTime());
		operate.asyncRequest(mContext, true, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? (Entry) operate.getAtlas() : null,
						listener);
			}
		});
	}

	/**
	 * 获取统计装机量是否成功
	 * 
	 * @param listener
	 */
	public void getDown(final FetchEntryListener listener) {
		final DownOperate operate = new DownOperate(mContext);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getDown() : null, listener);
			}
		});
	}

	/**
	 * 判断版本号
	 * 
	 * @param listener
	 */
	public void checkVersion(final FetchEntryListener listener) {
		final CheckVersionOperate operate = new CheckVersionOperate();
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getVersion() : null, listener);
			}
		});
	}

	/**
	 * 分享
	 * 
	 * @param issue
	 * @param columnId
	 * @param articleId
	 * @param shareType
	 * @param listener
	 */
	public void share(Issue issue, String columnId, String articleId,
			String shareType, final FetchEntryListener listener) {
		if (issue == null) {
			sendMessage(null, listener);
			return;
		}
		final ShareOperate operate = new ShareOperate(issue, columnId,
				articleId, shareType);
		operate.asyncRequest(mContext, true, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getShare() : null, listener);
			}
		});
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
			final FetchEntryListener listener) {
		final GetWeatherOperate operate = new GetWeatherOperate(longitude,
				latitude);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getWeather() : null, listener);
			}
		});
	}

	/**
	 * 获取最新未读文章id
	 * 
	 * @param listener
	 */
	public void getLastestArticleIds(int issueId, boolean useLocalData,
			final FetchEntryListener listener) {
		final GetLastestArticleIdOperate operate = new GetLastestArticleIdOperate(
				mContext, issueId);
		operate.asyncRequest(mContext, useLocalData, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getmLastestArticleId() : null,
						listener);
			}
		});
	}

	/**
	 * 获取广告列表
	 * 
	 * @param useLocal
	 * @param listener
	 */
	public void getAdvList(boolean useLocal, final FetchEntryListener listener) {
		final GetAdvListOperate operate = new GetAdvListOperate();
		operate.asyncRequest(mContext, useLocal, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getAdvList() : null, listener);
			}
		});
	}

	/**
	 * 获取iWeekly入版广告
	 * 
	 * @param listener
	 */
	public void getWeeklyInApp(boolean useLocalData,
			final FetchEntryListener listener) {
		final GetInAppAdvOperate operate = new GetInAppAdvOperate();
		operate.asyncRequest(mContext, useLocalData, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getInAppAdv() : null, listener);
			}
		});
	}
	
	/**
	 * 独立栏目index
	 * 
	 * @param catId
	 * @param fromOffset
	 *            (from_0)取from前的所有数据(最新数据)
	 * @param toOffset
	 *            (0_to)取to后的所有数据(旧数据) ......0_0代表取全部数据
	 * @param fecthNew
	 *            是否获取新数据
	 * @param listener
	 * @return
	 */
	public void getSoloCatIndex(String catId, String fromOffset,
			String toOffset, boolean fecthNew, int position,
			final FetchEntryListener listener) {
		final GetSoloCatIndexOperate operate = new GetSoloCatIndexOperate(
				mContext, catId, fromOffset, toOffset, position);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getCatIndexArticle() : null,
						listener);
			}
		});
	}

	/**
	 * 独立栏目文章列表
	 * 
	 * @param catId
	 * @param issue
	 * @param listener
	 */
	public void getSoloArticleList(String catId, String fromOffset,
			String toOffset, boolean fetchNew, final FetchEntryListener listener) {
		final GetSoloArticleListOperate operate = new GetSoloArticleListOperate(
				mContext, catId, fromOffset, toOffset, true, fetchNew);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getArticleList() : null, listener);
			}
		});
	}

	/**
	 * 获取文章详情
	 * 
	 * @param issue
	 * @param columnId
	 * @param articleId
	 * @param listener
	 */
	public void getSoloArticleById(FavoriteItem detail,
			final FetchEntryListener listener) {
		if (detail == null) {
			sendMessage(null, listener);
			return;
		}
		final GetSoloArticleOperate operate = new GetSoloArticleOperate(detail);
		operate.asyncRequest(mContext, true, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getAtlas() : null, listener);
			}
		});
	}

	/**
	 * 获取独立栏目列表
	 * 
	 * @param listener
	 */
	public void getSoloColumn(boolean useCache,
			final FetchEntryListener listener) {
		final GetSoloColumnOperate operate = new GetSoloColumnOperate();
		operate.asyncRequest(mContext, useCache, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getColumn() : null, listener);
			}
		});
	}
}
