package cn.com.modernmedia.mainprocess;

import android.content.Context;
import android.content.Intent;
import cn.com.modernmedia.mainprocess.MainProcessManage.FetchDataCallBack;

/**
 * 主流程工厂创造器
 * 
 * @author user
 * 
 */
public class MainProcessFactory implements MainProcessAbstractFactory {

	@Override
	public BaseMainProcess createNormalProcess(Context context,
			FetchDataCallBack fetchCallBack) {
		return new MainProcessNormal(context, fetchCallBack);
	}

	@Override
	public BaseMainProcess createCacheProcess(Context context,
			FetchDataCallBack fetchCallBack) {
		return new MainProcessCache(context, fetchCallBack);
	}

	@Override
	public BaseMainProcess createParseProcess(Context context,
			FetchDataCallBack fetchCallBack, Intent intent) {
		return new MainProcessParse(context, fetchCallBack);
	}

	@Override
	public BaseMainProcess createPreIssueProcess(Context context,
			FetchDataCallBack fetchCallBack) {
		return new MainProcessPreIssue(context, fetchCallBack);
	}

}
