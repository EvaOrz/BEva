package cn.com.modernmedia.views.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.views.column.book.BookColumnActivity;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.ActiveActivity;
import cn.com.modernmediausermodel.BandDetailActivity;
import cn.com.modernmediausermodel.LoginActivity;
import cn.com.modernmediausermodel.vip.MyVipActivity;
import cn.com.modernmediausermodel.vip.VipCodeActivity;
import cn.com.modernmediausermodel.vip.VipOpenActivity;
import cn.com.modernmediausermodel.vip.VipProductPayActivity;


/**
 * 支付页面跳转User 模块中的广播接收器
 *
 * @author Eva.
 */
public class PayToLoginReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("cn.com.modernmediausermodel.LoginActivity_nomal")) {
            Intent i = new Intent(context, LoginActivity.class);
            if (!TextUtils.isEmpty(intent.getStringExtra("pid")))
                i.putExtra("pid", intent.getStringExtra("pid"));
            if (!TextUtils.isEmpty(intent.getStringExtra("needTel")))
                i.putExtra("needTel", intent.getStringExtra("needTel"));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if (intent.getAction().equals("cn.com.modernmediausermodel.ActiveActivity")) {
            Intent i = new Intent(context, ActiveActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if (intent.getAction().equals("cn.com.modernmediausermodel.BandDetailActivity_nomal")) {
            Intent i = new Intent(context, BandDetailActivity.class);
            i.putExtra("band_type", intent.getIntExtra("band_type", 0));
            i.putExtra("band_user", intent.getSerializableExtra("band_user"));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if (intent.getAction().equals("cn.com.modernmediausermodel.VipOpenActivity_nomal")) {
            Intent i = new Intent(context, VipOpenActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if (intent.getAction().equals("cn.com.modernmediausermodel.VipProductPayActivity_nomal")) {
            Intent i = new Intent(context, VipProductPayActivity.class);
            if (intent.getBundleExtra("html_pay") != null)
                i.putExtra("html_pay", intent.getBundleExtra("html_pay"));
            if (!TextUtils.isEmpty(intent.getStringExtra("pay_pid")))
                i.putExtra("pay_pid", intent.getStringExtra("pay_pid"));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if (intent.getAction().equals("cn.com.modernmediausermodel.VipCodeActivity_nomal")) {
            Intent i = new Intent(context, VipCodeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if (intent.getAction().equals("cn.com.modernmedia.views.column.book.BookColumnActivity")) {
            Intent i = new Intent(context, BookColumnActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String tagname = intent.getStringExtra("tagname");
            if (!TextUtils.isEmpty(tagname)) {
                TagInfoList.TagInfo tagInfo = new TagInfoList.TagInfo();
                tagInfo.setTagName(tagname);
                i.putExtra("book_deatail", tagInfo);
                context.startActivity(i);
            }
        } else if (intent.getAction().equals("cn.com.modernmediausermodel.vip")) {
            User user = SlateDataHelper.getUserLoginInfo(context);
            if (user != null) {
                if (SlateDataHelper.getVipLevel(context) > 0) {//vip有效期内
                    Intent i = new Intent(context, MyVipActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } else {
                    Intent i = new Intent(context, VipOpenActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            } else {
                Intent i = new Intent(context, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}
