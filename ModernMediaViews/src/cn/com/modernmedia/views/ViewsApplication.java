package cn.com.modernmedia.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.LastestArticleId;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmedia.views.column.ColumnView.ColumnFooterItemIsSeletedListener;
import cn.com.modernmedia.views.listener.ChildCatClickListener;
import cn.com.modernmedia.views.listener.FlowPositionChangedListener;
import cn.com.modernmedia.views.listener.NotifyLastestChangeListener;
import cn.com.modernmediausermodel.UserApplication;
import cn.com.modernmediausermodel.db.RecommendCardDb;
import cn.com.modernmediausermodel.db.TimelineDb;
import cn.com.modernmediausermodel.db.UserCardInfoDb;
import cn.com.modernmediausermodel.db.UserInfoDb;

public class ViewsApplication extends CommonApplication {
	// 监听栏目列表的footer项是否选中
	public static ColumnFooterItemIsSeletedListener itemIsSelectedListener;
	// 点击子栏目导航栏
	public static ChildCatClickListener catClickListener;
	// 从画报详情返回
	public static FlowPositionChangedListener positionChangedListener;
	// 通知刷新未读个数(相同类型的只存在一个,比如栏目首页,一次只能显示一个)
	private static Map<String, NotifyLastestChangeListener> notifyLastestMap = new HashMap<String, NotifyLastestChangeListener>();
	public static LastestArticleId lastestArticleId;
	public static List<Integer> readedArticles = new ArrayList<Integer>();

	public static void addListener(String name,
			NotifyLastestChangeListener lastestChangeListener) {
		notifyLastestMap.put(name, lastestChangeListener);
	}

	public static void clearListener() {
		notifyLastestMap.clear();
	}

	public static void notifyLastest() {
		if (notifyLastestMap.isEmpty())
			return;
		for (String name : notifyLastestMap.keySet()) {
			NotifyLastestChangeListener listener = notifyLastestMap.get(name);
			if (listener != null)
				listener.changeCount();
		}
	}

	public static void exit() {
		PrintHelper.print("ViewsApplication exit");
		RecommendCardDb.getInstance(mContext).close();
		TimelineDb.getInstance(mContext).close();
		UserCardInfoDb.getInstance(mContext).close();
		UserInfoDb.getInstance(mContext).close();
		soloColumn = null;
		UserApplication.exit();
		itemIsSelectedListener = null;
		catClickListener = null;
		lastestArticleId = null;
		positionChangedListener = null;
		clearListener();
		readedArticles.clear();
	}
}
