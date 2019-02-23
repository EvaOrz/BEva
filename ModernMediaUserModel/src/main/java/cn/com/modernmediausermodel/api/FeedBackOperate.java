package cn.com.modernmediausermodel.api;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 意见反馈api
 *
 * @author Eva.
 */
public class FeedBackOperate extends SlateBaseOperate {
    private FeedBackResult backResult = new FeedBackResult();
    private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
    private List<String> imgs = new ArrayList<String>();
    private Context context;

    public FeedBackOperate(Context context, String subject, String name,
                           String emailOrPhone, List<String> images) {
        this.context = context;
        for (int i = 0; i < 3; i++) {
            if (images.size() > i)
                imgs.add(images.get(i));
            else
                imgs.add("");

        }

        // post 参数设置
        JSONObject object = new JSONObject();
        try {
            addPostParams(object, "Name", name);
            if (UserTools.checkIsPhone(context, emailOrPhone))
                object.put("Phone", emailOrPhone);
            else
                object.put("Email", emailOrPhone);


            addPostParams(object, "Question", subject);
            object.put("AppId", ConstData.getInitialAppId());
            object.put("Version", getClientVersion());
            object.put("DeviceId", Tools.getDeviceId(context));
            object.put("UserId", SlateDataHelper.getUid(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
        params.add(new BasicNameValuePair("data", object.toString()));
    }

    @Override
    protected ArrayList<NameValuePair> getPostParams() {
        return params;
    }

    @Override
    protected List<String> getPostImagePath() {
        return imgs;
    }

    @Override
    protected String getUrl() {
        return UrlMaker.getFeedBackUrl();
    }

    @Override
    protected void handler(JSONObject jsonObject) {
        if (jsonObject != null) {
            backResult.setState(jsonObject.optInt("status", -1));
            backResult.setDesc(jsonObject.optString("msg"));
        }

    }

    /**
     * 获取客户端版本号
     *
     * @return
     */
    private String getClientVersion() {
        String version = "";
        // 获取version
        PackageManager manager = context.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return version;
    }

    @Override
    protected void saveData(String data) {

    }

    @Override
    protected String getDefaultFileName() {
        return null;
    }

    public FeedBackResult getResult() {
        return backResult;
    }

    public class FeedBackResult extends Entry {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private String desc = "";
        private int state = -1;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }
    }

}
