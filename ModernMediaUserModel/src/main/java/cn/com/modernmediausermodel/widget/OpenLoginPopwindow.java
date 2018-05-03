package cn.com.modernmediausermodel.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import cn.com.modernmediausermodel.LoginActivity;
import cn.com.modernmediausermodel.R;

/**
 * Created by Eva. on 17/4/1.
 */

public class OpenLoginPopwindow implements View.OnClickListener {
    private Context mContext;
    private PopupWindow window;

    public OpenLoginPopwindow(Context context) {
        mContext = context;
        initview();
    }

    /**
     * 获取图片作为头像
     */
    public void initview() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_openlogin_pop, null);
        window = new PopupWindow(view, RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams lp = ((LoginActivity) mContext).getWindow().getAttributes();
        lp.alpha = 0.4f;
        ((LoginActivity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((LoginActivity) mContext).getWindow().setAttributes(lp);
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        window.setBackgroundDrawable(new BitmapDrawable());
        window.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        view.findViewById(R.id.open_login_weibo).setOnClickListener(this);
        view.findViewById(R.id.open_login_weixin).setOnClickListener(this);
        view.findViewById(R.id.open_login_qq).setOnClickListener(this);
        view.findViewById(R.id.open_login_cancel).setOnClickListener(this);

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {

            // 在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((LoginActivity) mContext).getWindow().getAttributes();
                lp.alpha = 1f;
                ((LoginActivity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                ((LoginActivity) mContext).getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.open_login_weibo) {
            ((LoginActivity) mContext).doSinaLogin();

        } else if (id == R.id.open_login_weixin) {
            ((LoginActivity) mContext).doWeixinlogin();
        } else if (id == R.id.open_login_qq) {

            ((LoginActivity) mContext).doQQLogin();
        }
        window.dismiss();
    }

}

