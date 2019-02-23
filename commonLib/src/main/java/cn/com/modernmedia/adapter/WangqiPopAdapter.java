package cn.com.modernmedia.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmediaslate.model.Entry;

/**
 * Created by Eva. on 16/12/30.
 */

public class WangqiPopAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<ArticleItem> datas;
    private int currentPos = 0;

    public WangqiPopAdapter(Context context, List<ArticleItem> datas) {
        this.context = context;
        this.datas = datas;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Entry getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return datas.get(position).getArticleId();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        final ArticleItem a = datas.get(position);

        convertView = mInflater.inflate(R.layout.item_wangqi_popmenu, null);
        holder = new ViewHolder();
        holder.dot = (ImageView) convertView.findViewById(R.id.img_popmenu);
        holder.title = (TextView) convertView.findViewById(R.id.title_popmenu);

        holder.title.setText(a.getTitle());

        if (a.getArticleId() == datas.get(currentPos).getArticleId()) {
            holder.dot.setImageResource(R.drawable.menu_dot_active);
            holder.title.setTextColor(context.getResources().getColor(R.color.vip_mine_pay));
        } else {
            holder.dot.setImageResource(R.drawable.menu_dot);
            holder.title.setTextColor(context.getResources().getColor(R.color.black_bg));
        }
        convertView.setTag(holder);
        return convertView;
    }

    public static class ViewHolder {
        public TextView title;
        public ImageView dot;

    }

    public void setSelection(int position) {
        currentPos = position - 1;
        //        if (currentPos != -1) notifyDataSetInvalidated();
    }

}
