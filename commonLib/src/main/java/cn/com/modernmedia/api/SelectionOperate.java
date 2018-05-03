package cn.com.modernmedia.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.util.Log;
import cn.com.modernmediaslate.model.ErrorMsg;

/**
 * 添加日志
 * 
 * @author lusiyuan
 *
 */
public class SelectionOperate extends BaseOperate {
	private ErrorMsg error;
	private String data;
	// post 参数设置
	ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

	/**
	 * post 方法传参有问题，目前不使用这个构造方法
	 * 
	 * @param array
	 */
	public SelectionOperate(JSONObject array) {
		error = new ErrorMsg();
		this.data = array.toString();
		// params.add(new BasicNameValuePair("data", data));
	}

	public SelectionOperate(String path) {
		error = new ErrorMsg();
		ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
		p.add(new BasicNameValuePair("name", path));
		setPostParams(p);
	}

	@Override
	protected Map<String, String> getHeader() {
		return new HashMap<String, String>();
	}

	/**
	 * 塞post参数
	 * 
	 * @return
	 */
	@Override
	protected ArrayList<NameValuePair> getPostParams() {
		return params;
	}

	protected void setPostParams(ArrayList<NameValuePair> p) {
		this.params = p;
	}

	public ErrorMsg getError() {
		return error;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getSelection();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		if (jsonObject != null) {
			Log.w("测试返回", jsonObject.toString());
		}
	}

	@Override
	protected void saveData(String data) {

	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

}
