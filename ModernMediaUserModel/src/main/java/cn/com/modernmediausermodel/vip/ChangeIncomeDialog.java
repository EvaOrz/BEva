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
 * @author: zhufei
 */
public class ChangeIncomeDialog extends Dialog implements View.OnClickListener {
    private static final int MAX_TEXT_SIZE = 24;
    private static final int MIN_TEXT_SIZE = 14;

    private Context context;

    private WheelView incomeWheelView;
    private IncomeAdapter incomeAdapter;
    private VipInfo.VipChildCategory parentCategory;

    private View vChangeIncome;
    private TextView btnSure;
    private TextView btnCancel;

    private VipInfo.VipCategory incomeCategory;
    private String childCatId;

    private OnIncomeListener onIncomeListener;

    public ChangeIncomeDialog(Context context, VipInfo.VipCategory incomeCategory, String childCatId) {
        super(context, R.style.ShareDialog);
        this.context = context;
        this.incomeCategory = incomeCategory;
        this.childCatId = childCatId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changeincome);
        incomeWheelView = (WheelView) findViewById(R.id.wv_income);
        incomeWheelView.setVisibleItems(3);


        vChangeIncome =  findViewById(R.id.vip_myinfo_changeincome);
        btnSure = (TextView) findViewById(R.id.btn_myinfo_sure);
        btnCancel = (TextView) findViewById(R.id.btn_myinfo_cancel);

        vChangeIncome.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        if (incomeCategory == null)
            return;

        initCategory();
    }

    private void initCategory() {
        if (TextUtils.isEmpty(childCatId)) {
            parentCategory = incomeCategory.firstCategoryList.get(0);

            initWheel(0);
            return;
        }

        int parentIndex = 0;

        for (int i = 0; i < incomeCategory.firstCategoryList.size(); i++) {
            VipInfo.VipChildCategory category = incomeCategory.firstCategoryList.get(i);
            if (TextUtils.equals(category.cate_id, childCatId)) {
                parentCategory = category;
                parentIndex = i;
                break;
            }

            if (parentCategory != null)
                break;
        }

        initWheel(parentIndex);
    }

    private void initWheel(int parentIndex) {
        incomeAdapter = new IncomeAdapter(context, incomeCategory.firstCategoryList, parentIndex);
        incomeWheelView.setViewAdapter(incomeAdapter);
        incomeWheelView.setCurrentItem(parentIndex);


        incomeWheelView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                parentCategory = incomeAdapter.getCategory(wheel.getCurrentItem());
            }
        });
        incomeWheelView.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                VipInfo.VipChildCategory category = incomeAdapter.getCategory(wheel.getCurrentItem());
                setTextviewSize(category.category, incomeAdapter);
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v == btnSure) {
            if (onIncomeListener != null) {
                onIncomeListener.onClick(parentCategory);
            }
        } else if (v == btnSure) {
        }
        dismiss();
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, IncomeAdapter adapter) {
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
    public interface OnIncomeListener {
        void onClick(VipInfo.VipChildCategory income);
    }

    private class IncomeAdapter extends AbstractWheelTextAdapter {
        List<VipInfo.VipChildCategory> list;

        protected IncomeAdapter(Context context, List<VipInfo.VipChildCategory> list, int currentItem) {
            super(context,R.layout.item_birth_year, NO_RESOURCE, currentItem, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
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

    public void setIncomeListener(OnIncomeListener onIncomeListener) {
        this.onIncomeListener = onIncomeListener;
    }

}
