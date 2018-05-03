package cn.com.modernmedia.businessweek.jingxuan.zhuantipian;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.jingxuan.ShangchengListActivity;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.widget.FullVideoView;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.jzvd.JZVideoPlayer;

/**
 * jiaozi 列表播放视频
 * Created by Eva. on 2017/9/26.
 */

public class ZhuantiPianAdapter extends BaseAdapter {


    private Context context;
    private List<ArticleItem> articleItems;

    public ZhuantiPianAdapter(Context context, List<ArticleItem> articleItems) {
        this.context = context;
        this.articleItems = articleItems;
    }

    @Override
    public int getCount() {
        return articleItems.size();
    }

    @Override
    public Object getItem(int position) {
        return articleItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        // setdata
        final ArticleItem a = articleItems.get(position);
        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.item_shang_jilu, null);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.jzVideoPlayer = convertView.findViewById(R.id.shang_jilu_video);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(CommonApplication.width, (CommonApplication.width) * 9 / 16);
        viewHolder.jzVideoPlayer.setLayoutParams(ll);
        viewHolder.title = (TextView) convertView.findViewById(R.id.shang_jilu_title);
        viewHolder.desc = (TextView) convertView.findViewById(R.id.shang_jilu_desc);

        // 免费且无权限  显示试看
        if (a.getProperty().getLevel() == 0 && !SlateDataHelper.getLevelByType(context, 5)) {
            Drawable bb = context.getResources().getDrawable(R.drawable.shikan);
            bb.setBounds(0, 0, 80, 40);
            ImageSpan imgSpan = new ImageSpan(bb);
            SpannableString spanString = new SpannableString("  ssssssssss  ");
            spanString.setSpan(imgSpan, 2, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.title.setText(spanString);
            viewHolder.title.append(a.getTitle());
        } else {
            viewHolder.title.setText(a.getTitle());
        }
        viewHolder.desc.setText(a.getDesc());
        if (ParseUtil.listNotNull(a.getPicList()) && !TextUtils.isEmpty(a.getPicList().get(0).getVideolink())) {
            viewHolder.jzVideoPlayer.setData(a, a.getProperty().getLevel(), JZVideoPlayer.SCREEN_WINDOW_LIST, new FullVideoView.OnVideoStartListener() {
                @Override
                public void onHasLevel(boolean hasLevel) {
                    if (!hasLevel) {
                        if (context instanceof ShangchengListActivity)
                            ((ShangchengListActivity) context).goInfo();
                    }
                }
            });
        }

        return convertView;
    }

    class ViewHolder {
        public TextView title;
        public TextView desc;
        public TextView date;
        public FullVideoView jzVideoPlayer;
    }
}