package cn.com.modernmedia.listener;

import cn.com.modernmedia.model.Entry;


/**
 * �����Զ���view����ʵ�ִ˽ӿ�,���ýӿڳɹ���ʵ�ִ˽ӿ�
 * 
 * @author ZhuQiao
 * 
 */
public interface FetchEntryListener {
	/**
	 * ��View��������
	 * 
	 * @param entry
	 */
	public void setData(Entry entry);
}
