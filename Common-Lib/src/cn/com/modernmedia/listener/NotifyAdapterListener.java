package cn.com.modernmedia.listener;

/**
 * 通知adapter更新
 * 
 * @author ZhuQiao
 * 
 */
public interface NotifyAdapterListener {
	/**
	 * 通知更新已读文章
	 */
	public void notifyReaded();

	/**
	 * 通知更新选中的item
	 * 
	 * @param args
	 */
	public void nofitySelectItem(Object args);

	public void notifyChanged();
}
