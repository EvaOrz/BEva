package cn.com.modernmedia.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 获取接口信息
 *
 * @author ZhuQiao
 */
public class UrlMaker {
    /**
     * Host信息
     **/
    private static String HOST;
    /**
     * 基础URL信息
     **/
    private static String MODEL_URL = "";
    private static String SLATE_URL = "";
    private static String USER_MODEL_URL = ""; // 与用户相关
    private static String SLATE_BASE_URL = "";
    private static String PRODUCT_URL = "";// 付费相关

    public static void setMODEL_URL() {
        if (ConstData.IS_DEBUG == 0) {
            if (ConstData.getAppId() == 1) {
                HOST = "http://content.cdn.bb.bbwc.cn";
            } else if (ConstData.getAppId() == 2) {
                HOST = "http://content.cdn.imlady.bbwc.cn";
            } else if (ConstData.getAppId() == 20) {
                HOST = "http://content.cdn.iweekly.bbwc.cn";
            } else if (ConstData.getAppId() == 10) {
                HOST = "http://content.cdn.onewaystreet.cn";
            } else {
                HOST = "http://content.cdn.bbwc.cn";
            }
            PRODUCT_URL = "https://buy.bbwc.cn";
            USER_MODEL_URL = "http://user.bbwc.cn/";
        } else if (ConstData.IS_DEBUG == 1) {
            HOST = "http://content.test.bbwc.cn";
            PRODUCT_URL = "https://buy-test.bbwc.cn";
            USER_MODEL_URL = "http://user.test.bbwc.cn/";
        } else if (ConstData.IS_DEBUG == 2) {// 测试版
            HOST = "http://develop.cname.bbwc.cn/dev";
            SLATE_BASE_URL = "http://develop.cname.bbwc.cn/slateInterface";
            USER_MODEL_URL = "http://develop.cname.bbwc.cn/mmuser/interface/index.php";
        } else if (ConstData.IS_DEBUG == 4) {
            HOST = "http://develop.cname.bbwc.cn/zhanglei";
        } else if (ConstData.IS_DEBUG == 5) {
            HOST = "http://cms.bbwc.cn/dev";// 预览版
        } else if (ConstData.IS_DEBUG == 6) {
            HOST = "http://cms.bbwc.cn/editor";
        } else if (ConstData.IS_DEBUG == 7) {
            HOST = "http://content.test.bbwc.cn/zhanglei";
        } else if (ConstData.IS_DEBUG == 8) {// 编辑版
            HOST = "http://content.editor.bbwc.cn";
            PRODUCT_URL = "https://buy-test.bbwc.cn";
            USER_MODEL_URL = "http://user.bbwc.cn/";
        }
        if (SLATE_BASE_URL == "") SLATE_BASE_URL = HOST + "/slateInterface";
        MODEL_URL = HOST + "/v" + ConstData.API_VERSION + "/app" + ConstData.getInitialAppId();

        SLATE_URL = SLATE_BASE_URL + "/v" + ConstData.API_VERSION + "/app_" + ConstData.getInitialAppId() + "/android";
    }

    private static String getEnd() {
        return ConstData.IS_DEBUG == 1 || ConstData.IS_DEBUG == 2 || ConstData.IS_DEBUG == 4 || ConstData.IS_DEBUG == 6 || ConstData.IS_DEBUG == 7 ? ".html" : ".api";
    }

    /**
     * 统计装机量
     *
     * @return
     */
    protected static String download(Context context) {
        return "http://android.bbwc.cn/interface/index.php?m=down&res=" + CommonApplication.CHANNEL + "&uuid=" + Tools.getMyUUID(context) + "&appid=" + ConstData.getInitialAppId() + "&version=" + ConstData.VERSION + "&devicetype=" + android.os.Build.MODEL + "&osversion=" + android.os.Build.VERSION.RELEASE;
    }

    /**
     * 判断版本号，不一致就升级
     *
     * @return
     */
    protected static String checkVersion(String version) {
        if (ConstData.IS_DEBUG == 0) {
            return "https://user.bbwc.cn/device/versionUpdate?appid=" + ConstData.getInitialAppId() + "&type=android&version=" + version + "&src=" + CommonApplication.CHANNEL;
        } else
            return "https://user-test.bbwc.cn/device/versionUpdate?appid=" + ConstData.getInitialAppId() + "&type=android&version=" + version + "&src=" + CommonApplication.CHANNEL;
    }

