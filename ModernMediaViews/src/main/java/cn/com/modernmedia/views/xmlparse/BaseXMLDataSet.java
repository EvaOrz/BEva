package cn.com.modernmedia.views.xmlparse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.views.AuthorCenterActivity;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.fav.BindFavToUserImplement;
import cn.com.modernmedia.views.index.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.CommonWebView;
import cn.com.modernmedia.widget.FullVideoView;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.jzvd.JZVideoPlayer;

/**
 * 设置数据基类
 *
 * @author zhuqiao
 */
public class BaseXMLDataSet {
    protected Context mContext;
    protected HashMap<String, View> map = new HashMap<String, View>();
    protected List<View> ninePatchViewList = new ArrayList<View>();
    protected List<View> clickViewList;

    public BaseXMLDataSet(Context context, HashMap<String, View> map, List<View> clickViewList, List<View> ninePatchViewList) {
        mContext = context;
        this.map = map;
        this.clickViewList = clickViewList;
        this.ninePatchViewList = ninePatchViewList;
    }

    /**
     * webView 广告
     */
    protected void webAdv(ArticleItem item) {
        if (map.containsKey(FunctionXML.ADV_IMAGE))
            map.get(FunctionXML.ADV_IMAGE).setVisibility(View.GONE);

        AdvSource advPic = item.getAdvSource();
        if (!map.containsKey(FunctionXML.ADV_WEBVIWE)) return;
        View view = map.get(FunctionXML.ADV_WEBVIWE);
        view.setVisibility(View.VISIBLE);

        if (advPic != null && view instanceof CommonWebView) {
            view.getLayoutParams().width = CommonApplication.width;
            if (advPic != null && advPic.getWidth() > 0) {
                int height = advPic.getHeight() * CommonApplication.width / advPic.getWidth();
                view.getLayoutParams().height = height;
            }
            Log.e("加载网页广告", item.getAdvSource().getUrl());
            ((CommonWebView) view).loadUrl(advPic.getUrl());
        }
    }

