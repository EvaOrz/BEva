package cn.com.modernmedia.listener;

/**
 * 缓存接口
 * 
 * @author user
 * 
 * @param <K>
 * @param <V>
 */
public interface MemoryCacheListener<K, V> {
	public boolean put(K key, V value);

	public V get(K key);

	public void remove(K key);

	public void clear();
}