    protected static String getWeather(double longitude, double latitude) {
        return "http://weather.iweek.ly/get_weather?longitude=" + longitude + "&latitude=" + latitude;
    }

    /**
     * 获取iweekly最新文章
     *
     * @param tagName
     * @return
     */
    public static String getWeeklyLatestArticleId(String tagName) {
        String str = TextUtils.isEmpty(tagName) ? "/tag" : "/tag/" + tagName;
        String url = SLATE_BASE_URL + "/v" + ConstData.API_VERSION + "/app_20/android";
        return url + str + "/newarticles";
    }

    /**
     * 获取广告
     * @return
     */
    public static String getAdvList() {
        String host = "";
        if (ConstData.IS_DEBUG == 0) {
            host = "http://adver.cdn.bbwc.cn";
        } else if (ConstData.IS_DEBUG == 8) {
            host = "http://adver.editor.bbwc.cn";
        } else {
            host = "http://adver.inhouse.bbwc.cn";
        }
        String url = host + "/adv/v" + ConstData.API_VERSION + "/list/" + ConstData.getInitialAppId() + "-" + ConstData.DEVICE_TYPE + "-" + ConstData.DATA_TYPE + ".html";
        if (!TextUtils.isEmpty(AppValue.appInfo.getAdvUpdateTime())) {
            Log.e("getAdvList()", url + "?updatetime=" + AppValue.appInfo.getAdvUpdateTime());
            return url + "?updatetime=" + AppValue.appInfo.getAdvUpdateTime();
        }
        Log.e("getAdvList()", url);
        return url;
    }

    /**
     * 获取第三方广告
     * @return
     */
    public static String getOtherAdvUrl(){
        return "http://api.htp.hubcloud.com.cn:45600/json/v1/sdk0";
    }

    /**
     * iWeekly入版广告
     *
     * @return
     */
    // protected static String getWeeklyInApp() {
    // if (ConstData.IS_DEBUG == 0)
    // return "http://beta.iweek.ly/api/json/intro";
    // else
    // return "http://iw-cdn-test.iweek.ly/api/json/intro";
    // }

    /**
     * 获取某tag信息URL
     *
     * @param tagName tag统一标识，可以传多个tagname,用逗号隔开
     * @return
     */
    public static String getTagInfo(String tagName) {
        return SLATE_URL + "/tag/" + tagName;
    }

    /**
     * 获取tag子标签URL
     *
     * @param tagName tag统一标识，可以传多个tagname,用逗号隔开
     * @param group   1.app类 2.issue类 3.column类 4.独立栏目 'all'.所有（默认值）
     * @param top     用于分页，取下一页$top=最后一个标签的offset
     * @param isChild 用于分页，取上一页$bottom=第一个标签的offset
     * @return
     */
    protected static String getTagChild(String parentTagName, String tagName, String group, String top, boolean isChild, boolean isWangqi) {
        String url = SLATE_URL + "/tag";
        if (!TextUtils.isEmpty(parentTagName)) {
            url += "/" + parentTagName;
        }
        if (!TextUtils.isEmpty(tagName)) url += "/" + tagName;
        url += isChild ? "/child" : "/tree";
        if (TextUtils.isEmpty(group) && TextUtils.isEmpty(top)) {
            if (!TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
                url += "?";
                return addUpdatetime(url);
            }
            return url;
        }
        url += "?";
        if (!TextUtils.isEmpty(group)) {
            String and = url.endsWith("?") ? "" : "&";
            url += and + "group=" + group;
        }
        if (!TextUtils.isEmpty(top)) {
            String and = url.endsWith("?") ? "" : "&";
            url += and + "top=" + top;
        }
        if (isWangqi) {
            String and = url.endsWith("?") ? "" : "&";
            url += and + "noCurrentIssue=1";
            // 往期暂时不传limit参数
            //            String and2 = url.endsWith("?") ? "" : "&";
            //            url += and2 + "limited=20";
        }
        if (!TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
            url = addUpdatetime(url);
        }
        Log.e("获取子栏目信息", url);
        return url;
    }


