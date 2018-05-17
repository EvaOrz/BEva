package cn.com.modernmedia.businessweek.jingxuan.kecheng;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * jiaozi 列表播放视频
 * Created by Eva. on 2017/9/26.
 */

public class KeChengAdapter extends BaseAdapter {


    private Context context;
    private List<ArticleItem> articleItems;

    public KeChengAdapter(Context context, List<ArticleItem> articleItems) {
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
            convertView = mInflater.inflate(R.layout.item_shang_kecheng, null);
            viewHolder.img = convertView.findViewById(R.id.shang_kecheng_img);
            viewHolder.title = convertView.findViewById(R.id.shang_kecheng_title);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setTag(viewHolder);

        viewHolder.img.setImageResource(R.drawable.placeholder);

        if (a.getThumbList() != null && a.getThumbList().size() > 0){
            viewHolder.img.setTag(a.getThumbList().get(0).getUrl());
            viewHolder.img.setId(a.getArticleId());
            CommonApplication.finalBitmap.display(viewHolder.img,(String) viewHolder.img.getTag());
        } else if (a.getPicList() != null && a.getPicList().size() > 0) {
            viewHolder.img.setTag(a.getPicList().get(0).getUrl());
            viewHolder.img.setId(a.getArticleId());
            CommonApplication.finalBitmap.display(viewHolder.img, (String) viewHolder.img.getTag());
        }
        // 免费且无权限  显示试读
        if (a.getProperty().getLevel() == 0 && !SlateDataHelper.getLevelByType(context, 7)) {
            Drawable bb = context.getResources().getDrawable(R.drawable.shidu);
            bb.setBounds(0, 0, 80, 40);
            ImageSpan imgSpan = new ImageSpan(bb);
            SpannableString spanString = new SpannableString("  ssssssssss  ");
            spanString.setSpan(imgSpan, 2, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.title.setText(spanString);
            viewHolder.title.append(a.getTitle());
        } else viewHolder.title.setText(a.getTitle());

        return convertView;
    }

    class ViewHolder {
        public TextView title;
        public ImageView img;
    }
}