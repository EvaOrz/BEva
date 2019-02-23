package cn.com.modernmedia.businessweek.jingxuan.pdf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.businessweek.PDFActivity;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.jingxuan.ShangchengListActivity;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.widget.RoundAngleImageView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;

/**
 * Created by Eva. on 16/12/22.
 */

public class ZhuantiAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<TagInfoList.TagInfo> datas;
    private List<ArticleItem> articleItems;
    private boolean isZhuanti = true;

    public ZhuantiAdapter(Context context, List<TagInfoList.TagInfo> datas, List<ArticleItem> articleItems, boolean isZhuanti) {
        this.context = context;
        this.datas = datas;
        this.articleItems = articleItems;
        this.isZhuanti = isZhuanti;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        if (isZhuanti) return datas.size();
        else return articleItems.size();
    }

    @Override
    public Entry getItem(int position) {

        if (isZhuanti) return datas.get(position);
        else return articleItems.get(position);
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

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_zhuanti_zhuankan, null);
            holder = new ViewHolder();
            holder.zhuanti_cover = (ImageView) convertView.findViewById(R.id.zhuanti_cover);
            holder.zhuankan_cover = (RoundAngleImageView) convertView.findViewById(R.id.zhuankan_cover);
            holder.title = (TextView) convertView.findViewById(R.id.zhuanti_title);
            holder.desc = (TextView) convertView.findViewById(R.id.zhuanti_desc);
            holder.cover_bg = (ImageView) convertView.findViewById(R.id.zhuanti_cover_bg);
            holder.date = (TextView) convertView.findViewById(R.id.zhuanti_date);
            holder.download = convertView.findViewById(R.id.zhuanti_down);
            holder.progress = convertView.findViewById(R.id.zhuanti_progress);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 专题item setdata
        if (isZhuanti) {
            final TagInfoList.TagInfo t = datas.get(pos);
            if (t.getColumnProperty().getBigPicture() != null && t.getColumnProperty().getBigPicture().size() > 0) {
                holder.zhuanti_cover.setTag(t.getColumnProperty().getBigPicture().get(0));
                CommonApplication.finalBitmap.display(holder.zhuanti_cover, (String) holder.zhuanti_cover.getTag());
            }
            holder.cover_bg.setVisibility(View.GONE);
            holder.zhuankan_cover.setVisibility(View.GONE);
            holder.title.setText(t.getColumnProperty().getName());
            holder.desc.setText(t.getDesc());
            holder.desc.setLines(5);
            holder.date.setVisibility(View.GONE);
            holder.download.setVisibility(View.GONE);
            holder.progress.setVisibility(View.GONE);
        } else {// 专刊Item setdata
            final ArticleItem a = articleItems.get(pos);
            if (a.getPicList() != null && a.getPicList().size() > 0) {

                holder.zhuankan_cover.setTag(a.getPicList().get(0).getUrl());
                CommonApplication.finalBitmap.display(holder.zhuankan_cover, (String) holder.zhuankan_cover.getTag());
            }
            holder.zhuanti_cover.setVisibility(View.GONE);
            holder.title.setText(a.getTitle());
            holder.desc.setText(a.getDesc());
            holder.date.setText(DateFormatTool.getStringToDate(a.getInputtime()));

            RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(60, 60);
            l.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            l.addRule(RelativeLayout.CENTER_VERTICAL);
            RelativeLayout.LayoutParams ll = new RelativeLayout.LayoutParams(75, 40);
            ll.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            ll.addRule(RelativeLayout.CENTER_VERTICAL);

            // 判断下载进度大于-1 ，因为进度监视太精确。 避免出现闪一下查看按钮
            if (a.getDownloadPercent() > -1 && a.getDownloadPercent() < 100) {
                holder.download.setVisibility(View.GONE);
                holder.progress.setVisibility(View.VISIBLE);
                holder.progress.setText(a.getDownloadPercent() + "%");
            } else {
                holder.download.setVisibility(View.VISIBLE);
                holder.progress.setVisibility(View.GONE);
                switch (getStatus(a)) {
                    case 0:// 购买
                        holder.download.setImageResource(R.drawable.goumai);
                        holder.download.setLayoutParams(ll);
                        break;
                    case 1:
                        holder.download.setImageResource(R.drawable.xiazai);
                        holder.download.setLayoutParams(l);
                        break;
                    case 2://查看
                        holder.download.setImageResource(R.drawable.chakan);
                        holder.download.setLayoutParams(l);
                        break;
                    case 3://试读
                        holder.download.setImageResource(R.drawable.shidu);
                        holder.download.setLayoutParams(ll);
                        break;
                }
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getStatus(a)) {
                        case 0:
                            if (context != null && context instanceof ShangchengListActivity) {
                                ((ShangchengListActivity) context).goInfo();
                            }
                            break;
                        case 1:
                            if (context != null && context instanceof ShangchengListActivity) {
                                ((ShangchengListActivity) context).downloadPdf(a);
                            }
                            Tools.showToast(context, "开始下载");
                            break;
                        case 2://查看
                            Intent i = new Intent(context, PDFActivity.class);
                            i.putExtra("pdf_article_item", a);
                            context.startActivity(i);
                            break;
                        case 3://试读
                            if (FileManager.ifHasPdfFilePath(a.getPageUrlList().get(0).getUrl())) {
                                Intent ii = new Intent(context, PDFActivity.class);
                                ii.putExtra("pdf_article_item", a);
                                context.startActivity(ii);

                            } else {
                                if (context != null && context instanceof ShangchengListActivity) {
                                    ((ShangchengListActivity) context).downloadPdf(a);
                                }
                                Tools.showToast(context, "开始缓存");
                            }

                            break;
                    }
                }
            });

        }
        convertView.setTag(holder);
        return convertView;
    }


    public static class ViewHolder {
        public TextView title;
        public TextView desc;
        public ImageView zhuanti_cover;
        public RoundAngleImageView zhuankan_cover;
        public ImageView cover_bg;
        public TextView date;
        public ImageView download;
        public TextView progress;
    }

    /**
     * 获取当前电子书的状态
     * 0 ：购买 1：下载 2：查看 3：试读
     *
     * @return
     */
    private int getStatus(ArticleItem articleItem) {
        if (articleItem.getProperty().getLevel() == 0) { // 免费
            if (FileManager.ifHasPdfFilePath(articleItem.getPageUrlList().get(0).getUrl())) {
                return 2;
            } else {
                return 1;
            }
        } else {
            if (SlateDataHelper.getLevelByType(context, 2)) {// 有看专刊权限
                if (FileManager.ifHasPdfFilePath(articleItem.getPageUrlList().get(0).getUrl())) {
                    return 2;
                } else {
                    return 1;
                }
            } else {
                if (articleItem.getFreePage() == 0) {// 没有试读
                    return 0;
                } else {
                    return 3;
                }
            }
        }
    }

}
