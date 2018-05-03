package cn.com.modernmedia.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.CaifuArticleActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.OnlineVideoActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.VipShowInfoActivity;
import cn.com.modernmedia.WangqiArticleActivity;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.vrvideo.MD360PlayerActivity;
import cn.com.modernmedia.widget.CommonAtlasView;
import cn.com.modernmedia.widget.CommonWebView;
import cn.com.modernmedia.widget.WebViewPop;
import cn.com.modernmedia.zxing.activity.CaptureActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;

/*
 * uri转换类
 * 转换规则
 *
 * 支持的
 *      打开文章 slate://article/tagname/{tagname}/{articleId}/{page}/
 *      跳转到外部浏览器 slate://web/{http_url} // iweekly3.3.0 改为打开内部浏览器
 *      跳转到我的VIP、开通、升级 slate://vip
 *
 *      跳转到内部浏览器 slate://webOnlyClose/{http_url}
 *                     slate://web/{http_url}
 *      打开视频 slate://video/{videoUrl}
 *      跳转拨号页面 slate://tel:/{telNumber}
 *      关注微博，微信 slate://follow/{weibo||weixin}
 *      跳转到扫一扫 slate://qrCodeScan/
 *      跳转到用户vip信息页 https://share.bbwc.cn//slate/vip/
 *      打开360度VR视频 slate://video360/{videoUrl}/
 *      打开推送文章（弹出内置浏览器）slate://articlePush/{pattern}
 *                                slate://tagArticlePush/{articleId}/singleColumn
 *      打开VR全景图片 slate://pictrue360/{pictrueUrl}/
 *      带desc的图集slate://photos/pic#desc,pic#desc,pic#desc
 *      打开往期列表 slate://issuearticle
 *      打开精选页面 slate://specialColumnList/monograph
 *      打开财富页面 slate://fortuneColumn

 *      点击往期封面下一页 slate://articleScrollToNextPage
 *
 *       打开广告：slate://tagAdv/{advid}
 *                slate://tagAdv/web/{url}
 *                slate://tagAdv/video/{url}
 *                slate://tagAdv/tagArticle/{ids}
 *
 *      刷新webview当前地址 slate://webReload/{url}
 *      打开有赞商品文章 slate://showGoods/{pid}
 *
 * 不支持的：
 *      图集slate://gallery/pic,pic,pic（需要commonWebview上下文）
 *      打开大图 slate://image/{imageUrl}（需要commonWebview上下文）
 *      打开栏目slate://column/tagname/{tagname}（需要首页CommonMainActivity上下文）
 *      打开当期栏目首页 slate://column/{columnId}/{issueId}/  无用
 *      打开当期今日焦点 slate://column/home/{issueId}/  无用
 *      打开某期 slate://issue/{issueId}/   无用
 *      打开最新期 slate://issue/latest/{appid}/  无用
 *
 */
public class UriParse {
    public static final String TAG = "UriParser";

    public static final String SETTINGS = "settings";
    public static final String ABOUT = "about";
    public static final String FEEDBACK = "feedback";
    public static final String WEIBO = "weibo";
    public static final String WEIXIN = "weixin";
    public static final String STORE = "store";// 去商店评论

    public static final String COLUMN = "column";
    public static final String VIP = "vip";
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String ARTICLE = "article";
    public static final String TAGARTICLE = "tagArticle";// v6版本之后的文章跳转标示
    public static final String GALLERY = "gallery";
    public static final String PHOTO = "photos";
    public static final String DESC = "desc";
    public static final String WEB = "web";// 外部浏览器打开
    public static final String WEBNOSHARE = "webOnlyClose";// 内部浏览器打开
    public static final String QRCODESCAN = "qrCodeScan";// 跳转到扫一扫页面
    public static final String VRVIDEO = "video360";// 打开360度VR视频
    public static final String ARTICLEPUSH = "articlePush";//
    public static final String TAGARTICLEPUSH = "tagArticlePush";
    public static final String VIPHEAD = "share.bbwc.cn";//跳转去vip会员信息
    public static final String GIFT = "gift";//跳转去兑换礼品码
    public static final String VIPOPEN = "vip/open";//跳转去VIP购买
    public static final String SUBSCRIPTION = "subscription";//文章内容页面，弹出付费界面 4.1.0同跳转去VIP购买
    public static final String VIPPAY = "vippay";//跳转去某个具体的支付套餐

