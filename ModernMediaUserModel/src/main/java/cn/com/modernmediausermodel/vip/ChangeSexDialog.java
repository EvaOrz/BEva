package cn.com.modernmediausermodel.vip;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
 * Vip 性别弹出框
 *
 * @author: zhufei
 */
public class ChangeSexDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private WheelView wvSex;

    private View vChangeSex;
    private View vChangeSexChild;
    private TextView btnSure;
    private TextView btnCancel;
    private List<String> sex = new ArrayList<String>();
    private SexAdapter mSexAdapter;
    private OnSexListener onSexListener;
    private String[] array;
    private int maxTextSize = 24;
    private int minTextSize = 14;

    private boolean issetdata = false;
    private String sex_text;

    public ChangeSexDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_myinfo_changesex);
        wvSex = (WheelView) findViewById(R.id.wv_sex);

        vChangeSex = findViewById(R.id.vip_myinfo_changesex);
        vChangeSexChild = findViewById(R.id.vip_myinfo_changesex_child);
        btnSure = (TextView) findViewById(R.id.btn_myinfo_sure);
        btnCancel = (TextView) findViewById(R.id.btn_myinfo_cancel);

        vChangeSex.setOnClickListener(this);
        vChangeSexChild.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        array = new String[]{mContext.getResources().getString(R.string.vip_woman), mContext.getResources().getString(R.string.vip_man), mContext.getResources().getString(R.string.vip_unknow)};
        initJobs();
        mSexAdapter = new SexAdapter(mContext, sex, 0, maxTextSize, minTextSize);
        wvSex.setVisibleItems(2);
        wvSex.setViewAdapter(mSexAdapter);
        wvSex.setCurrentItem(0);

        wvSex.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                sex_text = (String) mSexAdapter.getItemText(wheel.getCurrentItem());

            }
        });
        wvSex.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                sex_text = (String) mSexAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(sex_text, mSexAdapter);
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == btnSure) {
            if (onSexListener != null) {
                onSexListener.onClick(sex_text);
            }
        } else if (v == btnSure) {

        } else if (v == vChangeSexChild) {
            return;
        } else {
            dismiss();
        }
        dismiss();
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, SexAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(maxTextSize);
            } else {
                textvew.setTextSize(minTextSize);
            }
        }
    }

    public void setData(String msg) {
        sex_text = msg;
    }

    public void initJobs() {
        for (int i = 0; i < array.length; i++) {
            sex.add(array[i]);
        }
    }


    /**
     * 回调接口
     *
     * @author Administrator
     */
    public interface OnSexListener {
        void onClick(String sex);
    }

    private class SexAdapter extends AbstractWheelTextAdapter {
        List<String> list;

        protected SexAdapter(Context context, List<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, minsize);
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
            return list.get(index);
        }
    }

    public void setSexListener(OnSexListener onSexListener) {
        this.onSexListener = onSexListener;
    }
}
