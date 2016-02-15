package cn.com.modernmedia.views.index.head;

import java.util.HashMap;
import java.util.List;

import com.alipay.sdk.auth.h;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.views.ViewsMainActivity.LifeCycle;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.views.util.PlayVideoHelper;
import cn.com.modernmedia.views.xmlparse.FunctionXML;
import cn.com.modernmedia.views.xmlparse.XMLDataSetForHead;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.widget.LoopPagerAdapter;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 首页焦点图
 * 
 * @author zhuqiao
 * 
 */
public class IndexViewHead extends BaseIndexHeadView {
	private XMLParse parse;
	private XMLDataSetForHead dataSet;

	// 播放视频
	private PlayVideoHelper helper;

	private Handler handler = new Handler();

	public IndexViewHead(Context context, Template template) {
		super(context, template);
		helper = new PlayVideoHelper(context);
	}

	@Override
	protected void init() {
		parse = new XMLParse(mContext, null);
		addView(parse.inflate(template.getHead().getData(), null,
				template.getHost()));
		dataSet = parse.getDataSetForHead();
		viewPager = dataSet.getViewPager();
	}

	/**
	 * 更新title
	 * 
	 * @param item
	 */
	@Override
	protected void updateTitle(ArticleItem item) {
		dataSet.update(item);
	}

	/**
	 * 初始化dotll
	 * 
	 * @param itemList
	 * @param dots
	 */
	@Override
	protected void initDot(List<ArticleItem> itemList, List<ImageView> dots) {
		dataSet.dot(itemList, dots);
	}

	@Override
	protected void setDataToGallery(List<ArticleItem> list) {
		super.setDataToGallery(list);
		dataSet.initAnim(list);

		waitToCheck(0);
	}

	@Override
	public void updateDes(int position) {
		super.updateDes(position);
		dataSet.anim(mList, position);

		waitToCheck(position);
	}

	@Override
	public void updatePage(int state) {
		super.updatePage(state);
		if (state == ViewPager.SCROLL_STATE_DRAGGING) {
			helper.pauseVideo(false);
		} else if (state == ViewPager.SCROLL_STATE_IDLE) {
		} else if (state == ViewPager.SCROLL_STATE_SETTLING) {
			waitToCheck(viewPager.getCurrentItem());
		}
	}

	/**
	 * 
	 * @param cycle
	 */
	public void life(LifeCycle cycle) {
		if (cycle == LifeCycle.PAUSE) {
			helper.stopVideo();
		} else if (cycle == LifeCycle.RESUME) {
			waitToCheck(viewPager.getCurrentItem());
		}
	}

	/**
	 * 等待一定时间，等adapter的setPrimaryItem执行
	 */
	private void waitToCheck(final int position) {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				boolean has = findVideo(position);
				if (has) {
					stopRefresh();
				} else {
					startRefresh();
					if (helper.getCurrPlayerItem() != null)// 暂停播放视频
					{
						helper.stopVideo();
						Log.e("没有新视频", "暂停之前");
					}

				}
			}
		}, 500);
	}

	public boolean findVideo() {
		return findVideo(viewPager.getCurrentItem());
	}

	private boolean findVideo(int position) {
		if (mList == null || mList.size() <= position)
			return false;
		final ArticleItem item = mList.get(position);
		if (!ParseUtil.listNotNull(item.getPicList()))
			return false;
		if (TextUtils.isEmpty(item.getPicList().get(0).getVideolink()))
			return false;

		if (viewPager == null
				|| !(viewPager.getAdapter() instanceof LoopPagerAdapter))
			return false;
		LoopPagerAdapter loopPagerAdapter = (LoopPagerAdapter) viewPager
				.getAdapter();
		if (!(loopPagerAdapter.getPagerAdapter() instanceof IndexHeadPagerAdapter))
			return false;
		IndexHeadPagerAdapter adapter = (IndexHeadPagerAdapter) loopPagerAdapter
				.getPagerAdapter();

		final HashMap<String, View> viewMap = adapter.getCurrViewMap();
		if (viewMap == null)
			return false;
		if (!ParseUtil.mapContainsKey(viewMap, FunctionXML.VIDEO_VIEW))
			return false;
		View view = viewMap.get(FunctionXML.VIDEO_VIEW);
		if (!(view instanceof VideoView))
			return false;
		// 上一次播放的视频和当前可播放视频一致,那么继续播放
		if (helper.getCurrPlayerItem() == item) {
			// 允许自动播放
			if (DataHelper.getWiFiAutoPlayVedio(mContext)) {
				helper.resumeVideo();
				Log.e("一致 继续", "允许自动播放 -- 继续");
			} else {
				helper.pauseVideo(false);
				Log.e("一致 暂停", "不允许自动播放 -- 暂停");
			}
		} else {
			Log.e("不一致 停止", "开始播新的" + item.getTitle());
			// 播放别的视频之前先停止当前视频
			helper.stopVideo();
			// 开始播放别的视频
			helper.startVideo(viewMap, item);
		}
		return true;
	}

	public PlayVideoHelper getHelper() {
		return helper;
	}

}
