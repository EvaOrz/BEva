package cn.com.modernmedia.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.WebView;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;

//文章url:issue_截  加issue_
//图片：issue_截  加pictures/issue_
//css,js statics截  加statics/
public class UseLocalDataUtil {
	public static final String SPLIT = "issue_";
	public static final String RES_SPLIT = "statics";
	public static final String PICTURES = "pictures/";

	private WebView mWebView;
	private String mFolderPath = "";

	public UseLocalDataUtil(Context context, WebView view) {
		if (context instanceof CommonArticleActivity) {
			TransferArticle transferArticle = ((CommonArticleActivity) context)
					.getBundle();
			if (transferArticle != null)
				mFolderPath = transferArticle.getFloderName();
			// mFolderPath = "alldata-iphone-108-4_1362568368";
		}
		mWebView = view;
	}

	/**
	 * 获取html文件
	 * 
	 * @param link
	 * @return
	 */
	public boolean getLocalHtml(String link) {
//		System.out.println(link);
		String path = getUsefulPath(link, SPLIT);
		if (!TextUtils.isEmpty(path)) {
			path = SPLIT + path;
			// test
			path = path.replace("-10-2-", "-1-1-");
			File file = new File(getDefaultPath(path));
			if (file.exists()) {
				// mWebView.loadUrl(getHtmlDefaultPath(path));
				String html = replaceHttp(file);
				if (!TextUtils.isEmpty(html)) {
					mWebView.loadDataWithBaseURL(link, html, "text/html",
							"UTF-8", null);
					// System.out.println("html true");
					return true;
				}
			}
		}
		// System.out.println("html false");
		return false;
	}

	/**
	 * 获取本地图片
	 * 
	 * @param url
	 * @return
	 */
	private String getLocalPicPath(String link) {
		String path = getUsefulPath(link, SPLIT);
		if (!TextUtils.isEmpty(path)) {
			path = PICTURES + SPLIT + path;
			File file = new File(getDefaultPath(path));
			if (file.exists()) {
				// System.out.println("pic true");
				return getHtmlDefaultPath(path);
			}
		}
		// System.out.println("pic false");
		return null;
	}

	/**
	 * 获取本地资源css,js
	 * 
	 * @param url
	 * @return
	 */
	private String getLocalResPath(String link) {
		String path = getUsefulPath(link, RES_SPLIT);
		if (!TextUtils.isEmpty(path)) {
			path = RES_SPLIT + path;
			File file = new File(getDefaultPath(path));
			if (file.exists()) {
				// System.out.println("res true");
				return getHtmlDefaultPath(path);
			}
		}
		// System.out.println("res false");
		return null;
	}

	private String getUsefulPath(String link, String split) {
		if (!TextUtils.isEmpty(link) && link.contains(split)
				&& !TextUtils.isEmpty(mFolderPath)) {
			String[] arr = link.split(split);
			if (arr.length > 1) {
				return arr[1];
			}
		}
		return null;
	}

	private String getDefaultPath(String path) {
		return Environment.getExternalStorageDirectory().getPath()
				+ ConstData.DEFAULT_PACKAGE_PATH + mFolderPath + File.separator
				+ path;
	}

	private String getHtmlDefaultPath(String path) {
		return "file://" + Environment.getExternalStorageDirectory().getPath()
				+ ConstData.DEFAULT_PACKAGE_PATH + mFolderPath + File.separator
				+ path;
	}

	private String replaceHttp(File file) {
		String html = "";
		try {
			Document doc = Jsoup.parse(file, "UTF-8", "");
			html = doc.html();

			Elements links = doc.select("a[href]");
			Elements media = doc.select("[src]");
			Elements imports = doc.select("link[href]");
			Elements data_original = doc.select("[data-original]");

			List<String> list = new ArrayList<String>();
			for (Element src : media) {
				// System.out.println("medis==" + src.attr("abs:src"));
				list.add(src.attr("abs:src"));
			}

			for (Element link : imports) {
				// System.out.println("imports==" + link.attr("abs:href"));
				list.add(link.attr("abs:href"));
			}

			for (Element link : links) {
				// System.out.println("links==" + link.attr("abs:href"));
				list.add(link.attr("abs:href"));
			}

			for (Element orgin : data_original) {
				// System.out.println("data-original=="
				// + orgin.attr("abs:data-original"));
				list.add(orgin.attr("abs:data-original"));
			}

			if (ParseUtil.listNotNull(list)) {
				for (String url : list) {
					if (url.toLowerCase().endsWith(".png")
							|| url.toLowerCase().endsWith(".jpg")) {
						String path = getLocalPicPath(url);
						if (!TextUtils.isEmpty(path)) {
							html = html.replace(url, path);
						}
					} else if (url.toLowerCase().contains("statics")) {
						String path = getLocalResPath(url);
						if (!TextUtils.isEmpty(path)) {
							html = html.replace(url, path);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return html;
	}

}
