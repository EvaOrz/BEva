package cn.com.modernmedia.listener;

import java.util.List;

import android.content.Intent;
import cn.com.modernmedia.model.ArticleItem;

/**
 * slate uri 接口
 * 
 * @author ZhuQiao
 * 
 */
public interface SlateListener {
	/**
	 * if广告，不可点击
	 * 
	 * @param detail
	 */
	public void linkNull(ArticleItem item);

	/**
	 * 调用系统浏览器打开
	 * 
	 * @param intent
	 */
	public void httpLink(ArticleItem item, Intent intent);

	/**
	 * 跳转至指定文章
	 * 
	 * @param articleId
	 */
	public void articleLink(ArticleItem item, int articleId);

	/**
	 * 打开播放器
	 * 
	 * @param path
	 */
	public void video(ArticleItem item, String path);

	/**
	 * 跳转至指定栏目
	 * 
	 * @param columnId
	 */
	public void column(String columnId);

	/**
	 * 打开一张图片
	 * 
	 * @param url
	 */
	public void image(String url);

	/**
	 * 相册
	 * 
	 * @param url
	 */
	public void gallery(List<String> urlList);
}
