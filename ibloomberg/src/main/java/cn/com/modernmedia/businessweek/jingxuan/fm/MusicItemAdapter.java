package cn.com.modernmedia.businessweek.jingxuan.fm;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 电台页面adapter
 *
 * @author lusiyuan
 */
public class MusicItemAdapter extends BaseAdapter {

    private Context context;
    private List<PlayIngStatusModel> list;
    private LayoutInflater mInflater;
    private int showCount = 1; // 0: 显示 1：不显示

    public MusicItemAdapter(Context context, List<PlayIngStatusModel> list, int showCount) {
        this.context = context;
        this.list = list;
        this.showCount = showCount;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void setList(List<PlayIngStatusModel> list, int showCount) {
        this.list = list;
        this.showCount = showCount;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ArticleItem getItem(int position) {
        return list.get(position).getArticleItem();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final ArticleItem articleData = list.get(position).getArticleItem();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_music, null);
            holder = new ViewHolder();
            holder.cover = (ImageView) convertView.findViewById(R.id.music_item_cover);
            holder.title = (TextView) convertView.findViewById(R.id.music_item_title);
            holder.date = (TextView) convertView.findViewById(R.id.music_item_date);
            holder.red = convertView.findViewById(R.id.music_item_red);
            //            holder.playing = (GifView) convertView.findViewById(R.id.music_item_playing);
            holder.time = (TextView) convertView.findViewById(R.id.music_item_time);
            holder.duration = (TextView) convertView.findViewById(R.id.music_item_duration);
            holder.timeImg = (ImageView) convertView.findViewById(R.id.music_item_timeimg);
            holder.payStatus = (ImageView) convertView.findViewById(R.id.music_item_pay);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /**
         * 设置数据
         */
        if (articleData.getThumbList() != null && articleData.getThumbList().size() > 0)
            setCover(articleData.getThumbList().get(0).getUrl(), holder.cover);
        if (!TextUtils.isEmpty(articleData.getTitle()))
            holder.title.setText(articleData.getTitle());

        if (showCount == 1) {// 显示播放次数
            holder.timeImg.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
        } else {
            holder.timeImg.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
        }
        if (ParseUtil.listNotNull(articleData.getAudioList())) {
            holder.duration.setText(DateFormatTool.getTime(Long.valueOf(articleData.getAudioList().get(0).getDuration()) * 1000));
            holder.time.setText(articleData.getAudioList().get(0).getCount() + "次");
        }
        if (SlateDataHelper.getLevelByType(context, 3)) {
            holder.payStatus.setVisibility(View.GONE);
        } else {
            holder.payStatus.setVisibility(View.VISIBLE);
            if (articleData.getProperty().getLevel() > 0) {
                holder.payStatus.setImageResource(R.drawable.fm_suo);
            } else {
                holder.payStatus.setImageResource(R.drawable.shiting_1);
            }

        }

        if (list.get(position).getIsPlaying() == 0) {// 未播放
            //            TextPaint tp = holder.title.getPaint();
            //            tp.setFakeBoldText(false);
            holder.title.setTextColor(context.getResources().getColor(R.color.black_bg));
            holder.red.setVisibility(View.INVISIBLE);
            //            holder.playing.setVisibility(View.INVISIBLE);
        } else {// 正在播放
            //            TextPaint tp = holder.title.getPaint();
            //            tp.setFakeBoldText(true);
            holder.title.setTextColor(context.getResources().getColor(R.color.red));
            holder.red.setVisibility(View.VISIBLE);
            //            holder.playing.setVisibility(View.VISIBLE);
            //            holder.playing.setMovieResource(R.raw.music_playing);
        }
        holder.date.setText(DateFormatTool.format(articleData.getInputtime() * 1000, "yyyy.MM.dd"));
        // 设置暂停
        // gif2.setPaused(true);
        return convertView;
    }

    private void setCover(String url, final ImageView cover) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        UserTools.setAvatar(context, url, cover);
    }

    public static class ViewHolder {
        public ImageView cover;// 封面图
        public TextView title;
        public TextView duration;// 音频时长
        public TextView date;// 投放时间
        public TextView time;// 播放次数
        public View red;
        //        public GifView playing;
        public ImageView timeImg;
        public ImageView payStatus;// 试听
    }



}
