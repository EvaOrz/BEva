package cn.com.modernmedia.views.index.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmediaslate.adapter.ViewHolder;
import cn.com.modernmediaslate.unit.ParseUtil;


/**
 * 普通列表形式的listview适配器
 *
 * @author zhuqiao
 */
public class ListIndexAdapter extends BaseIndexAdapter {

    public ListIndexAdapter(Context context, Template template, ArticleType articleType) {
        super(context, template);
        this.articleType = articleType;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getPosition().getStyle();
    }

    @Override
    public int getViewTypeCount() {
        int size = template.getList().getMap().size();
        if (size == 0) {
            return 1;
        }
        if (template.getList().getShow_marquee() == 1) {
            // 添加跑马灯
            size++;
        }
        // 3.4 要求自定义模板从100开始计数,目前最大编号103
        return 104;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertViewIsNull = convertView == null;
        final ArticleItem item = getItem(position);
        int style = getItemViewType(position);
        if (style == 102) {
            ViewHolder holder = ViewHolder.get(mContext, convertView, R.layout.item_marquee);
            final TextView marqueeText = holder.getView(R.id.marquee_desc);
            marqueeText.setText(item.getOutline());
            marqueeText.post(new Runnable() {

                @Override
                public void run() {
                    marqueeText.setSelected(true);
                    marqueeText.requestFocus();
                }
            });
            holder.getConvertView().setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((CommonMainActivity) mContext).gotoMarquee();
                }
            });
            return holder.getConvertView();
            //            return new View(mContext);
        } else {
            IndexViewHolder holder = IndexViewHolder.get(mContext, convertView, getData(style), template.getHost());
            holder.setData(item, position, this, articleType);
            if ((style >= 1 && style <= 9) || style == 101) {
                // 正常文章或者广告item
                holder.getConvertView().setTag(R.id.adapter_article, item);
            }
            return holder.getConvertView();
        }
    }

    /**
     * 获取type对应的layout
     *
     * @param type
     * @return
     */
    private String getData(int type) {
        String data = "";
        if (ParseUtil.mapContainsKey(template.getList().getMap(), type)) {
            data = template.getList().getMap().get(type);
        }
        if (TextUtils.isEmpty(data)) {
            if (type >= 1 && type <= 9) {
                data = template.getList().getDefault_data();
            }
        }
        return data;
    }
}
