package cn.com.modernmedia.api;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.R;
import cn.com.modernmedia.listener.ImageDownloadStateListener;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.ImageScaleType;
import cn.com.modernmedia.util.PrintHelper;

/**
 * 图片下载工具
 * 
 * @author ZhuQiao
 * 
 */
public class ImageDownloader {
	private static final int POOL_SIZE = 20;

	private static ImageDownloader imageDownloader = null;
	private BlockingQueue<String> mUrlQueue = new ArrayBlockingQueue<String>(
			100);
	private ConcurrentHashMap<String, ImageView> imgViewMap = new ConcurrentHashMap<String, ImageView>();
	private ConcurrentHashMap<String, FetchImgThread> imgMap = new ConcurrentHashMap<String, ImageDownloader.FetchImgThread>();
	// private HashMap<String, SoftReference<Bitmap>> imageCache = new
	// HashMap<String, SoftReference<Bitmap>>();
	private LruCache<String, Bitmap> mLruCache;
	private HashMap<String, SoftReference<ImageDownloadStateListener>> listenerMap = new HashMap<String, SoftReference<ImageDownloadStateListener>>();

	private ImgHandlerThread mTask = null;

	private Handler taskHandler = null;
	private static boolean allowLoad = true;
	private Object lock = new Object();

	/**
	 * 锁住时不允许加载图片
	 */
	public void lock() {
		allowLoad = false;
	}

