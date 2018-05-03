package cn.com.modernmedia.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

public class LoopViewPager extends ViewPager {

    public LoopViewPager(Context context) {
        super(context);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        // offset first element so that we can scroll to the left
        setCurrentItem(0);
    }

    public void next() {
        int next = super.getCurrentItem() + 1;
        super.setCurrentItem(next, true);
    }

    @Override
    public void setCurrentItem(int item) {
        // offset the current item to ensure there is space to scroll
        setCurrentItem(item, false);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (getAdapter() != null && getAdapter().getCount() > 0)
            item = getOffsetAmount() + (item % getAdapter().getCount());
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public int getCurrentItem() {
        int position = super.getCurrentItem();
        LoopPagerAdapter infAdapter = (LoopPagerAdapter) getAdapter();
        if (getAdapter() instanceof LoopPagerAdapter && infAdapter.getRealCount() > 0) {

            // Return the actual item position in the data backing
            // InfinitePagerAdapter

            return (position % infAdapter.getRealCount());
        } else {
            return super.getCurrentItem();
        }
    }

    public int getRealCurrentItem() {
        return super.getCurrentItem();
    }

    private int getOffsetAmount() {
        if (getAdapter() instanceof LoopPagerAdapter) {
            LoopPagerAdapter infAdapter = (LoopPagerAdapter) getAdapter();
            // allow for 100 back cycles from the beginning
            // should be enough to create an illusion of infinity
            // warning: scrolling to very high values (1,000,000+) results in
            // strange drawing behaviour
            return infAdapter.getRealCount() * 100;
        } else {
            return 0;
        }
    }

}
