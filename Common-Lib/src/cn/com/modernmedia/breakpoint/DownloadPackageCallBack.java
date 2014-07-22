package cn.com.modernmedia.breakpoint;

/**
 * 断点下载回调
 * 
 * @author ZhuQiao
 * 
 */
public interface DownloadPackageCallBack {
	/**
	 * 当以前已经成功下载过，当点击下载的时候回调
	 */
	public void onSuccess(int issueId, String folderName);

	/**
	 * 下载暂停
	 */
	public void onPause(int issueId);

	/**
	 * 下载失败
	 */
	public void onFailed(int issueId);

	/**
	 * 下载中
	 * 
	 * @param complete
	 * @param total
	 */
	public void onProcess(int issueId, long complete, long total);
}
