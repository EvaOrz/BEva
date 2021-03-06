/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.modernmedia.widget;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import cn.com.modernmedia.adapter.MyPagerAdapter;

public class LoopPagerAdapter extends PagerAdapter {
    private PagerAdapter adapter;

    public LoopPagerAdapter(PagerAdapter adapter) {
        this.adapter = adapter;
    }

    public PagerAdapter getPagerAdapter() {
        return adapter;
    }

    @Override
    public int getCount() {
        return getRealCount() * 1000;
    }

    /**
     * @return the {@link #getCount()} result of the wrapped adapter
     */
    public int getRealCount() {
        return adapter.getCount();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int virtualPosition = position % getRealCount();
        if (adapter instanceof MyPagerAdapter) {
            if (position > 0) {
                return ((MyPagerAdapter) adapter).instantiateItem(container, virtualPosition);
            }
        }
        return adapter.instantiateItem(container, virtualPosition);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (getRealCount() == 0) return;
        int virtualPosition = position % getRealCount();
        if (adapter instanceof MyPagerAdapter) {
            if (position > 0) {
                ((MyPagerAdapter) adapter).destroyItem(container, virtualPosition, object);
            }
        } else {
            adapter.destroyItem(container, virtualPosition, object);
        }
    }

	/*
     * Delegate rest of methods directly to the inner adapter.
	 */

    @Override
    public void finishUpdate(ViewGroup container) {
        adapter.finishUpdate(container);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return adapter.isViewFromObject(view, object);
    }

    @Override
    public void restoreState(Parcelable bundle, ClassLoader classLoader) {
        adapter.restoreState(bundle, classLoader);
    }

    @Override
    public Parcelable saveState() {
        return adapter.saveState();
    }

    @Override
    public void startUpdate(ViewGroup container) {
        adapter.startUpdate(container);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int virtualPosition = position % getRealCount();
        return adapter.getPageTitle(virtualPosition);
    }

    @Override
    public float getPageWidth(int position) {
        return adapter.getPageWidth(position);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {

        if (adapter instanceof MyPagerAdapter && getRealCount() > 0) {
            int virtualPosition = position % getRealCount();
            if (position > 0) {
                ((MyPagerAdapter) adapter).setPrimaryItem(container, position, virtualPosition, object);
            }
        } else {
            adapter.setPrimaryItem(container, position, object);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        adapter.unregisterDataSetObserver(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        adapter.registerDataSetObserver(observer);
    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return adapter.getItemPosition(object);
    }

}