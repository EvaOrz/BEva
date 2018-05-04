package cn.com.modernmedia.views.column.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.views.R;
import cn.com.modernmediaslate.model.Entry;

/**
 * Created by xiaolin on 2015/1/24.
 */
public class GridViewAdapter extends BaseAdapter {
    public static int MY = 1;
    public static int ALL = 2;
    private Context context;
    private List<TagInfo> tags = new ArrayList<TagInfo>();
    private int hidePosition = AdapterView.INVALID_POSITION, type = -1;
    private LayoutInflater mInflater;
    private ViewHolder viewHolder = null;

    public GridViewAdapter(Context context, List<TagInfo> strList, int type) {
        this.context = context;
        this.tags = strList;
        this.type = type;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Entry getItem(int position) {
        return tags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        convertView = mInflater.inflate(R.layout.item_book, null);
        final TagInfo t = tags.get(pos);
        /**
         * 控件初始化
         */
        viewHolder = new ViewHolder();
        viewHolder.back = (FrameLayout) convertView.findViewById(R.id.book_item_back_color);
        viewHolder.title = (TextView) convertView.findViewById(R.id.book_item_title);
        viewHolder.weeklyTitle = (TextView) convertView.findViewById(R.id.book_item_title_weekly);
        viewHolder.cover = (ImageView) convertView.findViewById(R.id.book_item_cover);
        viewHolder.delete = (ImageView) convertView.findViewById(R.id.book_item_check);
        convertView.setTag(t);

        if (ConstData.getAppId() == 20) {// iweekly
            viewHolder.weeklyTitle.setVisibility(View.VISIBLE);
            viewHolder.cover.setLayoutParams(new LayoutParams(180, 180));
            viewHolder.weeklyTitle.setLayoutParams(new LayoutParams(180, 50, Gravity.BOTTOM));
            viewHolder.cover.setBackgroundResource(R.drawable.iweekly_book_item_bac);
        } else {
            viewHolder.title.setVisibility(View.VISIBLE);
        }

        // hide时隐藏Text
        if (pos != hidePosition) {
            String tagName = t.getColumnProperty().getCname();
            viewHolder.weeklyTitle.setText(t.getColumnProperty().getCname());

            // 有图初始化图片
            if (t.getColumnProperty().getBigPicture() != null && t.getColumnProperty().getBigPicture().size() > 0) {
                CommonApplication.finalBitmap.display(viewHolder.cover, t.getColumnProperty().getBigPicture().get(0));
            } else {// 无图初始化背景和tagname
                if (DataHelper.columnColorMap.containsKey(t.getTagName())) {
                    viewHolder.back.setBackgroundColor(DataHelper.columnColorMap.get(t.getTagName()));
                }
                //t.getColumnProperty().getCname()
                viewHolder.title.setText(t.getColumnProperty().getCname());
            }

            if (t.isCheck())// 排序状态
            {
                if (t.getIsFix() != 1)// 固定栏目字体颜色为灰色，不可选
                    viewHolder.delete.setVisibility(View.VISIBLE);
            } else {
                // 无法取消状态
                viewHolder.delete.setVisibility(View.GONE);
            }
            viewHolder.delete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((BookActivity) context).deleteBook(pos);
                }
            });
        }
        // else {
        // viewHolder.title.setText("");
        // }
        viewHolder.title.setId(pos);

        return convertView;
    }

    public void hideView(int pos) {
        hidePosition = pos;
        notifyDataSetChanged();
    }

    public void showHideView() {
        hidePosition = AdapterView.INVALID_POSITION;
        notifyDataSetChanged();
    }

    /**
     * 判断可拖动的区域
     *
     * @param draggedPos
     * @param destPos
     */
    // 更新拖动时的gridView
    public boolean swapView(int draggedPos, int destPos) {
        List<TagInfo> gudings = new ArrayList<TagInfo>();
        for (TagInfo t : tags) {
            if (t.getIsFix() == 1) gudings.add(t);
        }
        if (type == ALL || destPos < gudings.size()) return false;
        // 从前向后拖动，其他item依次前移
        if (draggedPos < destPos) {
            tags.add(destPos + 1, (TagInfo) getItem(draggedPos));
            tags.remove(draggedPos);
        }
        // 从后向前拖动，其他item依次后移
        else if (draggedPos > destPos) {
            tags.add(destPos, (TagInfo) getItem(draggedPos));
            tags.remove(draggedPos + 1);
        }
        hidePosition = destPos;
        notifyDataSetChanged();
        return true;
    }

    // 通过ViewHolder显示项的内容
    static class ViewHolder {
        public TextView title;
        public TextView weeklyTitle;// iweekly标签名
        public FrameLayout back;
        public ImageView cover, delete;
    }

    public List<TagInfo> getTagsList() {
        return tags;
    }
}
