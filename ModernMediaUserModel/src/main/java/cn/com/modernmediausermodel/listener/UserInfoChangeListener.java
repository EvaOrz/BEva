package cn.com.modernmediausermodel.listener;

/**
 * 当用户信息变化了（好友数、粉丝数、笔记数），刷新用户信息页面
 * 
 * @author user
 * 
 */
public interface UserInfoChangeListener {
	/**
	 * 添加笔记(收藏，写笔记)
	 * 
	 * @param num
	 */
	public void addCard(int num);

	/**
	 * 删除笔记(取消收藏、删除笔记)
	 * 
	 * @param num
	 */
	public void deleteCard(int num);

	/**
	 * 关注用户
	 * 
	 * @param num
	 *            关注数量
	 */
	public void addFollow(int num);

	/**
	 * 取消关注
	 * 
	 * @param num
	 *            取消关注的数量
	 */
	public void deleteFollow(int num);
}
