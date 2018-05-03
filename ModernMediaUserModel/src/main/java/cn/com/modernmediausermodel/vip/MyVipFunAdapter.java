package cn.com.modernmediausermodel.vip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import java.util.List;

import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediausermodel.R;

/**
 * 我的VIP 特权adapter
 *
 * @author: zhufei
 */

public class MyVipFunAdapter extends BaseAdapter {
    private Context mContext;
    private List<VipGoodList.Fun> mVipFun;
    private User user;

    public MyVipFunAdapter(Context context) {
        this.mContext = context;

    }

    public void setFunList(List<VipGoodList.Fun> list, User user) {
        if (list == null || list.size() == 0) return;
        this.mVipFun = list;
        this.user = user;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mVipFun == null || mVipFun.size() == 0) {
            return 0;
        } else if (mVipFun.size() >= 4) {
            return 4;
        } else return mVipFun.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.myvip_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.myvip_title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.myvip_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mVipFun.get(position).getFunName());
        //线上icon
        if (user.getUser_status() == 4) {//过期
            if (!mVipFun.get(position).getVipIcon().getStyle1_normal().isEmpty()) {
                FinalBitmap.create(mContext).display(holder.imageView, mVipFun.get(position).getVipIcon().getStyle1_normal());
            } else VipIconImg.setOutImg(holder.imageView, mVipFun.get(position).getFunId());
        } else {
            if (!mVipFun.get(position).getVipIcon().getNormal().isEmpty()) {

                FinalBitmap.create(mContext).display(holder.imageView, mVipFun.get(position).getVipIcon().getNormal());
            } else VipIconImg.setImg(holder.imageView, mVipFun.get(position).getFunId());
        }
        return convertView;

    }

    public static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }
}