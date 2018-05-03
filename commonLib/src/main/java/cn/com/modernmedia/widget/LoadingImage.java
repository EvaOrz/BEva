package cn.com.modernmedia.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.R;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;

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

	public LoadingImage(Context context, int width, int height) {
		super(context);
		mContext = context;
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

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setUrl(String url) {
		imageView.setImageBitmap(null);
		if (!TextUtils.isEmpty(tag)) {
			imageView.setTag(R.id.scale_type, tag);
		}
		loading();
		if (TextUtils.isEmpty(url))
			return;
		if (width == -1 || height == -1)
			CommonApplication.finalBitmap.display(imageView, url, this);
		else
			CommonApplication.finalBitmap.display(imageView, url, width,
					height, this);
	}

	public RedProcess getProcess() {
		return process;
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
		if (drawable != null)
			imageView.setImageDrawable(drawable);
		else
			imageView.setImageBitmap(bitmap);
	}

	@Override
	public void loadError() {
		process.setVisibility(View.VISIBLE);
		process.start();
	}


}
