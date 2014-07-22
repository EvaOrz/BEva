package cn.com.modernmedia.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import cn.com.modernmedia.listener.SlateListener;
import cn.com.modernmedia.model.ArticleItem;

/*
 * uri转换类
 * 转换规则
 * 打开文章 slate://article/{issueId}/{columnId}/{articleId}/{from}/{page}/
 * 打开视频 slate://video/{videoUrl}
 * 打开大图 slate://image/{imageUrl}
 * 打开当期栏目首页 slate://column/{columnId}/{issueId}/
 * 打开当期今日焦点 slate://column/home/{issueId}/
 * 图集slate://gallery/{issueId}/{columnId}/{articleId}
 * -打开某期 slate://issue/{issueId}/
 * -打开�?���?slate://issue/latest/{appid}/
 */
public class UriParse {
	public static final String TAG = "UriParser";

	/*
	 * @param String uri
	 * 
	 * @return arraylist {issueID,columnId,articleId,from,page};
	 * 
	 * @form 来源
	 * 
	 * @page 页码
	 */
	private static ArrayList<String> article(String uri) {
		// "issueId|columnId|articleId|from|page";
		ArrayList<String> list = new ArrayList<String>();
		String[] Array = uri.split("article/");
		if (Array.length == 2) {
			String[] param = Array[1].split("/");
			for (int i = 0; i < param.length; i++) {
				if (param[i] != null) {
					list.add(param[i]);
				}
			}

		}
		return list;
	}

	private static ArrayList<String> gallery(String uri) {
		// gallery/url,url
		ArrayList<String> list = new ArrayList<String>();
		String[] Array = uri.split("gallery/");
		if (Array.length == 2) {
			String[] param = Array[1].split(",");
			for (int i = 0; i < param.length; i++) {
				if (param[i] != null) {
					list.add(param[i]);
				}
			}

		}
		return list;
	}

	/*
	 * @param String uri
	 * 
	 * @return arraylist videoUrl 视频url
	 */
	private static ArrayList<String> video(String uri) {
		ArrayList<String> list = new ArrayList<String>();
		String[] param = uri.split("video/");

		if (param.length == 2 && param[1] != "") {
			list.add(param[1]);
		}
		return list;

	}

	/*
	 * @param String uri
	 * 
	 * @return arraylist imageUrl 图片url
	 */
	private static ArrayList<String> image(String uri) {
		ArrayList<String> list = new ArrayList<String>();
		String[] param = uri.split("image/");
		if (param.length == 2 && param[1] != "") {
			list.add(param[1]);
		}
		return list;
	}

	/*
	 * @param String uri
	 * 
	 * @return arraylist {columnId,issueId} columnID=home 打开当期今日焦点
	 * columnID!=home 打开当期栏目首页
	 */
	private static ArrayList<String> column(String uri) {
		// "issueId|columnId|articleId|from|page";
		ArrayList<String> list = new ArrayList<String>();
		String[] Array = uri.split("column/");
		if (Array.length == 2) {
			String[] param = Array[1].split("/");
			for (int i = 0; i < param.length; i++) {
				if (param[i] != null) {
					list.add(param[i]);
				}
			}

		}
		return list;
	}

	/*
	 * 返回uri类型和相关参数
	 * 
	 * @param String uri
	 * 
	 * @return ArrayList list
	 * 
	 * @list.get(0) 为uri类型 article,column....
	 */
	public static ArrayList<String> parser(String uri) {
		ArrayList<String> list = new ArrayList<String>();
		if (TextUtils.isEmpty(uri))
			return list;
		String[] Array = uri.split("://");
		if (Array.length > 1) {
			String[] param = Array[1].split("/");
			list.add(param[0]);
			if (param[0].equals("column")) {
				list.addAll(column(uri));
			} else if (param[0].equals("image")) {
				list.addAll(image(uri));
			} else if (param[0].equals("video")) {
				list.addAll(video(uri));
			} else if (param[0].equals("article")) {
				list.addAll(article(uri));
			} else if (param[0].equals("gallery")) {
				list.addAll(gallery(uri));
			}
		}
		return list;
	}

	/**
	 * 解析push消息(116-37-7591 期id-栏目id-文章id)
	 * 
	 * @param uri
	 * @return
	 */
	public static String[] parsePush(String uri) {
		String[] array = null;
		if (TextUtils.isEmpty(uri))
			return array;
		return uri.split("-");
	}

	public static void clickSlate(SlateListener listener, ArticleItem item) {
		if (listener == null)
			return;
		String link = "";
		int type = item.getAdv().getAdvProperty().getIsadv();
		if (type == 1) {// 广告
			link = item.getAdv().getColumnAdv().getLink();
		} else {
			link = item.getSlateLink();
		}
		clickSlate(listener, link, item);
	}

	public static void clickSlate(SlateListener listener, String link,
			ArticleItem item) {
		if (listener == null)
			return;
		if (TextUtils.isEmpty(link)) {
			listener.linkNull(item);
		} else if (link.toLowerCase().startsWith("http://")) {
			Uri uri = Uri.parse(link);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			listener.httpLink(item, intent);
		} else if (link.toLowerCase().startsWith("slate://")) {
			List<String> list = UriParse.parser(link);
			if (list.size() > 1) {
				if (list.get(0).equalsIgnoreCase("video")) {
					String path = list.get(1).replace(".m3u8", ".mp4");//
					if (path.toLowerCase().endsWith(".mp4")) {
						listener.video(item, path);
					}
				} else if (list.get(0).equalsIgnoreCase("article")) {
					if (list.size() > 3) {
						listener.articleLink(item,
								ParseUtil.stoi(list.get(3), -1));
					}
				} else if (list.get(0).equalsIgnoreCase("gallery")) {
					list.remove(0);
					listener.gallery(list);
				}
			}
		}
	}
}
