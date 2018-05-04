package cn.com.modernmedia.views.xmlparse;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.util.HashMap;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.WeeklyLogEvent;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.GifView;
import cn.com.modernmedia.widget.LoadingImage;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 为headview item设置数据
 *
 * @author zhuqiao
 */
public class XMLDataSetForHeadItem extends BaseXMLDataSet {
    private int position;

    public XMLDataSetForHeadItem(Context context, HashMap<String, View> map, List<View> clickViewList, List<View> ninePatchViewList) {
        super(context, map, clickViewList, ninePatchViewList);
    }

    /**
     * 设置数据
     *
     * @param item
     * @param position
     * @param articleType
     */
    public void setData(ArticleItem item, int position, ArticleType articleType) {
        this.position = position;
        if (map == null || map.isEmpty() || item == null) return;

        title(item, null);
        desc(item, 0, null);
        outline(item, 0, null);
        tag(item, 0, null);
        // 广告
        if (item.getAdvSource() != null) {
            if (item.isAdv() == 1) adv(item);
            else if (item.isAdv() == 2) webAdv(item);
            else if (item.isAdv() == 4) videoview(item, true);
        }
        date(item);
        fav(item);
        subTitle(item, 0, null);
        createUser(item, 0, null);
        modifyUser(item, 0, null);
        ninePatch();
        registerClick(item, articleType);
        pay(item);

        if (ParseUtil.listNotNull(item.getPicList())) {
            // 先检查是否有视频文章
            if (!TextUtils.isEmpty(item.getPicList().get(0).getVideolink())) {
                videoview(item, false);
            } else {
                String url = item.getPicList().get(0).getUrl();
                if (!TextUtils.isEmpty(url)) {
                    // 动图
                    if (url.endsWith(".gif")) gif(url);
                    else image(url);
                }
            }
        }
    }

    /**
     * 显示动图
     *
     * @param url
     */
    private void gif(String url) {
        if (!ParseUtil.mapContainsKey(map, FunctionXML.GIF_IMG)) return;
        View view = map.get(FunctionXML.GIF_IMG);
        if (ParseUtil.mapContainsKey(map, FunctionXML.IMAGE))
            map.get(FunctionXML.IMAGE).setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);

        if (view instanceof GifView) CommonApplication.finalBitmap.display(view, url);
    }

    /**
     * 图片
     */
    private void image(String url) {
        if (!ParseUtil.mapContainsKey(map, FunctionXML.IMAGE)) return;
        View view = map.get(FunctionXML.IMAGE);
        if (ParseUtil.mapContainsKey(map, FunctionXML.GIF_IMG))
            map.get(FunctionXML.GIF_IMG).setVisibility(View.GONE);
        if (ParseUtil.mapContainsKey(map, FunctionXML.ADV_WEBVIWE))
            map.get(FunctionXML.ADV_WEBVIWE).setVisibility(View.GONE);
        if (ParseUtil.mapContainsKey(map, FunctionXML.VIDEO_VIEW))
            map.get(FunctionXML.VIDEO_VIEW).setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);

        if (view instanceof ImageView) img(url, (ImageView) view);
        else if (view instanceof LoadingImage) img(url, (LoadingImage) view);
    }

    private void img(String url, ImageView imageView) {
        if (imageView.getTag(R.id.img_placeholder) instanceof String) {
            // TODO 占位图
            imageView.setScaleType(ScaleType.CENTER);
            V.setImage(imageView, imageView.getTag(R.id.img_placeholder).toString());
        }
        CommonApplication.finalBitmap.display(imageView, url);
    }

    private void img(String url, LoadingImage loadingImage) {
        loadingImage.setUrl(url);
    }

    @Override
    protected void log(ArticleItem item) {
        super.log(item);
        LogHelper.logAndroidTouchHeadline(mContext, position);
        if (ConstData.getInitialAppId() == 20) {
            WeeklyLogEvent.logAndroidColumnHeadviewClickCount(mContext);
        }
    }

}
