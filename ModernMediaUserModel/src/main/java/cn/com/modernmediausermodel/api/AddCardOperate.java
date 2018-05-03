package cn.com.modernmediausermodel.api;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediausermodel.UserApplication;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 添加卡片operate
 *
 * @author JianCong
 *         `
 */
public class AddCardOperate extends SlateBaseOperate {
    private ErrorMsg error;
    private ArrayList<NameValuePair> nameValuePairs; // post参数

    public ErrorMsg getError() {
        return error;
    }

    public AddCardOperate(CardItem cardItem) {
        this.error = new ErrorMsg();
        JSONObject postObject = new JSONObject();
        // post 参数设置
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        try {
            addPostParams(postObject, "uid", cardItem.getUid());
            addPostParams(postObject, "appid", UserConstData.getInitialAppId() + "");
            addPostParams(postObject, "time", cardItem.getTime());
            if (cardItem.getArticleId() != 0)
                addPostParams(postObject, "articleid", cardItem.getArticleId() + "");
            addPostParams(postObject, "contents", cardItem.getContents());
            params.add(new BasicNameValuePair("data", postObject.toString()));
            setPostParams(params);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造方法
     *
     * @param json post数据，中文需要进行UTF-8编码，但是如果其中含有换行符不需要编码
     */
    public AddCardOperate(String json) {
        this.error = new ErrorMsg();
        nameValuePairs.add(new BasicNameValuePair("data", json));
        setPostParams(nameValuePairs);
    }


    @Override
    protected String getUrl() {
        return UrlMaker.getAddCard();
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        System.out.println("添加笔记" + jsonObject.toString());
        JSONObject object = jsonObject.optJSONObject("error");
        if (object != null) {
            error.setNo(object.optInt("code", 0));
            error.setDesc(object.optString("msg", ""));
            if (UserApplication.infoChangeListener != null && error.getNo() == 0) {
                UserApplication.infoChangeListener.addCard(1);
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
