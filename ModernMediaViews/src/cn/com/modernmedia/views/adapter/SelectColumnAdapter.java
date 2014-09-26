package cn.com.modernmedia.views.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import cn.com.modernmedia.adapter.CheckScrollAdapter;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.views.BaseSelectColumnActivity;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.SelectColumnActivity;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.adapter.ViewHolder;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 选择栏目adapter
 * 
 * @author zhuqiao
 * 
 */
public class SelectColumnAdapter extends CheckScrollAdapter<TagInfo> {
	private Context mContext;
	private int width, height;

	public SelectColumnAdapter(Context context) {
		super(context);
		mContext = context;
		width = ViewsApplication.width * 3 / 4;
		height = width * 278 / 480;
		// padTop = mContext.getResources().getDimensionPixelSize(R.dimen.dp20);
	}

	/**
	 * 一级订阅栏目
	 * 
	 * @branch 无子栏目：tagLevel == 1 && enablesubscribe == 1 && noColumn == 0 &&
	 *         isfix == 0
	 * @branch 有子栏目：是否有子栏目满足tagLevel == 2 && enablesubscribe == 1 && noColumn ==
	 *         0 && isfix == 0
	 */
	public void setData(boolean currApp) {
		synchronized (AppValue.tempColumnList.getList()) {
			for (TagInfo item : AppValue.tempColumnList.getList()) {
				if (currApp) {
					if (item.getAppId() != ConstData.getInitialAppId()) {
						continue;
					}
				} else {
					if (item.getAppId() == ConstData.getInitialAppId()) {
						continue;
					}
				}
				if (item.getHaveChildren() == 0) {
					// TODO 无子栏目
					if (item.getTagLevel() == 1
							&& item.getEnablesubscribe() == 1
							&& item.getColumnProperty().getNoColumn() == 0
							&& item.getIsFix() == 0) {
						add(item);
					}
				} else {
					// TODO 有子栏目
					if (item.getEnablesubscribe() == 1) {
						if (((SelectColumnActivity) mContext)
								.checkHasChildCanSubscribe(item)) {
							add(item);
						}
					}
				}
			}
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final TagInfo info = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.select_column_list_item);
		// View view = holder.getView(R.id.select_column_item_contain);
		ImageView imageView = holder.getView(R.id.select_column_item_img);
		TextView name = holder.getView(R.id.select_column_item_name);
		ImageView select = holder.getView(R.id.select_column_item_select);
		final ImageView row = holder.getView(R.id.select_column_item_row);

		// view.setPadding(0, position == 0 ? 0 : padTop, 0, 0);
		imageView.getLayoutParams().width = width;
		imageView.getLayoutParams().height = height;
		V.setImage(imageView, "placeholder");
		imageView.setScaleType(ScaleType.CENTER);
		V.downSubscriptPicture(info.getTagName(), imageView);
		name.setText(info.getColumnProperty().getCname());
		row.setVisibility(info.getHaveChildren() == 1 ? View.VISIBLE
				: View.GONE);
		select.setImageResource(checkIsSelect(info) ? R.drawable.subscribe_checked
				: R.drawable.subscribe_check);

		select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clickSelect(info);
			}
		});
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clickSelect(info);
			}
		});
		holder.getConvertView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (row.getVisibility() == View.VISIBLE)
					((SelectColumnActivity) mContext)
							.gotoSelectChildColumnActvity(info);
			}
		});
		return holder.getConvertView();
	}

	/**
	 * 判断是否选中
	 * 
	 * @param info
	 * @return
	 */
	private boolean checkIsSelect(TagInfo info) {
		if (info.getIsFix() == 1) {
			return true;
		}
		if (info.getHaveChildren() == 0 && info.getHasSubscribe() == 1)
			return true;
		if (info.getHaveChildren() == 1) {
			return ((BaseSelectColumnActivity) mContext).checkParentIsSelect(
					info, false);
		}
		return false;
	}

	/**
	 * 切换选中状态
	 * 
	 * @param info
	 */
	private void clickSelect(TagInfo info) {
		if (info.getIsFix() == 1) {
			// TODO 不可被取消
			return;
		}
		if (info.getHasSubscribe() == 1) {
			info.setHasSubscribe(0);
		} else {
			info.setHasSubscribe(1);
		}
		synchronousChild(info);
		notifyDataSetChanged();
	}

	/**
	 * 同步子栏目
	 * 
	 * @param info
	 */
	private void synchronousChild(TagInfo info) {
		if (info.getHaveChildren() == 0)
			return;
		if (!ParseUtil.mapContainsKey(AppValue.tempColumnList.getChildMap(),
				info.getTagName()))
			return;
		List<TagInfo> list = AppValue.tempColumnList.getChildMap().get(
				info.getTagName());
		for (TagInfo child : list) {
			if (child.getIsFix() == 1
					|| child.getColumnProperty().getNoColumn() == 1
					|| child.getEnablesubscribe() == 0)
				continue;
			if (info.getHasSubscribe() == 1) {
				// TODO 把所有可订阅的置为已订阅
				if (child.getEnablesubscribe() == 1)
					child.setHasSubscribe(1);
			} else {
				// TODO 把所有可取消的置为未订阅
				if (child.getIsFix() == 0)
					child.setHasSubscribe(0);
			}
		}
	}

}
