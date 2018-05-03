package cn.com.modernmedia.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ProductList.Product;

/**
 * 支付商品列表
 * 
 * @author lusiyuan
 *
 */
public class ProductAdapter extends BaseAdapter {
	private Context context;
	private List<Product> list;
	private LayoutInflater mInflater;

	public ProductAdapter(Context context, List<Product> list) {
		this.context = context;
		this.list = list;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Product getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_product, null);
			holder = new ViewHolder();
			holder.textView = (Button) convertView.findViewById(R.id.pay_year);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.textView.setText(list.get(position).getName());
		return convertView;
	}

	public static class ViewHolder {
		public Button textView;
	}

}
