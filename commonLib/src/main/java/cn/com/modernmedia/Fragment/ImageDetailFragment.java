package cn.com.modernmedia.Fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.photoview.PhotoViewAttacher;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;

/**
 * 图片详情
 *
 * @author: zhufei
 */
public class ImageDetailFragment extends BaseFragment {

    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;
    private ProgressBar progressBar;
    private ArticleItem item;

    //传递fragment参数
    public static ImageDetailFragment newInstance(ArticleItem item, int pos) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString("url", item.getPicList().get(pos).getUrl());
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) view.findViewById(R.id.detail_fragment_image);
        mAttacher = new PhotoViewAttacher(mImageView);
        progressBar = (ProgressBar) view.findViewById(R.id.loading);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CommonApplication.finalBitmap.display(mImageView, getArguments().getString("url"),
                new ImageDownloadStateListener() {
                    @Override
                    public void loading() {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void loadOk(Bitmap bitmap, NinePatchDrawable drawable, byte[] gifByte) {
                        progressBar.setVisibility(View.GONE);
                        if (drawable != null)
                            mImageView.setImageDrawable(drawable);
                        else
                            mImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void loadError() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "图片无法显示", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void refresh() {

    }

    @Override
    public void showView(boolean show) {

    }
}
