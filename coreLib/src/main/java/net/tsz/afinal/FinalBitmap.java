package net.tsz.afinal;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.tsz.afinal.bitmap.core.BitmapCache;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import net.tsz.afinal.bitmap.core.BitmapProcess;
import net.tsz.afinal.bitmap.download.Downloader;
import net.tsz.afinal.bitmap.download.SimpleDownloader;
import net.tsz.afinal.core.AsyncTask;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.com.modernmedia.widget.GifView;
import cn.com.modernmediaslate.R;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;
import cn.com.modernmediaslate.unit.ImageScaleType;

public class FinalBitmap {
	private FinalBitmapConfig mConfig;
	private BitmapCache mImageCache;
	private BitmapProcess mBitmapProcess;
	private boolean mExitTasksEarly = false;
	private boolean mPauseWork = false;
	private final Object mPauseWorkLock = new Object();
	private Context mContext;
	private boolean mInit = false;
	private ExecutorService bitmapLoadAndDisplayExecutor;

	private static FinalBitmap mFinalBitmap;

	// //////////////////////// config method
	// start////////////////////////////////////
	private FinalBitmap(Context context) {
		mContext = context;
		mConfig = new FinalBitmapConfig(context);
		configDownlader(new SimpleDownloader());// 配置下载器
		init();
	}

	/**
	 * 创建finalbitmap
	 * 
	 * @param ctx
	 * @return
	 */
	public static synchronized FinalBitmap create(Context ctx) {
		if (mFinalBitmap == null) {
			mFinalBitmap = new FinalBitmap(ctx.getApplicationContext());
		}
		return mFinalBitmap;
	}

	/**
	 * 设置图片正在加载的时候显示的图片
	 * 
	 * @param bitmap
	 */
	public FinalBitmap configLoadingImage(Bitmap bitmap) {
		mConfig.defaultDisplayConfig.setLoadingBitmap(bitmap);
		return this;
	}

	/**
	 * 设置图片正在加载的时候显示的图片
	 * 
	 * @param bitmap
	 */
	public FinalBitmap configLoadingImage(int resId) {
		mConfig.defaultDisplayConfig.setLoadingBitmap(BitmapFactory
				.decodeResource(mContext.getResources(), resId));
		return this;
	}

	/**
	 * 设置图片加载失败时候显示的图片
	 * 
	 * @param bitmap
	 */
	public FinalBitmap configLoadfailImage(Bitmap bitmap) {
		mConfig.defaultDisplayConfig.setLoadfailBitmap(bitmap);
		return this;
	}

	/**
	 * 设置图片加载失败时候显示的图片
	 * 
	 * @param resId
	 */
	public FinalBitmap configLoadfailImage(int resId) {
		mConfig.defaultDisplayConfig.setLoadfailBitmap(BitmapFactory
				.decodeResource(mContext.getResources(), resId));
		return this;
	}

	/**
	 * 配置默认图片的小的高度
	 * 
	 * @param bitmapHeight
	 */
	public FinalBitmap configBitmapMaxHeight(int bitmapHeight) {
		mConfig.defaultDisplayConfig.setBitmapHeight(bitmapHeight);
		return this;
	}

	/**
	 * 配置默认图片的小的宽度
	 * 
	 * @param bitmapHeight
	 */
	public FinalBitmap configBitmapMaxWidth(int bitmapWidth) {
		mConfig.defaultDisplayConfig.setBitmapWidth(bitmapWidth);
		return this;
	}

	/**
	 * 设置下载器，比如通过ftp或者其他协议去网络读取图片的时候可以设置这项
	 * 
	 * @param downlader
	 * @return
	 */
	public FinalBitmap configDownlader(Downloader downlader) {
		mConfig.downloader = downlader;
		return this;
	}

	/**
	 * 配置内存缓存大小 大于2MB以上有效
	 * 
	 * @param size
	 *            缓存大小
	 */
	public FinalBitmap configMemoryCacheSize(int size) {
		mConfig.memCacheSize = size;
		return this;
	}

	/**
	 * 设置加载图片的线程并发数量
	 * 
	 * @param size
	 */
	public FinalBitmap configBitmapLoadThreadSize(int size) {
		if (size >= 1)
			mConfig.poolSize = size;
		return this;
	}

