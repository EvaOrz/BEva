package cn.com.modernmedia.businessweek.jingxuan.dubao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * Created by Eva. on 2017/8/23.
 */

public class DubaoAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<ArticleItem> articleItems;

    public DubaoAdapter(Context context, List<ArticleItem> articleItems) {
        this.context = context;
        this.articleItems = articleItems;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return articleItems.size();
    }

    @Override
    public Entry getItem(int position) {

        return articleItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        final int pos = position;
        // setdata
        final ArticleItem a = articleItems.get(pos);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_shang_dubao, null);
            holder.date = (TextView) convertView.findViewById(R.id.shang_jilu_date);
            holder.title = (TextView) convertView.findViewById(R.id.shang_jilu_title);
            holder.desc = (TextView) convertView.findViewById(R.id.shang_jilu_desc);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setTag(holder);

        holder.desc.setText(a.getDesc());
        if (a.getProperty().getLevel() == 0 && !SlateDataHelper.getLevelByType(context, 4)) {
            Drawable bb = context.getResources().getDrawable(R.drawable.shidu);
            bb.setBounds(0, 0, 80, 40);
            ImageSpan imgSpan = new ImageSpan(bb);
            SpannableString spanString = new SpannableString("  ssssssssss  ");
            spanString.setSpan(imgSpan, 2, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.title.setText(spanString);
            holder.title.append(a.getTitle());
        } else holder.title.setText(a.getTitle());

        Drawable aaa = context.getResources().getDrawable(R.drawable.shidu_dot);
        aaa.setBounds(0, 0, 16, 16);
        holder.date.setCompoundDrawables(aaa, null, null, null);
        holder.date.setCompoundDrawablePadding(20);
        holder.date.setText(DateFormatTool.format(a.getInputtime() * 1000, "yyyy-MM-dd " + "hh:mm"));

        return convertView;
    }

    public static class ViewHolder {
        public TextView title;
        public TextView desc;
        public TextView date;

    }

}
