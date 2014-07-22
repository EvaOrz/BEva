package cn.com.modernmedia.listener;

import java.util.List;

import android.content.Intent;
import cn.com.modernmedia.model.ArticleItem;

/**
 * slate uri �ӿ�
 * 
 * @author ZhuQiao
 * 
 */
public interface SlateListener {
	/**
	 * if��棬���ɵ��
	 * 
	 * @param detail
	 */
	public void linkNull(ArticleItem item);

	/**
	 * ����ϵͳ�������
	 * 
	 * @param intent
	 */
	public void httpLink(ArticleItem item, Intent intent);

	/**
	 * ��ת��ָ������
	 * 
	 * @param articleId
	 */
	public void articleLink(ArticleItem item, int articleId);

	/**
	 * �򿪲�����
	 * 
	 * @param path
	 */
	public void video(ArticleItem item, String path);

	/**
	 * ��ת��ָ����Ŀ
	 * 
	 * @param columnId
	 */
	public void column(String columnId);

	/**
	 * ��һ��ͼƬ
	 * 
	 * @param url
	 */
	public void image(String url);

	/**
	 * ���
	 * 
	 * @param url
	 */
	public void gallery(List<String> urlList);
}
