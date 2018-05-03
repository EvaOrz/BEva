package cn.com.modernmedia.util;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Time;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.BaseFragmentActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.breakpoint.BreakPointUtil;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.listener.BindFavToUserListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.BreakPoint;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;

public class ModernMediaTools {
    private static String makeCard = "";
    private static String imageSrc = "";// 点击对象的html属性
    public static final String EMAIL = "feedback@modernmedia.com.cn";
    public static final String CCEMAIL = "4006503206@modernmedia.com.cn";
    private static final String ASSESS_URL = "https://play.google.com/store/apps/details?id=";

    /**
     * 获取系统时间(m-d h:m)
     *
     * @return
     */
    public static String fetchTime() {
        Time localTime = new Time();
        localTime.setToNow();
        String tt = localTime.format("%m-%d %H:%M");
        return tt;
    }

    /**
     * 获取slate跳转文章的id(eg 收藏index中的item，当这个item是跳转到别的文章，那么就需要收藏跳转到的文章，而不是当前这个)
     *
     * @param item
     * @return
     */
    public static int fecthSlateArticleId(ArticleItem item) {
        if (item == null) return -1;
        int articleId = item.getArticleId();
        String link = "";
        link = item.getSlateLink();
        if (TextUtils.isEmpty(link)) if (link.toLowerCase().startsWith("slate://")) {
            List<String> list = UriParse.parser(link);
            if (list.size() > 1) {
                if (list.get(0).equalsIgnoreCase("article")) {
                    if (list.size() > 3) {
                        articleId = ParseUtil.stoi(list.get(3), -1);
                    }
                }
            }
        }
        return articleId;
    }

    public static void showLoadView(Context context, int flag) {
        if (context instanceof BaseActivity) {
            switch (flag) {
                case 0:
                    ((BaseActivity) context).disProcess();
                    break;
                case 1:
                    ((BaseActivity) context).showLoading();
                    break;
                case 2:
                    ((BaseActivity) context).showError();
                    break;
                default:
                    break;
            }
        } else if (context instanceof BaseFragmentActivity) {
            switch (flag) {
                case 0:
                    ((BaseFragmentActivity) context).disProcess();
                    break;
                case 1:
                    ((BaseFragmentActivity) context).showLoading();
                    break;
                case 2:
                    ((BaseFragmentActivity) context).showError();
                    break;
                default:
                    break;
            }
        }
    }

