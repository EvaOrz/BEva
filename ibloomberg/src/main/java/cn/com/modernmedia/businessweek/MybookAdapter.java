package cn.com.modernmedia.businessweek;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ShangchengIndex.ShangchengIndexItem;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediausermodel.vip.VipProductPayActivity;


/**
 * Created by Eva. on 2018/1/9.
 */

public class MybookAdapter extends BaseAdapter {

    private Context context;
    private List<ShangchengIndexItem> list = new ArrayList<>();
    private LayoutInflater mInflater;

    public MybookAdapter(Context context, List<ShangchengIndexItem> list) {
        this.context = context;
        this.list = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final ShangchengIndexItem shangchengIndexItem = list.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_my_book, null);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.item_book_img);
            holder.date = convertView.findViewById(R.id.item_book_date);
            holder.title = convertView.findViewById(R.id.item_book_title);
            holder.gotoY = convertView.findViewById(R.id.item_book_goto);
            holder.xuding = convertView.findViewById(R.id.item_book_xuding);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        V.setImage(holder.icon, shangchengIndexItem.getIcon());
        holder.title.setText(shangchengIndexItem.getName());
        if (shangchengIndexItem.getEndTime() < System.currentTimeMillis() / 1000) {
            holder.date.setText("有效期至：" + DateFormatTool.getStringToDate(shangchengIndexItem.getEndTime()) + "（已到期）");
            holder.xuding.setVisibility(View.VISIBLE);
        } else {
            holder.date.setText("有效期至：" + DateFormatTool.getStringToDate(shangchengIndexItem.getEndTime()));
            holder.xuding.setVisibility(View.GONE);
        }
        holder.gotoY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(shangchengIndexItem.getProtocolList())) {
//                    UriParse.clickSlate(context, shangchengIndexItem.getProtocolList(), new Entry[]{new ArticleItem()}, null);
                    UriParse.clickSlate(context, shangchengIndexItem.getProtocolList(), new Entry[]{shangchengIndexItem}, null);

                }
            }
        });
        holder.xuding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VipProductPayActivity.class);
                intent.putExtra("pay_pid", shangchengIndexItem.getGoodId());
                context.startActivity(intent);
            }
        });
        return convertView;
    }


    public static class ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView gotoY;
        public TextView xuding;
        public TextView date;
    }


}
