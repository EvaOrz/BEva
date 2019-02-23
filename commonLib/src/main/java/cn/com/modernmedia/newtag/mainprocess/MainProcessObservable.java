package cn.com.modernmedia.newtag.mainprocess;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 首页主流程被观察者
 * 
 * @author zhuqiao
 * 
 */
public class MainProcessObservable extends Observable {
	/**
	 * 设置栏目列表
	 */
	public static final int SET_DATA_TO_COLUMN = 1;
	/**
	 * 设置栏目首页(目前只有视野使用)
	 */
	public static final int SET_DATA_TO_SHIYE = 2;
	/**
	 * 显示子栏目(无用)
	 */
	@Deprecated
	public static final int SHOW_CHILD_CAT = 3;
	/**
	 * 显示首页滑屏
	 */
	public static final int SHOW_INDEX_PAGER = 4;

	/**
	 * 观察者数据model
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static class ObserverItem {
		public int flag;
		public Entry entry;

		public ObserverItem(int flag, Entry entry) {
			this.flag = flag;
			this.entry = entry;
		}

	}

	private Map<String, Observer> map = new HashMap<String, Observer>();

	public void addObserver(Observer observer, String className) {
		if (ParseUtil.mapContainsKey(map, className)) {
			deleteObserver(map.get(className));
		}
		super.addObserver(observer);
		map.put(className, observer);
	}

	/**
	 * 通知刷新
	 * 
	 * @param item
	 */
	public void notifyProcessChange(ObserverItem item) {
		setChanged();
		notifyObservers(item);
	}
}
