package cn.com.modernmedia.views.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.ImageDownloadStateListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.Atlas.AtlasPicture;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.listener.GalleryScrollListener;
import cn.com.modernmedia.views.listener.NotifyLastestChangeListener;
import cn.com.modernmedia.widget.RedProcess;
import cn.com.modernmediaslate.model.Entry;

/**
 * 画报图片item
 * 
 * @author user
 * 
 */
public class VerticalGalleryItem extends RelativeLayout implements
		ImageDownloadStateListener, GalleryScrollListener {
	private Context mContext;
	private ImageView img, newImg;
	private TextView title, desc;
	private RedProcess process;

	private String url = "";
	private int articleId;

	public VerticalGalleryItem(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		this.addView(LayoutInflater.from(mContext).inflate(
				R.layout.vertical_gallery_item, null));
		img = (ImageView) findViewById(R.id.vertical_gallery_img);
		newImg = (ImageView) findViewById(R.id.vertical_gallery_new_img);
		title = (TextView) findViewById(R.id.vertical_gallery_title);
		desc = (TextView) findViewById(R.id.vertical_gallery_desc);
		process = (RedProcess) findViewById(R.id.vertical_gallery_process);

		img.getLayoutParams().width = VerticalFlowGallery.childWidth
				- mContext.getResources().getDimensionPixelSize(R.dimen.dp5);// 空出标题图片
		img.getLayoutParams().height = VerticalFlowGallery.childHeight;
	}

	/**
	 * 设置数据
	 * 
	 * @param entry
	 * @param startLoad
	 *            是否开始加载
	 * @param showNew
	 *            是否显示new图片
	 */
	public void setData(Entry entry, boolean startLoad, boolean showNew) {
		if (entry instanceof ArticleItem) {
			ArticleItem item = (ArticleItem) entry;
			if (ParseUtil.listNotNull(item.getPicList())) {
				url = item.getPicList().get(0);
			} else if (ParseUtil.listNotNull(item.getThumbList())) {
				url = item.getThumbList().get(0);
			}
			title.setText(item.getTitle());
			desc.setText(item.getDesc());
			articleId = item.getArticleId();
		} else if (entry instanceof AtlasPicture) {
			AtlasPicture picture = (AtlasPicture) entry;
			url = picture.getUrl();
			title.setText(picture.getTitle());
			desc.setText(picture.getDesc());
			articleId = picture.getArticleId();
		}
		if (!showNew) {
			newImg.setVisibility(View.GONE);
		}
		loading();
		if (startLoad) {
			CommonApplication.finalBitmap.display(img, url, this);
		}
		if (showNew) {
			ViewsApplication.addListener(url,
					new NotifyLastestChangeListener() {

						@Override
						public void changeCount() {
							if (ReadDb.getInstance(mContext)
									.isReaded(articleId)) {
								newImg.setVisibility(View.GONE);
							}
						}
					});
		}
	}

	@Override
	public void loading() {
		process.setVisibility(View.VISIBLE);
		process.start();
	}

	@Override
	public void loadOk(Bitmap bitmap) {
		process.stop();
		process.setVisibility(View.GONE);
	}

	@Override
	public void loadError() {
		process.setVisibility(View.VISIBLE);
		process.start();
	}

	@Override
	public void scrolling(int currentX, int position) {
	}

	@Override
	public void scrollEnd(int currentIndex) {
		loading();
		CommonApplication.finalBitmap.display(img, url, this);
	}

	@Override
	public void destoryItem(int position) {
		CommonApplication.callGc();
	}
}
