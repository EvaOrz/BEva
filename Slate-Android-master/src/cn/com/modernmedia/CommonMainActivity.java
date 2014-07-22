package cn.com.modernmedia;

import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Cat;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.util.UpdateManager;
import cn.com.modernmedia.util.UpdateManager.CheckVersionListener;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.widget.MainHorizontalScrollView;

import com.parse.ParseAnalytics;

/**
 * 公共首页
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonMainActivity extends BaseFragmentActivity {
	public static final int REQUEST_CODE = 100;// 从文章页返回，刷新，设置已阅读文章
	private Context mContext;
	private OperateController controller;// 接口控制器
	private long lastClickTime = 0;
	private Issue issue;
	private Cat cat;
	private String columnId = "";
	private int errorType = 0;// 1--getissue;2--getcatlist;3--getindex;4--getcatindex
	// private boolean isPushIssue = false;
	private int pushArticleId = -1;
	private int pushCatId = -1;
	private boolean isFirstIn = true;// 显示切换动画

	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		init();
		ParseAnalytics.trackAppOpened(getIntent());
		controller = new OperateController(this);
		initProcess();
		if (getIntent().getExtras() != null) {
			String from = getIntent().getExtras().getString("FROM_ACTIVITY");
			if (!TextUtils.isEmpty(from) && from.equals("SPLASH")) {
				// 从首页进来
				if (CommonApplication.CHANNEL.equals("googleplay")
						|| CommonApplication.CHANNEL.equals("blackberry")) {
					getIssue(true);
				} else {
					checkVersion();
				}
			} else {
				// 从push消息进来
				parsePushMsg(getIntent());
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getExtras() != null) {
			parsePushMsg(intent);
		}
	}

	/**
	 * 解析push消息
	 * 
	 * @param intent
	 */
	private void parsePushMsg(Intent intent) {
		try {
			String msg = intent.getExtras().getString("com.parse.Data");
			if (!TextUtils.isEmpty(msg)) {
				JSONObject json = new JSONObject(msg);
				String na = json.optString("na", "");// 新文章
				JSONObject newissue = json.optJSONObject("newissue");// 新一期
				if (TextUtils.isEmpty(na)
						&& (JSONObject.NULL.equals(newissue) || newissue == null)) {
					if (issue != null)
						return;
					// 不提示任何信息
					if (CommonApplication.CHANNEL.equals("googleplay")
							|| CommonApplication.CHANNEL.equals("blackberry")) {
						getIssue(true);
					} else {
						checkVersion();
					}
					return;
				}

				if (!TextUtils.isEmpty(na)) {
					// 进入文章
					// 跳转至push文章页
					String[] arr = UriParse.parsePush(na);
					if (arr != null && arr.length == 3) {
						checkPushIssue(ParseUtil.stoi(arr[0], -1),
								ParseUtil.stoi(arr[2], -1),
								ParseUtil.stoi(arr[1], -1));
					}
				} else {
					int newIssueId = newissue.optInt("issueid", -1);
					// 提示新一期
					checkPushIssue(newIssueId, -1, -1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 比较本地锁存的issue和push消息里的issue
	 */
	private void checkPushIssue(int id, int articleId, int catId) {
		getScrollView().IndexClick();
		// (如果用户本地没有旧一期的id，那么直接给他获取最新一期)
		if (DataHelper.getIssueId(mContext) == -1) {
			CommonApplication.isFetchPush = true;
			getIssue(true);
		} else if (id != DataHelper.getIssueId(mContext)) {
			// (如果用户本地的缓存id与服务器推送消息的不一致，那么提示他是否查看最新一期)
			pushArticleId = articleId;
			pushCatId = catId;
			showDialog(1);
		} else {
			// (如果用户本地的缓存id与服务器推送消息的一致，那么获取相关推送数据)
			pushArticleId = articleId;
			pushCatId = catId;
			// 如果该文章在当前所看的这期中，直接调至文章页
			if (issue == null) {
				getIssue(true);
			} else {
				if (articleId != -1)
					gotoArticleActivity(pushArticleId, pushCatId, true);
			}
		}
	}

	/**
	 * 比较版本
	 */
	private void checkVersion() {
		showLoading();
		UpdateManager manager = new UpdateManager(this,
				new CheckVersionListener() {

					@Override
					public void checkEnd() {
						getIssue(true);
					}
				});
		manager.checkVersion();
	}

	/**
	 * 获取最新一期杂志信息 查看上一期
	 * 
	 * @param fetchNew
	 *            是否查看新一期(第一次进应用默认true)
	 */
	private void getIssue(final boolean fetchNew) {
		errorType = 1;
		// indexView.showLoading();
		showLoading();
		controller.getIssue(new FetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						if (entry != null && entry instanceof Issue) {
							issue = (Issue) entry;
							if (pushArticleId != -1) {
								// 调至至文章页
								if (fetchNew) {
									gotoArticleActivity(pushArticleId,
											pushCatId, true);
									DataHelper.setIssueId(mContext,
											issue.getId());
								}
							} else if (CommonApplication.isFetchPush) {
								DataHelper.setIssueId(mContext, issue.getId());
								CommonApplication.isFetchPush = false;
								CommonApplication.hasNewIssue = false;
								CommonApplication.viewOldIssue = false;
								getCatList();
							} else {
								if (CommonApplication.hasNewIssue) {
									if (mContext != null) {
										showDialog(0);
									}
								} else {
									getCatList();
								}
							}
						} else {
							// indexView.showError();
							showError();
						}
					}
				});
			}
		}, fetchNew);
	}

	/**
	 * 获取最新一期的栏目列表
	 * 
	 * @param Issue
	 * 
	 */
	private void getCatList() {
		errorType = 2;
		if (issue != null)
			// 下载进版广告
			CommonApplication.getImageDownloader().download(
					issue.getAdv().getColumnAdv().getUrl());
		controller.getCatList(issue, new FetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						if (entry != null && entry instanceof Cat) {
							cat = (Cat) entry;
							setDataForColumn(entry);
							getIndex();
						} else {
							// indexView.showError();
							showError();
						}
					}
				});
			}
		});
	}

	/**
	 * 获取首页数据
	 * 
	 */
	public void getIndex() {
		if (cat == null)
			return;
		errorType = 3;
		if (!isFirstIn)
			indexShowLoading(1);
		columnId = "-1";
		controller.getIndex(issue, new FetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				handler.post(new Runnable() {
					public void run() {
						if (entry != null && entry instanceof IndexArticle) {
							setDataForIndex(entry);
							if (cat != null && cat.getList() != null
									&& !cat.getList().isEmpty()) {
								setIndexTitle(cat.getList().get(0).getCname());
							}
							DataHelper.setColumnUpdateTime(mContext,
									issue.getColumnUpdateTime());
							if (!isFirstIn) {
								indexShowLoading(0);
								disProcess();
							} else {
								if (ConstData.APP_ID == 2) {
									moveToLeft();
								} else {
									getScrollView().clickButton(true);
									disProcess();
								}
								moveToIndex();
							}
						} else {
							if (isFirstIn) {
								showError();
							} else {
								indexShowLoading(2);
							}
						}
					}
				});
			}
		});
	}

	/**
	 * 获取栏目首页
	 * 
	 * @param issue
	 * @param columnId
	 */
	public void getCatIndex(String columnId) {
		errorType = 4;
		this.columnId = columnId;
		indexShowLoading(1);
		controller.getCartIndex(issue, columnId, new FetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						if (entry != null && entry instanceof CatIndexArticle) {
							setDataForIndex(entry);
							indexShowLoading(0);
						} else {
							indexShowLoading(2);
						}
					}
				});
			}
		});
	}

	/**
	 * 优家效果有点卡，延迟20毫秒执行
	 */
	private void moveToLeft() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				getScrollView().clickButton(true);
				disProcess();
			}
		}, 20);
	}

	private void moveToIndex() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				getScrollView().IndexClick();
				isFirstIn = false;
			}
		}, 1000);
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		Builder builder = new Builder(this);
		builder.setMessage(R.string.new_issue);
		builder.setPositiveButton(R.string.vew_later,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CommonApplication.viewOldIssue = true;
						CommonApplication.hasNewIssue = false;
						// 稍后查看
						if (id == 0) {
							getIssue(false);
						} else if (id == 1) {
							// push消息首页
							if (issue == null) {
								CommonApplication.oldIssueId = DataHelper
										.getIssueId(mContext);
								getIssue(false);
							}
						}
					}
				}).setNegativeButton(R.string.view_now,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CommonApplication.viewOldIssue = false;
						CommonApplication.hasNewIssue = false;
						// 立即查看
						if (id == 0) {
							DataHelper.setIssueId(CommonApplication.mContext,
									issue.getId());
							CommonApplication.columnUpdateTimeSame = true;
							CommonApplication.issueIdSame = true;
							CommonApplication.articleUpdateTimeSame = true;
							getCatList();
						} else if (id == 1) {
							// 推送，有新的一期
							CommonApplication.isFetchPush = true;
							getIssue(true);
						}
					}
				});
		return builder.create();
	}

	/**
	 * 初始化资源
	 */
	protected abstract void init();

	/**
	 * 初始化栏目列表
	 * 
	 * @param entry
	 */
	protected abstract void setDataForColumn(Entry entry);

	/**
	 * 初始化index
	 * 
	 * @param entry
	 */
	protected abstract void setDataForIndex(Entry entry);

	protected abstract MainHorizontalScrollView getScrollView();

	/**
	 * 跳转至文章页
	 * 
	 * @param artcleId
	 * @param isIndex
	 *            true:首页；false:收藏
	 */
	protected abstract void gotoArticleActivity(int artcleId, int catId,
			boolean isIndex);

	/**
	 * indexview 显示loading
	 * 
	 * @param flag
	 *            0.不显示，1.显示loading，2.显示error
	 */
	protected abstract void indexShowLoading(int flag);

	/**
	 * 设置首页title
	 * 
	 * @param name
	 */
	protected abstract void setIndexTitle(String name);

	protected abstract void notifyRead();

	public Issue getIssue() {
		return issue;
	}

	public int getPushArticleId() {
		return pushArticleId;
	}

	public void setPushArticleId(int pushArticleId) {
		this.pushArticleId = pushArticleId;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			long clickTime = System.currentTimeMillis() / 1000;
			if (clickTime - lastClickTime >= 3) {
				lastClickTime = clickTime;
				Toast.makeText(CommonMainActivity.this, R.string.exit_app,
						ConstData.TOAST_LENGTH).show();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void reLoadData() {
		if (isFirstIn)
			showLoading();
		else
			indexShowLoading(1);
		switch (errorType) {
		case 1:
			getIssue(!CommonApplication.viewOldIssue);
			break;
		case 2:
			getCatList();
			break;
		case 3:
			getIndex();
			break;
		case 4:
			getCatIndex(columnId);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CommonApplication.getImageDownloader().destroy();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE) {
				// indexView.notifyAdapter();
				notifyRead();
				if (pushArticleId != -1) {
					pushArticleId = -1;
					pushCatId = -1;
					// if (indexView != null)
					// indexView.showLoading();
					showLoading();
					getCatList();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getColumnId() {
		return columnId;
	}

}
