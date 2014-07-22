package cn.com.modernmedia.api;


/**
 * 图片下载工具
 * 
 * @author ZhuQiao
 * 
 */
public class ImageDownloader {
//	private Context mContext;
//	private static ImageDownloader instance;
//	private LruMemoryCache mMemoryCache;
//	private HashMap<String, BitmapAsyncTask> mTasksMap;
//	private HashMap<String, ImageDownloadStateListener> mListenerMap;
//	private HashSet<SoftReference<Bitmap>> mReusableBitmaps;
//
//	// private ExecutorService executorService =
//	// Executors.newFixedThreadPool(20); // 固定十个线程来执行任务
//	private final Handler handler = new Handler();
//
//	private ImageDownloader(Context context, int cacheMemory) {
//		mContext = context;
//		mMemoryCache = new LruMemoryCache(cacheMemory);
//		mTasksMap = new HashMap<String, BitmapAsyncTask>();
//		mListenerMap = new HashMap<String, ImageDownloadStateListener>();
//		mReusableBitmaps = new HashSet<SoftReference<Bitmap>>();
//	}
//
//	@SuppressWarnings("unused")
//	private static ImageDownloader getInstance(Context context, int cacheMemory) {
//		if (instance == null) {
//			instance = new ImageDownloader(context, cacheMemory);
//		}
//		return instance;
//	}
//
//	/**
//	 * @param options
//	 *            - BitmapFactory.Options with out* options populated
//	 * @return Bitmap that case be used for inBitmap
//	 */
//	public Bitmap getBitmapFromReusableSet(Options options) {
//		Bitmap bitmap = null;
//		if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
//			final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps
//					.iterator();
//			Bitmap item;
//			while (iterator.hasNext()) {
//				item = iterator.next().get();
//				if (null != item && item.isMutable()) {
//					// Check to see it the item can be used for inBitmap
//					if (canUseForInBitmap(item, options)) {
//						bitmap = item;
//						// Remove from reusable set so it can't be used again
//						iterator.remove();
//						break;
//					}
//				} else {
//					// Remove from the set if the reference has been cleared.
//					iterator.remove();
//				}
//			}
//		}
//		return bitmap;
//	}
//
//	/**
//	 * @param candidate
//	 *            - Bitmap to check
//	 * @param targetOptions
//	 *            - Options that have the out* value populated
//	 * @return true if <code>candidate</code> can be used for inBitmap re-use
//	 *         with <code>targetOptions</code>
//	 */
//	private static boolean canUseForInBitmap(Bitmap candidate,
//			BitmapFactory.Options targetOptions) {
//		int width = targetOptions.outWidth / targetOptions.inSampleSize;
//		int height = targetOptions.outHeight / targetOptions.inSampleSize;
//
//		return candidate.getWidth() == width && candidate.getHeight() == height;
//	}
//
//	/**
//	 * 加载本地图片
//	 * 
//	 * @param path
//	 * @param imageView
//	 */
//	public void downloadLocalPic(final String path, final ImageView imageView,
//			final int width, final int height) {
//		if (TextUtils.isEmpty(path) || this == null) {
//			return;
//		}
//		Bitmap bitmap = null;
//		if (mMemoryCache != null) {
//			bitmap = mMemoryCache.get(path);
//			if (bitmap != null && !bitmap.isRecycled()) {
//				final Bitmap b = bitmap;
//				handler.post(new Runnable() {
//
//					@Override
//					public void run() {
//						imageView.setImageBitmap(b);
//					}
//				});
//				return;
//			} else {
//				try {
//					mMemoryCache.remove(path);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		if (imageView != null)
//			imageView.setTag(path);
//		// 缓存中没有图像，则从SDcard取出数据，并将取出的数据缓存到内存中
//		new Thread() {
//
//			@Override
//			public void run() {
//				try {
//					final Bitmap bitmap = FileManager.getImageFromFileByPath(
//							path, width, height);
//					if (bitmap != null) {
//						mMemoryCache.put(path, bitmap);
//					}
//					handler.post(new Runnable() {
//						public void run() {
//							if (imageView.getTag() != null
//									&& imageView.getTag().equals(path))
//								imageView.setImageBitmap(bitmap);
//						}
//					});
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}
//			}
//
//		}.start();
//	}

