package cn.com.modernmediausermodel.vip;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.vip.adapters.AbstractWheelTextAdapter;
import cn.com.modernmediausermodel.vip.views.OnWheelChangedListener;
import cn.com.modernmediausermodel.vip.views.OnWheelScrollListener;
import cn.com.modernmediausermodel.vip.views.WheelView;


/**
 * Vip 职位job弹出框
 *
 * @author: zhufei
 */
public class ChangeJobDialog extends Dialog implements View.OnClickListener {
    private static final int MAX_TEXT_SIZE = 24;
    private static final int MIN_TEXT_SIZE = 14;

    private Context context;

    private WheelView parentJobWheelView;
    private JobAdapter parentJobAdapter;
    private VipInfo.VipChildCategory parentCategory;

    private WheelView childJobWheelView;
    private JobAdapter childJobAdapter;
    private VipInfo.VipChildCategory childCategory;

    private View vChangeJob;
    private View vChangeJobChild;
    private TextView btnSure;
    private TextView btnCancel;

    private VipInfo.VipCategory jobCategory;
    private String childCatId;

    private OnJobListener onJobListener;

    public ChangeJobDialog(Context context, VipInfo.VipCategory jobCategory, String childCatId) {
        super(context, R.style.ShareDialog);
        this.context = context;
        this.jobCategory = jobCategory;
        this.childCatId = childCatId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_myinfo_changejob);
        parentJobWheelView = (WheelView) findViewById(R.id.wv_job);
        parentJobWheelView.setVisibleItems(3);
        childJobWheelView = (WheelView) findViewById(R.id.child_wv_job);
        childJobWheelView.setVisibleItems(3);

        vChangeJob = findViewById(R.id.vip_myinfo_changejob);
        vChangeJobChild = findViewById(R.id.vip_myinfo_changejob_child);
        btnSure = (TextView) findViewById(R.id.btn_myinfo_sure);
        btnCancel = (TextView) findViewById(R.id.btn_myinfo_cancel);

        vChangeJob.setOnClickListener(this);
        vChangeJobChild.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        if (jobCategory == null || jobCategory.firstCategoryList.size() == 0) return;

        initCategory();
    }

    private void initCategory() {
        if (TextUtils.isEmpty(childCatId)) {
            parentCategory = jobCategory.firstCategoryList.get(0);
            if (parentCategory.secondCategoryList.size() > 0)
                childCategory = parentCategory.secondCategoryList.get(0);

            initWheel(0, 0);
            return;
        }

        int parentIndex = 0;
        int childIndex = 0;

        for (int i = 0; i < jobCategory.firstCategoryList.size(); i++) {
            VipInfo.VipChildCategory category = jobCategory.firstCategoryList.get(i);
            if (TextUtils.equals(category.cate_id, childCatId)) {
                parentCategory = category;
                parentIndex = i;
                break;
            }

            for (int j = 0; j < category.secondCategoryList.size(); j++) {
                VipInfo.VipChildCategory child = category.secondCategoryList.get(j);
                if (TextUtils.equals(child.cate_id, childCatId)) {
                    parentCategory = category;
                    childCategory = child;

                    parentIndex = i;
                    childIndex = j;
                    break;
                }
            }

            if (parentCategory != null) break;
        }

        initWheel(parentIndex, childIndex);
    }

    private void initWheel(int parentIndex, int childIndex) {
        parentJobAdapter = new JobAdapter(context, jobCategory.firstCategoryList, parentIndex);
        parentJobWheelView.setViewAdapter(parentJobAdapter);
        parentJobWheelView.setCurrentItem(parentIndex);

        if (parentCategory != null) {
            childJobAdapter = new JobAdapter(context, parentCategory.secondCategoryList, childIndex);
            childJobWheelView.setViewAdapter(childJobAdapter);
            childJobWheelView.setCurrentItem(childIndex);
        }

        parentJobWheelView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                parentCategory = parentJobAdapter.getCategory(wheel.getCurrentItem());
            }
        });
        parentJobWheelView.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                VipInfo.VipChildCategory category = parentJobAdapter.getCategory(wheel.getCurrentItem());
                setTextviewSize(category.category, parentJobAdapter);

                // 刷新子wheel
                childJobAdapter = new JobAdapter(context, category.secondCategoryList, 0);
                childJobWheelView.setViewAdapter(childJobAdapter);
                childJobWheelView.setCurrentItem(0);
                if (category.secondCategoryList.size() > 0)
                    childCategory = category.secondCategoryList.get(0);
            }
        });

        childJobWheelView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                childCategory = childJobAdapter.getCategory(wheel.getCurrentItem());
            }
        });
        childJobWheelView.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                if (childJobAdapter == null) return;

                VipInfo.VipChildCategory category = childJobAdapter.getCategory(wheel.getCurrentItem());
                setTextviewSize(category.category, childJobAdapter);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnSure) {
            if (onJobListener != null) {
                onJobListener.onClick(parentCategory, childCategory);
            }
        } else if (v == btnSure) {
        } else if (v == vChangeJobChild) {
            return;
        }
        dismiss();
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, JobAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(MAX_TEXT_SIZE);
            } else {
                textvew.setTextSize(MIN_TEXT_SIZE);
            }
        }
    }

    /**
     * 回调接口
     *
     * @author Administrator
     */
    public interface OnJobListener {
        void onClick(VipInfo.VipChildCategory job, VipInfo.VipChildCategory childJob);
    }

    private class JobAdapter extends AbstractWheelTextAdapter {
        List<VipInfo.VipChildCategory> list;

        protected JobAdapter(Context context, List<VipInfo.VipChildCategory> list, int currentItem) {
            super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index).category;
        }

        public VipInfo.VipChildCategory getCategory(int index) {
            return list.get(index);
        }
    }

    public void setJobListener(OnJobListener onJobListener) {
        this.onJobListener = onJobListener;
    }

}
