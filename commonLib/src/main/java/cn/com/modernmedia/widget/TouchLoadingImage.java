package cn.com.modernmedia.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.R;
import cn.com.modernmedia.widget.zoom.ZoomImageView;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;
import cn.com.modernmediaslate.unit.ImageScaleType;

/**
 * 可缩放，显示process的imageview
 * 
 * @author user
 * 
 */
public class TouchLoadingImage extends RelativeLayout implements
		ImageDownloadStateListener {
	private Context mContext;
	private ZoomImageView imageView;
	private RedProcess process;
	private int width, height, processSize;
	private int scaleType;
	private String url;

	public TouchLoadingImage(Context context, int width, int height) {
		this(context, 0, width, height);
	}

	public TouchLoadingImage(Context context, int scaleType, int width,
			int height) {
		super(context);
		mContext = context;
		this.scaleType = scaleType;
		this.width = width;
		this.height = height;
		init();
	}

	private void init() {
		processSize = mContext.getResources().getDimensionPixelSize(
				R.dimen.red_ring_size_small);
		imageView = new ZoomImageView(mContext);
		imageView.setCusScaleType(scaleType);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		imageView.setScaleType(ScaleType.MATRIX);
		imageView.setTag(R.id.scale_type, ImageScaleType.MATRIX);
		this.addView(imageView, lp);
		process = new RedProcess(mContext);
		lp = new LayoutParams(processSize, processSize);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.addView(process, lp);
	}

	public void setUrl(String url) {
		this.url = url;
		imageView.setImageBitmap(null);
		loading();
		if (TextUtils.isEmpty(url))
			return;
		CommonApplication.finalBitmap.display(imageView, url, width, height, this);
	}

	public ZoomImageView getImageView() {
		return imageView;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public void loading() {
		process.setVisibility(View.VISIBLE);
		process.start();
	}

	@Override
	public void loadOk(Bitmap bitmap, NinePatchDrawable drawable, byte[] gifByte) {
		process.stop();
		process.setVisibility(View.GONE);
	}

	@Override
	public void loadError() {
		process.setVisibility(View.VISIBLE);
		process.start();
	}

}
