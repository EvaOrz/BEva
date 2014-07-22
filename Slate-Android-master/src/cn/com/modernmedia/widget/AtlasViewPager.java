package cn.com.modernmedia.widget;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.Atlas.AtlasPicture;

/**
 * 图集
 * 
 * @author ZhuQiao
 * 
 */
public class AtlasViewPager extends ViewPager {
	private int totalNum = 0;// view总数
	private NotifyArticleDesListener listener;

	public AtlasViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnPageChangeListener(new OnPageChangeListener() {

			/**
			 * 当一个页面即将被加载时，调用此方法 Position index of the new selected page
			 */
			@Override
			public void onPageSelected(int position) {
				if (listener != null)
					listener.updateDes(position);
			}

			/**
			 * 当在一个页面滚动时，调用此方法postion:要滑向页面索引
			 */
			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
			}

			/**
			 * 状态有三个0空闲，1是增在滑行中，2目标加载完毕
			 */
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	public void setValue(List<AtlasPicture> list) {
		totalNum = list.size();
	}

	public void setListener(NotifyArticleDesListener listener) {
		this.listener = listener;
		listener.updateDes(0);
	}

	public int getTotalNum() {
		return totalNum;
	}

}
