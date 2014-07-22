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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.com.modernmedia.util.FileManager;

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
	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

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
								iv.setScaleType(ScaleType.FIT_XY);
								iv.setImageBitmap(bitmap);
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

	private ImageDownloader() {
		for (int i = 0; i < POOL_SIZE; i++) {
			imgMap.put(String.valueOf(i), new FetchImgThread());
		}
		if (mTask == null) {
			mTask = new ImgHandlerThread("imgtask");
			mTask.start();
			taskHandler = new Handler(mTask.getLooper(), mTask);
		}
	}

	public static ImageDownloader getInstance() {
		if (imageDownloader == null) {
			imageDownloader = new ImageDownloader();
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
		if (!imageCache.isEmpty()) {
			imageCache.clear();
		}
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
		try {
			Bitmap bitmap = getBitmapFromCache(url);
			if (bitmap == null) {
				// if (imgViewMap.containsKey(url)) {
				// return;
				// }
				// if (checkDupliUrl(url)) {
				// return;
				// }
				imgViewMap.put(url, imageView);
				if (mTask != null) {
					Message msg = taskHandler.obtainMessage();
					msg.what = 1000;
					msg.obj = url;
					taskHandler.sendMessage(msg);
				}
			} else {
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setImageBitmap(bitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
}