	/**
	 * 配置是否立即回收图片资源
	 * 
	 * @param recycleImmediately
	 * @return
	 */
	public FinalBitmap configRecycleImmediately(boolean recycleImmediately) {
		mConfig.recycleImmediately = recycleImmediately;
		return this;
	}

	/**
	 * 初始化finalBitmap
	 * 
	 * @return
	 */
	private FinalBitmap init() {
		if (!mInit) {
			BitmapCache.ImageCacheParams imageCacheParams = new BitmapCache.ImageCacheParams();
			imageCacheParams.setMemCacheSize(mConfig.memCacheSize);

			imageCacheParams.setRecycleImmediately(mConfig.recycleImmediately);
			// init Cache
			mImageCache = new BitmapCache(imageCacheParams);

			// init Executors
			bitmapLoadAndDisplayExecutor = Executors.newFixedThreadPool(
					mConfig.poolSize, new ThreadFactory() {
						@Override
						public Thread newThread(Runnable r) {
							Thread t = new Thread(r);
							// 设置线程的优先级别，让线程先后顺序执行（级别越高，抢到cpu执行的时间越多）
							t.setPriority(Thread.NORM_PRIORITY - 1);
							return t;
						}
					});

			// init BitmapProcess
			mBitmapProcess = new BitmapProcess(mConfig.downloader, mImageCache);

			mInit = true;
		}

		return this;
	}

	// //////////////////////// config method
	// end////////////////////////////////////

	public void display(String uri) {
		doDisplay(null, uri, null, null);
	}

	public void display(View imageView, String uri) {
		doDisplay(imageView, uri, null, null);
	}

	public void display(String uri, ImageDownloadStateListener listener) {
		doDisplay(null, uri, null, listener);
	}

	public void display(ImageView imageView, String uri,
			ImageDownloadStateListener listener) {
		doDisplay(imageView, uri, null, listener);
	}

	public void display(View imageView, String uri, int imageWidth,
			int imageHeight, ImageDownloadStateListener listener) {
		BitmapDisplayConfig displayConfig = configMap.get(imageWidth + "_"
				+ imageHeight);
		if (displayConfig == null) {
			displayConfig = getDisplayConfig();
			displayConfig.setBitmapHeight(imageHeight);
			displayConfig.setBitmapWidth(imageWidth);
			configMap.put(imageWidth + "_" + imageHeight, displayConfig);
		}

		doDisplay(imageView, uri, displayConfig, listener);
	}

	public void display(View imageView, String uri, int imageWidth,
			int imageHeight) {
		display(imageView, uri, imageWidth, imageHeight, null);
	}

	public void display(View imageView, String uri, Bitmap loadingBitmap) {
		BitmapDisplayConfig displayConfig = configMap.get(String
				.valueOf(loadingBitmap));
		if (displayConfig == null) {
			displayConfig = getDisplayConfig();
			displayConfig.setLoadingBitmap(loadingBitmap);
			configMap.put(String.valueOf(loadingBitmap), displayConfig);
		}

		doDisplay(imageView, uri, displayConfig, null);
	}

	public void display(View imageView, String uri, Bitmap loadingBitmap,
			Bitmap laodfailBitmap) {
		BitmapDisplayConfig displayConfig = configMap.get(String
				.valueOf(loadingBitmap) + "_" + String.valueOf(laodfailBitmap));
		if (displayConfig == null) {
			displayConfig = getDisplayConfig();
			displayConfig.setLoadingBitmap(loadingBitmap);
			displayConfig.setLoadfailBitmap(laodfailBitmap);
			configMap.put(
					String.valueOf(loadingBitmap) + "_"
							+ String.valueOf(laodfailBitmap), displayConfig);
		}

		doDisplay(imageView, uri, displayConfig, null);
	}

	public void display(View imageView, String uri, int imageWidth,
			int imageHeight, Bitmap loadingBitmap, Bitmap laodfailBitmap) {
		BitmapDisplayConfig displayConfig = configMap.get(imageWidth + "_"
				+ imageHeight + "_" + String.valueOf(loadingBitmap) + "_"
				+ String.valueOf(laodfailBitmap));
		if (displayConfig == null) {
			displayConfig = getDisplayConfig();
			displayConfig.setBitmapHeight(imageHeight);
			displayConfig.setBitmapWidth(imageWidth);
			displayConfig.setLoadingBitmap(loadingBitmap);
			displayConfig.setLoadfailBitmap(laodfailBitmap);
			configMap.put(
					imageWidth + "_" + imageHeight + "_"
							+ String.valueOf(loadingBitmap) + "_"
							+ String.valueOf(laodfailBitmap), displayConfig);
		}

		doDisplay(imageView, uri, displayConfig, null);
	}

