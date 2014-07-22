package cn.com.modernmedia.common;

import java.net.URLEncoder;

import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * Ã⁄—∂Œ¢≤©∞Ô÷˙¿‡
 * @version 1.0
 * @created 2012-3-22
 */
public class QQWeiboHelper {
	
	private static final String Share_URL = "http://share.v.t.qq.com/index.php?c=share&a=index";
	private static final String Share_Source = "bbwc";
	private static final String Share_Site = "bbwc.cn";
	private static final String Share_AppKey = "";
	
	/**
	 * ∑÷œÌµΩÃ⁄—∂Œ¢≤©
	 * @param activity
	 * @param title
	 * @param url
	 */
	public static void shareToQQ(Activity activity,String title,String url){
		String URL = Share_URL;
		try {
			URL += "&title=" + URLEncoder.encode(title, HTTP.UTF_8) + "&url=" + URLEncoder.encode(url, HTTP.UTF_8) + "&appkey=" + Share_AppKey + "&source=" + Share_Source + "&site=" + Share_Site;	
		} catch (Exception e) {
			e.printStackTrace();
		}
		Uri uri = Uri.parse(URL);
		activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));	
	}
}