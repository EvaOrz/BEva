package cn.com.modernmedia.listener;

import android.graphics.Bitmap;

/**
 * Í¼Æ¬ÏÂÔØ×´Ì¬
 * 
 * @author ZhuQiao
 * 
 */
public interface ImageDownloadStateListener {
	public void loading();

	public void loadOk(Bitmap bitmap);

	public void loadError();
	
}
