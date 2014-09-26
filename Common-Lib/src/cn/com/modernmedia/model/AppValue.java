package cn.com.modernmedia.model;

import cn.com.modernmedia.model.TagInfoList.AppProperty;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.mainprocess.TagBaseMainProcess;

/**
 * 应用使用的变量
 * 
 * @author zhuqiao
 * 
 */
public class AppValue {
	/**
	 * 应用信息
	 */
	public static AppProperty appInfo = new AppProperty();
	/**
	 * 当前issueTag
	 */
	public static String currentIssueTag = "";
	/**
	 * 原始栏目列表
	 */
	public static TagInfoList defaultColumnList = new TagInfoList();
	/**
	 * 封装过订阅的栏目列表
	 */
	public static TagInfoList ensubscriptColumnList = new TagInfoList();
	/**
	 * 临时栏目列表（点击确认之前）
	 */
	public static TagInfoList tempColumnList = new TagInfoList();

	/**
	 * 主页流程
	 */
	public static TagBaseMainProcess mainProcess;
	/**
	 * 当前选中栏目
	 */
	public static TagInfo currColumn;

	/**
	 * uri生成的栏目列表信息
	 */
	public static TagInfoList uriTagInfoList = new TagInfoList();

	public static void clear() {
		currentIssueTag = "";
		appInfo = new AppProperty();
		defaultColumnList = new TagInfoList();
		ensubscriptColumnList = new TagInfoList();
		mainProcess = null;
		currColumn = null;
		uriTagInfoList = new TagInfoList();
	}
}
