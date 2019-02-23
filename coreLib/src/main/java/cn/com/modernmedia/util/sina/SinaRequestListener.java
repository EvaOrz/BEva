package cn.com.modernmedia.util.sina;


/**
 * SinaAPI 请求数据的接口类
 * 
 * @author jiancong
 * 
 */
public interface SinaRequestListener {
	/**
	 * 数据请求成功
	 * 
	 * @param response
	 */
	public void onSuccess(String response);

	/**
	 * 数据请求失败
	 * 
	 * @param error
	 */
	public void onFailed(String error);
}
