package cn.com.modernmedia.views.xmlparse;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.Picture;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.index.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.widget.Style5ItemView;
import cn.com.modernmedia.widget.GifView;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 设置数据
 *
 * @author zhuqiao
 */
public class XMLDataSet extends BaseXMLDataSet {
    private BaseIndexAdapter adapter;

    public XMLDataSet(Context context, HashMap<String, View> map, List<View> clickViewList, List<View> ninePatchViewList, BaseIndexAdapter adapter) {
        super(context, map, clickViewList, ninePatchViewList);
        this.adapter = adapter;
    }

    /**
     * 设置数据
     *
     * @param item
     */
    public void setData(ArticleItem item, int position, ArticleType articleType) {
        if (map == null || map.isEmpty() || item == null || adapter == null) return;
        if (item.getPosition().getStyle() == 5) {
            style5(item);
        } else {
            if (item.isShowTitleBar()) {
                if (item.getPosition().getStyle() == 100 && map.containsKey(FunctionXML.TITLEBAR)) {
                    titleBar(item);
                } else if (item.getPosition().getStyle() == 103) {
                    authorTitleBar(item);
                }
            }
            frameBg(item);
            pay(item);
            title(item, adapter);
            desc(item, position, adapter);
            // 广告
            if (item.getAdvSource() != null) {
                if (item.isAdv() == 1) adv(item);
                else if (item.isAdv() == 2) webAdv(item);
                else if (item.isAdv() == 4) videoview(item, true);
            }
            outline(item, position, adapter);
            tag(item, position, adapter);
            catName(item);
            row(item);
            date(item);
            fav(item);
            subTitle(item, position, adapter);
            createUser(item, position, adapter);
            modifyUser(item, position, adapter);
            ninePatch();
            imageforGroup(item);// 组图模式初始化
            registerClick(item, articleType);

            List<Picture> list;
            // style == 1 取缩略图
            if (item.getPosition().getStyle() == 1) list = item.getThumbList();
            else list = item.getPicList();

            if (ParseUtil.listNotNull(list)) {
                // 先检查是否有视频文章
                if (!TextUtils.isEmpty(list.get(0).getVideolink())) {
                    videoview(item, false);
                } else {
                    String url = list.get(0).getUrl();
                    if (!TextUtils.isEmpty(url)) {
                        // 动图
                        if (url.endsWith(".gif")) gif(url);
                        else img(item);
                    }
                }

            }
        }
    }

    private void frameBg(ArticleItem item) {
        if (map.containsKey(FunctionXML.FRAME_CONTENT) && item.getProperty() != null && item.getProperty().getType() == 13) {

            View view = map.get(FunctionXML.FRAME_CONTENT);
            view.setBackgroundResource(R.drawable.gray_xiankuang);
        }
    }

