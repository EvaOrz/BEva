package cn.com.modernmedia.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.newtag.db.TagArticleListDb;
import cn.com.modernmedia.newtag.db.TagIndexDb;
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.newtag.db.UserSubscribeListDb;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmedia.views.column.NewColumnView.ColumnFooterItemIsSeletedListener;
import cn.com.modernmedia.views.fav.FavObserver;
import cn.com.modernmedia.views.index.IndexView.NavObservable;
import cn.com.modernmedia.views.index.head.AutoScrollObservable;
import cn.com.modernmedia.views.listener.ChildCatClickListener;
import cn.com.modernmedia.views.listener.ColumnChangedListener;
import cn.com.modernmedia.views.listener.FlowPositionChangedListener;
import cn.com.modernmedia.views.listener.NotifyLastestChangeListener;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediausermodel.UserApplication;
import cn.com.modernmediausermodel.db.CardListByArtilceIdDb;
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
	public static List<Integer> readedArticles = new ArrayList<Integer>();
	public static ColumnChangedListener columnChangedListener;
	public static NavObservable navObservable = new NavObservable();
	public static Class<?> articleGalleryCls; // 文章详情显示
	public static AutoScrollObservable autoScrollObservable = new AutoScrollObservable();

	@Override
	public void onCreate() {
		super.onCreate();
		articleCls = ArticleActivity.class;
		pushArticleCls = PushArticleActivity.class;
		UserApplication.favActivity = FavoritesActivity.class;
		favObservable.deleteObservers();
		favObservable.addObserver(new FavObserver(mContext));
	}

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
		CardListByArtilceIdDb.getInstance(mContext).close();
		UserCardInfoDb.getInstance(mContext).close();
		UserInfoDb.getInstance(mContext).close();
		TagInfoListDb.getInstance(mContext).close();
		TagArticleListDb.getInstance(mContext).close();
		TagIndexDb.getInstance(mContext).close();
		UserSubscribeListDb.getInstance(mContext).close();
		UserApplication.exit();
		itemIsSelectedListener = null;
		catClickListener = null;
		positionChangedListener = null;
		clearListener();
		readedArticles.clear();
		columnChangedListener = null;
		navObservable.deleteObservers();
		V.bitmapCache.clear();
		autoScrollObservable.clearAll();
	}
}
