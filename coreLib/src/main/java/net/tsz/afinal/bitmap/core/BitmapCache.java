/**
 * Copyright (c) 2012-2013, Michael Yang 杨福海 (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tsz.afinal.bitmap.core;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

public class BitmapCache {

	// 默认的内存缓存大小
	private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 8; // 8MB

	// 默认的磁盘缓存大小
	private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 100; // 100MB
	private static final int DEFAULT_DISK_CACHE_COUNT = 1000; // 缓存的图片数量

	// BitmapCache的一些默认配置
	private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
	private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;

	private IMemoryCache mMemoryCache;
	private ImageCacheParams mCacheParams;

	public BitmapCache(ImageCacheParams cacheParams) {
		init(cacheParams);
	}

	/**
	 * 初始化 图片缓存
	 * 
	 * @param cacheParams
	 */
	private void init(ImageCacheParams cacheParams) {
		mCacheParams = cacheParams;

		// 是否启用内存缓存
		if (mCacheParams.memoryCacheEnabled) {
			// 是否立即回收内存
			if (mCacheParams.recycleImmediately)
				mMemoryCache = new SoftMemoryCacheImpl(
						mCacheParams.memCacheSize);
			else
				mMemoryCache = new BaseMemoryCacheImpl(
						mCacheParams.memCacheSize);
		}
	}

	/**
	 * 添加图片到内存缓存中
	 * 
	 * @param url
	 *            Url 地址
	 * @param bitmap
	 *            图片数据
	 */
	public void addToMemoryCache(String url, Bitmap bitmap) {
		if (url == null || bitmap == null) {
			return;
		}
		mMemoryCache.put(url, bitmap);
	}

	/**
	 * 从内存缓存中获取bitmap.
	 */
	public Bitmap getBitmapFromMemoryCache(String data) {
		if (mMemoryCache != null)
			return mMemoryCache.get(data);
		return null;
	}

	/**
	 * 清空内存缓存和sdcard缓存
	 */
	public void clearCache() {
		clearMemoryCache();
	}

	public void clearMemoryCache() {
		if (mMemoryCache != null) {
			mMemoryCache.evictAll();
		}
	}

	public void clearCache(String key) {
		clearMemoryCache(key);
	}

	public void clearMemoryCache(String key) {
		if (mMemoryCache != null) {
			mMemoryCache.remove(key);
		}
	}

	/**
	 * Closes the disk cache associated with this ImageCache object. Note that
	 * this includes disk access so this should not be executed on the main/UI
	 * thread.
	 */
	public void close() {
	}

	/**
	 * Image cache 的配置信息.
	 */
	public static class ImageCacheParams {
		public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
		public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
		public int diskCacheCount = DEFAULT_DISK_CACHE_COUNT;
		public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
		public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
		public boolean recycleImmediately = true;

		public ImageCacheParams() {
		}

		/**
		 * 设置缓存大小
		 * 
		 * @param context
		 * @param percent
		 *            百分比，值的范围是在 0.05 到 0.8之间
		 */
		public void setMemCacheSizePercent(Context context, float percent) {
			if (percent < 0.05f || percent > 0.8f) {
				throw new IllegalArgumentException(
						"setMemCacheSizePercent - percent must be "
								+ "between 0.05 and 0.8 (inclusive)");
			}
			memCacheSize = Math.round(percent * getMemoryClass(context) * 1024
					* 1024);
		}

		public void setMemCacheSize(int memCacheSize) {
			this.memCacheSize = memCacheSize;
		}

		public void setDiskCacheSize(int diskCacheSize) {
			this.diskCacheSize = diskCacheSize;
		}

		private static int getMemoryClass(Context context) {
			return ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE))
					.getMemoryClass();
		}

		public void setDiskCacheCount(int diskCacheCount) {
			this.diskCacheCount = diskCacheCount;
		}

		public void setRecycleImmediately(boolean recycleImmediately) {
			this.recycleImmediately = recycleImmediately;
		}

	}

}
