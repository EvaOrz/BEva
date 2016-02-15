package cn.com.modernmedia.breakpoint;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.mainprocess.TagMainProcessPreIssue;
import cn.com.modernmedia.newtag.mainprocess.TagMainProcessPreIssue.PreIssusType;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.PageTransfer;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 下载往期package时的processbar
 * 
 * @author ZhuQiao
 * 
 */
public class DownloadProcessView extends View {
	public static final int INIT = 0;
	public static final int DONE = 1;
	public static final int LOADING = 2;
	public static final int PAUSE = 3;

	private Context mContext;
	private int size = 100;
	private Paint mBgPaint;
	private Paint mProcessPaint;
	private float mSweepAngle = 0f;
	private int status = INIT;// false:pause;
	private BreakPointUtil mUtil;
	private PreIssusType mStyle;
	private TagInfo issue = new TagInfo();

	public DownloadProcessView(Context context) {
		this(context, null);
	}

	public DownloadProcessView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mBgPaint = new Paint();
		mBgPaint.setAntiAlias(true);
		mBgPaint.setColor(Color.WHITE);
		mBgPaint.setStyle(Style.FILL);// 实心

		mProcessPaint = new Paint();
		mProcessPaint = new Paint();
		mProcessPaint.setAntiAlias(true);
		mProcessPaint.setColor(Color.BLACK);
		mProcessPaint.setStyle(Style.FILL);

		mUtil = new BreakPointUtil(mContext, new DownloadPackageCallBack() {

			@Override
			public void onSuccess(String tagName, String folderName) {
				setSweepAngle(360);
				status = DONE;
				doSuccess(folderName);
			}

			@Override
			public void onProcess(String tagName, long complete, long total) {
				// status = LOADING;
				if (total > 0
						&& issue != null
						&& !TextUtils.isEmpty(issue.getIssueProperty()
								.getFullPackage())) {
					// TODO complete可能会超出Integer.MAX_VALUE
					float sweepAngle = complete * 360 / total;
					setSweepAngle(sweepAngle);
				}
			}

			@Override
			public void onFailed(String tagName) {
				status = INIT;
				Tools.showToast(mContext, R.string.net_error);
				Log.e("********", "网络出错111111");
			}

			@Override
			public void onPause(String tagName) {
				if (status == LOADING) {
					status = PAUSE;
					PrintHelper.print("PAUSE");
				}
			}
		});
	}

	public void setSize(int size) {
		this.size = size;
		invalidate();
	}

	public void setSweepAngle(float angle) {
		mSweepAngle = angle;
		invalidate();
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void onClick(TagInfo info, PreIssusType style,
			final ProgressBar... bars) {
		mStyle = style;
		issue = info;
		if (style == PreIssusType.REFRESH_INDEX) {
			TagMainProcessPreIssue mainProcess = new TagMainProcessPreIssue(
					mContext, null);
			mainProcess.getPreIssue(issue, null, PreIssusType.REFRESH_INDEX);
			return;
		}

		if (TextUtils.isEmpty(issue.getTagName()))
			return;
		boolean isCacheValid = checkCacheIsvalid();
		if (TextUtils.isEmpty(issue.getIssueProperty().getFullPackage())) {
			// 没有完全包
			status = INIT;
			setBarVisibility(false, bars);
			gotoAritcleActivity(!isCacheValid);
		} else if (!Tools.checkNetWork(mContext)) {
			Tools.showToast(mContext, R.string.net_error);
			Log.e("********", "网络出错222222");
		} else {
			checkPackage(bars);
		}
	}

	/**
	 * 检查下载包是否存在及当前状态，并进行相应的处理
	 * 
	 * @param issueId
	 * @param bars
	 */
	private void checkPackage(ProgressBar... bars) {
		switch (status) {
		case LOADING:
			status = PAUSE;
			mUtil.pause();
			PrintHelper.print("PAUSE");
			break;
		case PAUSE:
			status = LOADING;
			PrintHelper.print("reStart");
			mUtil.reStart();
			break;
		case INIT:
		case DONE:
			ModernMediaTools.downloadPackage(mContext, issue, mUtil);
			break;
		default:
			break;
		}
		setBarVisibility(false, bars);
	}

	public int getStatus() {
		return status;
	}

	private void setBarVisibility(boolean showLoad, ProgressBar... bars) {
		if (bars != null && bars.length > 0) {
			bars[0].setVisibility(showLoad ? VISIBLE : GONE);
		}
	}

	/**
	 * 下载成功之后的操作
	 */
	private void doSuccess(String folderName) {
		PrintHelper.print("doSuccess");
		if (mContext instanceof CommonMainActivity) {
			if (mStyle == PreIssusType.Zip_GO_TO_ARTICLE) {
				Tools.showLoading(mContext, false);
				ModernMediaTools.showLoadView(mContext, 0);
				TransferArticle article = new TransferArticle(-1,
						issue.getTagName(), issue.getParent(),
						ArticleType.Last, "", issue.getPublishTime(),
						folderName);
				PageTransfer.gotoArticleActivity(mContext, article);
			}
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		synchronized (canvas) {
			canvas.drawColor(Color.TRANSPARENT);
			// 背景白色的圆
			canvas.drawCircle(size / 2, size / 2, size / 2, mBgPaint);
			// 根据进度画扇形
			RectF oval = new RectF(0, 0, size, size);
			// useCenter true:扇形；false,椭圆(-90,从12点钟方向开始)
			canvas.drawArc(oval, -90, mSweepAngle, true, mProcessPaint);
		}
	}

	/**
	 * 检查缓存是否存在且有效，无效时要清空缓存(包括zip及解压的文件)
	 * 
	 * @return
	 */
	private boolean checkCacheIsvalid() {
		if (!TextUtils.equals(DataHelper.getLastIssuePublishTime(mContext,
				issue.getTagName()), issue.getPublishTime())) {
			// 时间变化
			// 删除可能的文章列表文件
			FileManager.deleteFile(ConstData.getLastArticlesFileName(issue
					.getTagName()));
			// 删除可能的完全包
			final String folder = issue.getIssueProperty().getFullPackage();
			if (!TextUtils.isEmpty(folder)) { // 存在zip包或者解压文件，将其删除
				if (FileManager.containThisPackageFolder(folder)) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							String folderPath = FileManager
									.getPackageNameByUrl(ModernMediaTools
											.getPackageFolderName(folder));
							FileManager.deletePackageFold(new File(folderPath));
						}
					}).start();
				}
				if (FileManager.containThisPackage(folder)) {
					FileManager.deletePackageByName(folder);
				}
			}
			DataHelper.setLastIssuePublishTime(mContext, issue.getTagName(),
					issue.getPublishTime());
			return false;
		}
		return FileManager.containFile(ConstData.getLastArticlesFileName(issue
				.getTagName()));
	}

	private void gotoAritcleActivity(boolean isCheckNet) {
		if (isCheckNet && !Tools.checkNetWork(mContext)) {
			Tools.showToast(mContext, R.string.net_error);
			Log.e("********", "网络出错333333");
			
		} else {
			TransferArticle article = new TransferArticle(-1,
					issue.getTagName(), issue.getParent(), ArticleType.Last,
					"", issue.getPublishTime(), "");
			PageTransfer.gotoArticleActivity(mContext, article);
		}
	}
}
