package cn.com.modernmedia.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.widget.GifView;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;

/**
 * 电台页面adapter
 * 
 * @author lusiyuan
 *
 */
public class MusicItemAdapter extends BaseAdapter {

	private Context context;
	private List<ArticleItem> list;
	private LayoutInflater mInflater;
	private List<Integer> flags;// 判断当前播放item flags

	public MusicItemAdapter(Context context, List<ArticleItem> list,
			List<Integer> flags) {
		this.context = context;
		this.list = list;
		this.flags = flags;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ArticleItem getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final ArticleItem articleData = list.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_music, null);
			holder = new ViewHolder();
			holder.cover = (ImageView) convertView
					.findViewById(R.id.music_item_cover);
			holder.title = (TextView) convertView
					.findViewById(R.id.music_item_title);
			holder.dec = (TextView) convertView
					.findViewById(R.id.music_item_dec);
			holder.duration = (TextView) convertView
					.findViewById(R.id.music_item_duration);
			holder.playing = (GifView) convertView
					.findViewById(R.id.music_item_playing);
			holder.redLine = (ImageView) convertView
					.findViewById(R.id.play_red_line);

			/**
			 * 设置数据
			 */
			if (articleData.getThumbList() != null
					&& articleData.getThumbList().size() > 0)
				setCover(articleData.getThumbList().get(0).getUrl(),
						holder.cover);
			if (articleData.getTitle() != null)
				holder.title.setText(articleData.getTitle());
			if (articleData.getDesc() != null)
				holder.dec.setText(articleData.getDesc());
			// holder.duration.setText(text);
			// 设置背景gif图片资源
			if (flags != null && flags.size() > position
					&& flags.get(position) == 1) {
				holder.redLine.setVisibility(View.VISIBLE);
				holder.playing.setVisibility(View.VISIBLE);
				holder.playing.setMovieResource(R.raw.music_playing);
			} else {
				holder.redLine.setVisibility(View.INVISIBLE);
				holder.playing.setVisibility(View.INVISIBLE);
			}
			if (articleData.getAudioList() != null
					&& articleData.getAudioList().size() > 0)
				holder.duration.setText(articleData.getAudioList().get(0)
						.getSize()
						/ (1024 * 1024) + "M");

			// 设置暂停
			// gif2.setPaused(true);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}

	private void setCover(String url, final ImageView cover) {
		if (TextUtils.isEmpty(url)) {
			return;
		}
		SlateApplication.finalBitmap.display(url,
				new ImageDownloadStateListener() {

					@Override
					public void loading() {

					}

					@Override
					public void loadOk(Bitmap bitmap, NinePatchDrawable drawable) {
						FinalBitmap.transforCircleBitmap(bitmap, cover);
					}

					@Override
					public void loadError() {
					}
				});
	}

	public static class ViewHolder {
		public ImageView cover;// 封面图
		public TextView title;
		public TextView dec;// 描述
		public TextView duration;// 音频时长
		public GifView playing;
		public ImageView redLine;
	}

}
