package cn.com.modernmedia.listener;

/**
 * ֪ͨadapter����
 * 
 * @author ZhuQiao
 * 
 */
public interface NotifyAdapterListener {
	/**
	 * ֪ͨ�����Ѷ�����
	 */
	public void notifyReaded();

	/**
	 * ֪ͨ����ѡ�е�item
	 * 
	 * @param args
	 */
	public void nofitySelectItem(Object args);

	public void notifyChanged();
}
