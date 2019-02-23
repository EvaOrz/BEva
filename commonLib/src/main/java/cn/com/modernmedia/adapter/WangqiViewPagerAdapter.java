package cn.com.modernmedia.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.widget.ArticleDetailItem;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 循环viewpager的适配器
 *
 * @author user
 */
public class WangqiViewPagerAdapter extends PagerAdapter {
    protected List<ArticleItem> list = new ArrayList<ArticleItem>();
    protected Context mContext;

    public WangqiViewPagerAdapter(Context context, List<ArticleItem> list) {
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
        CommonApplication.callGc();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View view = fetchView(list.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
    }

    /**
     * 获取view.默认只有只有imageview，如果是特殊view,请重载此方法
     *
     * @param detail
     * @return
     */
    public View fetchView(final ArticleItem detail) {
        View view = null;
        view = new ArticleDetailItem(mContext, false) {

            @Override
            public void setBackGroundRes(ImageView imageView) {
                //                V.setImage(imageView, "head_placeholder");
            }

            @Override
            public void doScroll(int l, int t, int oldl, int oldt) {
            }

            @Override
            public void showGallery(List<String> urlList, String currentUrl, List<String> descList) {
                if (!ParseUtil.listNotNull(urlList) || CommonApplication.articleGalleryCls == null)
                    return;
                Intent intent = new Intent(mContext, CommonApplication.articleGalleryCls);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("URL_LIST", (ArrayList<String>) urlList);
                bundle.putString("TITLE", detail.getTitle() == null ? "" : detail.getTitle());
                bundle.putStringArrayList("DESC", (ArrayList<String>) descList);
                int index = 0;
                String[] current = currentUrl.split("\\.");
                if (current != null && current.length > 0) {
                    String splitUrl = currentUrl.substring(current[0].length());
                    for (int i = 0; i < urlList.size(); i++) {
                        if (urlList.get(i).endsWith(splitUrl)) {
                            index = i;
                            break;
                        }
                    }
                }
                bundle.putInt("INDEX", index);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }

        };
        ((ArticleDetailItem) view).setData(detail);
        ((ArticleDetailItem) view).changeFont();

        //        }
        return view;
    }
}
