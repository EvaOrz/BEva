package cn.com.modernmediausermodel.vip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediausermodel.R;

/**
 * Vip 套餐简介adapter
 *
 * @author: zhufei
 */
public class VipProductAdapter extends BaseAdapter {
    public static final String JOIN_KEY_ONLINE = "P9If6T26n6K6RRHfK37Zh7WLcHhkVsuN";
    private Context mContext;
    private VipGoodList.VipGood vipGood;
    private LayoutInflater mInflater;
    private User mUser;
    private int flag = 0;

    public VipProductAdapter(Context context, int flag) {
        this.mContext = context;
        this.flag = flag;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setVipGood(VipGoodList.VipGood vipGood, User user) {
        if (vipGood == null) return;
        this.vipGood = vipGood;
        this.mUser = user;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (vipGood == null || vipGood.getFunList().size() == 0) {
            return 0;
        } else return vipGood.getFunList().size();
    }

    @Override
    public VipGoodList.Fun getItem(int position) {
        return vipGood.getFunList().get(position);
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
            convertView = mInflater.inflate(R.layout.vipup_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.vipup_title);
            holder.desc = (TextView) convertView.findViewById(R.id.vipup_content);
            holder.link = (TextView) convertView.findViewById(R.id.vipup_link);
            holder.imageView = (ImageView) convertView.findViewById(R.id.vipup_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(vipGood.getFunList().get(position).getFunName());
        holder.desc.setText(vipGood.getFunList().get(position).getDesc());
        if (mUser.getLevel() == 2 && mUser.getVip_end_time() > System.currentTimeMillis() / 1000 && vipGood.getFunList().get(position).getFunId().equals("vip_interaction")) {
            holder.link.setText(mContext.getString(R.string.vip_joingroup));
            holder.link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    joinQQGroupOnLine(JOIN_KEY_ONLINE);
                }
            });
        }
        //线上icon
        if (flag != 10 || (mUser.getLevel() == 2 && mUser.getVip_end_time() > System.currentTimeMillis() / 1000)) {//没过期
            if (!TextUtils.isEmpty(vipGood.getFunList().get(position).getVipIcon().getNormal())) {
                FinalBitmap.create(mContext).display(holder.imageView, vipGood.getFunList().get(position).getVipIcon().getNormal());
            } else
                VipIconImg.setImg(holder.imageView, vipGood.getFunList().get(position).getFunId());
        } else {
            if (!TextUtils.isEmpty(vipGood.getFunList().get(position).getVipIcon().getStyle1_normal())) {
                FinalBitmap.create(mContext).display(holder.imageView, vipGood.getFunList().get(position).getVipIcon().getStyle1_normal());
            } else
                VipIconImg.setOutImg(holder.imageView, vipGood.getFunList().get(position).getFunId());

        }
        return convertView;
    }

    /****************
     * 发起添加群流程。群号：彭博商业周刊VIP读者群(603336514) 的 key 为： P9If6T26n6K6RRHfK37Zh7WLcHhkVsuN
     * 调用 joinQQGroup(P9If6T26n6K6RRHfK37Zh7WLcHhkVsuN) 即可发起手Q客户端申请加群 彭博商业周刊VIP读者群(603336514)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroupOnLine(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            mContext.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public static class ViewHolder {
        public ImageView imageView;
        public TextView textView, desc, link;

    }
}
