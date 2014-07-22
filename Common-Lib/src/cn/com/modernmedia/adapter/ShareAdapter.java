package cn.com.modernmedia.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.R;

/**
 * 分享列表适配器
 * 
 * @author ZhuQiao
 * 
 */
public class ShareAdapter extends ArrayAdapter<Intent> {
	private Context mContext;
	private LayoutInflater inflater;

	public ShareAdapter(Context context) {
		super(context, 0);
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	public void setData(List<Intent> list) {
		synchronized (list) {
			for (Intent item : list) {
				add(item);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Intent intent = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.share_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.app_icon);
			holder.name = (TextView) convertView.findViewById(R.id.app_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
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
			Drawable appIcon = appInfo.loadIcon(mContext.getPackageManager());
			holder.icon.setImageDrawable(appIcon);
			String appName = (String) appInfo.loadLabel(mContext
					.getPackageManager());
			holder.name.setText(appName == null ? "" : appName);
		} else {
			holder.icon.setImageDrawable(null);
			holder.name.setText("");
		}
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			holder.name.setText(R.string.share_to_gallery);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView name;
		ImageView icon;
	}
}
