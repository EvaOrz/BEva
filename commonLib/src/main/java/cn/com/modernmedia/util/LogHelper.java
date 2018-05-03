package cn.com.modernmedia.util;

import android.content.Context;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

import cn.com.modernmediaslate.unit.Tools;

/**
 * Flurry统计
 *
 * @author ZhuQiao
 */
public class LogHelper {

    public static final String OPEN_COLUMN_LIST = "OpenColumnList";
    public static final String OPEN_FAVORITE_ARTICLE_LIST = "OpenFavoriteArticleList";
    public static final String OPEN_COLUMN = "OpenColumn";
    public static final String OPEN_ARTICLE_FROM_FAVORITE_ARTICLE_LIST = "OpenArticleFromFavoriteArticleList";
    public static final String OPEN_ARTICLE_FROM_COLUMN_PAGE = "OpenArticleFromColumnPage";
    public static final String ADD_FAVORITE_ARTICLE = "AddFavoriteArticle";
    public static final String CHANGE_ARTICLE_FONT_SIZE = "ChangeArticleFontSize";
    public static final String SHARE_ARTICLE_BY_EMAIL = "ShareArticleByEmail";
    public static final String SHARE_ARTICLE_BY_WEIBO = "ShareArticleByWeibo";
    public static final String ANDROID_ADV_ENTERAPP = "android-adv-enterapp";
    public static final String ANDROID_ADV_HEADLINE = "android-adv-headline";
    public static final String ANDROID_SHOW_HIGHLIGHTS = "android-show-highlights";
    public static final String ANDROID_SHOW_HEADLINE = "android-show-headline";
    public static final String ANDROID_TOUCH_HEADLINE = "android-touch-headline";
    public static final String ANDROID_TOUCH_MORENEWS = "android-touch-morenews";
    public static final String ANDROID_SHOW_COLUMN = "android-show-column";
    public static final String ANDROID_SHOW_ARTICLE = "android-show-article";

    // add
    public static final String ANDROID_SHARE_OTHERS = "android-share-others";
    public static final String ANDROID_SHARE_WEIBO = "android-share-weibo";
    public static final String ANDROID_SHARE_WEIXIN = "android-share-weixin";
    public static final String ANDROID_OPEN_SUBSCRIBE_COLUMN = "android-open-subscribe-column";
    public static final String ANDROID_SUBSCRIBE_COLUMN = "android-subscribe-column";
    public static final String ANDROID_SHOW_SUBSCRIBE_COLUMN = "android-show-subscribe-column";
    public static final String ANDROID_MARQUEE_TOUCH_MORE = "android-marquee-touch-more";

    // test
    public static final String ANDROID_SHOW_PAGE = "android-show-page";
    public static final String OPEN_ARTICLE_FROM_PUSH = "OpenArticleFromPush";
    public static final String SHARE_ARTICLE_BY_WEIXIN_FRIENDS = "ShareArticleByWinxinFriends";
    public static final String SHARE_ARTICLE_BY_WEIXIN_MOMENTS = "ShareArticleByWinxinMoments";
    public static final String ANDROID_MANAGE_SUBSCRIBE_CLICK_COUNT = "android-manage-subscribe-click-count";
    public static final String ANDROID_SUBSCRIBE_ISSUE_NAME = "android-subscribe-issue-name";
    public static final String ANDROID_NEXT_ARTICLE_AFTER_PUSH = "android-next_article_after_push";

    // 底部导航点击统计
    public static final String ANDROID_BOTTOM_NAV_NEWS = "android-bottom-bar-column";
    public static final String ANDROID_BOTTOM_NAV_LIVE = "android-bottom-bar-webcast";
    public static final String ANDROID_BOTTOM_NAV_SPECIAL = "android-bottom-bar-special";
    public static final String ANDROID_BOTTOM_NAV_MINE = "android-bottom-bar-profile";

