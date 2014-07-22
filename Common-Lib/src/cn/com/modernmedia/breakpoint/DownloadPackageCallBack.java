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

	// /**
	// * 当一个子线程请求成功后回调，当发现complete==total时。表示所有子线程都跑完，下载成功
	// *
	// * @param complete
	// * @param total
	// */
	// public void onSingleSuccess(int complete, int total);

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
