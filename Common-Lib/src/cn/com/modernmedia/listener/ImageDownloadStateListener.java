package cn.com.modernmedia.listener;

import android.graphics.Bitmap;

/**
 * 图片下载状态
 * 
 * @author ZhuQiao
 * 
 */
public interface ImageDownloadStateListener {
	public void loading();

	public void loadOk(Bitmap bitmap);

	public void loadError();

}