    //VIP 点击统计
    public static final String ANDROID_VIP_UPGRADE = "android-vip-upgrade";
    public static final String ANDROID_VIP_ONLINEPAY = "android-vip-onlinepay";
    public static final String ANDROID_VIP_COMBO = "android-vip-combo";
    public static final String ANDROID_CODING_EXCHANGE_INPUT_BTN = "android-coding-exchange-input-btn";
    public static final String ANDROID_INTEGRAL_EXCHANGE_BTN = "android-integral-exchange-btn";
    public static final String ANDROID_CODING_EXCHANGE_SHOW = "android-coding-exchange-show";
    public static final String ANDROID_ME_CODING_EXCHANGE_CLICK = "android-me-coding-exchange-click";
    public static final String ANDROID_BUSINESS_CARD = "android-business-card";
    public static final String ANDROID_VIP_UPDATE_INFO = "android-vip-update-info";
    public static final String ANDROID_VIP_UPDATE_INFO_IGNORE = "android-vip-update-info-ignore";

    // VIP购买成功统计
    public static final String ANDROID_VIP_PAYSUCCESS = "android-vip-paysuccess";
    public static final String ANDROID_OPENAPP = "android-openApp";

    // 精选统计事件
    public static final String ANDROID_SPECIAL_LIST = "android-special-list";
    public static final String ANDROID_ISSUE_LIST = "android-issue-list";
    public static final String ANDROID_EBOOK_LIST = "android-ebook-list";

