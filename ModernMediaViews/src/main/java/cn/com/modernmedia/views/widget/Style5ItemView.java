package cn.com.modernmedia.views.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.ArticleActivity;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * style = 5，首页栏目模板
 * Created by Eva. on 16/9/23.
 */
public class Style5ItemView extends RelativeLayout {
    private Context mContext;
    private TextView tagname;
    private HorizontalScrollView horizontalScrollView;
    private View view;

    public Style5ItemView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }


    public Style5ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        view = LayoutInflater.from(mContext).inflate(R.layout.view_style5, null);
        addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null && v.getTag() instanceof ArticleItem)
                    V.clickSlate(mContext, (ArticleItem) v.getTag(), CommonArticleActivity.ArticleType.Default);
            }
        });
        tagname = (TextView) view.findViewById(R.id.style5_tagname);
        horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.style5_horizantal_scrollview);
    }

    public void setData(final ArticleItem articleItem) {
        List<ArticleItem> aItems = articleItem.getSubArticleList();
        tagname.setText(articleItem.getGroupdisplayname());
        view.setTag(articleItem);
        horizontalScrollView.removeAllViews();
        LinearLayout layout = new LinearLayout(mContext);
        for (final ArticleItem item : aItems) {
            LinearLayout itemView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_style5, null);
            ImageView img = (ImageView) itemView.findViewById(R.id.style5_image);
            TextView title = (TextView) itemView.findViewById(R.id.style5_title);
            TextView time = (TextView) itemView.findViewById(R.id.style5_time);
            if (ParseUtil.listNotNull(item.getPicList())) {
                CommonApplication.finalBitmap.display(img, item.getPicList().get(0).getUrl());
            } else V.setImage(img, "placeholder");
            title.setText(item.getTitle());
            time.setText(DateFormatTool.getStandardDate(item.getInputtime()));
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("首页栏目点击", item.getTitle());
                    V.clickSlate(mContext, item, CommonArticleActivity.ArticleType.Default, ArticleActivity.class);
                }
            });
            layout.addView(itemView);
        }
        horizontalScrollView.addView(layout);
    }
}
