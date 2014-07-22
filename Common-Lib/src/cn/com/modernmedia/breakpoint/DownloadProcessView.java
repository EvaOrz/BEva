package cn.com.modernmedia.breakpoint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.util.ViewPreviousIssue;
import cn.com.modernmedia.util.ViewPreviousIssue.FetchPreviousIssueCallBack;

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
	public static final int HTTP = 4;

	private Context mContext;
	private int size = 100;
	private Paint mBgPaint;
	private Paint mProcessPaint;
	private float mSweepAngle = 0f;
	private int status = INIT;// false:pause;
	private BreakPointUtil mUtil;
	private Issue mIssue;
	private int mStyle;

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
			public void onSuccess(int issueId, String folderName) {
				setSweepAngle(360);
				status = DONE;
				doSuccess(folderName);
			}

			@Override
			public void onProcess(int issueId, long complete, long total) {
				// status = LOADING;
				if (total > 0 && mIssue != null
						&& !TextUtils.isEmpty(mIssue.getFullPackage())) {
					// TODO complete可能会超出Integer.MAX_VALUE
					float sweepAngle = complete * 360 / total;
					setSweepAngle(sweepAngle);
				}
			}

			@Override
			public void onFailed(int issueId) {
				status = INIT;
				ModernMediaTools.showToast(mContext, R.string.net_error);
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

	public int getStyle() {
		return mStyle;
	}

	public void onClick(final int issueId, int style, final ProgressBar... bars) {
		mStyle = style;
		if (mIssue != null && mIssue.getId() == issueId) {// 防止adapter复用
			if (TextUtils.isEmpty(mIssue.getFullPackage())) {
				return;
			}
			if (status == LOADING) {
				status = PAUSE;
				System.out.println("PAUSE");
				mUtil.pause();
			} else if (status == PAUSE) {
				status = LOADING;
				System.out.println("reStart");
				mUtil.reStart();
			} else if (status == INIT) {
				ModernMediaTools.downloadPackage(mContext, mIssue, mUtil);
			} else if (status == DONE) {
				ModernMediaTools.downloadPackage(mContext, mIssue, mUtil);
			}
			setBarVisibility(false, bars);
			return;
		}
		if (status == HTTP)
			return;
		ViewPreviousIssue preIssue = new ViewPreviousIssue(mContext);
		status = HTTP;
		setBarVisibility(true, bars);
		preIssue.getIssue(issueId, style, new FetchPreviousIssueCallBack() {

			@Override
			public void onSuccess(Issue issue) {
				setBarVisibility(false, bars);
				mIssue = issue;
				status = LOADING;
				ModernMediaTools.downloadPackage(mContext, issue, mUtil);
			}

			@Override
			public void onFailed() {
				setBarVisibility(false, bars);
				status = INIT;
			}
		});
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
		System.out.println("doSuccess");
		if (mContext instanceof CommonMainActivity) {
			if (mStyle == ViewPreviousIssue.REFRESH_INDEX) {
				// TODO 如果查看往期之后需要显示当前往期index，则把main activity的issue改为当前issue
				// ((CommonMainActivity) mContext).setIssue(mIssue);
				// ((CommonMainActivity) mContext).getCatList();
			} else if (mStyle == ViewPreviousIssue.GO_TO_ARTICLE) {
				ModernMediaTools.showLoading(mContext, false);
				CommonApplication.currentIssueId = mIssue.getId();
				((CommonMainActivity) mContext).setReSetIssueId(true);
				TransferArticle article = new TransferArticle(mIssue, -1, -1,
						ArticleType.Default, "", folderName);
				((CommonMainActivity) mContext).gotoArticleActivity(article);
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

}
