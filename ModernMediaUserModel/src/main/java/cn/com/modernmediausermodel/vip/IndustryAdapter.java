package cn.com.modernmediausermodel.vip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.modernmediausermodel.R;

/**
 * @author: zhufei
 */
public class IndustryAdapter extends BaseAdapter {
    private Context mContext;
    private VipInfo.VipCategory industry;
    private LayoutInflater mInflater;
    private Intent intent;

    public IndustryAdapter(Context context, VipInfo.VipCategory list) {
        this.mContext = context;
        this.industry = list;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return industry.firstCategoryList.size();
    }

    @Override
    public VipInfo.VipCategory getItem(int position) {
        return industry.firstCategoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.industry_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.industry_item_text);
            holder.img = (ImageView) convertView.findViewById(R.id.industry_item_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(industry.firstCategoryList.get(position).category);
//        final ViewHolder finalHolder = holder;
//        holder.textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finalHolder.img.setVisibility(View.VISIBLE);
//                intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString("industry", industry.firstCategoryList.get(position).category);
//                intent.putExtras(bundle);
//
//            }
//        });


        return convertView;
    }

    public static class ViewHolder {
        public ImageView img;
        public TextView textView;
    }
}