    /**
     * 标题
     *
     * @param item
     */
    protected void title(ArticleItem item, BaseIndexAdapter adapter) {
        if (!map.containsKey(FunctionXML.TITLE)) return;
        View view = map.get(FunctionXML.TITLE);
        if (!(view instanceof TextView)) return;
        TextView title = (TextView) view;

        String titleText = "";
        String sss = " 专享  ";
        if (!TextUtils.equals(item.getApiTag(), "cat_15") && !TextUtils.equals(item.getApiTag(), "cat_13") && item.getProperty().getType() != 13)
            title.setSingleLine();

        if (!TextUtils.equals(item.getApiTag(), "cat_15") || item.getProperty().getLevel() != 1 || item.getPosition().getStyle() != 1) {
            titleText = item.getTitle();
        } else {
            titleText = sss + item.getTitle();
        }
//        title.setText(titleText);
        // 即时头条不显示栏目title
        //        if (item.getPosition().getStyle() == 3 && TextUtils.equals(item.getApiTag(), "cat_15")) {
        //            titleText = item.getGroupdisplayname() + " | " + item.getTitle();
        //        } else
        TextPaint tp = title.getPaint();

        if (item.getProperty().getIsadv() == 1) {
//            title.setText("推广   " + titleText);
            Drawable drawable= title.getContext().getResources().getDrawable(R.drawable.recommend);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth() * 10/15, drawable.getMinimumHeight() * 10/15);
            //普通图片
//            title.setCompoundDrawablePadding(20);
//            title.setCompoundDrawables(drawable,null,null,null);

            //富文本图片
            ImageSpan imageSpan = new ImageSpan(drawable);
            SpannableString spannableString = new SpannableString("ssssssssss  ");
            spannableString.setSpan(imageSpan,0,10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            title.setText(spannableString);
            title.append(titleText);
        }else {
//            title.setCompoundDrawables(null,null,null,null);
            title.setText(titleText);
        }
        if (adapter == null) return;
        //已读
        if (V.articleIsReaded(item)) {
            if (title.getTag(R.id.title_readed_color) instanceof String) {
                String color = title.getTag(R.id.title_readed_color).toString();

                if (!TextUtils.equals(item.getApiTag(), "cat_15") || item.getProperty().getLevel() != 1 || item.getPosition().getStyle() != 1) {
                    if (color.startsWith("#")) title.setTextColor(Color.BLACK);
                } else {
                    if (color.startsWith("#")) {
                        SpannableStringBuilder builder = new SpannableStringBuilder(title.getText().toString());

                        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
                        ForegroundColorSpan bSpan = new ForegroundColorSpan(Color.BLACK);
                        builder.setSpan(redSpan, 0, sss.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        builder.setSpan(bSpan, sss.length(), titleText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        title.setText(builder);
                    }

                }

            }
            tp.setFakeBoldText(false);
            //未读
        } else {
            if (title.getTag(R.id.title_default_color) instanceof String) {
                String color = title.getTag(R.id.title_default_color).toString();

                if (!TextUtils.equals(item.getApiTag(), "cat_15") || item.getProperty().getLevel() != 1 || item.getPosition().getStyle() != 1) {
                    if (color.startsWith("#")) title.setTextColor(Color.parseColor(color));
                } else {
                    if (color.startsWith("#")) {
                        SpannableStringBuilder builder = new SpannableStringBuilder(title.getText().toString());

                        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
                        ForegroundColorSpan bSpan = new ForegroundColorSpan(Color.parseColor(color));
                        builder.setSpan(redSpan, 0, sss.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        builder.setSpan(bSpan, sss.length(), titleText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        title.setText(builder);
                    }
                }
                //            if (title.getTag(R.id.title_default_font) instanceof String && item.getPosition().getStyle() == 3 && TextUtils.equals(item.getApiTag(), "cat_15")) {
                //                title.setTextColor(item.getGroupdisplaycolor());
                //                tp.setFakeBoldText(true); // 推荐首页大图模式已读文章设为加粗字体
                //            }
            }

            tp.setFakeBoldText(true); // 推荐首页大图模式已读文章设为加粗字体
        }
    }

    /**
     * 商周付费专享logo
     */
    protected void pay(ArticleItem item) {
        if (!map.containsKey(FunctionXML.HK_PAY_IMG)) return;
        View view = map.get(FunctionXML.HK_PAY_IMG);
        if ((TextUtils.equals(item.getApiTag(), "cat_15") && item.getPosition().getId() != 1) || item.getProperty().getLevel() != 1)
            view.setVisibility(View.GONE);
        else {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 描述
     */
    protected void desc(ArticleItem item, int position, BaseIndexAdapter adapter) {
        if (!map.containsKey(FunctionXML.DESC)) return;
        View view = map.get(FunctionXML.DESC);
        if (!(view instanceof TextView)) return;
        TextView desc = (TextView) view;
        if (TextUtils.equals(item.getApiTag(), "cat_15")) desc.setText(item.getGroupdisplayname());
        else {
            if (adapter == null) {
                desc.setText(item.getDesc());
                return;
            }
            if (desc.getTag(R.id.desc_check_scroll) == null) {
                desc.setText(item.getDesc());
                return;
            }
            if (!adapter.isConvertViewIsNull()) desc.setText("");
            if (adapter.hasInitDesc(position)) desc.setText(item.getDesc());
            if (!adapter.isScroll()) {
                if (!adapter.hasInitDesc(position)) {
                    desc.setText(item.getDesc());
                    adapter.addDesc(position);
                }
            }
        }
    }


    /**
     * 播放视频
     */
    protected void videoview(final ArticleItem item, boolean isAdv) {

        if (!map.containsKey(FunctionXML.VIDEO_VIEW)) {
            return;
        }
        if (ParseUtil.mapContainsKey(map, FunctionXML.IMAGE))
            map.get(FunctionXML.IMAGE).setVisibility(View.GONE);

        if (ParseUtil.mapContainsKey(map, FunctionXML.ADV_WEBVIWE))
            map.get(FunctionXML.ADV_WEBVIWE).setVisibility(View.GONE);


        final FullVideoView videoView = (FullVideoView) map.get(FunctionXML.VIDEO_VIEW);
        videoView.setVisibility(View.VISIBLE);
        if (isAdv) {
            videoView.setData(item, -1, JZVideoPlayer.SCREEN_WINDOW_LIST, null);

        } else {
            // 付费视频需要阅读权限
            videoView.setData(item, item.getProperty().getLevel(), JZVideoPlayer.SCREEN_WINDOW_LIST, new FullVideoView.OnVideoStartListener() {
                @Override
                public void onHasLevel(boolean hasLevel) {
                    if (!hasLevel) {
                        onClick(videoView, item, ArticleType.Default);
                    }
                }
            });
        }

    }

    /**
     * outline
     */
    protected void outline(ArticleItem item, int position, BaseIndexAdapter adapter) {
        if (!map.containsKey(FunctionXML.OUTLINE)) return;
        View view = map.get(FunctionXML.OUTLINE);
        if (!(view instanceof TextView)) return;
        TextView outline = (TextView) view;
        if (adapter == null) {
            outline.setText(item.getOutline());
            return;
        }
        if (outline.getTag(R.id.desc_check_scroll) == null) {
            outline.setText(item.getOutline());
            return;
        }
        if (!adapter.isConvertViewIsNull()) outline.setText("");
        if (adapter.hasInitDesc(position)) outline.setText(item.getOutline());
        if (!adapter.isScroll()) {
            if (!adapter.hasInitDesc(position)) {
                outline.setText(item.getOutline());
                adapter.addDesc(position);
            }
        }
    }

    /**
     * tag
     */
    protected void tag(ArticleItem item, int position, BaseIndexAdapter adapter) {
        if (!map.containsKey(FunctionXML.TAG)) return;
        View view = map.get(FunctionXML.TAG);
        if (!(view instanceof TextView)) return;
        TextView tag = (TextView) view;
        if (adapter == null) {
            tag.setText(item.getTag());
            return;
        }
        if (tag.getTag(R.id.desc_check_scroll) == null) {
            tag.setText(item.getTag());
            return;
        }
        if (!adapter.isConvertViewIsNull()) tag.setText("");
        if (adapter.hasInitDesc(position)) tag.setText(item.getTag());
        if (!adapter.isScroll()) {
            if (!adapter.hasInitDesc(position)) {
                tag.setText(item.getTag());
                adapter.addDesc(position);
            }
        }
    }

    /**
     * sub_title
     */
    protected void subTitle(ArticleItem item, int position, BaseIndexAdapter adapter) {
        if (!map.containsKey(FunctionXML.SUB_TITLE)) return;
        View view = map.get(FunctionXML.SUB_TITLE);
        if (!(view instanceof TextView)) return;
        TextView sub_title = (TextView) view;
        if (adapter == null) {
            sub_title.setText(item.getSubtitle());
            return;
        }
        if (sub_title.getTag(R.id.desc_check_scroll) == null) {
            sub_title.setText(item.getSubtitle());
            return;
        }
        if (!adapter.isConvertViewIsNull()) sub_title.setText("");
        if (adapter.hasInitDesc(position)) sub_title.setText(item.getSubtitle());
        if (!adapter.isScroll()) {
            if (!adapter.hasInitDesc(position)) {
                sub_title.setText(item.getSubtitle());
                adapter.addDesc(position);
            }
        }
    }

    /**
     * create_user
     */
    protected void createUser(ArticleItem item, int position, BaseIndexAdapter adapter) {
        if (!map.containsKey(FunctionXML.CREATE_USER)) return;
        View view = map.get(FunctionXML.CREATE_USER);
        if (!(view instanceof TextView)) return;
        TextView create_user = (TextView) view;
        if (adapter == null) {
            create_user.setText(item.getCreateuser());
            return;
        }
        if (create_user.getTag(R.id.desc_check_scroll) == null) {
            create_user.setText(item.getCreateuser());
            return;
        }
        if (!adapter.isConvertViewIsNull()) create_user.setText("");
        if (adapter.hasInitDesc(position)) create_user.setText(item.getCreateuser());
        if (!adapter.isScroll()) {
            if (!adapter.hasInitDesc(position)) {
                create_user.setText(item.getCreateuser());
                adapter.addDesc(position);
            }
        }
    }

    /**
     * modify_user
     */
    protected void modifyUser(ArticleItem item, int position, BaseIndexAdapter adapter) {
        if (!map.containsKey(FunctionXML.MODIFY_USER)) return;
        View view = map.get(FunctionXML.MODIFY_USER);
        if (!(view instanceof TextView)) return;
        TextView modify_user = (TextView) view;
        if (adapter == null) {
            modify_user.setText(item.getModifyuser());
            return;
        }
        if (modify_user.getTag(R.id.desc_check_scroll) == null) {
            modify_user.setText(item.getModifyuser());
            return;
        }
        if (!adapter.isConvertViewIsNull()) modify_user.setText("");
        if (adapter.hasInitDesc(position)) modify_user.setText(item.getModifyuser());
        if (!adapter.isScroll()) {
            if (!adapter.hasInitDesc(position)) {
                modify_user.setText(item.getModifyuser());
                adapter.addDesc(position);
            }
        }
    }

    /**
     * 日期
     *
     * @param item
     */
    protected void date(ArticleItem item) {
        for (String key : map.keySet()) {
            if (!key.startsWith(FunctionXML.DATE)) continue;
            View view = map.get(key);
            if (!(view instanceof TextView)) continue;
            if (!(view.getTag(R.id.date_format) instanceof String)) continue;
            TextView date = (TextView) view;
            String language = view.getTag(R.id.date_format_language) == null ? "" : view.getTag(R.id.date_format_language).toString();
            date.setText(DateFormatTool.format(item.getInputtime() * 1000L, view.getTag(R.id.date_format).toString(), language));
        }
    }

    /**
     * 收藏
     *
     * @param item
     */
    protected void fav(final ArticleItem item) {
        if (!map.containsKey(FunctionXML.FAV)) return;
        View view = map.get(FunctionXML.FAV);
        changeFav(view, item);
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ModernMediaTools.addFav(mContext, item, Tools.getUid(mContext), new BindFavToUserImplement(mContext));
                changeFav(v, item);
            }
        });
    }

    /**
     * 改变收藏背景图
     *
     * @param view
     * @param item
     */
    private void changeFav(View view, ArticleItem item) {
        boolean faved = NewFavDb.getInstance(mContext).containThisFav(item.getArticleId(), Tools.getUid(mContext));
        if (faved) {
            if (view.getTag(R.id.select_bg) instanceof String) {
                V.setImage(view, view.getTag(R.id.select_bg).toString());
            }
        } else {
            if (view.getTag(R.id.unselect_bg) instanceof String) {
                V.setImage(view, view.getTag(R.id.unselect_bg).toString());
            }
        }
    }

    /**
     * 设置.9图片
     */
    protected void ninePatch() {
        if (!ParseUtil.listNotNull(ninePatchViewList)) return;
        for (View view : ninePatchViewList) {
            if (view.getTag(R.id.nine_patch_img) instanceof String) {
                V.setImage(view, view.getTag(R.id.nine_patch_img).toString());
            }
        }
    }

    /**
     * 注册点击事件
     *
     * @param item
     * @param articleType
     */
    protected void registerClick(final ArticleItem item, final ArticleType articleType) {
        if (ParseUtil.listNotNull(clickViewList)) {
            for (final View view : clickViewList) {
                view.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        BaseXMLDataSet.this.onClick(view, item, articleType);
                    }
                });
            }
        }
    }