    /**
     * 获取某个栏目的子栏目列表
     *
     * @param tagName subscribelistcol
     * @return
     */
    public static String getCatList(String tagName) {
        String url = SLATE_URL + "/tag";
        if (!TextUtils.isEmpty(tagName)) {
            url += "/" + tagName;
        }
        url += "/subscribelistcol?firstColumnHaveChild=1";
        if (!TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
            url += "?";
            return addUpdatetime(url);
        }
        Log.e("getCatList", url);
        return url;
    }

    public static String addUpdatetime(String url) {
        String and = url.endsWith("?") ? "" : "&";
        url += and + "updatetime=" + AppValue.appInfo.getUpdatetime();
        return url;
    }

    /**
     * 获取tag对应的文章URl
     *
     * @param info    tag统一标识，可以传多个tagname,用逗号隔开, 当前tagname只支持栏目ID且该栏目没有child
     * @param top     用于分页，取下一页$top=最后一个标签的offset
     * @param limited 用于分页，取上一页$bottom=第一个标签的offset
     * @return
     */
    public static String getTagCatIndex(Context context, TagInfo info, String top, String limited) {
        if (info == null) return "";
        if (info.getTagName().endsWith("cat_32_zuixin"))// 商周【最新】专用接口
            return getMarquee();

        String url = SLATE_URL + "/tag/";
        if (TextUtils.equals(limited, "5")) {
            url += info.getTagName() + "/";
            url += AppValue.ensubscriptColumnList.getSubscriptTagMergeName();
        } else {
            url += info.getTagName();
            String merge = info.getMergeName(true);// 子栏目集合
            if (!TextUtils.isEmpty(merge)) {
                url += "/" + merge;
            }
        }
        url += "/tagindex";
        if (!TextUtils.isEmpty(top)) {
            url += "?top=" + top;
        }
        if (!TextUtils.isEmpty(limited)) {
            url += url.contains("?") ? "&" : "?";
            url += "limited=" + limited;
        }
        url += url.contains("?") ? "&" : "?";
        //        if (TextUtils.equals(limited, "5") && !TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
        if (!TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
            url += "updatetime=" + AppValue.appInfo.getUpdatetime();
        } else {
            url += "updatetime=" + info.getColoumnupdatetime();
        }
        url += url.contains("?") ? "&" : "?";
        url += "appVersion=" + Tools.getAppVersionName(context);
        Log.e("getTagCatIndex", url);
        return url;
    }

    /**
     * 获取tag对应的文章URl
     *
     * @param info    tag信息
     * @param top     用于分页，取下一页$top=最后一个标签的offset
     * @param limited 用于分页，取上一页$bottom=第一个标签的offset
     * @return
     */
    public static String getArticlesByTag(Context c, TagInfo info, String top, String limited) {
        if (info == null) return "";
        if (info.getTagName().endsWith("cat_32_zuixin"))// 商周【最新】专用接口
            return getMarquee();
        String url = SLATE_URL + "/tag/";
        if (TextUtils.equals(limited, "5")) {
            url += info.getTagName() + "/";
            url += AppValue.ensubscriptColumnList.getSubscriptTagMergeName();
        } else {
            url += info.getTagName();
            String merge = info.getMergeName(true);// 子栏目集合
            if (!TextUtils.isEmpty(merge)) {
                url += "/" + merge;
            }
        }
        url += "/articlelist";
        if (!TextUtils.isEmpty(top)) {
            url += "?top=" + top;
        }
        if (!TextUtils.isEmpty(limited)) {
            url += url.contains("?") ? "&" : "?";
            url += "limited=" + limited;
        }
        url += url.contains("?") ? "&" : "?";
        if (!TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
            url += "updatetime=" + AppValue.appInfo.getUpdatetime();
        } else {
            url += "updatetime=" + info.getArticleupdatetime();
        }
        url += url.contains("?") ? "&" : "?";
        url += "appVersion=" + Tools.getAppVersionName(c);
        return url;
    }

