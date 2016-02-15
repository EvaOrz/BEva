package cn.com.modernmediaslate.unit;

import java.util.Observable;

/**
 * 收藏模块观察者
 * 
 * @author zhuqiao
 * 
 */
public class FavObservable extends Observable {
	public static final int AFTER_LOGIN = 1;
	public static final int UPDATE = 2;
	public static final int DATA_CHANGE = 3; // 数据迁移时用

	public void setData(Object tag) {
		setChanged();
		notifyObservers(tag);
	}
}
