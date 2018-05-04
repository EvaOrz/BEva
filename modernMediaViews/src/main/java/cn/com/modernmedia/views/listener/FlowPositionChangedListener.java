package cn.com.modernmedia.views.listener;

/**
 * 当画报详情移动位置的时候，通知画报更新位置
 * 
 * @author ZhuQiao
 * 
 */
public interface FlowPositionChangedListener {
	public void setCurrentPosition(int position);
}