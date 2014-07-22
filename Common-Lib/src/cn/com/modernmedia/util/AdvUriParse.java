package cn.com.modernmedia.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.widget.CommonWebView;
import cn.com.modernmediaslate.model.Entry;

/**
 * 广告的slate
 * 
 * @author user
 * 
 */
public class AdvUriParse {
	public static final String GOTO_ADV_ARTICLE = "goto_adv_article";
	public static final String GOTO_ADV_SOLO_ARTICLE = "goto_adv_solo_article";
	public static final String GOTO_VIDEO = "goto_video";
	public static final String GOTO_INSIDE_WEB = "goto_inside_web";
	public static final String GOTO_NORMAL_ARTICLE = "goto_normal_article";

	/**
	 * @v1 跳转到文章间广告(第一篇) : slate://adv/{advid}
	 * @v1_2 跳转到独立栏目文章间广告(第一篇) : slate://adv/{advid}/{issueId}/{catId}/
	 * 
	 * @v2 跳转到视频 : slate://adv/video/{url}
	 * 
	 * @v3 跳转到内置浏览器 slate://adv/web/{url}
	 * 
	 * @v4 跳转到普通文章
	 *     slate://adv/article/{issueId}/{columnId}/{articleId}/{from}/{page}/
	 * 
	 * @param uri
	 * @return
	 */
	private static ArrayList<String> parser(String uri) {
		ArrayList<String> list = new ArrayList<String>();
		if (TextUtils.isEmpty(uri))
			return list;
		if (uri.startsWith("slate://adv/")) {
			String[] array = uri.split("slate://adv/");
			parseArray(uri, array, list);
		} else if (uri.startsWith("slate://")) {
			// TODO 兼容老版本slate
			String[] array = uri.split("slate://");
			parseArray(uri, array, list);
		}
		return list;
	}

	private static void parseArray(String uri, String[] array,
			ArrayList<String> list) {
		if (array.length > 1) {
			String[] param = array[1].split("/");
			if (param.length == 1) {
				list.add(GOTO_ADV_ARTICLE);
				list.add(param[0]);
			} else if (param.length > 1) {
				if (param[0].equals("video")) {
					// TODO 跳转到视频
					// slate://adv/video/http://v.cdn.bb.bbwc.cn/bloomberg/2013/09/24/20130924175709349/20130924175709349.mp4
					String[] videoPa = uri.split("video/");
					if (videoPa.length == 2 && videoPa[1] != "") {
						String path = videoPa[1].replace(".m3u8", ".mp4");//
						list.add(GOTO_VIDEO);
						list.add(path);
					}
				} else if (param[0].equals("web")) {
					// TODO 跳转到内置浏览器
					String[] webPa = uri.split("web/");
					if (webPa.length == 2 && webPa[1] != "") {
						list.add(GOTO_INSIDE_WEB);
						list.add(webPa[1]);
					}
				} else if (param[0].equals("article") && param.length > 3) {
					list.add(GOTO_NORMAL_ARTICLE);
					list.add(param[1]);
					list.add(param[2]);
					list.add(param[3]);
				} else {
					// TODO 跳转到广告文章
					if (param.length == 3) {
						list.add(GOTO_ADV_SOLO_ARTICLE);
						list.add(param[0]);
						list.add(param[1]);
						list.add(param[2]);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param context
	 * @param entries
	 *            [0] ArticleItem;[1]TransferArticle...
	 * @param view
	 * @param cls
	 *            [0]为特定的文章页
	 */
	public static void clickSlate(Context context, String link,
			Entry[] entries, View view, Class<?>... cls) {
		if (TextUtils.isEmpty(link)) {
		} else if (link.toLowerCase().startsWith("http://")
				|| link.toLowerCase().startsWith("https://")) {
			UriParse.doLinkHttp(context, link);
		} else if (link.toLowerCase().startsWith("slate://")) {
			List<String> list = parser(link);
			if (ParseUtil.listNotNull(list) && list.size() > 1) {
				String key = list.get(0);
				if (key.equals(GOTO_ADV_ARTICLE)) {
					doLinkAdv(context, list, entries, view, cls);
				} else if (key.equals(GOTO_ADV_SOLO_ARTICLE)) {
					doLinkAdv(context, list, entries, view, cls);
				} else if (key.equals(GOTO_VIDEO)) {
					UriParse.doLinkVideo(context, list.get(1));
				} else if (key.equals(GOTO_INSIDE_WEB)) {
					UriParse.doLinkWeb(context, list.get(1));
				} else if (key.equals(GOTO_NORMAL_ARTICLE) && list.size() > 3) {
					UriParse.doLinkArticle(context, list, entries, view, cls);
				}
			}
		}
	}

	private static void doLinkAdv(Context context, List<String> list,
			Entry[] entries, View view, Class<?>... cls) {
		int advId = ParseUtil.stoi(list.get(1), -1);
		int catId = -1;
		if (list.size() == 4)
			catId = ParseUtil.stoi(list.get(3), -1);
		if (view instanceof CommonWebView) {
			((CommonWebView) view).gotoAdv(advId);
		} else {
			TransferArticle tr = new TransferArticle(-1, catId, advId,
					list.size() > 2 ? ArticleType.Solo : ArticleType.Default);
			TransferArticle transferArticle = entries.length > 1 ? (TransferArticle) entries[1]
					: null;
			if (transferArticle != null)
				tr.setUid(transferArticle.getUid());
			if (cls != null && cls.length > 0) {
				PageTransfer.gotoArticleActivity(context, cls[0], tr);
			} else if (context instanceof CommonMainActivity) {
				((CommonMainActivity) context).gotoArticleActivity(tr);
			}
		}
	}

}