    public static final String TAGADV_ID = "tagAdv";
    public static final String TAGADV_WEB = "tagAdv/web";
    public static final String TAGADV_VIDEO = "tagAdv/video";
    public static final String TAGADV_ARTICLE = "tagAdv/tagArticle";

    public static final String ISSUEARTICLE = "issuearticle";// 打开往期详情列表
    public static final String SHANGCHENGLIST = "list_goods_scene_cms";//商城list页面
    public static final String SHANGCHENG_INFO = "list_goods_scene";// 打开简介页面
    public static final String SCROLLTONEXTPAGE = "articleScrollToNextPage";//往期封面下一页
    public static final String SHANGCHENG = "specialColumnList";//商城页面
    public static final String CAIFU = "fortuneColumn";
    public static final String CAIFU_ARTICLE = "buySeparatelyArticle";// 财富的文章详情页
    public static final String CAIFU_ARTICLE_INTRO = "buySeparatelyArticleIntroduce";// 财富的文章详情页
    public static final String WEB_RELOAD = "webReload";// 刷新
    public static final String SHOWGOODS = "showGoods";// 打开有赞文章

    private static ArrayList<String> tagAdvID(String uri) {
        ArrayList<String> list = new ArrayList<String>();
        String[] Array = uri.split("tagAdv/");
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

    // web/http://adv.bbwc.cn/articles/activitys/live1/index.html?questionnaireid=10065
    private void gotoAdv(Context c, String url) {
        ArrayList<String> list = new ArrayList<String>();
        String[] Array = url.split("/");

    }

    private static String tagAdvWeb(String uri) {
        String[] Array = uri.split("tagAdv/web/");
        if (Array.length == 2) {
            return Array[1];
        }
        return null;
    }

    private static String tagAdvArticle(String uri) {
        String[] Array = uri.split("tagAdv/tagArticle/");
        if (Array.length == 2) {
            return Array[1];

        }
        return null;
    }

    private static String buySeparatelyArticle(String uri) {
        String[] Array = uri.split("buySeparatelyArticle/");
        if (Array.length == 2) {
            return Array[1];

        }
        return null;
    }

    private static String tagAdvVideo(String uri) {
        String[] Array = uri.split("tagAdv/video/");
        if (Array.length == 2) {
            return Array[1];
        }
        return null;
    }

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

    /**
     * 商周3.4 网页调应用内webview加载文章详情
     *
     * @param uri
     * @return
     */
    private static String articlePush(String uri) {
        String[] Array = uri.split(ARTICLEPUSH + "/");
        if (Array.length == 2) {
            return Array[1];
        }
        return null;
    }

    private static String tagArticlePush(String uri) {
        String[] Array = uri.split(TAGARTICLEPUSH + "/");
        if (Array.length == 2) {
            String[] aa = Array[1].split("/");
            if (aa.length > 0) return aa[0];
        }
        return null;
    }

    /**
     * slate://video360/http://...mp4
     *
     * @param uri
     * @return
     */
    private static String vrVideo(String uri) {
        String[] Array = uri.split(VRVIDEO + "/");
        if (Array.length == 2) {
            return Array[1];
        }
        return null;
    }

    /*
     * @param String uri
     */
    private static ArrayList<String> tagArticle(String uri) {
        // slate://tagArticle/latest/cat_1383/200051351
        ArrayList<String> list = new ArrayList<String>();
        String[] Array = uri.split("tagArticle/");
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

    /**
     * 调用带desc的gallery
     *
     * @param uri
     * @return
     */
    private static ArrayList<ArticleItem.Picture> photos(String uri) {
        // photos/url,url
        ArrayList<ArticleItem.Picture> list = new ArrayList<ArticleItem.Picture>();
        String[] array = uri.split("slate://photos/");
        if (array.length == 2) {
            String[] param = array[1].split(",");
            for (int i = 0; i < param.length; i++) {
                ArticleItem.Picture p = new ArticleItem.Picture();
                if (param[i] != null && param.length == 2) {
                    String[] jj = param[i].split("#");
                    if (jj.length > 0) p.setBigimgurl(jj[0]);
                    if (jj.length > 1) p.setDesc(jj[1]);
                    list.add(p);
                } else {//仅图片无描述
                    p.setBigimgurl(param[i]);
                    list.add(p);
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
        // slate://column/{tagname}
        ArrayList<String> list = new ArrayList<String>();
        if (uri.contains("tagname")) {
            String[] Array = uri.split("column/tagname/");
            if (Array.length == 2) {
                String[] param = Array[1].split("/");
                for (int i = 0; i < param.length; i++) {
                    if (param[i] != null) {
                        list.add(param[i]);
                    }
                }

            }
        } else {
            String[] Array = uri.split("column/");
            if (Array.length == 2) {
                list.add(Array[1]);
            }
        }

        return list;
    }


    /**
     * slate://web/http://www.modernmedia.com.cn/
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
     * slate://webOnlyClose/http://adv.bbwc.cn/articles/2016-manage-bbs/index.html?active_id=10038&m_id=10046
     *
     * @param uri
     * @return
     */
    private static String webNoShare(String uri) {
        String[] Array = uri.split(WEBNOSHARE + "/");
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
     * slate://qrCodeScan/ 打开扫一扫
     *
     * @param uri
     * @return
     */
    private static String qrCodeScan(String uri) {
        String[] Array = uri.split("qrCodeScan/");
        if (Array.length == 2) {
            return Array[1];
        }
        return null;
    }

    /**
     * slate://gift/ 打开激活码
     *
     * @param uri
     * @return
     */
    private static String giftCode(String uri) {
        String[] Array = uri.split("gift/");
        if (Array.length == 2) {
            return Array[1];
        }
        return null;
    }

    /**
     * slate://vip/open vip购买
     *
     * @param uri
     * @return
     */
    private static String vipOpen(String uri) {
        String[] Array = uri.split("vip/open");
        if (Array.length == 2) {
            return Array[1];
        }
        return null;
    }


    /**
     * slate://vippay/{pid} 购买具体套餐
     *
     * @param uri
     * @return
     */
    private static String vipPaySlate(String uri) {
        String[] Array = uri.split("vippay/");
        if (Array.length == 2) {
            return Array[1];
        }
        return null;
    }

    /**
     * slate://articleScrollToNextPage 点击往期封面下一页
     *
     * @param uri
     * @return
     */
    private static String scrollToNextPage(String uri) {
        String[] Array = uri.split("slate://");
        if (Array.length == 2) {
            return Array[1];
        }
        return null;
    }

    /**
     * slate://subscription 文章内容页面，弹出付费界面
     *
     * @param uri
     * @return
     */
    private static String subscription(String uri) {
        String[] Array = uri.split("subscription");
        if (Array.length == 2) {
            return Array[1];
        }
        return null;
    }

    /**
     * https://share.bbwc.cn/slate/vip/info/756376 打开VIP信息
     *
     * @param uri
     * @return
     */
    private static String vipShowInfo(String uri) {
        String[] Array = uri.split(VIPHEAD + "/slate/vip/info/");
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
        if (TextUtils.isEmpty(uri)) return list;
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
            } else if (param[0].equals(TAGARTICLE)) {
                list.addAll(tagArticle(uri));
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
            } else if (param[0].equals(QRCODESCAN)) {
                String qr = qrCodeScan(uri);
                if (!TextUtils.isEmpty(qr)) {
                    list.add(qr);
                }
            } else if (param[0].equals(VIPHEAD)) {
                String vip = vipShowInfo(uri);
                if (!TextUtils.isEmpty(vip)) {
                    list.add(vip);
                }
            } else if (param[0].equals(GIFT)) {
                String gift = giftCode(uri);
                if (!TextUtils.isEmpty(gift)) {
                    list.add(gift);
                }
            } else if (param[0].equals(VIPOPEN)) {
                String vipOp = vipOpen(uri);
                if (!TextUtils.isEmpty(vipOp)) {
                    list.add(vipOp);
                }
            } else if (param[0].equals(VIPPAY)) {
                String vipPay = vipPaySlate(uri);
                if (!TextUtils.isEmpty(vipPay)) {
                    list.add(vipPay);
                }
            } else if (param[0].equals(SUBSCRIPTION)) {
                String subscri = subscription(uri);
                if (!TextUtils.isEmpty(subscri)) {
                    list.add(subscri);
                }
            } else if (param[0].equals(SCROLLTONEXTPAGE)) {
                String scrollTo = scrollToNextPage(uri);
                if (!TextUtils.isEmpty(scrollTo)) {
                    list.add(scrollTo);
                }
            } else if (param[0].equals(VRVIDEO)) {
                String url = vrVideo(uri);
                if (!TextUtils.isEmpty(url)) {
                    list.add(url);
                }
            } else if (param[0].equals(ARTICLEPUSH)) {
                String url = articlePush(uri);
                if (!TextUtils.isEmpty(url)) {
                    list.add(url);
                }
            } else if (param[0].equals(TAGARTICLEPUSH)) {
                String url = tagArticlePush(uri);
                if (!TextUtils.isEmpty(url)) {
                    list.add(url);
                }
            } else if (param[0].equals(TAGADV_ID) || param[0].equals("adv")) {
                if (param.length > 2) {
                    list.add(param[1]);
                    if (param[1].equals("web")) {
                        list.add(tagAdvWeb(uri));
                    } else if (param[1].equals("video")) {
                        list.add(tagAdvVideo(uri));
                    } else if (param[1].equals("tagArticle")) {
                        list.add(tagAdvArticle(uri));
                    } else {
                        list.add(param[2]);
                    }
                }
            } else if (param[0].equals(ISSUEARTICLE)) {
                //id
                if (param.length > 1) {
                    list.add(param[1]);
                }
            } else if (param[0].equals(SHANGCHENGLIST)) {
                if (param.length > 2) {
                    list.add(param[2]);
                }
            } else if (param[0].equals(SHANGCHENG_INFO) || param[0].equals(CAIFU_ARTICLE_INTRO)) {
                if (param.length > 1) {
                    list.add(param[1]);
                }
            } else if (param[0].equals(SHANGCHENG)) {
                if (param.length > 1) {
                    list.add(param[1]);
                }
            } else if (param[0].equals(CAIFU_ARTICLE)) {
                if (param.length > 3) {
                    list.add(param[1]);
                    list.add(param[2]);
                    list.add(param[3]);
                }

            } else if (param[0].equals(SHOWGOODS)) {
                if (param.length > 1) {
                    list.add(param[1]);
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
        if (TextUtils.isEmpty(uri)) return array;
        return uri.split("-");
    }

    /**
     * 普通列表点击
     *
     * @param context
     * @param entries [0] ArticleItem;[1]TransferArticle...
     * @param cls     [0]为特定的文章页
     */
    public static void clickSlate(Context context, Entry[] entries, Class<?>... cls) {
        click(context, entries, null, cls);
    }

    /**
     * 特殊view点击(commonwebview)
     *
     * @param context
     * @param entries [0] ArticleItem;[1]TransferArticle;[2]issue...
     * @param view
     * @param cls
     */
    public static void clickSlate(Context context, Entry[] entries, View view, Class<?>... cls) {
        click(context, entries, view, cls);
    }

    private static void click(Context context, Entry[] entries, View view, Class<?>... cls) {
        if (entries != null && entries[0] instanceof ArticleItem) {
            String link = "";
            ArticleItem item = (ArticleItem) entries[0];
            if (item.getAdvSource() != null) {// 广告
                link = item.getAdvSource().getLink();
            } else {
                link = item.getSlateLink();
            }
            clickSlate(context, link, entries, view, cls);
        }
    }

    /**
     * 检查协议是否来源外部
     *
     * @param link
     * @return
     */
    private static boolean checkFromSplash(String link) {
        if (link.contains("slate1")) return true;
        else return false;
    }

    public static boolean clickSlate(Context context, String link, Entry[] entries, View view, Class<?>... cls) {
        boolean checkFromSplash = checkFromSplash(link);
        link = link.replace("slate1", "slate");
        if (TextUtils.isEmpty(link)) {
            doLinkNull(context, entries, cls);
        } else if (link.equals("slate://openapp")) {
            return false;
        }
        if (link.startsWith("slate://adv/")) {
            // TODO 跳转到广告文章
            AdvUriParse.clickSlate(context, link, entries, view, cls);
        } else if (link.toLowerCase().startsWith("https://share.bbwc.cn/slate/vip/info")) {
            List<String> list1 = parser(link);
            String key1 = list1.size() > 0 ? list1.get(0).toLowerCase() : "";
            if (list1.size() > 1) {
                if (key1.equals(VIPHEAD.toLowerCase())) {//VIP二维码
                    VipShowInfoActivity.launch(context, list1.get(1));

                }
            }
        } else if (link.toLowerCase().startsWith("https://share.bbwc.cn/slate/pay")) {
            String[] cards = link.split("share.bbwc.cn/slate/pay/");
            buyVip(context, cards[1]);
        } else if (link.toLowerCase().startsWith("http://") || link.toLowerCase().startsWith("https://")) {
            doLinkHttp(context, link);
        } else if (link.toLowerCase().startsWith("slate://card/") || link.toLowerCase().startsWith("slate://user/")) {
            String key = link.toLowerCase().startsWith("slate://card/") ? "slate://card/" : "slate://user/";
            String[] cards = link.split(key);
            if (cards != null && cards.length == 2 && !TextUtils.isEmpty(cards[1]) && CommonApplication.userUriListener != null) {
                CommonApplication.userUriListener.doCardUri(context, cards[1]);
            }
        } else if (link.toLowerCase().startsWith("slate://photos/")) {

            List<ArticleItem.Picture> pic = photos(link);
            if (pic != null && pic.size() > 0) {
                doLinkGalleryForDesc(pic, view);
            }
        } else if (link.toLowerCase().startsWith("slate://")) {
            List<String> list = parser(link);
            String key = list.size() > 0 ? list.get(0).toLowerCase() : "";
            if (list.size() > 1) {
                if (key.equals(VIDEO)) {
                    //                    String path = list.get(1).replace(".m3u8", ".mp4");
                    doLinkVideo(context, list.get(1));
                } else if (key.equals(ARTICLE) || key.equals("tagarticle")) {

                    if (list.size() > 3) {
                        doLinkArticle(context, list, entries, view, cls);
                    }
                } else if (key.equals(GALLERY)) {
                    list.remove(0);
                    doLinkGallery(list, view);
                } else if (key.equals(WEB)) {
                    doLinkWeb(context, list.get(1), true);
                    //                    doLinkHttp(context, list.get(1));
                } else if (key.equals(WEBNOSHARE.toLowerCase())) {
                    doLinkWeb(context, list.get(1), true);
                } else if (key.equals(COLUMN)) {
                    doTagname(context, list.get(1));
                } else if (key.equals(VRVIDEO)) {// VR视频播放
                    doOpenVrVedioActivity(context, list.get(1));
                } else if (key.equals(ARTICLEPUSH.toLowerCase()) || key.equals(TAGARTICLEPUSH.toLowerCase())) {// 打开特定文章链接
                    gotoPushArticleActity(context, list.get(1));
                } else if (key.equals("tagadv")) {
                    if (list.get(1).equals("web")) {
                        gotoPushArticleActity(context, list.get(2));
                    } else if (list.get(1).equals("video")) {
                        doLinkVideo(context, list.get(2));
                    }
                } else if (key.equals(ISSUEARTICLE)) {
                    gotoWangqiActivity(context, list.get(1));
                } else if (key.equals(SCROLLTONEXTPAGE.toLowerCase())) {//往期封面进入下一页
                    scrollToNextPage(context);
                } else if (key.equals(VIPPAY.toLowerCase())) {//购买具体vip套餐
                    String pid = list.get(1);
                    buyVip(context, pid);

                } else if (key.equals(VIPOPEN.toLowerCase()) || key.equals(SUBSCRIPTION.toLowerCase())) {//开通VIP
                    String broadcastIntent = "cn.com.modernmediausermodel.VipOpenActivity_nomal";
                    Intent intent = new Intent(broadcastIntent);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    context.sendBroadcast(intent);

                } else if (key.equals(SHANGCHENG.toLowerCase())) {
                    gotoSpecial(context, 3);
                } else if (key.equals(SHANGCHENGLIST.toLowerCase())) {
                    goListActivity(context, list.get(1), checkFromSplash);
                } else if (key.equals(SHANGCHENG_INFO.toLowerCase()) || key.equals(CAIFU_ARTICLE_INTRO.toLowerCase())) {
                    goListInfoActivity(context, list.get(1), checkFromSplash);
                } else if (key.equals(CAIFU_ARTICLE.toLowerCase())) {
                    if (list.size() > 3)
                        gotoCaifuArticleAc(context, list.get(1), list.get(2), list.get(3));
                } else if (key.equals(SHOWGOODS.toLowerCase())) {
                    if (list.size() > 1) {
                        String broadcastIntent = "cn.com.modernmedia.businessweek.YouzanGoodActivity";
                        Intent intent = new Intent(broadcastIntent);
                        if (entries != null && entries.length > 0)
                            intent.putExtra("youzan_good_item", entries[0]);
                        intent.putExtra("youzan_good_id", list.get(1));
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        context.sendBroadcast(intent);
                    }
                }

            } else if (key.equals(ABOUT) || key.equals(SETTINGS)) {// TODO cls为空
                gotoActivity(context, cls);
            } else if (key.equals(QRCODESCAN.toLowerCase())) {
                gotoActivity(context, CaptureActivity.class);
            } else if (key.equals(FEEDBACK)) {
                ModernMediaTools.feedBack(context, null, null);
            } else if (key.equals(STORE)) {
                ModernMediaTools.assess(context);
            } else if (key.equals(CAIFU.toLowerCase())) {
                gotoSpecial(context, 2);
            } else if (key.equals(GIFT.toLowerCase())) {//兑换激活码
                String broadcastIntent = "cn.com.modernmediausermodel.VipCodeActivity_nomal";
                Intent intent = new Intent(broadcastIntent);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                context.sendBroadcast(intent);

            } else if (key.equals(VIP)) {
                String broadcastIntent = "cn.com.modernmediausermodel.vip";
                Intent intent = new Intent(broadcastIntent);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                context.sendBroadcast(intent);

            } else if (key.equals(WEB_RELOAD.toLowerCase())) {
                webReload(link, view);
            }
        } else if (link.startsWith("tel://")) {
            String arr[] = link.split("tel://");
            if (arr.length == 2) doCall(context, arr[1]);
        }
        return true;
    }

    /**
     * 重载网页
     *
     * @param url
     * @param view
     */
    private static void webReload(String url, View view) {
        if (!TextUtils.isEmpty(url) && view != null && view instanceof CommonWebView) {
            try {
                String[] array = url.split(WEB_RELOAD + "/");
                if (array.length == 2) {
                    WebView webView = (WebView) view;
                    webView.loadUrl(URLDecoder.decode(array[1], "UTF-8"));
                }
            } catch (UnsupportedEncodingException exception) {

            }


        }
    }

    /**
     * 财富文章详情页面
     *
     * @param context
     * @param pid
     * @param title
     */
    private static void gotoCaifuArticleAc(Context context, String aid, String pid, String title) {
        Intent i = new Intent(context, CaifuArticleActivity.class);
        i.putExtra("caifu_aid", aid);
        i.putExtra("caifu_pid", pid);
        i.putExtra("caifu_title", title);
        context.startActivity(i);
    }

    /**
     * 去往往期页面
     *
     * @param issueId
     */
    private static void gotoWangqiActivity(Context context, String issueId) {
        Intent intent = new Intent(context, WangqiArticleActivity.class);
        TagInfoList.TagInfo t = new TagInfoList.TagInfo();
        t.setTagName(issueId);
        intent.putExtra("Wangqi_taginfo", t);
        context.startActivity(intent);
    }

    /**
     * 购买具体vip套餐
     *
     * @param context
     * @param pid
     */
    private static void buyVip(Context context, String pid) {
        String broadcastIntent = "cn.com.modernmediausermodel.VipProductPayActivity_nomal";
        Intent intent = new Intent(broadcastIntent);
        intent.putExtra("pay_pid", pid);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.sendBroadcast(intent);
    }


    /**
     * 点击往期下一页
     */
    private static void scrollToNextPage(Context context) {
        ((WangqiArticleActivity) context).gotoArticle(0);
    }

    /**
     * 前往指定文章详情页
     *
     * @param context
     * @param id
     */
    private static void gotoPushArticleActity(Context context, String id) {
        Intent intent = new Intent(context, CommonApplication.pushArticleCls);
        intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_ID, id);
        //        intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_LEVEL, _level);
        context.startActivity(intent);
    }

    /**
     * 打开VR视频播放器
     */
    private static void doOpenVrVedioActivity(Context context, String url) {
        if (url.endsWith(".mp4")) MD360PlayerActivity.startVideo(context, Uri.parse(url));
        else MD360PlayerActivity.startBitmap(context, Uri.parse(url));
    }

    /**
     * 如果slate为空，默认跳转到文章页
     *
     * @param context
     */
    private static void doLinkNull(Context context, Entry[] entries, Class<?>... cls) {
        ArticleItem item = (ArticleItem) entries[0];
        TransferArticle transferArticle = entries.length > 1 ? (TransferArticle) entries[1] : null;
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
        TransferArticle tr = new TransferArticle(item.getArticleId(), tagName, item.getParent(), -1, ArticleType.Default);
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
        Intent i = new Intent();
        Log.e("跳转至视频播放:", path);
        //        if (path.endsWith(".mp4")) {
        //            i.setClass(context, VideoPlayerActivity.class);
        //        } else {
        i.setClass(context, OnlineVideoActivity.class);
        Log.e("跳转至视频播放:", "OnlineVideoActivity");
        //        }
        i.putExtra("vpath", path);
        context.startActivity(i);
    }

    /**
     * 进入文章页跳转至某一篇文章(webview是直接跳转到某一篇文章)
     *
     * @param context
     * @param list
     */
    public static void doLinkArticle(Context context, List<String> list, Entry[] entries, View view, Class<?>... cls) {
        TransferArticle tr = new TransferArticle(ParseUtil.stoi(list.get(3), -1), list.get(2), "", -1, ArticleType.Default);
        TransferArticle transferArticle = entries.length > 1 ? (TransferArticle) entries[1] : null;
        if (transferArticle != null) tr.setUid(transferArticle.getUid());

        if (context instanceof CommonArticleActivity) {
            tr.setUid(Tools.getUid(context));
            if (view instanceof CommonWebView) {
                Log.e("文章跳转链接", list.get(0) + list.get(list.size() - 1));
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
            ((CommonWebView) view).fetchGalleryList(urlList, null);
        }
    }

    /**
     * 显示gallery
     *
     * @param picList
     * @param view
     */
    public static void doLinkGalleryForDesc(List<ArticleItem.Picture> picList, View view) {
        if (view instanceof CommonWebView) {

            List<String> pics = new ArrayList<String>();
            List<String> descs = new ArrayList<String>();
            for (ArticleItem.Picture p : picList) {
                pics.add(p.getBigimgurl());
                descs.add(p.getDesc());
            }
            ((CommonWebView) view).fetchGalleryList(pics, descs);
        }
    }

    /**
     * 去精选tab
     *
     * @param context
     * @param type
     */
    private static void gotoSpecial(Context context, int type) {
        Intent i = new Intent(context, CommonApplication.mainCls);
        i.putExtra("from_slate", type);
        context.startActivity(i);
    }

    /**
     * 跳转到内部浏览器
     *
     * @param link
     */
    public static void doLinkWeb(Context context, String link, boolean isNeedAni, Class<?>... cls) {

        Log.e("跳转到内部浏览器", link);
        if (link.contains("articles/news/")) {
            // 即时头条点击链接
            //        if (cls == null) {
            new WebViewPop(context, link, R.string.ibloomberg_now_title);
        } else {
            Intent intent = new Intent(context, CommonApplication.activeCls);
            intent.putExtra("is_from_splash", -1);
            intent.putExtra("from_splash_url", link);
            context.startActivity(intent);
            if (context instanceof Activity && isNeedAni)
                ((Activity) context).overridePendingTransition(R.anim.down_in, R.anim.hold);
        }
    }

    /**
     * 跳转到拨号页面
     *
     * @param context
     * @param telNumber 电话号码
     */
    public static void doCall(Context context, String telNumber) {
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
     * 文章页标签跳转栏目首页
     *
     * @param context
     * @param tagName
     */
    private static void doTagname(Context context, String tagName) {
        if (CommonApplication.mConfig.getIs_index_pager() == 1) {
            if (context instanceof CommonMainActivity)
                ((CommonMainActivity) context).clickItemIfPager(tagName, true);
            else {
                String broadcastIntent = "cn.com.modernmedia.views.column.book.BookColumnActivity";
                Intent intent = new Intent(broadcastIntent);
                intent.putExtra("tagname", tagName);
                context.sendBroadcast(intent);
            }
        }

    }

    /**
     * 前往列表页面
     *
     * @param type
     */
    private static void goListActivity(Context context, String type, boolean isFromSpa) {
        String broadcastIntent = "cn.com.modernmedia.shangcheng";
        Intent i = new Intent(broadcastIntent);
        i.putExtra("is_from_splash", isFromSpa);
        i.putExtra("ShangchengList_type", type);
        context.sendBroadcast(i);
    }

    /**
     * 前往列表简介页面
     *
     * @param type
     */
    private static void goListInfoActivity(Context context, String type, boolean isFromSpa) {
        String broadcastIntent = "cn.com.modernmedia.shangcheng_info";
        Intent i = new Intent(broadcastIntent);
        i.putExtra("is_from_splash", isFromSpa);
        i.putExtra("ShangchengList_type", type);
        context.sendBroadcast(i);
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


}