    private void style5(ArticleItem item) {
        if (!map.containsKey(FunctionXML.FRAME_CONTENT)) return;

        View view = map.get(FunctionXML.FRAME_CONTENT);
        if (view instanceof LinearLayout) {
            LinearLayout vv = (LinearLayout) view;
            Style5ItemView style5ItemView = new Style5ItemView(mContext);
            style5ItemView.setData(item);
            vv.removeAllViews();
            vv.addView(style5ItemView);
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
     * 组图模式，三张图，style=2
     */
    private void imageforGroup(ArticleItem item) {
        if (!(map.containsKey(FunctionXML.IMAGE_1) && map.containsKey(FunctionXML.IMAGE_2) && map.containsKey(FunctionXML.IMAGE_3)))
            return;
        View view1 = map.get(FunctionXML.IMAGE_1);
        View view2 = map.get(FunctionXML.IMAGE_2);
        View view3 = map.get(FunctionXML.IMAGE_3);
        if (!(view1 instanceof ImageView && view2 instanceof ImageView && view3 instanceof ImageView))
            return;
        ImageView imageView1 = (ImageView) view1;
        ImageView imageView2 = (ImageView) view2;
        ImageView imageView3 = (ImageView) view3;
        if (item.getPicList().size() > 0) V.setImage(imageView1, item.getPicList().get(0).getUrl());
        if (item.getPicList().size() > 1) V.setImage(imageView2, item.getPicList().get(1).getUrl());
        if (item.getPicList().size() > 2) V.setImage(imageView3, item.getPicList().get(2).getUrl());
    }

    /**
     * 栏目标题栏
     */
    private void titleBar(ArticleItem item) {
        if (!(map.containsKey(FunctionXML.TITLEBAR) && map.containsKey(FunctionXML.TITLEBAR_1) && map.containsKey(FunctionXML.TITLEBAR_2)))
            return;
        View view = map.get(FunctionXML.TITLEBAR);
        View view1 = map.get(FunctionXML.TITLEBAR_1);
        View view2 = map.get(FunctionXML.TITLEBAR_2);

        if (!(view instanceof TextView)) return;
        TextView textView = (TextView) view;
        //        if (item.getInputtime().equals(mContext.getString(R.string.theme_magazine))) {
        //            textView.setBackgroundColor(Color.RED);
        //            textView.setText(item.getInputtime());
        //            return;
        //        }
        view1.setBackgroundColor(item.getGroupdisplaycolor());
        view2.setBackgroundColor(item.getGroupdisplaycolor());
        textView.setText(item.getGroupdisplayname());
        textView.setTextColor(item.getGroupdisplaycolor());
    }

    /**
     * 专栏作者信息titlebar
     */

    private void authorTitleBar(ArticleItem item) {
        if (!(map.containsKey(FunctionXML.TITLEBAR_NAME) && map.containsKey(FunctionXML.TITLEBAR_DESC) && map.containsKey(FunctionXML.TITLEBAR_COLOR) && map.containsKey(FunctionXML.TITLEBAR_PIC)))
            return;
        View name = map.get(FunctionXML.TITLEBAR_NAME);
        View pic = map.get(FunctionXML.TITLEBAR_PIC);
        View desc = map.get(FunctionXML.TITLEBAR_DESC);
        View color = map.get(FunctionXML.TITLEBAR_COLOR);

        if (!(name instanceof TextView) || !(desc instanceof TextView)) return;
        ((TextView) name).setText(item.getZhuanlanAuthor().getName());
        ((TextView) desc).setText(item.getZhuanlanAuthor().getDesc());
        if (!(pic instanceof ImageView)) return;
        V.setImage((ImageView) pic, item.getZhuanlanAuthor().getPicture());

    }


    /**
     * 列表图片
     */
    private void img(ArticleItem item) {
        if (!map.containsKey(FunctionXML.IMAGE)) return;
        View view = map.get(FunctionXML.IMAGE);
        if (ParseUtil.mapContainsKey(map, FunctionXML.GIF_IMG))
            map.get(FunctionXML.GIF_IMG).setVisibility(View.GONE);
        if (ParseUtil.mapContainsKey(map, FunctionXML.ADV_WEBVIWE))

            map.get(FunctionXML.ADV_WEBVIWE).setVisibility(View.GONE);
        if (ParseUtil.mapContainsKey(map, FunctionXML.VIDEO_VIEW))
            map.get(FunctionXML.VIDEO_VIEW).setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
        if (!(view instanceof ImageView)) return;
        ImageView imageView = (ImageView) view;

        // 首页小图模式要大图
        if (item.getPosition().getStyle() == 1 && TextUtils.equals(item.getApiTag(), "cat_15")) {
            if (!map.containsKey(FunctionXML.IMAGE_FRAME)) return;
            View fff = map.get(FunctionXML.IMAGE_FRAME);
            fff.setPadding(0, 0, 0, 0);
        }

        if (imageView.getTag(R.id.img_placeholder) instanceof String) {
            // TODO 占位图
            imageView.setScaleType(ScaleType.CENTER);
            V.setImage(imageView, imageView.getTag(R.id.img_placeholder).toString());
        } else {
            if (ConstData.getAppId() == 20) {
                imageView.setImageResource(R.drawable.white_bg);
            } else {
                imageView.setImageBitmap(null);
            }
        }
        if (!adapter.isScroll()) {
            // TODO 下载图片
            boolean usePicture = true;
            if (imageView.getTag(R.id.img_use) instanceof String) {
                String use = imageView.getTag(R.id.img_use).toString();
                usePicture = !TextUtils.equals("thumb", use);
            }
            adapter.downImage(item, imageView, usePicture);
        }
    }


    /**
     * 箭头
     *
     * @param item
     */
    private void row(ArticleItem item) {
        if (!map.containsKey(FunctionXML.ROW)) return;
        View view = map.get(FunctionXML.ROW);
        if (!(view instanceof ImageView)) return;
        if (TextUtils.equals(item.getApiTag(), "cat_15")) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            V.setIndexItemButtonImg((ImageView) view, item);
        }

    }

    /**
     * 4.0 时间
     */
    private void time(ArticleItem item) {
        if (!map.containsKey(FunctionXML.TIME)) return;
        View view = map.get(FunctionXML.TIME);
        if (!(view instanceof TextView)) return;
        ((TextView) view).setText(DateFormatTool.getStandardDate(item.getInputtime()));
    }


    /**
     * 4.0 时间
     */
    private void catName(ArticleItem item) {
        if (!map.containsKey(FunctionXML.TAGNAME)) return;
        View view = map.get(FunctionXML.TAGNAME);
        if (!(view instanceof TextView)) return;
        if (item.getProperty().getType() == 13) {
            if (!TextUtils.isEmpty(item.getSubcat())) {
                ((TextView) view).setText(item.getSubcat());
                view.setBackground(mContext.getResources().getDrawable(R.drawable.youzan_subcat_bg));
            }

        } else ((TextView) view).setText(item.getCatName());
    }

}
