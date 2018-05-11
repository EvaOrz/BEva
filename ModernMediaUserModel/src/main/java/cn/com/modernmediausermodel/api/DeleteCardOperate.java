package cn.com.modernmediausermodel.api;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediausermodel.UserApplication;

/**
 * 删除卡片operate
 *
 * @author JianCong
 */
public class DeleteCardOperate extends SlateBaseOperate {
    private ArrayList<NameValuePair> nameValuePairs; // post参数
    private ErrorMsg error;

    public ErrorMsg getError() {
        return error;
    }

    protected DeleteCardOperate(String uid, String cardid) {
        this.error = new ErrorMsg();
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject postObject = new JSONObject();
        try {
            addPostParams(postObject, "uid", uid);
            addPostParams(postObject, "id", cardid);
            addPostParams(postObject, "submit", "delete");
            params.add(new BasicNameValuePair("data", postObject.toString()));
            setPostParams(params);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getDelCard();
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        JSONObject object = jsonObject.optJSONObject("error");
        if (object != null) {
            error.setNo(object.optInt("code", 0));
            error.setDesc(object.optString("msg", ""));
            if (UserApplication.infoChangeListener != null
                    && error.getNo() == 0) {
                UserApplication.infoChangeListener.deleteCard(1);
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
