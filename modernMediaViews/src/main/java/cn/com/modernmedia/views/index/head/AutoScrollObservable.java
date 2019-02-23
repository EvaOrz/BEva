package cn.com.modernmedia.views.index.head;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动滑动观察者
 * 
 * @author user
 *
 */
public class AutoScrollObservable {
	List<AutoScrollHandler> list = new ArrayList<AutoScrollHandler>();

	public void addHandler(AutoScrollHandler handler) {
		list.add(handler);
	}

	public void deleteHandler(AutoScrollHandler handler) {
		list.remove(handler);
	}

	public void stopAll() {
		for (AutoScrollHandler handler : list) {
			handler.stopChange();
		}
	}

	public void clearAll() {
		stopAll();
		list.clear();
	}
}