    public static String getPackageFileName(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("/")) {
                url = url.substring(url.lastIndexOf("/"));
            }
            if (url.startsWith("/")) {
                url = url.substring(1);
            }
        }
        return url;
    }

    public static String getPackageFolderName(String url) {
        url = getPackageFileName(url);
        if (url.endsWith(".zip")) {
            url = url.substring(0, url.lastIndexOf(".zip"));
        } else if (url.endsWith(".pdf")) {
            url = url.substring(0, url.lastIndexOf(".pdf"));
        }
        return url;
    }

    /**
     * 断点下载
     *
     * @param context
     * @param tagInfo        期信息
     * @param breakPointUtil
     */
    public static void downloadPackage(Context context, TagInfo tagInfo, BreakPointUtil breakPointUtil) {
        if (tagInfo == null || TextUtils.isEmpty(tagInfo.getIssueProperty().getFullPackage())) {
            Tools.showToast(context, R.string.no_issue_zip);
            return;
        }
        BreakPoint breakPoint = new BreakPoint();
        if (FileManager.containThisPackageFolder(tagInfo.getIssueProperty().getFullPackage())) {
            // TODO 如果包含解压包，则直接加载
            breakPoint.setStatus(BreakPointUtil.DONE);
        } else if (FileManager.containThisPackage(tagInfo.getIssueProperty().getFullPackage())) {
            // TODO 如果包含zip包,执行断点下载
            breakPoint.setStatus(BreakPointUtil.BREAK);
        } else {
            breakPoint.setStatus(BreakPointUtil.NONE);
        }
        breakPoint.setTagName(tagInfo.getTagName());
        breakPoint.setUrl(tagInfo.getIssueProperty().getFullPackage());
        CommonApplication.addPreIssueDown(tagInfo.getTagName(), breakPointUtil);
        CommonApplication.notityDwonload(tagInfo.getTagName(), 1);
        breakPointUtil.downLoad(breakPoint);
    }

    /**
     * 是否是独立栏目
     *
     * @param link
     * @return !=-1是独立栏目；-1不是
     */
    public static int isSoloCat(String link) {
        if (TextUtils.isEmpty(link)) return -1;
        ArrayList<String> list = UriParse.parser(link);
        if (ParseUtil.listNotNull(list) && list.size() == 3) {
            // slate://column/32/0/
            if (list.get(0).equals("column") && list.get(2).equals("0")) {
                return ParseUtil.stoi(list.get(1), -1);
            }
        }
        return -1;
    }

    /**
     * 获取当前cat所在catList的位置
     *
     * @return
     */
    public static int getCatPosition(String tagName, TagInfoList tagInfoList) {
        if (TextUtils.isEmpty(tagName)) {
            return -1;
        }
        for (int i = 0; i < tagInfoList.getList().size(); i++) {
            TagInfo tagInfo = tagInfoList.getList().get(i);
            if (TextUtils.equals(tagName, tagInfo.getTagName())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取makecard的js代码
     *
     * @param context
     * @param x
     * @param y
     * @return
     */
    public static String getMakeCard(Context context, int x, int y) {
        if (TextUtils.isEmpty(makeCard)) {
            InputStreamReader is = null;
            BufferedReader br = null;
            try {
                is = new InputStreamReader(context.getAssets().open("make_card"));// 文件只能放在主工程里面。。。
                br = new BufferedReader(is, 1024);
                String line = "";
                while ((line = br.readLine()) != null) {
                    makeCard += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return makeCard.replace("##x", x + "").replace("##y", y + "");
    }

    /**
     * 获取点击图片的src
     *
     * @param context
     * @param x
     * @param y
     * @return
     */
    public static String getImageSrc(Context context, int x, int y) {
        //		if (TextUtils.isEmpty(imageSrc)) {
        //			InputStreamReader is = null;
        //			BufferedReader br = null;
        //			try {
        //				is = new InputStreamReader(context.getAssets().open(
        //						"get_image_src"));// 文件只能放在主工程里面。。。
        //				br = new BufferedReader(is, 1024);
        //				String line = "";
        //				while ((line = br.readLine()) != null) {
        //					imageSrc += line;
        //				}
        //			} catch (Exception e) {
        //				e.printStackTrace();
        //			} finally {
        //				try {
        //					if (is != null)
        //						is.close();
        //				} catch (IOException e) {
        //					e.printStackTrace();
        //				}
        //			}
        //		}
        //		return imageSrc.replace("##x", x + "").replace("##y", y + "");

        StringBuffer buffer = new StringBuffer();
        buffer.append("function(){\n");
        buffer.append("  var x = " + x + "; var y = " + y + ";\n");
        buffer.append("  var scale = screen.width / document.documentElement.clientWidth;\n");
        buffer.append("  x = x / scale;\n");
        buffer.append("  y = y / scale;\n");
        buffer.append("  var el = document.elementFromPoint(x, y);\n");
        buffer.append("  if (el.nodeName == 'IMG') {\n");
        buffer.append("    return el.src;\n");
        buffer.append("  }\n");
        buffer.append("  return '';\n");
        buffer.append("}()");
        return buffer.toString();
    }

    public static String getMeta() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("function(){\n");
        buffer.append("  var m = document.getElementsByTagName('meta');\n");
        buffer.append("  for(var i in m) { \n");
        buffer.append("    if(m[i].name == 'sharemessage') {\n");
        buffer.append("      return m[i].content;\n");
        buffer.append("    }\n");
        buffer.append("  }\n");
        buffer.append("  return '';\n");
        buffer.append("}()");
        return buffer.toString();
    }

    public static String getShareMeta() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("function(){\n");
        buffer.append("  var m = document.getElementsByTagName('meta');\n");
        buffer.append("  for(var i in m) { \n");
        buffer.append("    if(m[i].name == '%@') {\n");
        buffer.append("      return m[i].content;\n");
        buffer.append("    }\n");
        buffer.append("  }\n");
        buffer.append("  return '';\n");
        buffer.append("}()");
        return buffer.toString();
    }

    /**
     * 独立栏目数据库where条件
     *
     * @param fromOffset
     * @param toOffset
     * @return
     */
    public static String checkSelection(String fromOffset, String toOffset, String offsetName, boolean containFL) {
        String greaterOperate = containFL ? " >= " : " > ";
        String smallerOperate = containFL ? " <= " : " < ";
        String result = "";
        // TODO 因为要取文章的范围，必须包含第一篇和最后一篇
        String greaterThanFrom = " and " + offsetName + greaterOperate + "'" + fromOffset + "'";
        String smallerThanTo = " and " + offsetName + smallerOperate + "'" + toOffset + "'";
        if (fromOffset.compareTo("0") == 0 && toOffset.compareTo("0") == 0) {
            // 0_0 获取全部
            return result;
        }
        if (fromOffset.compareTo("0") > 0 && toOffset.compareTo("0") > 0) {
            // from_to 获取中间
            result += greaterThanFrom;
            result += smallerThanTo;
            return result;
        }
        if (fromOffset.compareTo("0") > 0) {
            // from_0
            result += greaterThanFrom;
        } else if (toOffset.compareTo("0") > 0) {
            // 0_to
            result += smallerThanTo;
        }
        return result;
    }

    /**
     * 添加/删除收藏
     *
     * @param context
     * @param fav
     * @param listener
     */
    public static boolean addFav(Context context, ArticleItem fav, String uid, BindFavToUserListener listener) {
        if (fav == null) return false;
        NewFavDb db = NewFavDb.getInstance(context);
        if (db.containThisFav(fav.getArticleId(), uid)) {
            if (listener != null) listener.deleteFav(fav, uid);
            else db.deleteFav(fav.getArticleId(), uid);
            Toast.makeText(context, R.string.delete_fav, Toast.LENGTH_SHORT).show();
        } else {
            if (listener != null) listener.addFav(fav, uid);
            else db.addFav(fav, uid, false);
            Toast.makeText(context, R.string.add_fav, Toast.LENGTH_SHORT).show();
        }
        LogHelper.logAddFavoriteArticle(context, fav.getArticleId() + "", fav.getTagName());
        CommonApplication.notifyFav();
        return true;
    }

    public static void dismissLoad(Context context) {
        showLoadView(context, 0);
        if (context instanceof CommonMainActivity)
            ((CommonMainActivity) context).checkIndexLoading(0);
        Tools.showLoading(context, false);
    }

    /**
     * 意见反馈
     *
     * @param context
     */
    public static void feedBack(Context context, String subject, String text) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_CC, new String[]{CCEMAIL});
            // intent.putExtra(Intent.EXTRA_BCC, bccs);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL});
            intent.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将官方微信信息放入剪贴板中以便微信应用直接搜索，同时给用户提示
     *
     * @param context
     */
    public static void followWeixin(Context context) {
        if (TextUtils.isEmpty(CommonApplication.mConfig.getWeixin_public_number())) return;
        ClipboardManager board = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        board.setText(CommonApplication.mConfig.getWeixin_public_number());
        new AlertDialog.Builder(context).setMessage(R.string.weixin_info).setPositiveButton(R.string.sure, null).create().show();
    }


    /**
     * 评价我们
     *
     * @param context
     */
    public static void assess(Context context) {
        Uri uri = Uri.parse(ASSESS_URL + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static Map<String ,String> getHttpHeaders(Context mContext){
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("X-Slate-UserId", Tools.getUid(mContext));
        headerMap.put("X-Slate-DeviceId", Tools.getDeviceId(mContext));
        headerMap.put("X-Slate-AppId", CommonApplication.APP_ID + "");
        headerMap.put("X-Slate-DeviceType", android.os.Build.MODEL);
        headerMap.put("X-Slate-UUID", Tools.getMyUUID(mContext));
        headerMap.put("X-Slate-AppId", ConstData.getInitialAppId() + "");
        headerMap.put("X-SLATE-JAILBROKEN", Tools.isRooted() ? "11" : "10");//是否root（如果可以获取就获取）
        headerMap.put("X-Slate-DeviceToken", SlateDataHelper.getPushToken(mContext));
        headerMap.put("x-slate-os", "android");
        headerMap.put("X-Slate-AndroidToken", Tools.getDeviceToken(mContext));
        headerMap.put("X-SLATE-CLIENTVERSION", Tools.getAppVersionName(mContext));
        return headerMap;
    }
}
