package cn.com.modernmedia.util;

import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.listener.MemoryCacheListener;

/**
 * lru缓存
 * 
 * @author user
 * 
 */
public class LruMemoryCache implements MemoryCacheListener<String, Bitmap> {
	private final LinkedHashMap<String, Bitmap> map;

	private int maxSize;// 最大的缓存空间
	private int size;// 当前存储的空间

	public LruMemoryCache(int maxSize) {
		this.maxSize = maxSize;
		map = new LinkedHashMap<String, Bitmap>(0, 0.75f, true);
	}

	@Override
	public boolean put(String key, Bitmap value) {
		if (TextUtils.isEmpty(key) || value == null) {
			return false;
		}
		size += sizeOf(value);
		Bitmap previous = map.put(key, value);// 获取以前是否添加过这个bitmap
		if (previous != null) {
			size -= sizeOf(previous);
		}
		trimToSize(maxSize * 4 / 5);
		return true;
	}

	@Override
	public Bitmap get(String key) {
		if (TextUtils.isEmpty(key)) {
			return null;
		}
		synchronized (this) {
			return map.get(key);
		}
	}

	@Override
	public void remove(String key) {
		if (TextUtils.isEmpty(key)) {
			return;
		}
		synchronized (this) {
			Bitmap previous = map.remove(key);
			if (previous != null) {
				size -= sizeOf(previous);
			}
		}
	}

	@Override
	public void clear() {
		trimToSize(-1);
	}

	/**
	 * 计算bitmap大小
	 * 
	 * @param bitmap
	 * @return
	 */
	private int sizeOf(Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	/**
	 * 计算当前缓存使用空间，如果超出了最大使用空间，那么清除最旧的数据
	 * 
	 * @param maxSize
	 *            如果=-1，那么清除所有的缓存
	 */
	private void trimToSize(int maxSize) {
		boolean trim = false;
		while (true) {
			String key;
			Bitmap value;
			synchronized (this) {
				if (size < 0 || (map.isEmpty() && size != 0))
					break;
				if (size <= maxSize || map.isEmpty())
					break;
				Map.Entry<String, Bitmap> toEvict = map.entrySet().iterator()
						.next();
				// TODO 需要清除的(每次取第一条)
				if (toEvict == null)
					break;
				key = toEvict.getKey();
				value = toEvict.getValue();
				map.remove(key);
				if (value != null) {
					trim = true;
					size -= sizeOf(value);
				}
			}
		}
		if (trim)
			CommonApplication.callGc();
	}

	// mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue));

}
