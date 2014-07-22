package cn.com.modernmedia.widget;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.Atlas.AtlasPicture;

/**
 * ͼ��
 * 
 * @author ZhuQiao
 * 
 */
public class AtlasViewPager extends ViewPager {
	private int totalNum = 0;// view����
	private NotifyArticleDesListener listener;

	public AtlasViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnPageChangeListener(new OnPageChangeListener() {

			/**
			 * ��һ��ҳ�漴��������ʱ�����ô˷��� Position index of the new selected page
			 */
			@Override
			public void onPageSelected(int position) {
				if (listener != null)
					listener.updateDes(position);
			}

			/**
			 * ����һ��ҳ�����ʱ�����ô˷���postion:Ҫ����ҳ������
			 */
			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
			}

			/**
			 * ״̬������0���У�1�����ڻ����У�2Ŀ��������
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
