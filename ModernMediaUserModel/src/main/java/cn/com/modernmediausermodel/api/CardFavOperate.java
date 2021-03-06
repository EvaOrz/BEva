package cn.com.modernmediausermodel.api;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediausermodel.UserApplication;

/**
 * 添加卡片operate
 *
 * @author JianCong
 */
public class CardFavOperate extends SlateBaseOperate {
    public static final int TYPE_ADD = 0; // 收藏卡片
    public static final int TYPE_DELETE = 1; // 删除卡片
    private ErrorMsg error;
    private ArrayList<NameValuePair> nameValuePairs; // post参数
    private int type = 0;

    public ErrorMsg getError() {
        return error;
    }

    protected CardFavOperate(String uid, String cardId, int type) {
        this.error = new ErrorMsg();
        this.type = type;
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject postObject = new JSONObject();
        try {
            postObject.put("uid", uid);
            postObject.put("id", cardId);
            if (type == TYPE_ADD) {
                postObject.put("submit", "addFav");
            } else if (type == TYPE_DELETE) {
                postObject.put("submit", "delFav");
            }
            params.add(new BasicNameValuePair("data", postObject.toString()));
            setPostParams(params);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected String getUrl() {
        String url = UrlMaker.getAddCardFav();
        if (type == TYPE_DELETE) {
            url = UrlMaker.getCancelCardFav();
        }
        return url;
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        JSONObject object = jsonObject.optJSONObject("error");
        if (object != null) {
            error.setNo(object.optInt("code", 0));
            error.setDesc(object.optString("msg", ""));
            if (UserApplication.infoChangeListener != null
                    && error.getNo() == 0) {
                if (type == TYPE_ADD) {
                    UserApplication.infoChangeListener.addCard(1);
                } else if (type == TYPE_DELETE) {
                    UserApplication.infoChangeListener.deleteCard(1);
                }
            }
        }
    }

    @Override
    protected void saveData(String data) {
    }

    @Override
    protected String getDefaultFileName() {
        return null;
    }

    @Override
    protected ArrayList<NameValuePair> getPostParams() {
        return nameValuePairs;
    }

    protected void setPostParams(ArrayList<NameValuePair> params) {
        this.nameValuePairs = params;
    }
}
