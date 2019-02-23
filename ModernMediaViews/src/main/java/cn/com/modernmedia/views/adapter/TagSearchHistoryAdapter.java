package cn.com.modernmedia.views.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.views.R;
import cn.com.modernmediaslate.adapter.ViewHolder;


/**
 * @author: zhufei
 */
public class TagSearchHistoryAdapter extends ArrayAdapter<String> {
    private Context mContext;

    public TagSearchHistoryAdapter(Context context) {
        super(context, 0);
        this.mContext = context;
    }

    public void setData(List<String> list) {
        clear();
        for (String history : list) {
            add(history);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, R.layout.search_history_item);
        TextView content = holder.getView(R.id.search_history_item_text);
        String tagSearchHistory = getItem(position);
        content.setText(tagSearchHistory);
        return holder.getConvertView();
    }
}
