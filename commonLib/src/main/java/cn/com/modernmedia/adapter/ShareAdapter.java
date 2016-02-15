package cn.com.modernmedia.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ShareDialogItem;
import cn.com.modernmediaslate.adapter.ViewHolder;

/**
 * 分享列表适配器
 * 
 * @author ZhuQiao
 * 
 */
public class ShareAdapter extends ArrayAdapter<ShareDialogItem> {
	private Context mContext;

	public ShareAdapter(Context context) {
		super(context, 0);
		mContext = context;
	}

	public void setData(List<ShareDialogItem> list) {
		synchronized (list) {
			for (ShareDialogItem item : list) {
				add(item);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ShareDialogItem item = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.share_item);
		ImageView icon = holder.getView(R.id.app_icon);
		TextView name = holder.getView(R.id.app_name);

		Intent intent = item.getIntent();
		if (intent == null) {
			name.setText(item.getName());
			icon.setImageResource(item.getIcon());
		} else {
			ApplicationInfo appInfo = null;
			try {
				String packageName = intent.getPackage();
				if (!TextUtils.isEmpty(packageName)) {
					appInfo = mContext.getPackageManager().getApplicationInfo(
							packageName, 0);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			if (appInfo != null) {
				Drawable appIcon = appInfo.loadIcon(mContext
						.getPackageManager());
				icon.setImageDrawable(appIcon);
				String appName = (String) appInfo.loadLabel(mContext
						.getPackageManager());
				name.setText(appName == null ? "" : appName);
			} else {
				icon.setImageDrawable(null);
				name.setText("");
			}
			if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				name.setText(R.string.share_to_gallery);
			}
		}

		return holder.getConvertView();
	}

}
