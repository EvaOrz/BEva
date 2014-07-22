package cn.com.modernmedia.mainprocess;

import android.content.Context;
import android.content.Intent;
import cn.com.modernmedia.mainprocess.MainProcessManage.FetchDataCallBack;

/**
 * 主页流程工程创造器
 * 
 * @author user
 * 
 */
public interface MainProcessAbstractFactory {
	/**
	 * 创建普通流程
	 * 
	 * @param context
	 * @param fetchCallBack
	 * @return
	 */
	public BaseMainProcess createNormalProcess(Context context,
			FetchDataCallBack fetchCallBack);

	/**
	 * 创建缓存流程
	 * 
	 * @param context
	 * @param fetchCallBack
	 * @return
	 */
	public BaseMainProcess createCacheProcess(Context context,
			FetchDataCallBack fetchCallBack);

	/**
	 * 创建推送流程
	 * 
	 * @param context
	 * @param fetchCallBack
	 * @param intent
	 * @return
	 */
	public BaseMainProcess createParseProcess(Context context,
			FetchDataCallBack fetchCallBack, Intent intent);

	/**
	 * 创建阅读往期流程
	 * 
	 * @param context
	 * @param fetchCallBack
	 * @return
	 */
	public BaseMainProcess createPreIssueProcess(Context context,
			FetchDataCallBack fetchCallBack);
}
