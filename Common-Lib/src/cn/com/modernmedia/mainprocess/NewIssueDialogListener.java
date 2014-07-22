package cn.com.modernmedia.mainprocess;

/**
 * 新一期提示回调
 * 
 * @author user
 * 
 */
public interface NewIssueDialogListener {
	/**
	 * 稍后再看
	 */
	public void viewLater(int id);

	/**
	 * 现在就看
	 */
	public void viewNow(int id);
}
