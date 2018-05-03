package cn.com.modernmedia.businessweek.jingxuan;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;


public class ShangHeadAdapter extends PagerAdapter {
    protected List<AdvItem> list = new ArrayList<AdvItem>();
    protected Context mContext;

    public ShangHeadAdapter(Context context, List<AdvItem> list) {
        this.mContext = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final AdvItem advItem = list.get(position);
        ImageView view = new ImageView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(CommonApplication.width, CommonApplication.width / 2);
        view.setLayoutParams(params);
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        if (advItem != null && ParseUtil.listNotNull(advItem.getSourceList()) && !TextUtils.isEmpty(advItem.getSourceList().get(0).getUrl())) {
            MyApplication.finalBitmap.display(view, advItem.getSourceList().get(0).getUrl());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UriParse.clickSlate(mContext, advItem.getSourceList().get(0).getLink(), new Entry[]{new ArticleItem()}, null, new Class<?>[0]);
                }
            });
        }

        container.addView(view, params);
        return view;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
    }

}