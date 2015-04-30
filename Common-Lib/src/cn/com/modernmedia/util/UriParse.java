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
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.VideoPlayerActivity;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.widget.CommonAtlasView;
import cn.com.modernmedia.widget.CommonWebView;
import cn.com.modernmedia.widget.WebViewPop;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;

/*
 * uri转换类
 * 转换规则
 * 打开文章 slate://article/tagname/{tagname}/{articleId}/{page}/
 * 打开视频 slate://video/{videoUrl}
 * 打开大图 slate://image/{imageUrl}
 * 打开当期栏目首页 slate://column/{columnId}/{issueId}/  无用
 * 打开当期今日焦点 slate://column/home/{issueId}/  无用
 * 图集slate://gallery/pic,pic,pic
 * 打开某期 slate://issue/{issueId}/   无用
 * 打开最新期 slate://issue/latest/{appid}/  无用
 * 打开栏目slate://column/tagname/{tagname}
 */
public class UriParse {
	public static final String TAG = "UriParser";

	public static final String SETTINGS = "settings";
	public static final String ABOUT = "about";
	public static final String FEEDBACK = "feedback";
	public static final String FOLLOW = "follow";
	public static final String WEIBO = "weibo";
	public static final String WEIXIN = "weixin";
	public static final String STORE = "store";// 去商店评论

	public static final String COLUMN = "column";
	public static final String IMAGE = "image";
	public static final String VIDEO = "video";
	public static final String ARTICLE = "article";
	public static final String GALLERY = "gallery";
	public static final String WEB = "web";// 外部浏览器打开
	public static final String WEBNOSHARE = "webNoShare";// 内部浏览器打开

	// public static final String TAGNAME = "tagname";