	public void display(View imageView, String uri, BitmapDisplayConfig config) {
		doDisplay(imageView, uri, config, null);
	}

	private void doDisplay(View imageView, String uri,
			BitmapDisplayConfig displayConfig,
			ImageDownloadStateListener listener) {
		if (!mInit) {
			init();
		}

		if (TextUtils.isEmpty(uri)) {
			return;
		}

		if (imageView != null) {
			imageView.setTag(uri);
		}

		if (displayConfig == null)
			displayConfig = mConfig.defaultDisplayConfig;

		askListener(listener, 0, null, uri, null);

		// 先判断是否是动图
		if (imageView instanceof GifView) {
			final GifLoadAndDisplayTask task = new GifLoadAndDisplayTask(
					imageView, listener);
			task.executeOnExecutor(bitmapLoadAndDisplayExecutor, uri);
			return;
		}
		Bitmap bitmap = null;
		if (mImageCache != null) {
			bitmap = mImageCache.getBitmapFromMemoryCache(uri);
		}
		if (bitmap != null) {// 内存取图成功
			setBitmapForImage(imageView, bitmap, uri);
			askListener(listener, 1, bitmap, uri, null);
		} else if (checkImageTask(uri, imageView)) {// 是否有取图线程
			final BitmapLoadAndDisplayTask task = new BitmapLoadAndDisplayTask(
					imageView, displayConfig, listener);
			task.executeOnExecutor(bitmapLoadAndDisplayExecutor, uri);
		} else {
			askListener(listener, 2, null, uri, null);
		}
	}

	private void askListener(ImageDownloadStateListener listener, int status,
			Bitmap bitmap, String uri, byte[] gifByte) {
		if (listener == null) {
			return;
		}
		if (status == 0) {
			listener.loading();
		} else if (status == 1) {
			listener.loadOk(bitmap, getNinePatchDrawable(bitmap, uri), gifByte);
		} else {
			listener.loadError();
		}
	}

	private void setBitmapForImage(View iv, Bitmap bitmap, String uri) {
		if (iv == null || bitmap == null || uri == null)
			return;
		if (iv.getTag() == null || !iv.getTag().toString().equals(uri))
			return;
		NinePatchDrawable drawable = getNinePatchDrawable(bitmap, uri);
		if (iv instanceof ImageView) {
			ImageView imageView = (ImageView) iv;
			if (iv.getTag(R.id.scale_type) != null) {
				String scale_type = (String) iv.getTag(R.id.scale_type);
				ImageScaleType.setScaleType(imageView, scale_type);
			} else {
				imageView.setScaleType(ScaleType.FIT_XY);
			}
			if (drawable != null)
				imageView.setImageDrawable(drawable);
			else
				imageView.setImageBitmap(bitmap);
		} else {
			if (drawable != null)
				iv.setBackgroundDrawable(drawable);
			else
				iv.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}
	}

	/**
	 * 获取.9图片
	 * 
	 * @param bitmap
	 * @param uri
	 * @return
	 */
	private NinePatchDrawable getNinePatchDrawable(Bitmap bitmap, String uri) {
		if (bitmap == null || TextUtils.isEmpty(uri) || !uri.contains(".9."))
			return null;
		final byte[] chunk = bitmap.getNinePatchChunk();
		if (chunk == null || !NinePatch.isNinePatchChunk(chunk))
			return null;
		NinePatchChunk ninePatchChunk = NinePatchChunk.deserialize(chunk);
		if (ninePatchChunk == null)
			return null;
		return new NinePatchDrawable(mContext.getResources(), bitmap, chunk,
				ninePatchChunk.mPaddings, null);
	}

	// public Bitmap getBitmapFromFile(String key) {
	// if (mBitmapProcess != null) {
	// return mBitmapProcess
	// .getFromDisk(key, mConfig.defaultDisplayConfig);
	// }
	// return null;
	// }

	private HashMap<String, BitmapDisplayConfig> configMap = new HashMap<String, BitmapDisplayConfig>();