	/**
	 * 解锁时加载图片
	 */
	public void unlock() {
		allowLoad = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	private class ImgHandlerThread extends HandlerThread implements Callback {

		public ImgHandlerThread(String name) {
			super(name);
		}

		// 为了低api，修改quit方法。
		@Override
		public boolean quit() {
			Looper l = getLooper();
			if (l != null) {
				l.quit();
				return true;
			}
			return false;
			// return super.quit();
		}

		public FetchImgThread getWorker() {
			FetchImgThread t = null;
			for (String key : imgMap.keySet()) {
				State state = imgMap.get(key).getState();
				if (Thread.State.NEW == state) {
					t = imgMap.get(key);
					t.setName("FetchImgThread-" + key);
					break;
				} else if (Thread.State.TERMINATED == state) {
					t = new FetchImgThread();
					t.setName("FetchImgThread-" + key);
					imgMap.put(key, t);
					break;
				}
			}
			return t;
		}

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 1000) {
				String url = (String) msg.obj;
				if (!TextUtils.isEmpty(url)) {
					FetchImgThread f = getWorker();
					if (f != null) {
						f.setUrl(url);
						f.start();
					} else {
						return false;
					}
				}
			}
			return true;
		}
	}

	private class FetchImgThread extends Thread {
		private String url = "";

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public void run() {
			if (!allowLoad) {
				synchronized (lock) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (TextUtils.isEmpty(url)) {
				return;
			} else {
				Bitmap bitmap = downLoadBitmap(url);
				if (bitmap != null) {
					initImg(url, bitmap);
					addBitmapToCache(url, bitmap);
				} else {
					initImg(url, null);
				}
			}
		}

	}

	private void initImg(final String url, final Bitmap bitmap) {
		if (!TextUtils.isEmpty(url)) {
			if (imgViewMap.containsKey(url)) {
				final ImageView iv = imgViewMap.get(url);
				if (iv != null && iv.getTag().equals(url)) {
					iv.post(new Runnable() {

						@Override
						public void run() {
							if (bitmap != null) {
								// iv.setScaleType(ScaleType.FIT_XY);
								setScaleType(iv);
								iv.setImageBitmap(bitmap);
								askListener(url, 1, bitmap);
							} else {
								askListener(url, 2, null);
							}
							try {
								if (mUrlQueue.contains(url)) {
									mUrlQueue.remove(url);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
				imgViewMap.remove(url);
			}
			if (bitmap != null) {
				addBitmapToCache(url, bitmap);
			}
		}
	}

	private ImageDownloader(Context context) {
		mLruCache = new LruCache<String, Bitmap>(getCacheSize(context)) {

			@Override
			protected Bitmap create(String key) {
				return super.create(key);
			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				PrintHelper.print("entryRemoved");
				super.entryRemoved(evicted, key, oldValue, newValue);
			}

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}

		};
		for (int i = 0; i < POOL_SIZE; i++) {
			imgMap.put(String.valueOf(i), new FetchImgThread());
		}
		if (mTask == null) {
			mTask = new ImgHandlerThread("imgtask");
			mTask.start();
			taskHandler = new Handler(mTask.getLooper(), mTask);
		}
	}

	public static ImageDownloader getInstance(Context context) {
		if (imageDownloader == null) {
			imageDownloader = new ImageDownloader(context);
		}
		allowLoad = true;
		return imageDownloader;
	}

	/**
	 * 离开应用时回收相关数据
	 */
	public void destroy() {
		if (!imgViewMap.isEmpty()) {
			imgViewMap.clear();
		}
		if (!imgMap.isEmpty()) {
			imgMap.clear();
		}
		if (!mUrlQueue.isEmpty()) {
			mUrlQueue.clear();
		}
		// if (!imageCache.isEmpty()) {
		// imageCache.clear();
		// }
		if (mLruCache != null && mLruCache.size() > 0) {
			mLruCache.evictAll();
			mLruCache = null;
		}
		if (!listenerMap.isEmpty())
			listenerMap.clear();
		imageDownloader = null;
		System.gc();
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
		// imageCache.put(url, new SoftReference<Bitmap>(bitmap));
		if (this != null && mLruCache != null)
			mLruCache.put(url, bitmap);
		FileManager.saveImageToFile(bitmap, url);
		CommonApplication.callGc();
	}

	/**
	 * 从缓存中拿图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapFromCache(String url, int width, int height) {
		if (TextUtils.isEmpty(url) || this == null) {
			return null;
		}
		Bitmap bitmap = null;
		if (mLruCache != null) {
			bitmap = mLruCache.get(url);
			if (bitmap != null && !bitmap.isRecycled())
				return bitmap;
			else
				try {
					mLruCache.remove(url);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		if (width != 0) {
			bitmap = FileManager.getImageFromFile(url, width, height);
		} else {
			bitmap = FileManager.getImageFromFile(url);
		}
		if (bitmap != null && mLruCache != null) {
			mLruCache.put(url, bitmap);
		}
		return bitmap;
	}

	private Bitmap getBitmapFromCache(String url) {
		return getBitmapFromCache(url, 0, 0);
	}

	/**
	 * 
	 * @param url
	 * @param mode
	 *            0.loading,1.loadok,2loaderror
	 */
	private void askListener(String url, int mode, Bitmap bitmap) {
		if (!listenerMap.isEmpty() && listenerMap.containsKey(url)) {
			ImageDownloadStateListener listener = listenerMap.get(url).get();
			if (mode == 0) {
				listener.loading();
			} else if (mode == 1) {
				listener.loadOk(bitmap);
			} else if (mode == 2) {
				listener.loadError();
			}
		}
	}

	/**
	 * 设置imageview的scaletype,默认fit_xy
	 * 
	 * @param iv
	 */
	private void setScaleType(ImageView iv) {
		if (iv.getTag(R.id.scale_type) != null) {
			String scale_type = (String) iv.getTag(R.id.scale_type);
			if (!TextUtils.isEmpty(scale_type)
					&& scale_type.equals(ImageScaleType.CENTER_CROP)) {
				iv.setScaleType(ScaleType.CENTER_CROP);
				return;
			}
		}
		iv.setScaleType(ScaleType.FIT_XY);
	}

	@SuppressWarnings("unused")
	private boolean checkDupliUrl(String url) {
		boolean du = false;
		try {
			if (mUrlQueue.contains(url)) {
				du = true;
			} else {
				mUrlQueue.add(url);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mUrlQueue.poll();
			return du;
		}
		return du;
	}

	public void download(String url, ImageView imageView) {
		if (TextUtils.isEmpty(url))
			return;
		imageView.setTag(url);
		Bitmap bitmap = getBitmapFromCache(url);
		fetchImage(bitmap, url, imageView, null);
	}

	public void downloadLoadingImage(String url, ImageView imageView,
			ImageDownloadStateListener listener) {
		if (TextUtils.isEmpty(url))
			return;
		imageView.setTag(url);
		listenerMap.put(url, new SoftReference<ImageDownloadStateListener>(
				listener));
		askListener(url, 0, null);
		Bitmap bitmap = getBitmapFromCache(url);
		fetchImage(bitmap, url, imageView, listener);
	}

	public void downloadLoadingImage(String url, ImageView imageView,
			int width, int height, ImageDownloadStateListener listener) {
		if (TextUtils.isEmpty(url))
			return;
		imageView.setTag(url);
		listenerMap.put(url, new SoftReference<ImageDownloadStateListener>(
				listener));
		askListener(url, 0, null);
		Bitmap bitmap = getBitmapFromCache(url, width, height);
		fetchImage(bitmap, url, imageView, listener);
	}

	private void fetchImage(Bitmap cacheBitmap, String url,
			ImageView imageView, ImageDownloadStateListener listener) {
		try {
			if (cacheBitmap == null) {
				imgViewMap.put(url, imageView);
				if (mTask != null) {
					Message msg = taskHandler.obtainMessage();
					msg.what = 1000;
					msg.obj = url;
					taskHandler.sendMessage(msg);
				}
			} else {
				if (listener != null)
					askListener(url, 1, cacheBitmap);
				setScaleType(imageView);
				imageView.setImageBitmap(cacheBitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 直接获得图片
	 * 
	 * @param url
	 * @param listener
	 */
	public void fetchImage(String url, ImageDownloadStateListener listener,
			int width, int height) {
		if (TextUtils.isEmpty(url))
			return;
		listenerMap.put(url, new SoftReference<ImageDownloadStateListener>(
				listener));
		askListener(url, 0, null);
		try {
			Bitmap bitmap = getBitmapFromCache(url, width, height);
			if (bitmap == null) {
				FetchImageTask task = new FetchImageTask();
				task.execute(url);
			} else {
				askListener(url, 1, bitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 仅限单品页加载大图
	 **/
	private class FetchImageTask extends AsyncTask<String, Void, Bitmap> {
		private String url;

		@Override
		protected Bitmap doInBackground(String... params) {
			url = params[0];
			if (url == null) {
				return null;
			} else {
				return downLoadBitmap(url);
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				addBitmapToCache(url, result);
				askListener(url, 1, result);
			} else {
				askListener(url, 2, null);
			}
		}
	}

	/**
	 * 下载进版广告图
	 * 
	 * @param url
	 */
	public void download(final String url) {
		if (TextUtils.isEmpty(url))
			return;
		if (FileManager.getImageFromFile(url) != null)
			return;
		new Thread(new Runnable() {

			@Override
			public void run() {
				Bitmap bitmap = downLoadBitmap(url);
				if (bitmap != null)
					FileManager.saveImageToFile(bitmap, url);
			}
		}).start();
	}

	/**
	 * 从网络下载图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap downLoadBitmap(String url) {
		HttpGet httpRequest = null;
		final HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;
		InputStream is = null;
		HttpEntity entity = null;
		try {
			httpRequest = new HttpGet(url);
			httpclient.getParams().setParameter(
					HttpConnectionParams.SO_TIMEOUT, 20000);
			response = httpclient.execute(httpRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			entity = response.getEntity();
			if (entity != null) {
				BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(
						entity);
				is = bufferedHttpEntity.getContent();
				return BitmapFactory.decodeStream(is);
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
				if (is != null) {
					is.close();
				}
				if (httpRequest != null) {
					httpRequest.abort();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private int getCacheSize(Context context) {
		// According to the phone memory, set a proper cache size for LRU cache
		// dynamically.
		final ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		final int memClass = am.getMemoryClass();

		int cacheSize;
		if (memClass <= 24) {
			cacheSize = (memClass << 20) / 24;
		} else if (memClass <= 36) {
			cacheSize = (memClass << 20) / 18;
		} else if (memClass <= 48) {
			cacheSize = (memClass << 20) / 12;
		} else {
			cacheSize = (memClass << 20) >> 3;
		}
		PrintHelper.print("cacheSize == " + cacheSize);
		return cacheSize;
	}
}
