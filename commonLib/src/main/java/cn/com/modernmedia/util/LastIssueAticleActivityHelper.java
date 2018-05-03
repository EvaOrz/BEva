package cn.com.modernmedia.util;

import android.content.Context;

import java.util.List;

import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 往期文章页helper
 *
 * @author zhuqiao
 */
public class LastIssueAticleActivityHelper {
    private Context mContext;
    private TransferArticle transferArticle;

    public LastIssueAticleActivityHelper(Context mContext, TransferArticle transferArticle) {
        this.mContext = mContext;
        this.transferArticle = transferArticle;
    }

    private CommonArticleActivity getActivity() {
        if (mContext instanceof CommonArticleActivity) {
            return (CommonArticleActivity) mContext;
        }
        return null;
    }

    /**
     * 读取往期文章
     */
    public void doGetLastIssueAricles() {
        if (transferArticle == null || getActivity() == null) return;
        String tagName = transferArticle.getTagName();
        String flieName = ConstData.getLastArticlesFileName(tagName);
        //		if (FileManager.containFile(flieName)) {
        // 有缓存文件
        getLastArticleList("", true);
        //		} else {
        //			getLastIssueCats();
        //		}
    }

    /**
     * 获取往期栏目列表
     */
    private void getLastIssueCats() {
        getActivity().showLoading();
        OperateController.getInstance(mContext).getLastIssueCats(transferArticle.getTagName(), new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry instanceof TagInfoList) {
                    TagInfoList catList = (TagInfoList) entry;
                    List<TagInfo> list = catList.getList();
                    if (ParseUtil.listNotNull(list)) {
                        StringBuilder builder = new StringBuilder();
                        for (TagInfo cat : list) {
                            if (cat.getHaveChildren() == 0) // 排除父栏目
                                builder.append(cat.getTagName()).append(",");
                        }
                        String name = builder.toString();
                        if (name.length() > 1) {
                            name = name.substring(0, name.length() - 1);
                        }
                        getLastArticleList(name, false);
                        return;
                    }
                }
                getActivity().showError();
            }
        });
    }

    /**
     * 获取往期文章
     *
     * @param name 栏目列表名称
     */
    private void getLastArticleList(String name, final boolean useCache) {
        FetchApiType type = useCache ? FetchApiType.USE_CACHE_FIRST : FetchApiType.USE_HTTP_FIRST;
        OperateController.getInstance(mContext).getLastIssueArticles(transferArticle.getTagName(), name, "", transferArticle.getPublishTime(), type, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (useCache && !checkCacheUseable(entry)) {
                    getLastIssueCats();
                } else {
                    getActivity().afterFecthArticleList(entry);
                }
            }
        });
    }

    /**
     * 判断缓存是否可用
     *
     * @param entry
     * @return
     */
    private boolean checkCacheUseable(Entry entry) {
        if (!(entry instanceof TagArticleList)) {
            return false;
        }
        TagArticleList articleList = (TagArticleList) entry;
        return ParseUtil.listNotNull(articleList.getArticleList());
    }
}
