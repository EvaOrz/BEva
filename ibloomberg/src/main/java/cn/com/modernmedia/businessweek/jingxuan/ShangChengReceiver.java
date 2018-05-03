package cn.com.modernmedia.businessweek.jingxuan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import cn.com.modernmedia.businessweek.SplashScreenActivity;
import cn.com.modernmedia.businessweek.YouzanGoodActivity;

/**
 * Created by Eva. on 2017/11/8.
 */

public class ShangChengReceiver extends BroadcastReceiver {
    public static String APP1SPECIAL = "app1_choice_specialissue";
    public static String APP1FM = "app1_choice_fm";

    public static String APP1READNEWS = "app1_choice_readnewspaper";
    public static String APP1DOCUMENT = "app1_choice_documentaire";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("cn.com.modernmedia.shangcheng")) {
            String type = intent.getStringExtra("ShangchengList_type");
            if (!TextUtils.isEmpty(type)) {
                Intent i = new Intent(context, ShangchengListActivity.class);
                i.putExtra("ShangchengList_type", getSenid(type));
                i.putExtra("is_from_splash", intent.getBooleanExtra("is_from_splash", true));
                i.putExtra("ShangchengList_senid", type);
                i.putExtra("ShangchengList_fm", getSenid(type) == 7 ? 0 : -1);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else {
                Intent i = new Intent(context, SplashScreenActivity.class);

                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        } else if (intent.getAction().equals("cn.com.modernmedia.shangcheng_info")) {
            String type = intent.getStringExtra("ShangchengList_type");
            if (!TextUtils.isEmpty(type)) {
                Intent i = new Intent(context, ShangchengInfoActivity.class);
                i.putExtra("is_from_splash", intent.getBooleanExtra("is_from_splash", true));
                i.putExtra("ShangchengInfo_senid", type);
                i.putExtra("ShangchengInfo_type", getSenid(type));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else {
                Intent i = new Intent(context, SplashScreenActivity.class);

                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        } else if (intent.getAction().equals("cn.com.modernmedia.businessweek.YouzanGoodActivity")) {
            Intent i = new Intent(context, YouzanGoodActivity.class);
            i.putExtra("youzan_good_item", intent.getSerializableExtra("youzan_good_item"));
            i.putExtra("youzan_good_id", intent.getStringExtra("youzan_good_id"));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        }
    }


    private int getSenid(String type) {
        int tt = 0;// 6专刊；8读报；7FM；9专题片；10金融研究所
        if (type.equals("app1_choice_specialissue")) {
            tt = 6;
        } else if (type.equals("app1_choice_fm")) {
            tt = 7;
        } else if (type.equals("app1_choice_readnewspaper")) {
            tt = 8;
        } else if (type.equals("app1_choice_documentaire")) {
            tt = 9;
        } else if (type.equals("caifu_finance_ceri")) {
            tt = 10;
        }
        return tt;
    }
}
