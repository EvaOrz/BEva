package cn.com.modernmedia.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.com.modernmedia.Fragment.ImageDetailFragment;
import cn.com.modernmedia.model.ArticleItem;

/**
 * 文章内图片浏览
 *
 * @author: zhufei
 */
public class ImagePagerAdapter extends FragmentPagerAdapter{

    private ArticleItem mItem = new ArticleItem();

    public ImagePagerAdapter(FragmentManager fm,ArticleItem item) {
        super(fm);
        this.mItem = item;
    }

    @Override
    public int getCount() {
        return mItem.getPicList().size();
    }

    @Override
    public Fragment getItem(int position) {

        return ImageDetailFragment.newInstance(mItem,position);
    }
}