//	/**
//	 * 只下载图片
//	 * 
//	 * @param url
//	 */
//	public void download(String url) {
//		download(url, null, 0, 0, null);
//	}
//
//	/**
//	 * 下载图片，只包含url和imageview
//	 * 
//	 * @param url
//	 * @param imageView
//	 */
//	public void download(String url, ImageView imageView) {
//		download(url, imageView, 0, 0, null);
//	}
//
//	/**
//	 * 下载图片，包含url和imageview,需要的width，height
//	 * 
//	 * @param url
//	 * @param imageView
//	 * @param width
//	 * @param height
//	 */
//	public void download(String url, ImageView imageView, int width, int height) {
//		download(url, imageView, width, height, null);
//	}
//
//	/**
//	 * 下载图片，包含url以及回调函数
//	 * 
//	 * @param url
//	 * @param listener
//	 */
//	public void download(String url, ImageDownloadStateListener listener) {
//		download(url, null, 0, 0, listener);
//	}
//
//	/**
//	 * 下载图片
//	 * 
//	 * @param url
//	 * @param imageView
//	 * @param width
//	 * @param height
//	 * @param listener
//	 */
//	public void download(String url, ImageView imageView, int width,
//			int height, ImageDownloadStateListener listener) {
//		// PrintHelper.print("memory cache size[KB]:"
//		// + (mMemoryCache.size() / 1024));
//		if (TextUtils.isEmpty(url))
//			return;
//		if (listener != null) {
//			mListenerMap.put(url, listener);
//			askListener(url, 0, null);
//		}
//		Bitmap bitmap = fetchBitmapFromCache(url, width, height);
//		if (bitmap != null) {
//			if (listener != null) {
//				askListener(url, 1, bitmap);
//			}
//			if (imageView != null) {
//				setScaleType(imageView);
//				imageView.setImageBitmap(bitmap);
//			}
//		} else {
//			BitmapAsyncTask task = mTasksMap.get(url);
//			if (task == null) {
//				if (imageView != null)
//					imageView.setTag(url);
//				task = new BitmapAsyncTask(width, height, imageView);
//				mTasksMap.put(url, task);
//				task.execute(url);
//			}
//		}
//	}
//
//	/**
//	 * 从缓存中拿图片
//	 * 
//	 * @param url
//	 * @return
//	 */
//	public Bitmap fetchBitmapFromCache(String url, int width, int height) {
//		if (TextUtils.isEmpty(url) || this == null) {
//			return null;
//		}
//		Bitmap bitmap = null;
//		if (mMemoryCache != null) {
//			bitmap = mMemoryCache.get(url);
//			if (bitmap != null && !bitmap.isRecycled())
//				return bitmap;
//			else
//				try {
//					mMemoryCache.remove(url);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//		}
//		if (width != 0) {
//			bitmap = FileManager.getImageFromFile(mContext, url, width, height);
//		} else {
//			bitmap = FileManager.getImageFromFile(url);
//		}
//		if (bitmap != null && mMemoryCache != null) {
//			mMemoryCache.put(url, bitmap);
//		}
//		return bitmap;
//	}
//
//	/**
//	 * 通知接口当前图片下载状态
//	 * 
//	 * @param url
//	 * @param mode
//	 *            0.loading,1.loadok,2loaderror
//	 */
//	private void askListener(String url, int mode, Bitmap bitmap) {
//		if (!mListenerMap.isEmpty() && mListenerMap.containsKey(url)) {
//			ImageDownloadStateListener listener = mListenerMap.get(url);
//			if (listener == null) {
//				mListenerMap.remove(url);
//				return;
//			}
//			if (mode == 0) {
//				listener.loading();
//			} else if (mode == 1) {
//				listener.loadOk(bitmap);
//				removeListener(url);
//			} else if (mode == 2) {
//				listener.loadError();
//			}
//		}
//	}
//
//	/**
//	 * 设置imageview的scaletype,默认fit_xy
//	 * 
//	 * @param iv
//	 */
//	private void setScaleType(ImageView iv) {
////		if (iv.getTag(R.id.scale_type) != null) {
////			String scale_type = (String) iv.getTag(R.id.scale_type);
////			ImageScaleType.setScaleType(iv, scale_type);
////		} else {
////			iv.setScaleType(ScaleType.FIT_XY);
////		}
//	}
//
//	/**
//	 * 当无需显示或者想要回收图片，可调用此方法
//	 * 
//	 * @param url
//	 */
//	public void removeBitmapFromCache(String url, boolean recycleBitmap) {
//		if (mMemoryCache != null) {
//			Bitmap bitmap = mMemoryCache.get(url);
//			if (bitmap != null && !bitmap.isRecycled()) {
//				mMemoryCache.remove(url);
//				if (recycleBitmap) {
//					bitmap.recycle();
//					bitmap = null;
//				}
//			}
//		}
//		removeListener(url);
//		if (mTasksMap != null && mTasksMap.containsKey(url)) {
//			BitmapAsyncTask task = mTasksMap.get(url);
//			if (task != null && !task.isCancelled()) {
//				task.cancel(true);
//			}
//		}
//	}
//
//	public void removeListener(String url) {
//		if (mListenerMap != null && mListenerMap.containsKey(url)) {
//			ImageDownloadStateListener sr = mListenerMap.get(url);
//			if (sr != null) {
//				mListenerMap.remove(url);
//			}
//		}
//	}
//
//	/**
//	 * 从网络下载图片
//	 * 
//	 * @param url
//	 * @param width
//	 *            需要显示的图片宽度
//	 * @param height
//	 *            需要显示的图片高度
//	 * @return
//	 */
//	private Bitmap downLoadBitmap(String url, int width, int height) {
//		HttpGet httpRequest = null;
//		final HttpClient httpclient = new DefaultHttpClient();
//		HttpResponse response = null;
//		InputStream is = null;
//		HttpEntity entity = null;
//		try {
//			httpRequest = new HttpGet(url);
//			httpclient.getParams().setParameter(
//					HttpConnectionParams.SO_TIMEOUT, 20000);
//			response = httpclient.execute(httpRequest);
//			final int statusCode = response.getStatusLine().getStatusCode();
//			if (statusCode != HttpStatus.SC_OK) {
//				return null;
//			}
//			entity = response.getEntity();
//			if (entity != null) {
//				BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(
//						entity);
//				is = bufferedHttpEntity.getContent();
//				FileManager.saveImageToFile(is, url);
//				return FileManager.getImageFromFile(mContext, url, width,
//						height);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (entity != null) {
//					entity.consumeContent();
//				}
//				if (is != null) {
//					is.close();
//				}
//				if (httpRequest != null) {
//					httpRequest.abort();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}
//
//	private class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
//		private String url;
//		private int mWidth;
//		private int mHeight;
//		private ImageView mImageView;
//
//		private BitmapAsyncTask(int width, int height, ImageView imageView) {
//			mWidth = width;
//			mHeight = height;
//			mImageView = imageView;
//		}
//
//		@Override
//		protected Bitmap doInBackground(String... params) {
//			url = params[0];
//			if (TextUtils.isEmpty(url))
//				return null;
//			return downLoadBitmap(url, mWidth, mHeight);
//		}
//
//		@Override
//		protected void onPostExecute(Bitmap bitmap) {
//			mTasksMap.remove(url);
//			if (isCancelled()) {
//				return;
//			}
//			if ((bitmap != null) && !TextUtils.isEmpty(url)) {
//				mMemoryCache.put(url, bitmap);
//				if (mImageView != null && mImageView.getTag() != null
//						&& mImageView.getTag().toString().equals(url)) {
//					setScaleType(mImageView);
//					mImageView.setImageBitmap(bitmap);
//				}
//				askListener(url, 1, bitmap);
//			} else {
//				askListener(url, 2, null);
//			}
//		}
//
//		protected void onCancelled() {
//			mTasksMap.remove(url);
//		}
//	}

	/**
	 * 离开应用时回收相关数据
	 */
	public void destroy() {
//		if (mMemoryCache != null) {
//			mMemoryCache.clear();
//		}
//		if (!mListenerMap.isEmpty())
//			mListenerMap.clear();
//		if (!mTasksMap.isEmpty())
//			mTasksMap.clear();
//		instance = null;
//		System.gc();
	}
}
