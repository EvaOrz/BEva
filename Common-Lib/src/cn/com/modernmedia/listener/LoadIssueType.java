package cn.com.modernmedia.listener;

/**
 * 首页读取issue方式
 * 
 * @author ZhuQiao
 * 
 */
public enum LoadIssueType {
	/** 普通加载(获取最新一期) **/
	NORMAL,
	/** 下拉刷新加载 (获取最新一期) 只要判断getIssue接口就行 ,因为每次pull都从getIssue接口开始 **/
	PULL,
	/** 查看往期 (获取具体的往期) **/
	PRE,
}
