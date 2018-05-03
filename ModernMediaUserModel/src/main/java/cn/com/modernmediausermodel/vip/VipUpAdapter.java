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
import cn.com.modernmediausermodel.R;

/**
 * vip升级 adapter
 *
 * @author: zhufei
 */

public class VipUpAdapter extends BaseAdapter {
    private Context mContext;
    private List<VipGoodList.Fun> mVipFun;

    VipUpAdapter(Context context) {
        this.mContext = context;
    }

    public void setFunList(List<VipGoodList.Fun> list) {
        if (list == null || list.size() == 0)
            return;
        mVipFun = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mVipFun == null || mVipFun.size() == 0) {
            return 0;
        } else if (mVipFun.size() > 4) {
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.vipup_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.vipup_title);
            holder.content = (TextView) convertView.findViewById(R.id.vipup_content);
            holder.imageView = (ImageView) convertView.findViewById(R.id.vipup_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageView.setBackgroundResource(R.drawable.vip_circle);
        holder.title.setText(mVipFun.get(position).getFunName());
        holder.content.setText(mVipFun.get(position).getDesc());
        //线上icon
        if (!TextUtils.isEmpty(mVipFun.get(position).getVipIcon().getNormal())) {
            FinalBitmap.create(mContext).display(holder.imageView, mVipFun.get(position).getVipIcon().getNormal());
        } else VipIconImg.setImg(holder.imageView, mVipFun.get(position).getFunId());

        return convertView;
    }

    public static class ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView content;

    }
}
