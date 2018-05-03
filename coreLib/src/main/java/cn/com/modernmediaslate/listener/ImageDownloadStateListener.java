package cn.com.modernmediaslate.listener;

import android.graphics.Bitmap;
import android.graphics.drawable.NinePatchDrawable;

/**
 * 图片下载状态
 * 
 * @author ZhuQiao
 * 
 */
public interface ImageDownloadStateListener {
	public void loading();

	public void loadOk(Bitmap bitmap, NinePatchDrawable drawable, byte[] gifByte);

	public void loadError();

}