    /**
     * 注册点击事件
     *
     * @param view
     */
    protected void onClick(View view, ArticleItem item, ArticleType articleType) {

        if (item.getArticleId() == -1 && item.getZhuanlanAuthor() != null) {
            Intent i = new Intent(mContext, AuthorCenterActivity.class);
            i.putExtra("author_info", item);
            mContext.startActivity(i);
        } else if (item.getIsTekan() == 1 && !SlateDataHelper.getLevelByType(mContext, item.getProperty().getLevel())) {
            LogHelper.checkVipOpen(mContext, "article-content");
            if (SlateDataHelper.getUserLoginInfo(mContext) == null) {//未登录
                String broadcastIntent = "cn.com.modernmediausermodel.LoginActivity_nomal";
                Intent intent = new Intent(broadcastIntent);
                mContext.sendBroadcast(intent);
            } else {//普通用户 非付费用户,非VIP
                String broadcastIntent = "cn.com.modernmediausermodel.VipOpenActivity_nomal";
                Intent intent = new Intent(broadcastIntent);
                mContext.sendBroadcast(intent);
            }
        } else {
            V.clickSlate(mContext, item, articleType);
        }
        AdvTools.requestClick(item);
        log(item);
    }


    /**
     * 广告
     */
    public void adv(ArticleItem item) {
        if (item.isAdv() != 0) {

            if (map.containsKey(FunctionXML.ADV_IMAGE)) {
                View view = map.get(FunctionXML.ADV_IMAGE);
                view.setVisibility(View.VISIBLE);
                if (map.get(FunctionXML.ADV_WEBVIWE) != null)
                    map.get(FunctionXML.ADV_WEBVIWE).setVisibility(View.GONE);
                if (map.get(FunctionXML.VIDEO_VIEW) != null)
                    map.get(FunctionXML.VIDEO_VIEW).setVisibility(View.GONE);

                if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    imageView.getLayoutParams().width = CommonApplication.width;
                    AdvSource advPic = item.getAdvSource();
                    if (advPic != null && advPic.getWidth() > 0) {
                        int height = advPic.getHeight() * CommonApplication.width / advPic.getWidth();
                        imageView.getLayoutParams().height = height;
                        imageView.setScaleType(ImageView.ScaleType.CENTER);
                        V.setImage(imageView, "placeholder");
                        Log.e("加载广告图片", advPic.getTitle());
                        CommonApplication.finalBitmap.display(imageView, advPic.getUrl());
                    }
                }
            }
        }
    }

    protected void log(ArticleItem item) {
        LogHelper.logOpenArticleFromColumnPage(mContext, item.getArticleId() + "", item.getTagName() + "");
    }
}