	private BitmapDisplayConfig getDisplayConfig() {
		BitmapDisplayConfig config = new BitmapDisplayConfig();
		config.setAnimation(mConfig.defaultDisplayConfig.getAnimation());
		config.setAnimationType(mConfig.defaultDisplayConfig.getAnimationType());
		config.setBitmapHeight(mConfig.defaultDisplayConfig.getBitmapHeight());
		config.setBitmapWidth(mConfig.defaultDisplayConfig.getBitmapWidth());
		config.setLoadfailBitmap(mConfig.defaultDisplayConfig
				.getLoadfailBitmap());
		config.setLoadingBitmap(mConfig.defaultDisplayConfig.getLoadingBitmap());
		return config;
	}

	private void clearCacheInternalInBackgroud() {
		if (mImageCache != null) {
			mImageCache.clearCache();
		}
	}

	private void clearCacheInBackgroud(String key) {
		if (mImageCache != null) {
			mImageCache.clearCache(key);
		}
	}

	/**
	 * 执行过此方法后,FinalBitmap的缓存已经失效,建议通过FinalBitmap.create()获取新的实例
	 * 
	 * @author fantouch
	 */
	private void closeCacheInternalInBackgroud() {
		if (mImageCache != null) {
			mImageCache.close();
			mImageCache = null;
			mFinalBitmap = null;
		}
	}

	/**
	 * 网络加载bitmap
	 * 
	 * @param data
	 * @return
	 */
	private Bitmap processBitmap(String uri, BitmapDisplayConfig config) {
		if (mBitmapProcess != null) {
			return mBitmapProcess.getBitmap(uri, config);
		}
		return null;
	}

	/**
	 * 网络加载gif
	 * 
	 * @param uri
	 * @return
	 */
	private byte[] processGifByte(String uri) {
		if (mBitmapProcess != null) {
			return mBitmapProcess.getMovie(uri);
		}
		return null;
	}

	/**
	 * 从缓存（内存缓存和磁盘缓存）中直接获取bitmap，注意这里有io操作，最好不要放在ui线程执行
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromCache(String key) {
		Bitmap bitmap = getBitmapFromMemoryCache(key);
		if (bitmap == null)
			bitmap = getBitmapFromDiskCache(key);

		return bitmap;
	}

	/**
	 * 从内存缓存中获取bitmap
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		return mImageCache.getBitmapFromMemoryCache(key);
	}

	/**
	 * 从磁盘缓存中获取bitmap，，注意这里有io操作，最好不要放在ui线程执行
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromDiskCache(String key) {
		return getBitmapFromDiskCache(key, null);
	}

	public Bitmap getBitmapFromDiskCache(String key, BitmapDisplayConfig config) {
		return mBitmapProcess.getFromDisk(key, config);
	}

	public void setExitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
	}

	/**
	 * activity onResume的时候调用这个方法，让加载图片线程继续
	 */
	public void onResume() {
		setExitTasksEarly(false);
	}

	/**
	 * activity onPause的时候调用这个方法，让线程暂停
	 */
	public void onPause() {
		setExitTasksEarly(true);
	}

	/**
	 * activity onDestroy的时候调用这个方法，释放缓存
	 * 执行过此方法后,FinalBitmap的缓存已经失效,建议通过FinalBitmap.create()获取新的实例
	 * 
	 * @author fantouch
	 */
	public void onDestroy() {
		closeCache();
	}

