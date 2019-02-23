package cn.com.modernmediausermodel.vip;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import java.util.List;

import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmediaslate.unit.ImageScaleType;
import cn.com.modernmediausermodel.R;

/**
 * 套餐功能adapter
 *
 * @author: zhufei
 */

public class VipFunAdapter extends BaseAdapter {
    private Context mContext;
    private List<VipGoodList.Fun> mVipFun;

    VipFunAdapter(Context context) {
        this.mContext = context;

    }

    public void setFunList(List<VipGoodList.Fun> list) {
        if (list == null || list.size() == 0) return;
        this.mVipFun = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mVipFun == null || mVipFun.size() == 0) {
            return 0;
        } else if (mVipFun.size() >= 5) {
            return 6;
        } else return mVipFun.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return mVipFun.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.tabs, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.tab_title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.tab_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mVipFun.size() >= 5) {
            if (position < 5) {
                holder.textView.setText(mVipFun.get(position).getFunName());

                if (!TextUtils.isEmpty(mVipFun.get(position).getVipIcon().getNormal())) {
                    holder.imageView.setTag(R.id.scale_type, ImageScaleType.CENTER);
                    FinalBitmap.create(mContext).display(holder.imageView, mVipFun.get(position).getVipIcon().getNormal());
                } else VipIconImg.setImg(holder.imageView, mVipFun.get(position).getFunId());
            } else if (position == 5) {
                setClickMore(holder);
            }
        } else {
            if (position < mVipFun.size()) {
                holder.textView.setText(mVipFun.get(position).getFunName());

                if (!TextUtils.isEmpty(mVipFun.get(position).getVipIcon().getNormal())) {
                    FinalBitmap.create(mContext).display(holder.imageView, mVipFun.get(position).getVipIcon().getNormal());
                } else VipIconImg.setImg(holder.imageView, mVipFun.get(position).getFunId());
            } else {
                setClickMore(holder);
            }
        }
        return convertView;
    }

    //点击查看更多
    private void setClickMore(ViewHolder holder) {
        holder.textView.setText(mContext.getString(R.string.vip_more));
        holder.imageView.setImageResource(R.drawable.more);
    }

    public static class ViewHolder {
        public ImageView imageView;
        public TextView textView;

    }
}
