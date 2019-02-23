package cn.com.modernmedia.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 广告的slate
 *
 * @author user
 */
public class AdvUriParse {
    public static final String GOTO_ADV_ARTICLE = "goto_adv_article";

    /**
     * 跳转到广告文章 slate://adv/advid
     *
     * @param uri
     * @return
     */
    private static ArrayList<String> parser(String uri) {
        ArrayList<String> list = new ArrayList<String>();
        if (TextUtils.isEmpty(uri)) return list;
        if (uri.startsWith("slate://adv/")) {
            String[] array = uri.split("slate://adv/");
            parseArray(uri, array, list);
        }
        return list;
    }

    private static void parseArray(String uri, String[] array, ArrayList<String> list) {
        if (array.length > 1) {
            String[] param = array[1].split("/");
            if (param.length == 1) {
                if (CommonApplication.advList == null) return;
                list.add(GOTO_ADV_ARTICLE);
                AdvItem item = getAdvById(ParseUtil.stoi(param[0]));
                if (item == null) return;
                list.add(item.getTagname());
                list.add(param[0]);
            }
        }
    }

    private static AdvItem getAdvById(int advId) {
        Map<Integer, List<AdvItem>> advMap = CommonApplication.advList.getAdvMap();
        for (int key : advMap.keySet()) {
            List<AdvItem> list = advMap.get(key);
            for (AdvItem item : list) {
                if (item.getAdvId() == advId) return item;
            }
        }
        return null;
    }

    /**
     * @param context
     * @param entries [0] ArticleItem;[1]TransferArticle...
     * @param view
     * @param cls     [0]为特定的文章页
     */
    public static void clickSlate(Context context, String link, Entry[] entries, View view, Class<?>... cls) {
        List<String> list = parser(link);
        if (ParseUtil.listNotNull(list) && list.size() > 1) {
            String key = list.get(0);
            if (key.equals(GOTO_ADV_ARTICLE)) {
                if (list.size() >= 3) doLinkAdv(context, list, entries, view, cls);
            }
        }
    }

    private static void doLinkAdv(Context context, List<String> list, Entry[] entries, View view, Class<?>... cls) {
        // int advId = ParseUtil.stoi(list.get(2), -1);
        // if (context instanceof CommonArticleActivity) {
        // if (view instanceof CommonWebView) {
        // ((CommonWebView) view).gotoAdv(advId);
        // return;
        // }
        // }
        TransferArticle tr = new TransferArticle(-1, list.get(1), "", ParseUtil.stoi(list.get(2), -1), ArticleType.Default);
        TransferArticle transferArticle = entries.length > 1 ? (TransferArticle) entries[1] : null;
        if (transferArticle != null) tr.setUid(transferArticle.getUid());
        PageTransfer.gotoArticleActivity(context, tr);
    }

}
