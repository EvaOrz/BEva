package cn.com.modernmediausermodel.vip;

import android.widget.ImageView;

import cn.com.modernmediausermodel.R;

/**
 * VIP套餐本地icon选择
 *
 * @author: zhufei
 */

public class VipIconImg {
    public static final int STROKE = 2;// 边框宽度

    static void setImg(ImageView img, String funId) {
        switch (funId) {
            case "card":
                img.setImageResource(R.drawable.vip_card);
                break;
            case "vip_sign":
                img.setImageResource(R.drawable.vip_sign);
                break;
            case "active":
                img.setImageResource(R.drawable.active);
                break;
            case "vip_interaction":
                img.setImageResource(R.drawable.vip_interaction);
                break;
            case "article_read":
                img.setImageResource(R.drawable.article_read);
                break;
            case "e-book":
                img.setImageResource(R.drawable.e_book);
                break;
            case "pass":
                img.setImageResource(R.drawable.pass);
                break;
            case "business_calendar":
                img.setImageResource(R.drawable.business_calendar);
                break;
            case "magazine_paper":
                img.setImageResource(R.drawable.magazine_paper);
                break;
            case "gift":
                img.setImageResource(R.drawable.gift);
                break;
            default:
                img.setImageResource(R.drawable.vip_card);
                break;
        }

    }

    static void setOutImg(ImageView img, String funId) {
        switch (funId) {
            case "card":
                img.setImageResource(R.drawable.vip_card_ex);
                break;
            case "vip_sign":
                img.setImageResource(R.drawable.vip_sign_ex);
                break;
            case "active":
                img.setImageResource(R.drawable.active_ex);
                break;
            case "vip_interaction":
                img.setImageResource(R.drawable.vip_interaction_ex);
                break;
            case "article_read":
                img.setImageResource(R.drawable.article_read_ex);
                break;
            case "e-book":
                img.setImageResource(R.drawable.e_book_ex);
                break;
            case "pass":
                img.setImageResource(R.drawable.pass_ex);
                break;
            case "business_calendar":
                img.setImageResource(R.drawable.business_calendar_ex);
                break;
            case "magazine_paper":
                img.setImageResource(R.drawable.magazine_paper_ex);
                break;
            case "gift":
                img.setImageResource(R.drawable.gift_ex);
                break;
            default:
                img.setImageResource(R.drawable.vip_card_ex);
                break;
        }

    }

}