    /**
     * 统计 专题点击
     *
     * @param context
     */
    public static void logSpecialList(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_SPECIAL_LIST, map);
        SelectionHelper.getInstance().add(context, ANDROID_SPECIAL_LIST, map);

    }

    /**
     * 统计 往期点击
     *
     * @param context
     */
    public static void logIssueList(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_ISSUE_LIST, map);
        SelectionHelper.getInstance().add(context, ANDROID_ISSUE_LIST, map);

    }

    /**
     * 统计 专刊点击
     *
     * @param context
     */
    public static void logEbookList(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_EBOOK_LIST, map);
        SelectionHelper.getInstance().add(context, ANDROID_EBOOK_LIST, map);

    }


    /**
     * 打开页面统计
     *
     * @param context
     */
    public static void logOpenPage(Context context) {
        if (ConstData.getInitialAppId() != 20) {
            return;
        }
        FlurryAgent.onPageView();
    }

    /**
     * 从push打开文章页
     *
     * @param context
     * @param articleId
     * @param title
     */
    public static void logOpenArticleFromPush(Context context, String articleId, String title) {
        if (ConstData.getInitialAppId() != 20) {
            return;
        }
        DataHelper.setAfterPushStatus(context, true);
        Map<String, String> map = setDefaultMap(context, articleId, "0");
        // map.put("articleId", articleId);
        map.put("title", title);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        map.put("issueId", DataHelper.getIssueId(context) + "");
        FlurryAgent.logEvent(OPEN_ARTICLE_FROM_PUSH, map);
        SelectionHelper.getInstance().add(context, OPEN_ARTICLE_FROM_PUSH, map);
    }

    /**
     * 推送打开文章后的第二篇文章的事件统计
     *
     * @param articleId
     * @param title
     */
    public static void logNextArticleAfterPush(Context context, String articleId, String title) {
        if (ConstData.getInitialAppId() != 20) return;
        if (DataHelper.getAfterPushStatus(context)) {
            DataHelper.setAfterPushStatus(context, false);
            Map<String, String> map = setDefaultMap(context, articleId, "0");
            // map.put("articleId", articleId);
            map.put("title", title);
            // map.put("userId", Tools.getUid(context));
            // map.put("deviceId", CommonApplication.getMyUUID());
            FlurryAgent.logEvent(ANDROID_SUBSCRIBE_ISSUE_NAME, map);
            SelectionHelper.getInstance().add(context, ANDROID_SUBSCRIBE_ISSUE_NAME, map);
        }
    }

    /**
     * 文章页里分享文章到微信
     *
     * @param context
     * @param articleId
     * @param columnId
     */
    public static void logShareArticleByWinxin(Context context, String articleId, String columnId) {
        if (ConstData.getInitialAppId() != 20) return;
        Map<String, String> map = setDefaultMap(context, articleId, columnId);
        // map.put("articleId", articleId);
        // map.put("columnId", columnId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        map.put("issueId", DataHelper.getIssueId(context) + "");
        FlurryAgent.logEvent(SHARE_ARTICLE_BY_WEIXIN_FRIENDS, map);
        SelectionHelper.getInstance().add(context, SHARE_ARTICLE_BY_WEIXIN_FRIENDS, map);
    }

    /**
     * 文章页里分享文章到微信朋友圈
     *
     * @param context
     * @param articleId
     * @param columnId
     */
    public static void logShareArticleByWinxinMoments(Context context, String articleId, String columnId) {
        if (ConstData.getInitialAppId() != 20) return;
        Map<String, String> map = setDefaultMap(context, articleId, columnId);
        // map.put("articleId", articleId);
        // map.put("columnId", columnId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        map.put("issueId", DataHelper.getIssueId(context) + "");
        FlurryAgent.logEvent(SHARE_ARTICLE_BY_WEIXIN_MOMENTS, map);
        SelectionHelper.getInstance().add(context, SHARE_ARTICLE_BY_WEIXIN_MOMENTS, map);
    }

    /**
     * 点击管理我的主题杂志
     */
    public static void logManageSubscribeClickCount(Context context) {
        if (ConstData.getInitialAppId() != 20) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        FlurryAgent.logEvent(ANDROID_MANAGE_SUBSCRIBE_CLICK_COUNT, map);
        SelectionHelper.getInstance().add(context, ANDROID_MANAGE_SUBSCRIBE_CLICK_COUNT, map);
    }

    /**
     * 添加主题杂志
     *
     * @param name       杂志名称或者栏目名称
     * @param parentName 添加的栏目是子栏目时表示杂志名称
     */
    public static void logSubscribeIssueName(Context context, String name, String parentName) {
        if (ConstData.getInitialAppId() != 20) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        map.put("issueName", name);
        map.put("parentName", name);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        FlurryAgent.logEvent(ANDROID_SUBSCRIBE_ISSUE_NAME, map);
        SelectionHelper.getInstance().add(context, ANDROID_SUBSCRIBE_ISSUE_NAME, map);
    }

    /**
     * 呼出左侧栏目列表
     *
     * @param context
     */
    public static void logOpenColumnList(Context context) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        map.put("issueId", DataHelper.getIssueId(context) + "");
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        FlurryAgent.logEvent(OPEN_COLUMN_LIST, map);
        SelectionHelper.getInstance().add(context, OPEN_COLUMN_LIST, map);
    }

    /**
     * 呼出右侧收藏列表
     */
    public static void logOpenFavoriteArticleList(Context context) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        FlurryAgent.logEvent(OPEN_FAVORITE_ARTICLE_LIST, map);
        SelectionHelper.getInstance().add(context, OPEN_FAVORITE_ARTICLE_LIST, map);
    }

    /**
     * 在左侧栏目列表点击栏目
     *
     * @param context
     * @param columnId
     */
    public static void logOpenColumn(Context context, String columnId) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, "0", columnId);
        // map.put("columnId", columnId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        map.put("issueId", DataHelper.getIssueId(context) + "");
        FlurryAgent.logEvent(OPEN_COLUMN, map);
        SelectionHelper.getInstance().add(context, OPEN_COLUMN, map);
    }

    /**
     * 在右侧收藏列表点击文章
     *
     * @param context
     * @param articleId
     * @param columnId
     */
    public static void logOpenArticleFromFavoriteArticleList(Context context, String articleId, String columnId) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, articleId, columnId);
        // map.put("articleId", articleId);
        // map.put("columnId", columnId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        map.put("issueId", DataHelper.getIssueId(context) + "");
        FlurryAgent.logEvent(OPEN_ARTICLE_FROM_FAVORITE_ARTICLE_LIST, map);
        SelectionHelper.getInstance().add(context, OPEN_ARTICLE_FROM_FAVORITE_ARTICLE_LIST, map);
    }

    /**
     * 在栏目页点击文章
     *
     * @param context
     * @param articleId
     * @param columnId
     */
    public static void logOpenArticleFromColumnPage(Context context, String articleId, String columnId) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, articleId, columnId);
        // map.put("articleId", articleId);
        // map.put("columnId", columnId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        map.put("issueId", DataHelper.getIssueId(context) + "");
        FlurryAgent.logEvent(OPEN_ARTICLE_FROM_COLUMN_PAGE, map);
        SelectionHelper.getInstance().add(context, OPEN_ARTICLE_FROM_COLUMN_PAGE, map);
    }

    /**
     * 文章页里点击“收藏”按钮
     *
     * @param context
     * @param articleId
     * @param columnId
     */
    public static void logAddFavoriteArticle(Context context, String articleId, String columnId) {
        if (ConstData.getAppId() != 1 && ConstData.getInitialAppId() != 20) return;
        Map<String, String> map = setDefaultMap(context, articleId, columnId);
        // map.put("articleId", articleId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        // map.put("columnId", columnId);
        map.put("issueId", DataHelper.getIssueId(context) + "");
        FlurryAgent.logEvent(ADD_FAVORITE_ARTICLE, map);
        SelectionHelper.getInstance().add(context, ADD_FAVORITE_ARTICLE, map);
    }

    /**
     * 文章页里点击“Aa”按钮
     *
     * @param context
     * @param articleId
     * @param columnId
     */
    public static void logChangeArticleFontSize(Context context, String articleId, String columnId) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, articleId, columnId);
        // map.put("articleId", articleId);
        // map.put("columnId", columnId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        map.put("issueId", DataHelper.getIssueId(context) + "");
        FlurryAgent.logEvent(CHANGE_ARTICLE_FONT_SIZE, map);
        SelectionHelper.getInstance().add(context, CHANGE_ARTICLE_FONT_SIZE, map);
    }

    /**
     * 文章页里分享文章by email
     *
     * @param context
     * @param articleId
     * @param columnId
     */
    public static void logShareArticleByEmail(Context context, String articleId, String columnId) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, articleId, columnId);
        // map.put("articleId", articleId);
        // map.put("columnId", columnId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        map.put("issueId", DataHelper.getIssueId(context) + "");
        FlurryAgent.logEvent(SHARE_ARTICLE_BY_EMAIL, map);
        SelectionHelper.getInstance().add(context, SHARE_ARTICLE_BY_EMAIL, map);
    }

    /**
     * 文章页里分享文章by sina weibo
     *
     * @param context
     * @param articleId
     * @param columnId
     */
    public static void logShareArticleByWeibo(Context context, String articleId, String columnId) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, articleId, columnId);
        // map.put("articleId", articleId);
        // map.put("columnId", columnId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        map.put("issueId", DataHelper.getIssueId(context) + "");
        FlurryAgent.logEvent(SHARE_ARTICLE_BY_WEIBO, map);
        SelectionHelper.getInstance().add(context, SHARE_ARTICLE_BY_WEIBO, map);
    }

    /**
     * 进版广告
     *
     * @param advId
     */
    public static void logAndroidAdvEnterapp(Context context, String advId) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        map.put("advId", advId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        FlurryAgent.logEvent(ANDROID_ADV_ENTERAPP, map);
        SelectionHelper.getInstance().add(context, ANDROID_ADV_ENTERAPP, map);
    }

    /**
     * 头条广告
     *
     * @param advId
     */
    public static void logAndroidAdvHeadline(Context context, String advId) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        map.put("advId", advId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        FlurryAgent.logEvent(ANDROID_ADV_HEADLINE, map);
        SelectionHelper.getInstance().add(context, ANDROID_ADV_HEADLINE, map);
    }

    /**
     * 焦点页展示(首页展示)
     */
    public static void logAndroidShowHighlights(Context context) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        FlurryAgent.logEvent(ANDROID_SHOW_HIGHLIGHTS, map);
        SelectionHelper.getInstance().add(context, ANDROID_SHOW_HIGHLIGHTS, map);
    }

    /**
     * 焦点页头条展示(首页焦点图) 改为手动滑动时 添加log
     *
     * @param position 焦点图位置
     */
    public static void lodAndroidShowHeadline(Context context, int position) {
        if (ConstData.getAppId() != 1 && ConstData.getInitialAppId() != 20) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        map.put("index", position + "");
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        FlurryAgent.logEvent(ANDROID_SHOW_HEADLINE, map);
        SelectionHelper.getInstance().add(context, ANDROID_SHOW_HEADLINE, map);
    }

    /**
     * 焦点页头条点击(首页点击焦点图)
     *
     * @param position 焦点图位置
     */
    public static void logAndroidTouchHeadline(Context context, int position) {
        if (ConstData.getAppId() != 1 && ConstData.getInitialAppId() != 20) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        map.put("index", position + "");
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        FlurryAgent.logEvent(ANDROID_TOUCH_HEADLINE, map);
        SelectionHelper.getInstance().add(context, ANDROID_TOUCH_HEADLINE, map);
    }

    /**
     * 更多即时头条按钮点击
     */
    public static void logAndroidTouchMorenews(Context context) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        FlurryAgent.logEvent(ANDROID_TOUCH_MORENEWS, map);
        SelectionHelper.getInstance().add(context, ANDROID_TOUCH_MORENEWS, map);
    }

    /**
     * 栏目页展示
     *
     * @param columnId 栏目id
     */
    public static void logAndroidShowColumn(Context context, String columnId) {
        if (ConstData.getAppId() != 1 && ConstData.getInitialAppId() != 20) return;
        Map<String, String> map = setDefaultMap(context, "0", columnId);
        // map.put("columnId", columnId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        map.put("issueId", DataHelper.getIssueId(context) + "");
        FlurryAgent.logEvent(ANDROID_SHOW_COLUMN, map);
        SelectionHelper.getInstance().add(context, ANDROID_SHOW_COLUMN, map);
    }

    /**
     * 文章页展示
     *
     * @param context
     * @param columnId
     * @param articleId
     */
    public static void logAndroidShowArticle(Context context, String columnId, String articleId) {
        if (ConstData.getAppId() != 1 && ConstData.getInitialAppId() != 20) return;
        Map<String, String> map = setDefaultMap(context, articleId, columnId);
        map.put("issueId", DataHelper.getIssueId(context) + "");
        // map.put("columnId", columnId);
        map.put("articleId", articleId);
        // map.put("userId", Tools.getUid(context));
        // map.put("deviceId", CommonApplication.getMyUUID());
        FlurryAgent.logEvent(ANDROID_SHOW_ARTICLE, map);
        SelectionHelper.getInstance().add(context, ANDROID_SHOW_ARTICLE, map);
    }

    /**
     * 分享log
     */
    public static void logShare(Context context, String logtype, String weburl, String title) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        map.put("weburl", weburl);
        map.put("title", title);
        FlurryAgent.logEvent(logtype, map);
        SelectionHelper.getInstance().add(context, logtype, map);
    }

    /**
     * 是点击“＋”，打开栏目订阅页面的事件。参数：无
     */
    public static void openSubcribeColumn(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_OPEN_SUBSCRIBE_COLUMN, map);
        SelectionHelper.getInstance().add(context, ANDROID_OPEN_SUBSCRIBE_COLUMN, map);

    }

    /**
     * 订阅一个栏目的事件 参数：栏目id
     */
    public static void subcribeColumn(Context context, String columnId) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        map.put("columnId", columnId);
        FlurryAgent.logEvent(ANDROID_SUBSCRIBE_COLUMN, map);
        SelectionHelper.getInstance().add(context, ANDROID_SUBSCRIBE_COLUMN, map);

    }

    /**
     * 点击某个未订阅的栏目事件，参数 栏目id
     */
    public static void showSubcribeColumn(Context context, String columnId) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        map.put("columnId", columnId);
        FlurryAgent.logEvent(ANDROID_SHOW_SUBSCRIBE_COLUMN, map);
        SelectionHelper.getInstance().add(context, ANDROID_SHOW_SUBSCRIBE_COLUMN, map);

    }

    /**
     * 跑马灯点击事件
     */
    public static void marqueeTouchMore(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_MARQUEE_TOUCH_MORE, map);
        SelectionHelper.getInstance().add(context, ANDROID_MARQUEE_TOUCH_MORE, map);

    }

    /**
     * deviceToken|uid|articleId|catId 例：
     * 314D3D6C-A350-4924-BCBA-DD8644445400|17259|0|cat_32_zuixin
     */
    private static Map<String, String> setDefaultMap(Context context, String articleId, String catId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("linkageKey", Tools.getDeviceToken(context) + "|" + Tools.getUid(context) + "|" + articleId +
                "|" + catId);
        return map;
    }

    /**
     * 点击底部导航 新闻
     */
    public static void checkBottomNavNews(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_BOTTOM_NAV_NEWS, map);
        SelectionHelper.getInstance().add(context, ANDROID_BOTTOM_NAV_NEWS, map);
    }

    /**
     * 点击底部导航 直播
     */
    public static void checkBottomNavLive(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_BOTTOM_NAV_LIVE, map);
        SelectionHelper.getInstance().add(context, ANDROID_BOTTOM_NAV_LIVE, map);
    }

    /**
     * 点击底部导航 特刊
     */
    public static void checkBottomNavSpecial(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_BOTTOM_NAV_SPECIAL, map);
        SelectionHelper.getInstance().add(context, ANDROID_BOTTOM_NAV_SPECIAL, map);
    }

    /**
     * 点击底部导航 我（个人中心）
     */
    public static void checkBottomNavMine(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_BOTTOM_NAV_MINE, map);
        SelectionHelper.getInstance().add(context, ANDROID_BOTTOM_NAV_MINE, map);
    }

    /**
     * 支付成功 VIP
     */
    public static void paySuccess(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_VIP_PAYSUCCESS, map);
        SelectionHelper.getInstance().add(context, ANDROID_VIP_PAYSUCCESS, map);
    }

    /**
     * 打开app VIP
     */
    public static void openApp(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_OPENAPP, map);
        SelectionHelper.getInstance().add(context, ANDROID_OPENAPP, map);
    }

    /**
     * 升级VIP 侧边栏
     */
    public static void logVipUpgrade(Context context, String pos) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        map.put("position", pos);
        FlurryAgent.logEvent(ANDROID_VIP_UPGRADE, map);
        SelectionHelper.getInstance().add(context, ANDROID_VIP_UPGRADE, map);
    }

    /**
     * 在线支付 VIP
     */
    public static void logVipOnlinepay(Context context, String pos, String goodId) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        map.put("position", pos);
        map.put("id", goodId);
        FlurryAgent.logEvent(ANDROID_VIP_ONLINEPAY, map);
        SelectionHelper.getInstance().add(context, ANDROID_VIP_ONLINEPAY, map);
    }

    /**
     * vip购买入口
     */
    public static void checkVipOpen(Context context, String pos) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        map.put("position", pos);
        FlurryAgent.logEvent(ANDROID_VIP_COMBO, map);
        SelectionHelper.getInstance().add(context, ANDROID_VIP_COMBO, map);
    }

    /**
     * 立即激活按钮
     */
    public static void checkVipCodingExchange(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_CODING_EXCHANGE_INPUT_BTN, map);
        SelectionHelper.getInstance().add(context, ANDROID_CODING_EXCHANGE_INPUT_BTN, map);
    }

    /**
     * 积分兑换vip按钮
     */
    public static void checkVipIntegralExchange(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_INTEGRAL_EXCHANGE_BTN, map);
        SelectionHelper.getInstance().add(context, ANDROID_INTEGRAL_EXCHANGE_BTN, map);
    }

    /**
     * vip会员名片显示
     */
    public static void logBusinesscard(Context context) {
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_BUSINESS_CARD, map);
        SelectionHelper.getInstance().add(context, ANDROID_BUSINESS_CARD, map);
    }

    /**
     * 支付完成 vip完善资料 去填写
     */
    public static void checkVipUpdateInfo(Context context) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_VIP_UPDATE_INFO, map);
        SelectionHelper.getInstance().add(context, ANDROID_VIP_UPDATE_INFO, map);
    }

    /**
     * 支付完成 vip完善资料 跳过
     */
    public static void checkVipUpdateInfoIgnore(Context context) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_VIP_UPDATE_INFO_IGNORE, map);
        SelectionHelper.getInstance().add(context, ANDROID_VIP_UPDATE_INFO_IGNORE, map);
    }

    /**
     * 兑换码界面显示
     */
    public static void logCodeShow(Context context) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_CODING_EXCHANGE_SHOW, map);
        SelectionHelper.getInstance().add(context, ANDROID_CODING_EXCHANGE_SHOW, map);
    }

    /**
     * 我的兑换码 点击
     */
    public static void checkCode(Context context) {
        if (ConstData.getAppId() != 1) return;
        Map<String, String> map = setDefaultMap(context, "0", "0");
        FlurryAgent.logEvent(ANDROID_ME_CODING_EXCHANGE_CLICK, map);
        SelectionHelper.getInstance().add(context, ANDROID_ME_CODING_EXCHANGE_CLICK, map);
    }

}
