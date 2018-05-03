package cn.com.modernmediausermodel.listener;

/**
 * 用户积分变化监听接口
 * 
 * @author jiancong
 * 
 */
public interface UserCentChangeListener {
	/**
	 * 积分总数改变的数量
	 * 
	 * @param number
	 */
	public void change(int number);
}
