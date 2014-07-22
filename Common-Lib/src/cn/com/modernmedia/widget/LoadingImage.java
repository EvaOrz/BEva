package cn.com.modernmedia.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.R;
import cn.com.modernmedia.listener.ImageDownloadStateListener;

/**
 * 显示process的imageview
 * 
 * @author ZhuQiao
 * 
 */
public class LoadingImage extends RelativeLayout implements
		ImageDownloadStateListener {
	private Context mContext;
	private ImageView imageView;
	private RedProcess process;
	private String tag = "";
	private int width, height;

	public LoadingImage(Context context, String tag, int width, int height) {
		super(context);
		mContext = context;
		this.tag = tag;
		this.width = width;
		this.height = height;
		init();
	}

	private void init() {
		this.addView(LayoutInflater.from(mContext).inflate(
				R.layout.loading_image, null));
		imageView = (ImageView) findViewById(R.id.loading_image);
		process = (RedProcess) findViewById(R.id.loading_red_process);
	}

	public void setUrl(String url) {
		imageView.setImageBitmap(null);
		if (!TextUtils.isEmpty(tag))
			imageView.setTag(R.id.scale_type, tag);
		loading();
		if (TextUtils.isEmpty(url))
			return;
		CommonApplication.getImageDownloader().download(url, imageView, width,
				height, this);
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

}
