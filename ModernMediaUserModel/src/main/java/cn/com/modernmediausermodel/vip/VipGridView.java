package cn.com.modernmediausermodel.vip;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author: zhufei
 */

public class VipGridView extends GridView {

    public VipGridView(Context context) {
        super(context);
    }

    public VipGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VipGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO 自动生成的方法存根
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
