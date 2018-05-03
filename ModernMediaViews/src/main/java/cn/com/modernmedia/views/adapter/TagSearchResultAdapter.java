package cn.com.modernmedia.views.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.adapter.ViewHolder;

/**
 * @author: zhufei
 */
public class TagSearchResultAdapter extends ArrayAdapter<ArticleItem> {
    private Context mContext;

    public TagSearchResultAdapter(Context context) {
        super(context, 0);
        this.mContext = context;
    }

    public void setData(List<ArticleItem> list) {
        clear();
        for (ArticleItem item : list) {
            add(item);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, R.layout.search_result_item);
        ImageView imageView = holder.getView(R.id.result_img);
        TextView title = holder.getView(R.id.result_title);
        TextView desc = holder.getView(R.id.result_title_desc);

        ArticleItem articleItem = getItem(position);
        V.setImage(imageView, "placeholder");
        if (articleItem.getThumbList().size() > 0) {
            SlateApplication.finalBitmap.display(imageView, articleItem.getThumbList().get(0).getUrl());
        }
        title.setText(articleItem.getTitle());
        desc.setText(articleItem.getDesc());

        return holder.getConvertView();
    }
}
