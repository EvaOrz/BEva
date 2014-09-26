package cn.com.modernmedia.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.WebView;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmediaslate.unit.ParseUtil;

//文章url:issue_截  加issue_
//图片：issue_截  加pictures/issue_
//css,js statics截  加statics/
public class UseLocalDataUtil {
	public static final String SPLIT = "issue_";
	public static final String RES_SPLIT = "statics";
	public static final String PICTURES = "pictures/";
	public static final String ENCODE = "UTF-8";

	private WebView mWebView;
	private String mFolderPath = "";

	public UseLocalDataUtil(Context context, WebView view) {
		if (context instanceof CommonArticleActivity) {
			// test 文章
			mFolderPath = ((CommonArticleActivity) context)
					.getLocalArticlesFolder();
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
		// System.out.println(link);
		String path = getUsefulPath(link, SPLIT);
		if (!TextUtils.isEmpty(path)) {
			path = SPLIT + path;
			// test
			path = path.replace("-10-2-", "-1-1-");
			File file = new File(getDefaultPath(path));
			if (file.exists()) {
				// mWebView.loadUrl(getHtmlDefaultPath(path));
				String html = replaceHttp(file.getAbsolutePath());
				if (!TextUtils.isEmpty(html)) {
					mWebView.loadDataWithBaseURL(link, html, "text/html",
							ENCODE, null);
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

	private String openFile(String szFileName) {
		BufferedReader bis = null;
		try {
			bis = new BufferedReader(new InputStreamReader(new FileInputStream(
					new File(szFileName)), ENCODE));
			String szContent = "";
			String szTemp;

			while ((szTemp = bis.readLine()) != null) {
				szContent += szTemp + "\n";
			}
			return szContent;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null)
				try {
					bis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		return "";
	}

	private String replaceHttp(String filePath) {
		String html = "";
		try {
			List<String> list = new ArrayList<String>();
			html = openFile(filePath);
			Parser parser = Parser.createParser(html, ENCODE);
			NodeList nodes = parser.extractAllNodesThatMatch(new NodeFilter() {
				private static final long serialVersionUID = 1L;

				public boolean accept(Node node) {
					return true;
				}
			});

			for (int i = 0; i < nodes.size(); i++) {
				Node nodet = nodes.elementAt(i);
				if (nodet instanceof Tag) {
					Tag tag = (Tag) nodet;
					if (!TextUtils.isEmpty(tag.getAttribute("src"))) {
						list.add(tag.getAttribute("src"));
					}
					if (!TextUtils.isEmpty(tag.getAttribute("data-original"))) {
						list.add(tag.getAttribute("data-original"));
					}
					if (!TextUtils.isEmpty(tag.getAttribute("href"))) {
						list.add(tag.getAttribute("href"));
					}
				}
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
}
