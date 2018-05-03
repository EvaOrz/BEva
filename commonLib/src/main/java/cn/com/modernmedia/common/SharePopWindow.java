package cn.com.modernmedia.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * Created by Eva. on 2017/9/7.
 */

public class SharePopWindow extends PopupWindow {
    private Context mContext;
    private View conentView;
    private PopupWindow window;
    private ShareTools shareTools;
    private boolean isShareApp = false;
    private ArticleItem articleItem;
    private int type = 0;// 1：邮件；2：微信；3:朋友圈；4：微博；5：qq
    private String content;

    private void init() {
        shareTools = ShareTools.getInstance(mContext);
        conentView = LayoutInflater.from(mContext).inflate(R.layout.view_share_dialog, null);
        conentView.findViewById(R.id.share_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
            }
        });
        window = new PopupWindow(conentView, RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.setAnimationStyle(R.style.fetch_image_popup_anim);
        window.update();
        window.setBackgroundDrawable(new BitmapDrawable());
        window.showAtLocation(conentView, Gravity.BOTTOM, 0, 0);
        window.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                switch (type) {
                    case 1:
                        if (articleItem != null)
                            shareTools.shareByMail(articleItem.getTitle(), articleItem.getDesc(), articleItem.getWeburl());
                        else {
                            shareTools.shareByMail("【商周笔记】", content, "");
                        }
                        break;
                    case 2:
                        if (articleItem == null) {
                            shareTools.shareText(content, false);
                        } else if (TextUtils.isEmpty(articleItem.getWeburl())) {
                            shareTools.shareText(articleItem.getTitle(), false);
                        } else {
                            shareTools.shareImageAndTextToWeixin(articleItem.getTitle(), articleItem.getDesc(), articleItem.getWeburl(), false);
                        }
                        break;
                    case 3:
                        if (articleItem == null) {
                            shareTools.shareText(content, true);
                        } else if (TextUtils.isEmpty(articleItem.getWeburl())) {
                            shareTools.shareText(articleItem.getTitle(), true);
                        } else {
                            shareTools.shareImageAndTextToWeixin(articleItem.getTitle(), articleItem.getDesc(), articleItem.getWeburl(), true);
                        }
                        break;
                    case 4:
                        String cc = "";
                        if (articleItem == null) {
                            cc = content;
                        } else {
                            cc = shareTools.getWeiBoContent(articleItem.getTitle(), articleItem.getDesc(), articleItem.getWeburl());
                        }

                        shareTools.shareWithSina(cc);
                        break;
                    case 5:

                        if (mContext instanceof Activity) {
                            if (articleItem == null)
                                shareTools.shareByQQ((Activity) mContext, content);
                            else shareTools.shareByQQ((Activity) mContext, articleItem, isShareApp);
                        }


                        break;
                }
            }
        });
    }

    /**
     * 下载分享图片
     *
     * @param sharePic
     */
    private void prepareShare(String sharePic) {
        if (!TextUtils.isEmpty(sharePic)) shareTools.prepareShareAfterFetchBitmap(sharePic);
    }

    private String getShareUrl(ArticleItem a) {
        String picUrl = "";
        if (ParseUtil.listNotNull(a.getPicList()) && !TextUtils.isEmpty(a.getPicList().get(0).getUrl())) {
            picUrl = a.getPicList().get(0).getUrl();
        }
        return picUrl;
    }

    /**
     * 分享文章
     *
     * @param context
     * @param calendarModel
     */
    public SharePopWindow(final Context context, final ArticleItem calendarModel) {
        mContext = context;
        this.articleItem = calendarModel;
        init();
        prepareShare(getShareUrl(calendarModel));

        conentView.findViewById(R.id.share_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 1;
                window.dismiss();
            }
        });
        // 微信分享
        conentView.findViewById(R.id.share_weixin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 2;
                window.dismiss();
            }
        });
        conentView.findViewById(R.id.share_pengyouquan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 3;
                window.dismiss();
            }
        });
        // 微博分享
        conentView.findViewById(R.id.share_sina).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 4;
                window.dismiss();
            }
        });
        //qq分享
        conentView.findViewById(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 5;
                window.dismiss();
            }
        });
    }


    /**
     * 分享文章
     *
     * @param context
     */
    public SharePopWindow(final Context context, final String content) {
        mContext = context;
        this.content = content;
        init();

        conentView.findViewById(R.id.share_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 1;
                window.dismiss();
            }
        });
        // 微信分享
        conentView.findViewById(R.id.share_weixin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 2;
                window.dismiss();
            }
        });
        conentView.findViewById(R.id.share_pengyouquan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 3;
                window.dismiss();
            }
        });
        // 微博分享
        conentView.findViewById(R.id.share_sina).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 4;
                window.dismiss();
            }
        });
        //qq分享
        conentView.findViewById(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 5;
                window.dismiss();
            }
        });
    }

    public void setIsShareApp(boolean isShareApp) {
        this.isShareApp = isShareApp;
    }


    public void showViewFromBo(View v) {
        if (this.isShowing()) {
            dismiss();
        } else {

            this.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * 显示 隐藏
     */
    public void showView(View v) {
        if (this.isShowing()) {
            dismiss();
        } else {

            this.showAsDropDown(v);
        }
    }
}