	/*
	 * @param String uri
	 */
	private static ArrayList<String> article(String uri) {
		// slate://article/tagname/cat_19/46204/1
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
	 */
	private static ArrayList<String> column(String uri) {
		// slate://column/tagname/{tagname}
		ArrayList<String> list = new ArrayList<String>();
		String[] Array = uri.split("column/tagname/");
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

	/**
	 * slate://webNoShare/http://www.standardchartered.com.cn/nextgen/index.html
	 * 
	 * @param uri
	 * @return
	 */
	private static String webNoShare(String uri) {
		String[] Array = uri.split("webNoShare/");
		if (Array.length == 2) {
			return Array[1];
		}
		return null;
	}

	/**
	 * slate://tagname/cat_xxx 跳转到某个栏目
	 * 
	 * @param uri
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String tagname(String uri) {
		String[] Array = uri.split("tagname/");
		if (Array.length == 2) {
			return Array[1];
		}
		return null;
	}

	/**
	 * slate://about/ 打开关于
	 * 
	 * @param uri
	 * @return
	 */
	private static String about(String uri) {
		String[] Array = uri.split("about/");
		if (Array.length == 2) {
			return Array[1];
		}
		return null;
	}

	/**
	 * slate://settings/ 打开设置
	 * 
	 * @param uri
	 * @return
	 */
	private static String settings(String uri) {
		String[] Array = uri.split("settings/");
		if (Array.length == 2) {
			return Array[1];
		}
		return null;
	}

	/**
	 * slate://follow/weibo/ 关注官方微博 slate://follow/weixin/ 关注官方微信
	 * 
	 * @param uri
	 * @return
	 */
	private static String follow(String uri) {
		String[] Array = uri.split("follow/");
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
			} else if (param[0].equals(WEBNOSHARE)) {
				String url = webNoShare(uri);
				if (!TextUtils.isEmpty(url)) {
					list.add(url);
				}
			} else if (param[0].equals(SETTINGS)) {
				String setting = settings(uri);
				if (!TextUtils.isEmpty(setting)) {
					list.add(setting);
				}
			} else if (param[0].equals(ABOUT)) {
				String about = about(uri);
				if (!TextUtils.isEmpty(about)) {
					list.add(about);
				}
			} else if (param[0].equals(FOLLOW)) {
				String follow = follow(uri);
				if (!TextUtils.isEmpty(follow)) {
					list.add(follow);
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
				if (TextUtils.isEmpty(link))
					return;
				if (link.startsWith("slate://adv/")) {
					// TODO 跳转到广告文章
					AdvUriParse.clickSlate(context, link, entries, view, cls);
					return;
				}
				link = link.replace("adv/", "");
			} else {
				link = item.getSlateLink();
			}

			clickSlate(context, link, entries, view, cls);
		}
	}

	public static void clickSlate(Context context, String link,
			Entry[] entries, View view, Class<?>... cls) {
		if (TextUtils.isEmpty(link)) {
			doLinkNull(context, entries, cls);
		} else if (link.toLowerCase().startsWith("http://")
				|| link.toLowerCase().startsWith("https://")) {
			doLinkHttp(context, link);
		} else if (link.toLowerCase().startsWith("slate://card/")
				|| link.toLowerCase().startsWith("slate://user/")) {
			String key = link.toLowerCase().startsWith("slate://card/") ? "slate://card/"
					: "slate://user/";
			String[] cards = link.split(key);
			if (cards != null && cards.length == 2
					&& !TextUtils.isEmpty(cards[1])
					&& CommonApplication.userUriListener != null) {
				CommonApplication.userUriListener.doCardUri(context, cards[1]);
			}
		} else if (link.toLowerCase().startsWith("slate://")) {
			List<String> list = parser(link);
			String key = list.size() > 0 ? list.get(0).toLowerCase() : "";
			if (list.size() > 1) {
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
				} else if (key.equals(WEBNOSHARE)) {
					doLinkWeb(context, list.get(1));
				} else if (key.equals(COLUMN)) {
					doTagname(context, list.get(1));
				} else if (key.equals(FOLLOW)) { // 微博、微信关注
					doFollow(context, list.get(1));
				}
			} else if (key.equals(ABOUT) || key.equals(SETTINGS)) {
				gotoActivity(context, cls);
			} else if (key.equals(FEEDBACK)) {
				ModernMediaTools.feedBack(context);
			} else if (key.equals(STORE)) {
				ModernMediaTools.assess(context);
			}
		} else if (link.startsWith("tel://")) {
			String arr[] = link.split("tel://");
			if (arr.length == 2)
				doCall(context, arr[1]);
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
		String tagName = "";
		if (item.getApiTag().equals(item.getTagName())) {
			tagName = item.getTagName();
		} else {
			if (item.getApiTag().contains(",")) {
				// TODO
				// 组合tagname,使用item自己的tagname再去数据库里比较，如果直接给文章页传组合id，再数据库查找的时候会找不到
				tagName = item.getTagName();
			} else {
				// TODO
				// 如果请求api的tagname和当前item的tagname不一致，并且api不是组合id,那么使用api的tagname(商周首页)
				tagName = item.getApiTag();
			}
		}
		TransferArticle tr = new TransferArticle(item.getArticleId(), tagName,
				item.getParent(), -1, ArticleType.Default);
		if (transferArticle != null) {
			tr.setUid(transferArticle.getUid());
			tr.setArticleType(transferArticle.getArticleType());
		}
		PageTransfer.gotoArticleActivity(context, tr);
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
		TransferArticle tr = new TransferArticle(
				ParseUtil.stoi(list.get(3), -1), list.get(2), "", -1,
				ArticleType.Default);
		TransferArticle transferArticle = entries.length > 1 ? (TransferArticle) entries[1]
				: null;
		if (transferArticle != null)
			tr.setUid(transferArticle.getUid());

		if (context instanceof CommonArticleActivity) {
			tr.setUid(Tools.getUid(context));
			if (view instanceof CommonWebView) {
				((CommonWebView) view).gotoArticle(tr);
				return;
			} else if (view instanceof CommonAtlasView) {
				((CommonAtlasView) view).gotoArticle(tr);
				return;
			}
		}
		PageTransfer.gotoArticleActivity(context, tr);
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

	/**
	 * 跳转到拨号页面
	 * 
	 * @param context
	 * @param telNumber
	 *            电话号码
	 */
	private static void doCall(Context context, String telNumber) {
		try {
			Uri uri = Uri.parse("tel:" + telNumber); // 拨打电话号码的URI格式
			Intent intent = new Intent(); // 实例化Intent
			intent.setAction(Intent.ACTION_DIAL); // 指定Action
			intent.setData(uri); // 设置数据
			context.startActivity(intent);// 启动Acitivity
		} catch (Exception e) {
			Tools.showToast(context, R.string.dial_error);
			e.printStackTrace();
		}
	}

	/**
	 * 跳转到某个栏目
	 * 
	 * @param context
	 * @param tagName
	 */
	private static void doTagname(Context context, String tagName) {
		if (CommonApplication.mConfig.getIs_index_pager() == 1) {
			if (context instanceof CommonMainActivity)
				((CommonMainActivity) context).clickItemIfPager(tagName, true);
		}
	}

	/**
	 * 跳转到指定的activity
	 * 
	 * @param context
	 * @param cls
	 */
	private static void gotoActivity(Context context, Class<?>... cls) {
		if (cls != null && cls.length > 0) {
			Intent intent = new Intent(context, cls[0]);
			context.startActivity(intent);
		}
	}

	/**
	 * 微博、微信关注
	 * 
	 * @param context
	 * @param type
	 */
	private static void doFollow(Context context, String type) {
		if (WEIBO.equals(type)) {
			ModernMediaTools.followWeibo(context);
		} else if (WEIXIN.equals(type)) {
			ModernMediaTools.followWeixin(context);
		}
	}
}