    /**
     * 获取tag对应的文章URl
     *
     * @param tagname tag统一标识，可以传多个tagname,用逗号隔开, 当前tagname只支持栏目ID且该栏目没有child
     * @param top     用于分页，取下一页$top=最后一个标签的offset
     * @return
     */
    protected static String getArticlesByTag(String tagname, String top) {
        String url = SLATE_URL + "/tag/" + tagname + "/articlelist";
        if (!TextUtils.isEmpty(top)) {
            url += "?top=" + top;
        }
        return url;
    }

    /**
     * 获取往期tag对应的文章URl
     *
     * @param tagname tag统一标识，可以传多个tagname,用逗号隔开, 当前tagname只支持栏目ID且该栏目没有child
     * @param top     用于分页，取下一页$top=最后一个标签的offset
     * @return
     */
    protected static String getLastArticlesByTag(String tagname, String top, String publishTime) {
        String url = SLATE_URL + "/tag/" + tagname + "/articlelist?orderby=sortByTagnameFirst";
        if (!TextUtils.isEmpty(top)) {
            url += "&top=" + top;
        }
        if (!TextUtils.isEmpty(publishTime)) {
            url += "&updatetime=" + publishTime;
        }
        return url;
    }

    /**
     * 获取文章详细信息URL
     *
     * @param articleId   文章统一标识，可以传多个articleid
     * @param contentType 1.文章全部信息 2.文章摘要信息 (默认为1)
     * @return
     */
    protected static String getArticleDetails(String articleId, int contentType) {
        String url = SLATE_URL + "/article/" + articleId;
        return contentType == 2 ? url + "?contenttype=2" : url;
    }

    /**
     * @param articleId
     * @return
     */
    public static String getArticleHtmlTxt(String articleId) {
        return SLATE_URL + "/article/get/" + articleId + "?datatype=" + ConstData.DATA_TYPE + "&updatetime=" + AppValue.appInfo.getUpdatetime();
    }

