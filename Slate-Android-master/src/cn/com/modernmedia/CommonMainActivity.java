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
 * ������ҳ
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonMainActivity extends BaseFragmentActivity {
	public static final int REQUEST_CODE = 100;// ������ҳ���أ�ˢ�£��������Ķ�����
	private Context mContext;
	private OperateController controller;// �ӿڿ�����
	private long lastClickTime = 0;
	private Issue issue;
	private Cat cat;
	private String columnId = "";
	private int errorType = 0;// 1--getissue;2--getcatlist;3--getindex;4--getcatindex
	// private boolean isPushIssue = false;
	private int pushArticleId = -1;
	private int pushCatId = -1;
	private boolean isFirstIn = true;// ��ʾ�л�����

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
				// ����ҳ����
				if (CommonApplication.CHANNEL.equals("googleplay")
						|| CommonApplication.CHANNEL.equals("blackberry")) {
					getIssue(true);
				} else {
					checkVersion();
				}
			} else {
				// ��push��Ϣ����
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
	 * ����push��Ϣ
	 * 
	 * @param intent
	 */
	private void parsePushMsg(Intent intent) {
		try {
			String msg = intent.getExtras().getString("com.parse.Data");
			if (!TextUtils.isEmpty(msg)) {
				JSONObject json = new JSONObject(msg);
				String na = json.optString("na", "");// ������
				JSONObject newissue = json.optJSONObject("newissue");// ��һ��
				if (TextUtils.isEmpty(na)
						&& (JSONObject.NULL.equals(newissue) || newissue == null)) {
					if (issue != null)
						return;
					// ����ʾ�κ���Ϣ
					if (CommonApplication.CHANNEL.equals("googleplay")
							|| CommonApplication.CHANNEL.equals("blackberry")) {
						getIssue(true);
					} else {
						checkVersion();
					}
					return;
				}

				if (!TextUtils.isEmpty(na)) {
					// ��������
					// ��ת��push����ҳ
					String[] arr = UriParse.parsePush(na);
					if (arr != null && arr.length == 3) {
						checkPushIssue(ParseUtil.stoi(arr[0], -1),
								ParseUtil.stoi(arr[2], -1),
								ParseUtil.stoi(arr[1], -1));
					}
				} else {
					int newIssueId = newissue.optInt("issueid", -1);
					// ��ʾ��һ��
					checkPushIssue(newIssueId, -1, -1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �Ƚϱ��������issue��push��Ϣ���issue
	 */
	private void checkPushIssue(int id, int articleId, int catId) {
		getScrollView().IndexClick();
		// (����û�����û�о�һ�ڵ�id����ôֱ�Ӹ�����ȡ����һ��)
		if (DataHelper.getIssueId(mContext) == -1) {
			CommonApplication.isFetchPush = true;
			getIssue(true);
		} else if (id != DataHelper.getIssueId(mContext)) {
			// (����û����صĻ���id�������������Ϣ�Ĳ�һ�£���ô��ʾ���Ƿ�鿴����һ��)
			pushArticleId = articleId;
			pushCatId = catId;
			showDialog(1);
		} else {
			// (����û����صĻ���id�������������Ϣ��һ�£���ô��ȡ�����������)
			pushArticleId = articleId;
			pushCatId = catId;
			// ����������ڵ�ǰ�����������У�ֱ�ӵ�������ҳ
			if (issue == null) {
				getIssue(true);
			} else {
				if (articleId != -1)
					gotoArticleActivity(pushArticleId, pushCatId, true);
			}
		}
	}

	/**
	 * �Ƚϰ汾
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
	 * ��ȡ����һ����־��Ϣ �鿴��һ��
	 * 
	 * @param fetchNew
	 *            �Ƿ�鿴��һ��(��һ�ν�Ӧ��Ĭ��true)
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
								// ����������ҳ
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
	 * ��ȡ����һ�ڵ���Ŀ�б�
	 * 
	 * @param Issue
	 * 
	 */
	private void getCatList() {
		errorType = 2;
		if (issue != null)
			// ���ؽ�����
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
	 * ��ȡ��ҳ����
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
	 * ��ȡ��Ŀ��ҳ
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
	 * �ż�Ч���е㿨���ӳ�20����ִ��
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
						// �Ժ�鿴
						if (id == 0) {
							getIssue(false);
						} else if (id == 1) {
							// push��Ϣ��ҳ
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
						// �����鿴
						if (id == 0) {
							DataHelper.setIssueId(CommonApplication.mContext,
									issue.getId());
							CommonApplication.columnUpdateTimeSame = true;
							CommonApplication.issueIdSame = true;
							CommonApplication.articleUpdateTimeSame = true;
							getCatList();
						} else if (id == 1) {
							// ���ͣ����µ�һ��
							CommonApplication.isFetchPush = true;
							getIssue(true);
						}
					}
				});
		return builder.create();
	}

	/**
	 * ��ʼ����Դ
	 */
	protected abstract void init();

	/**
	 * ��ʼ����Ŀ�б�
	 * 
	 * @param entry
	 */
	protected abstract void setDataForColumn(Entry entry);

	/**
	 * ��ʼ��index
	 * 
	 * @param entry
	 */
	protected abstract void setDataForIndex(Entry entry);

	protected abstract MainHorizontalScrollView getScrollView();

	/**
	 * ��ת������ҳ
	 * 
	 * @param artcleId
	 * @param isIndex
	 *            true:��ҳ��false:�ղ�
	 */
	protected abstract void gotoArticleActivity(int artcleId, int catId,
			boolean isIndex);

	/**
	 * indexview ��ʾloading
	 * 
	 * @param flag
	 *            0.����ʾ��1.��ʾloading��2.��ʾerror
	 */
	protected abstract void indexShowLoading(int flag);

	/**
	 * ������ҳtitle
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
