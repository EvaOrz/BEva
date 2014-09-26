package cn.com.modernmedia.views.index;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.breakpoint.DownloadProcessView;
import cn.com.modernmedia.model.TagInfoList.IssueProperty;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.mainprocess.TagMainProcessPreIssue.PreIssusType;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.adapter.IssueListAdapter;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 期刊列表单个期刊View
 * 
 * @author jiancong
 * 
 */
public class IssueItemView extends RelativeLayout {
	private Context mContext;
	private ImageView image, down;
	private TextView title, desc;
	private DownloadProcessView pro;
	private ProgressBar bar;
	private int imgWidth, imgHeight;

	public IssueItemView(Context context) {
		this(context, null);
	}

	public IssueItemView(Context context, AttributeSet attr) {
		super(context, attr);
		this.mContext = context;
		init();
	}

	private void init() {
		this.addView(LayoutInflater.from(mContext).inflate(
				R.layout.issue_list_item_single, null));
		image = (ImageView) findViewById(R.id.issue_pic);
		title = (TextView) findViewById(R.id.issue_title);
		desc = (TextView) findViewById(R.id.issue_desc);
		pro = (DownloadProcessView) findViewById(R.id.issue_pro);
		bar = (ProgressBar) findViewById(R.id.issue_process);
		down = (ImageView) findViewById(R.id.issue_down);
	}

	/**
	 * 设置view的相关控件
	 * 
	 * @param item
	 * @param parm
	 * @param adapter
	 */
	public void setData(final TagInfo item, IssueListAdapter adapter) {
		if (item == null)
			return;
		// 设置占位图
		IssueProperty issueProperty = item.getIssueProperty();
		image.setScaleType(ScaleType.CENTER);
		V.setImage(image, "placeholder");
		// 下载图片
		if (ParseUtil.listNotNull(issueProperty.getPictureList())) {
			CommonApplication.finalBitmap.display(image, issueProperty
					.getPictureList().get(0));
		}
		title.setText(issueProperty.getTitle());
		desc.setText(getDate(issueProperty.getStartTime(),
				issueProperty.getEndTime()));
		bar.setVisibility(pro.getStatus() == DownloadProcessView.LOADING ? View.VISIBLE
				: View.GONE);
		// 设置下载进度
		if (adapter != null) {
			adapter.setProcess(pro, down, item.getTagName());
		}

		if (getVisibility() == View.VISIBLE) {
			image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pro.onClick(item, PreIssusType.Zip_GO_TO_ARTICLE, bar);
				}
			});
		}
	}

	/**
	 * 格式化时间,拼成yyyy-MM-dd~MM-dd
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	protected String getDate(String startTime, String endTime) {
		return DateFormatTool.format2(ParseUtil.stol(startTime) * 1000,
				ParseUtil.stol(endTime) * 1000, "yyyy-MM-dd", "MM-dd", "~");
	}

	/**
	 * 计算图片大小
	 * 
	 * @param reduce
	 *            view的大小
	 */
	private void computerImageSize(int viewWidth) {
		imgWidth = viewWidth
				- mContext.getResources().getDimensionPixelSize(
						R.dimen.issue_pic_marginleft);
		imgWidth -= mContext.getResources().getDimensionPixelSize(
				R.dimen.issue_pic_marginright);
		imgWidth -= 2 * mContext.getResources().getDimension(
				R.dimen.issue_pic_spacing);
		imgWidth /= 3;
		imgHeight = 256 * imgWidth / 180;
	}

	/**
	 * 设置图片大小
	 * 
	 * @param viewWidth
	 *            view的大小
	 */
	public void setImgSize(int viewWidth) {
		computerImageSize(viewWidth);
		image.getLayoutParams().width = imgWidth;
		image.getLayoutParams().height = imgHeight;
		pro.setSize(mContext.getResources().getDimensionPixelSize(
				R.dimen.issue_process_size));
		// textview的大小与图片相同
		title.setWidth(imgWidth);
		desc.setWidth(imgWidth);
		title.setGravity(Gravity.CENTER_HORIZONTAL);
		desc.setGravity(Gravity.CENTER_HORIZONTAL);
	}
}