    /**
     * 跑马灯最新接口
     *
     * @return
     */
    public static String getMarquee() {
        return SLATE_URL + "/news/articlelist?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 取用户订阅列表URL
     *
     * @param uid bac: getUserSubscribeListCols
     * @return
     */
    public static String getUserSubscribeList(String uid, String token) {
        String url = USER_MODEL_URL + "subscribeColumn/getUserSubscribeListCols?uid=" + uid + "&appid=" + ConstData.getInitialAppId() + "&datatype=" + ConstData.DATA_TYPE + "&token=" + token;
        Log.e("getUserSubscribeList", url);
        return url;

    }

    /**
     * 存用户订阅列表URL * bac: getUserSubscribeListCols
     *
     * @param uid saveUserSubscribeListCols
     * @return
     */
    public static String getAddUserSubscribeList(String uid) {
        return USER_MODEL_URL + "subscribeColumn/saveUserSubscribeListCols?uid=" + uid + "&appid=" + ConstData.getInitialAppId() + "&datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 存单个订阅
     *
     * @param uid
     * @return
     */
    public static String getAddUserSubscribeSingle(String uid) {
        return USER_MODEL_URL + "Subscribecolumn/appendCol?uid=" + uid + "&appid=" + ConstData
                .getInitialAppId() + "&datatype=" + ConstData.DATA_TYPE;

    }

    /**
     * 同步收藏
     *
     * @return
     */
    public static String getUpdateFav(String uid) {
        return SLATE_BASE_URL + "/Favorites/save?datatype=" + ConstData.DATA_TYPE + "&uid=" + uid + "&appId=" + ConstData.getInitialAppId() + "&deviceType=" + ConstData.DEVICE_TYPE;
    }

    /**
     * 获取服务器上的收藏列表 uid,appid,
     *
     * @return
     */
    public static String getFav(String uid) {
        return SLATE_BASE_URL + "/Favorites/get?datatype=" + ConstData.DATA_TYPE + "&uid=" + uid + "&appId=" + ConstData.getInitialAppId() + "&deviceType=" + ConstData.DEVICE_TYPE;
    }

    /**
     * 获取push文章地址
     *
     * @param articleId
     * @return
     */
    public static String getPushArticle(String articleId) {
        String url = SLATE_URL + "/push/" + articleId;
        if (!TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
            url += "?";
            return addUpdatetime(url);
        }
        return url;
    }

    /**
     * 添加日志 (参数方式)
     */
    public static String getSelection() {
        return "http://log.bbwc.cn/php/logs.php";// 3.4.0 版本接口
        //        return "http://statistics.bbwc.cn/bbwc_android";// 3.0.3 修复版本新接口
    }

    /**
     * 添加日志 (文件方式)
     */
    public static String getSelectionByFile() {
        return "http://statistics.bbwc.cn/upload";
    }

    /**
     * 栏目订阅
     */
    public static String getBookColumns() {
        return SLATE_URL + "/tag/app_" + ConstData.getInitialAppId() + "/subscribelist?datatype=" + ConstData.DATA_TYPE + "&firstColumnHaveChild=1&fetch_all=0";
    }

    /**
     * 特刊列表接口
     *
     * @param top
     * @return
     */
    public static String getSpecial(String top) {
        String url = SLATE_URL + "/tag/topiclist?limited=20";
        if (!TextUtils.isEmpty(top)) {
            url += "&top=" + top;
        }
        return url + "&datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 获取搜索热门标签
     *
     * @return
     */
    public static String getSearchTags(String top) {
        String url = SLATE_URL + "/tag/keyword?limited=20";
        if (!TextUtils.isEmpty(top)) {
            url += "&top=" + top;
        }
        return url + "&datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 获取电子书接口
     * http://content.cdn.bb.bbwc.cn/slateInterface/v9/app_1/android/tag/ebook?updatetime=1486351547&datatype=2
     */
    public static String getEbookTag(String top) {
        String url = SLATE_URL + "/tag/ebook?limited=20";

        if (!TextUtils.isEmpty(top)) {
            url += "&top=" + top;
        }
        url += "&datatype=" + ConstData.DATA_TYPE;
        if (!TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
            return addUpdatetime(url);
        }
        return url;
    }


    /**
     * 获取fm接口
     * http://content.test.bbwc.cn/slateInterface/v9/app_1/android/tag/fm?datatype=2
     */
    public static String getFmTag() {
        String url = SLATE_URL + "/tag/fm?datatype=" + ConstData.DATA_TYPE;
        if (!TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
            return addUpdatetime(url);
        }
        return url;
    }


    /**
     * 新支付：获取订单
     * type :1- weixin 2- zhifubao
     */
    public static String newGetOrder(int type) {
        return PRODUCT_URL + "/order/add/type/" + type + "?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 新支付：获取用户订阅身份
     */
    public static String newGetUserPermission() {
        return PRODUCT_URL + "/product/readuserpermission?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 新支付：更新订单状态
     * type :1- weixin 2- zhifubao
     */
    public static String newUpdateOrderStatus(int type) {
        return PRODUCT_URL + "/order/pay/type/" + type + "?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 新支付：恢复订单
     */
    public static String newRecoverOrder() {
        return PRODUCT_URL + "/order/review?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 新支付vip：我的特权
     */
    public static String myPermission() {
        return PRODUCT_URL + "/vip/myPermission?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * vip 升级套餐列表
     *
     * @return
     */
    public static String getVipUpList() {
        return PRODUCT_URL + "/vip/exchangeList?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * VIP 开通VIP支付列表
     */
    public static String vipOrder() {
        return PRODUCT_URL + "/product/listvip?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 订阅到 vip的升级套餐
     */
    public static String vipUpdateList() {
        return PRODUCT_URL + "/product/listvipupdate?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * VIP 升级接口
     */
    public static String vipUp() {
        return PRODUCT_URL + "/vip/update?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * VIP 获取某个商品信息接口
     */
    public static String getProduct() {
        return PRODUCT_URL + "/product/readpidpayinfo?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * VIP 会员说明
     */
    public static String vipNotice() {
        return PRODUCT_URL + "/html/agreement.html";
    }

    /**
     * VIP 取消自动续订
     */
    public static String vipCancelAuto() {
        return PRODUCT_URL + "/html/help/cancel-auto-subscribet.html";
    }

    public static String getMyBooks() {
        return PRODUCT_URL + "/myorder/subscibeList?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * VIP 服务协议
     */
    public static String vipServiceAgreement() {
        return PRODUCT_URL + "/html/vip_service_agreement.html";
    }

    /**
     * VIP 扫描二维码显示VIP信息
     *
     * @return
     */
    public static String getVipShow() {
        return USER_MODEL_URL + "user/showvip?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 获取VIP邮寄地址 list
     *
     * @return
     */
    public static String listddress() {
        if (ConstData.IS_DEBUG == 1) {
            return "https://user-test.bbwc.cn/index.php/useraddress/list?datatype=" + ConstData.DATA_TYPE;
        } else
            return "https://user.bbwc.cn/index.php/useraddress/list?datatype=" + ConstData.DATA_TYPE;

    }

    /**
     * 修改VIP邮寄地址 edit
     *
     * @return
     */
    public static String editAddress() {
        if (ConstData.IS_DEBUG == 1) {
            return "https://user-test.bbwc.cn/index.php/useraddress/edit?datatype=" + ConstData.DATA_TYPE;
        } else
            return "https://user.bbwc.cn/index.php/useraddress/edit?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 新添加VIP邮寄地址 add
     *
     * @return
     */
    public static String addAddress() {
        if (ConstData.IS_DEBUG == 1) {
            return "https://user-test.bbwc.cn/index.php/useraddress/add?datatype=" + ConstData.DATA_TYPE;
        } else
            return "https://user.bbwc.cn/index.php/useraddress/add?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 全文搜索接口
     *
     * @param msg
     * @return
     */
    public static String getSearchResult(String msg, long startTime, long endTime, String top) {
        String url = "";
        try {
            if (startTime != 0 && endTime != 0) {
                url = SLATE_URL + "/article/search?datatype=" + ConstData.DATA_TYPE + "&starttime=" + startTime + "&endtime=" + endTime + "&limit=50";
                if (!TextUtils.isEmpty(top)) {
                    url += "&top=" + top;
                }
            } else {
                url = SLATE_URL + "/article/search?datatype=" + ConstData.DATA_TYPE + "&keywords=" + URLEncoder.encode(msg, "UTF-8");
            }


        } catch (UnsupportedEncodingException e) {

        }
        return url;

    }

    /**
     * 商城列表
     */
    public static String getShangchengIndex() {
        return PRODUCT_URL + "/productplus/index?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 直接去商城某列表或者间接页面
     */
    public static String getShangchengList() {
        return PRODUCT_URL + "/productplus/list?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 商城数据
     */
    public static String getShangchengList(String tags) {
        return SLATE_URL + "/tag/" + tags + "/multiarticles?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 推送：收集设备信息接口
     */
    public static String getDeviceInfoForPush() {
        if (ConstData.IS_DEBUG == 0)
            return "http://user.bbwc.cn/device/add/type/1?datatype=" + ConstData.DATA_TYPE;
        else return "http://user.test.bbwc.cn/device/add/type/1?datatype=" + ConstData.DATA_TYPE;
    }

    /**
     * 匹配设备的广告标签列表
     *
     * @return
     */
    public static String getAdvTagUrl() {
        if (ConstData.IS_DEBUG == 0)
            return "https://user.bbwc.cn/client/read?datatype=" + ConstData.DATA_TYPE;
        else {
            return "https://user-test.bbwc.cn/client/read?datatype=" + ConstData.DATA_TYPE;
        }
    }

    /**
     * 有赞登录接口
     *
     * @return
     */
    public static String youzanLogin() {
        if (ConstData.IS_DEBUG == 0)
            return "https://user.bbwc.cn/youzan/login?datatype=" + ConstData.DATA_TYPE;
        else {
            return "https://user-test.bbwc.cn/youzan/login?datatype=" + ConstData.DATA_TYPE;
        }
    }

    /**
     * 有赞登出接口
     *
     * @return
     */
    public static String youzanLogout() {
        return "https://uic.youzan.com/sso/open/logout";
    }

    /**
     * 财富首页链接
     */
    public static String getCaiFuUrl(Context context) {
        if (ConstData.IS_DEBUG == 0)
            return "http://live.bbwc.cn/caifu/index/os/android/ver/" + Tools.getAppVersionName(context) + "?t=" + System.currentTimeMillis();
        else
            return "http://live-test.bbwc.cn/caifu/index/os/android/ver/" + Tools.getAppVersionName(context) + "?t=" + System.currentTimeMillis();

    }
}
