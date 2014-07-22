package cn.com.modernmedia.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import cn.com.modernmedia.util.FileManager;

/**
 * Listview 加载图片（只有当listview滑动停止时，加载当前显示的imageview）
 * 
 * @author ZhuQiao
 * 
 */
public class SyncImageLoader {
	private Object lock = new Object();
	private boolean mAllowLoad = true;
	private boolean firstLoad = true;
	private int mStartLoadLimit = 0;
	private int mStopLoadLimit = 0;

	final Handler handler = new Handler();
	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	private static SyncImageLoader instance = null;

	public interface OnImageLoadListener {
		public void onImageLoad(Integer t, Bitmap bitmap);

		public void onError(Integer t);
	}

	private SyncImageLoader() {
	}

	public static SyncImageLoader getInstance() {
		if (instance == null)
			instance = new SyncImageLoader();
		return instance;
	}

	public void setLoadLimit(int startLoadLimit, int stopLoadLimit) {
		if (startLoadLimit > stopLoadLimit) {
			return;
		}
		mStartLoadLimit = startLoadLimit;
		mStopLoadLimit = stopLoadLimit;
	}

	public void restore() {
		mAllowLoad = true;
		firstLoad = true;
	}

	public void lock() {
		mAllowLoad = false;
		firstLoad = false;
	}

	public void unlock() {
		mAllowLoad = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	/**
	 * 加载图片
	 * 
	 * @param t
	 * @param imageUrl
	 * @param listener
	 */
	public void loadImage(Integer t, String imageUrl,
			OnImageLoadListener listener) {
		final OnImageLoadListener mListener = listener;
		final String mImageUrl = imageUrl;
		final Integer mt = t;
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (!mAllowLoad) {
					synchronized (lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				if (mAllowLoad && firstLoad) {
					loadImage(mImageUrl, mt, mListener);
				}

				if (mAllowLoad && mt <= mStopLoadLimit && mt >= mStartLoadLimit) {
					loadImage(mImageUrl, mt, mListener);
				}
			}

		}).start();
	}

	/**
	 * 获取图片
	 * 
	 * @param mImageUrl
	 * @param mt
	 * @param mListener
	 */
	private void loadImage(final String mImageUrl, final Integer mt,
			final OnImageLoadListener mListener) {
		final Bitmap bitmap = getBitmapFromCache(mImageUrl);
		if (bitmap != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (mAllowLoad) {
						mListener.onImageLoad(mt, bitmap);
					}
				}
			});
			return;
		}
		try {
			final Bitmap urlBitmap = downLoadBitmap(mImageUrl);
			addBitmapToCache(mImageUrl, urlBitmap);
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (mAllowLoad) {
						mListener.onImageLoad(mt, urlBitmap);
					}
				}
			});
		} catch (Exception e) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					mListener.onError(mt);
				}
			});
			e.printStackTrace();
		}
	}

	/**
	 * 从网络下载图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap downLoadBitmap(String url) {
		final HttpClient client = new DefaultHttpClient();
		HttpGet getRequest = null;
		InputStream inputStream = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		HttpEntity entity = null;
		try {
			getRequest = new HttpGet(url);
			client.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT,
					20000);
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			entity = response.getEntity();
			if (entity != null) {
				inputStream = entity.getContent();
				return BitmapFactory.decodeStream(inputStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (entity != null) {
					entity.consumeContent();
				}
				if (inputStream != null) {
					inputStream.close();
				}
				if (getRequest != null) {
					getRequest.abort();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 添加到缓存
	 * 
	 * @param url
	 * @param bitmap
	 */
	private void addBitmapToCache(String url, Bitmap bitmap) {
		if (TextUtils.isEmpty(url) || bitmap == null)
			return;
		imageCache.put(url, new SoftReference<Bitmap>(bitmap));
		FileManager.saveImageToFile(bitmap, url);
	}

	/**
	 * 从缓存中拿图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapFromCache(String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		Bitmap bitmap = null;
		if (imageCache.containsKey(url)) {
			bitmap = imageCache.get(url).get();
			if (bitmap != null)
				return bitmap;
		}
		return FileManager.getImageFromFile(url);
	}
}