	/**
	 * 清除所有缓存（磁盘和内存）
	 */
	public void clearCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLEAR);
	}

	/**
	 * 根据key清除指定的内存缓存
	 * 
	 * @param key
	 */
	public void clearCache(String key) {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLEAR_KEY,
				key);
	}

	/**
	 * 清除缓存
	 */
	public void clearMemoryCache() {
		if (mImageCache != null)
			mImageCache.clearMemoryCache();
	}

	/**
	 * 根据key清除指定的内存缓存
	 * 
	 * @param key
	 */
	public void clearMemoryCache(String key) {
		if (mImageCache != null)
			mImageCache.clearMemoryCache(key);
	}

	/**
	 * 清除磁盘缓存
	 */
	public void clearDiskCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLEAR_DISK);
	}

	/**
	 * 根据key清除指定的内存缓存
	 * 
	 * @param key
	 */
	public void clearDiskCache(String key) {
		new CacheExecutecTask().execute(
				CacheExecutecTask.MESSAGE_CLEAR_KEY_IN_DISK, key);
	}

	/**
	 * 关闭缓存 执行过此方法后,FinalBitmap的缓存已经失效,建议通过FinalBitmap.create()获取新的实例
	 * 
	 * @author fantouch
	 */
	public void closeCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLOSE);
	}

	/**
	 * 退出正在加载的线程，程序退出的时候调用词方法
	 * 
	 * @param exitTasksEarly
	 */
	public void exitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
		if (exitTasksEarly)
			pauseWork(false);// 让暂停的线程结束
	}

	/**
	 * 暂停正在加载的线程，监听listview或者gridview正在滑动的时候条用词方法
	 * 
	 * @param pauseWork
	 *            true停止暂停线程，false继续线程
	 */
	public void pauseWork(boolean pauseWork) {
		synchronized (mPauseWorkLock) {
			mPauseWork = pauseWork;
			if (!mPauseWork) {
				mPauseWorkLock.notifyAll();
			}
		}
	}

	private static BitmapLoadAndDisplayTask getBitmapTaskFromImageView(
			View imageView) {
		if (imageView != null) {
			Drawable drawable = null;
			if (imageView instanceof ImageView) {
				drawable = ((ImageView) imageView).getDrawable();
			} else {
				drawable = imageView.getBackground();
			}

			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	/**
	 * 检测 imageView中是否已经有线程在运行
	 * 
	 * @param data
	 * @param imageView
	 * @return true 没有 false 有线程在运行了
	 */
	public static boolean checkImageTask(Object data, View imageView) {
		final BitmapLoadAndDisplayTask bitmapWorkerTask = getBitmapTaskFromImageView(imageView);

		if (bitmapWorkerTask != null) {
			final Object bitmapData = bitmapWorkerTask.data;
			if (bitmapData == null || !bitmapData.equals(data)) {
				bitmapWorkerTask.cancel(true);
			} else {
				// 同一个线程已经在执行
				return false;
			}
		}
		return true;
	}

	public static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapLoadAndDisplayTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap,
				BitmapLoadAndDisplayTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapLoadAndDisplayTask>(
					bitmapWorkerTask);
		}

		public BitmapLoadAndDisplayTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	private class CacheExecutecTask extends AsyncTask<Object, Void, Void> {
		public static final int MESSAGE_CLEAR = 1;
		public static final int MESSAGE_CLOSE = 2;
		public static final int MESSAGE_CLEAR_DISK = 3;
		public static final int MESSAGE_CLEAR_KEY = 4;
		public static final int MESSAGE_CLEAR_KEY_IN_DISK = 5;

		@Override
		protected Void doInBackground(Object... params) {
			switch ((Integer) params[0]) {
			case MESSAGE_CLEAR:
				clearCacheInternalInBackgroud();
				break;
			case MESSAGE_CLOSE:
				closeCacheInternalInBackgroud();
				break;
			case MESSAGE_CLEAR_DISK:
				break;
			case MESSAGE_CLEAR_KEY:
				clearCacheInBackgroud(String.valueOf(params[1]));
				break;
			case MESSAGE_CLEAR_KEY_IN_DISK:
				break;
			}
			return null;
		}
	}

	/**
	 * bitmap下载显示的线程
	 * 
	 * @author michael yang
	 */
	private class BitmapLoadAndDisplayTask extends
			AsyncTask<Object, Void, Bitmap> {
		private String uri;
		private Object data;
		@SuppressWarnings("unused")
		private final WeakReference<View> imageViewReference;
		private final BitmapDisplayConfig displayConfig;
		private final ImageDownloadStateListener listener;
		private View view;

		public BitmapLoadAndDisplayTask(View imageView,
				BitmapDisplayConfig config, ImageDownloadStateListener listener) {
			this.view = imageView;
			if (imageView != null)
				imageViewReference = new WeakReference<View>(imageView);
			else
				imageViewReference = null;
			displayConfig = config;
			this.listener = listener;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			data = params[0];
			uri = String.valueOf(data);
			Bitmap bitmap = null;

			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			if (bitmap == null && !isCancelled() && !mExitTasksEarly) {
				bitmap = processBitmap(uri, displayConfig);
			}

			if (bitmap != null) {
				mImageCache.addToMemoryCache(uri, bitmap);
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled() || mExitTasksEarly) {
				bitmap = null;
				askListener(listener, 2, null, uri, null);
			}

			if (bitmap != null) {
				askListener(listener, 1, bitmap, uri, null);
				setBitmapForImage(view, bitmap, uri);
			} else if (bitmap == null) {
				askListener(listener, 2, bitmap, uri, null);
			}
		}

		@Override
		protected void onCancelled(Bitmap bitmap) {
			super.onCancelled(bitmap);
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll();
			}
		}
	}

	/**
	 * @title 配置信息
	 */
	private class FinalBitmapConfig {
		public Downloader downloader;
		public BitmapDisplayConfig defaultDisplayConfig;
		public int memCacheSize;// 内存缓存百分比
		public int poolSize = 3;// 默认的线程池线程并发数量
		public boolean recycleImmediately = true;// 是否立即回收内存

		public FinalBitmapConfig(Context context) {
			defaultDisplayConfig = new BitmapDisplayConfig();

			defaultDisplayConfig.setAnimation(null);
			defaultDisplayConfig
					.setAnimationType(BitmapDisplayConfig.AnimationType.fadeIn);

			// 设置图片的显示最大尺寸（为屏幕的大小,默认为屏幕宽度的1/2）
			DisplayMetrics displayMetrics = context.getResources()
					.getDisplayMetrics();
			int defaultWidth = (int) Math.floor(displayMetrics.widthPixels / 2);
			defaultDisplayConfig.setBitmapHeight(defaultWidth);
			defaultDisplayConfig.setBitmapWidth(defaultWidth);

		}
	}

	/**
	 * 把图片转换成圆形
	 * 
	 * @param bitmapimg
	 * @return
	 */
	public static void transforCircleBitmap(Bitmap bitmap, ImageView imageView) {
		if (bitmap == null)
			return;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 取小的
		int radius = width > height ? height / 2 : width / 2;
		Bitmap output = Bitmap.createBitmap(radius * 2, radius * 2,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		int left = 0, top = 0;
		int right = width, bottom = height;
		if (width > height) {
			left = width / 2 - radius;
			right = width / 2 + radius;
		} else if (width < height) {
			top = height / 2 - radius;
			bottom = height / 2 + radius;
		}
		Rect src = new Rect(left, top, right, bottom);// 截取原始图片的地方
		Rect dst = new Rect(0, 0, 2 * radius, 2 * radius);

		paint.setAntiAlias(true);
		canvas.drawCircle(radius, radius, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		canvas.drawBitmap(bitmap, src, dst, paint);

		imageView.setImageBitmap(bitmap);
	}

	/**
	 * bitmap下载显示的线程
	 * 
	 * @author michael yang
	 */
	private class GifLoadAndDisplayTask extends AsyncTask<Object, Void, byte[]> {
		private String uri;
		private Object data;
		@SuppressWarnings("unused")
		private final WeakReference<View> imageViewReference;
		private final ImageDownloadStateListener listener;
		private View view;

		public GifLoadAndDisplayTask(View imageView,
				ImageDownloadStateListener listener) {
			this.view = imageView;
			this.listener = listener;
			if (imageView != null)
				imageViewReference = new WeakReference<View>(imageView);
			else
				imageViewReference = null;
		}

		@Override
		protected byte[] doInBackground(Object... params) {
			data = params[0];
			uri = String.valueOf(data);
			byte[] bitmap = null;

			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			// gif图只做一级文件缓存，暂时没有做内存缓存
			if (bitmap == null && !isCancelled() && !mExitTasksEarly)
				bitmap = processGifByte(uri);

			return bitmap;
		}

		@Override
		protected void onPostExecute(byte[] bitmap) {
			if (isCancelled() || mExitTasksEarly) {
				bitmap = null;
			}

			if (bitmap != null) {
				askListener(listener, 1, null, uri, bitmap);
				if (view == null || bitmap == null || uri == null)
					return;
				if (view.getTag() == null
						|| !view.getTag().toString().equals(uri))
					return;
				if (view instanceof GifView) {
					((GifView) view).setMovie(Movie.decodeByteArray(bitmap, 0,
							bitmap.length));
				}
			} else if (bitmap == null)
				askListener(listener, 2, null, uri, null);
		}

		@Override
		protected void onCancelled(byte[] bitmap) {
			super.onCancelled(bitmap);
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll();
			}
		}
	}

}
