package cn.com.modernmedia.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.VideoPlayerActivity;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.widget.CommonAtlasView;
import cn.com.modernmedia.widget.CommonWebView;
import cn.com.modernmedia.widget.WebViewPop;
import cn.com.modernmediaslate.model.Entry;

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

	public static final String COLUMN = "column";
	public static final String IMAGE = "image";
	public static final String VIDEO = "video";
	public static final String ARTICLE = "article";
	public static final String GALLERY = "gallery";
	public static final String WEB = "web";

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
		String[] param = uri.split("slate://video/");

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
		// solo : slate://column/32/0/
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

	/**
	 * slate://web/http://www.standardchartered.com.cn/nextgen/index.html
	 * 
	 * @param uri
	 * @return
	 */
	private static String web(String uri) {
		String[] Array = uri.split("web/");
		if (Array.length == 2) {
			return Array[1];
		}
		return null;
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
			if (param[0].equals(COLUMN)) {
				list.addAll(column(uri));
			} else if (param[0].equals(IMAGE)) {
				list.addAll(image(uri));
			} else if (param[0].equals(VIDEO)) {
				list.addAll(video(uri));
			} else if (param[0].equals(ARTICLE)) {
				list.addAll(article(uri));
			} else if (param[0].equals(GALLERY)) {
				list.addAll(gallery(uri));
			} else if (param[0].equals(WEB)) {
				String url = web(uri);
				if (!TextUtils.isEmpty(url)) {
					list.add(url);
				}
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

	/**
	 * 普通列表点击
	 * 
	 * @param context
	 * @param entries
	 *            [0] ArticleItem;[1]TransferArticle...
	 * @param cls
	 *            [0]为特定的文章页
	 */
	public static void clickSlate(Context context, Entry[] entries,
			Class<?>... cls) {
		click(context, entries, null, cls);
	}

	/**
	 * 特殊view点击(commonwebview)
	 * 
	 * @param context
	 * @param entries
	 *            [0] ArticleItem;[1]TransferArticle;[2]issue...
	 * @param view
	 * @param cls
	 */
	public static void clickSlate(Context context, Entry[] entries, View view,
			Class<?>... cls) {
		click(context, entries, view, cls);
	}

	private static void click(Context context, Entry[] entries, View view,
			Class<?>... cls) {
		if (entries != null && entries[0] instanceof ArticleItem) {
			String link = "";
			ArticleItem item = (ArticleItem) entries[0];
			if (item.getAdvSource() != null) {// 广告
				link = item.getAdvSource().getLink();
				AdvUriParse.clickSlate(context, link, entries, view, cls);
			} else {
				link = item.getSlateLink();
				clickSlate(context, link, entries, view, cls);
			}
		}
	}

	public static void clickSlate(Context context, String link,
			Entry[] entries, View view, Class<?>... cls) {
		if (TextUtils.isEmpty(link)) {
			doLinkNull(context, entries, cls);
		} else if (link.toLowerCase().startsWith("http://")
				|| link.toLowerCase().startsWith("https://")) {
			doLinkHttp(context, link);
		} else if (link.toLowerCase().startsWith("slate://card/")) {
			String[] cards = link.split("slate://card/");
			if (cards != null && cards.length == 2
					&& !TextUtils.isEmpty(cards[1])
					&& CommonApplication.userUriListener != null) {
				CommonApplication.userUriListener.doCardUri(context, cards[1]);
			}
		} else if (link.toLowerCase().startsWith("slate://")) {
			List<String> list = parser(link);
			if (list.size() > 1) {
				String key = list.get(0).toLowerCase();
				if (key.equals(VIDEO)) {
					String path = list.get(1).replace(".m3u8", ".mp4");
					doLinkVideo(context, path);
				} else if (key.equals(ARTICLE)) {
					if (list.size() > 3) {
						doLinkArticle(context, list, entries, view, cls);
					}
				} else if (key.equals(GALLERY)) {
					list.remove(0);
					doLinkGallery(list, view);
				} else if (key.equals(WEB)) {
					// doLinkWeb(context, list.get(1));
					doLinkHttp(context, list.get(1));
				}
			}
		}
	}

	/**
	 * 如果slate为空，默认跳转到文章页
	 * 
	 * @param context
	 * @param item
	 * @param transferArticle
	 */
	private static void doLinkNull(Context context, Entry[] entries,
			Class<?>... cls) {
		ArticleItem item = (ArticleItem) entries[0];
		TransferArticle transferArticle = entries.length > 1 ? (TransferArticle) entries[1]
				: null;
		TransferArticle tr = new TransferArticle(item.getArticleId(),
				item.getCatId(), -1, ArticleType.Default);
		if (transferArticle != null) {
			tr.setUid(transferArticle.getUid());
			tr.setArticleType(transferArticle.getArticleType());
		}
		if (cls != null && cls.length > 0) {
			PageTransfer.gotoArticleActivity(context, cls[0], tr);
		} else if (context instanceof CommonMainActivity) {
			((CommonMainActivity) context).gotoArticleActivity(tr);
		}
	}

	/**
	 * 如果slate以link为开头，跳转至外部浏览器
	 * 
	 * @param context
	 * @param link
	 */
	public static void doLinkHttp(Context context, String link) {
		Uri uri = Uri.parse(link);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		((Activity) context).startActivity(intent);
	}

	/**
	 * 跳转至视频播放activity
	 * 
	 * @param context
	 * @param path
	 */
	public static void doLinkVideo(Context context, String path) {
		Intent intent = new Intent(context, VideoPlayerActivity.class);
		intent.putExtra("vpath", path);
		context.startActivity(intent);
	}

	/**
	 * 进入文章页跳转至某一篇文章(webview是直接跳转到某一篇文章)
	 * 
	 * @param context
	 * @param list
	 * @param catId
	 * @param transferArticle
	 */
	public static void doLinkArticle(Context context, List<String> list,
			Entry[] entries, View view, Class<?>... cls) {
		if (view instanceof CommonWebView) {
			((CommonWebView) view).gotoArticle(ParseUtil.stoi(list.get(3), -1));
		} else if (view instanceof CommonAtlasView) {
			((CommonAtlasView) view)
					.gotoArticle(ParseUtil.stoi(list.get(3), -1));
		} else {
			int issueId = 0;
			if (list.size() > 1)
				issueId = ParseUtil.stoi(list.get(1), 0);
			TransferArticle tr = new TransferArticle(ParseUtil.stoi(
					list.get(3), -1), ParseUtil.stoi(list.get(2), -1), -1,
					issueId == 0 ? ArticleType.Solo : ArticleType.Default);
			TransferArticle transferArticle = entries.length > 1 ? (TransferArticle) entries[1]
					: null;
			if (transferArticle != null)
				tr.setUid(transferArticle.getUid());
			// test 跳转到其它期
			if (CommonApplication.issue != null && issueId != 0
					&& issueId != CommonApplication.issue.getId()) {
				tr.setArticleType(ArticleType.Last);
				CommonApplication.currentIssueId = issueId;
			}
			if (cls != null && cls.length > 0) {
				PageTransfer.gotoArticleActivity(context, cls[0], tr);
			} else if (context instanceof CommonMainActivity) {
				((CommonMainActivity) context).gotoArticleActivity(tr);
			}
		}
	}

	/**
	 * 显示gallery
	 * 
	 * @param urlList
	 * @param view
	 */
	public static void doLinkGallery(List<String> urlList, View view) {
		if (view instanceof CommonWebView) {
			((CommonWebView) view).fetchGalleryList(urlList);
		}
	}

	/**
	 * 跳转到内部浏览器
	 * 
	 * @param url
	 */
	public static void doLinkWeb(Context context, String link) {
		new WebViewPop(context, link);
	}
}
