package cn.com.modernmedia.newtag.mainprocess;

import android.content.Context;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.MainProcessParseCallBack;

/**
 * 往期流程
 * 
 * @author user
 * 
 */
public class TagMainProcessPreIssue extends TagBaseMainProcess {
	public enum PreIssusType {
		REFRESH_INDEX/** 获取往期issue后刷新页面，显示往期的内容 **/
		, GO_TO_ARTICLE/** 获取往期issue后不更新首页，只跳至文章页 **/
		, Zip_GO_TO_ARTICLE/** 获取往期issue后不更新首页，下载zip包,跳至文章页 **/
		;
	}

	public interface FetchPreviousIssueCallBack {
		public void onSuccess(TagInfo tagInfo);

		public void onFailed();
	}

	public TagMainProcessPreIssue(Context context,
			MainProcessParseCallBack callBack) {
		super(context, callBack);
	}

	/**
	 * 查看往期
	 * 
	 * @param issueId
	 * @param callBack
	 */
	public void getPreIssue(TagInfo tagInfo,
			FetchPreviousIssueCallBack callBack, PreIssusType preIssusType) {
		if (preIssusType == PreIssusType.REFRESH_INDEX) {
			// TODO 刷新首页直接走normal的流程
			TagMainProcessNormal normal = new TagMainProcessNormal(mContext,
					null);
			AppValue.appInfo.setTagName(tagInfo.getTagName());
			normal.getCatList();
		} else if (callBack != null) {
			callBack.onSuccess(tagInfo);
		}
	}

}
