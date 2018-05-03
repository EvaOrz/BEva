package cn.com.modernmedia.pay;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmedia.util.weixin.MD5;
import cn.com.modernmedia.util.weixin.Util;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;

/**
 * 【恢复购买】验证支付宝、微信订单
 * <p>
 * Created by Eva. on 16/5/24.
 */
public class VerifyOrderTools {

    private static String aliUrl = "https://mapi.alipay.com/gateway.do?service=single_trade_query";
    private static String weixinUrl = "https://api.mch.weixin.qq.com/pay/orderquery";


    public static void verify(Context context, OrderModel orderModel, FetchEntryListener mFetchDataListener) {
        if (orderModel.getPaytype() == 2) {
            verifyOrderFromAli(context, orderModel.getToid(), orderModel.getOpenid(), orderModel.getOid(), mFetchDataListener);
        } else if (orderModel.getPaytype() == 1) {
            verifyOrderFromWeixin(context, orderModel.getOpenid(), orderModel.getToid(), orderModel.getOid(), genNonceStr(), orderModel.getWeixinKey(), mFetchDataListener);
        }
    }

    /**
     * 验证支付宝订单
     *
     * @param context
     * @param trade_no
     * @param partner            商户id
     * @param out_trade_no
     * @param mFetchDataListener
     */
    public static void verifyOrderFromAli(Context context, String trade_no, String partner, String out_trade_no, FetchEntryListener mFetchDataListener) {
        String beforeSign = "";
        beforeSign = "&partner=" + partner;
        if (!TextUtils.isEmpty(trade_no)) {
            beforeSign += "&trade_no=" + trade_no;
        }
        beforeSign += "&out_trade_no=" + out_trade_no;

        // 对订单做RSA 签名
        String sign = MD5.getMessageDigest(beforeSign.toString().getBytes()).toUpperCase();

        aliUrl = aliUrl + beforeSign + "&sign=" + sign + "&sign_type=MD5";
        requestHttp(aliUrl, null, context, mFetchDataListener);

    }

    /**
     * 验证微信订单
     *
     * @param context
     * @param transaction_id     微信订单号
     * @param out_trade_no       商户订单号
     * @param nonce_str          随机串
     * @param mFetchDataListener
     */
    public static void verifyOrderFromWeixin(Context context, String partner_id, String transaction_id, String out_trade_no, String nonce_str, String key, FetchEntryListener mFetchDataListener) {
        List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
        packageParams.add(new BasicNameValuePair("appid", SlateApplication.mConfig.getWeixin_app_id()));

        packageParams.add(new BasicNameValuePair("mch_id", partner_id));
        // 微信回调接口地址
        packageParams.add(new BasicNameValuePair("nonce_str", nonce_str));
        if (!TextUtils.isEmpty(out_trade_no))
            packageParams.add(new BasicNameValuePair("out_trade_no", out_trade_no));
        if (!TextUtils.isEmpty(transaction_id))
            packageParams.add(new BasicNameValuePair("transaction_id", transaction_id));

        String sign = genPackageSign(packageParams, key);
        packageParams.add(new BasicNameValuePair("sign", sign));
        String xmlstring = toXml(packageParams);

        requestHttp(weixinUrl, xmlstring, context, mFetchDataListener);

    }

    /**
     * 微信支付生成签名
     */
    public static String genPackageSign(List<NameValuePair> params, String key) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(key);// API密钥，在商户平台设置

        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();

        Log.e("第一步：微信生成支付签名", packageSign);
        return packageSign;
    }

    /**
     * 微信支付防重复随机数
     *
     * @return
     */
    private static String genNonceStr() {
        Random random = new Random();
        return String.valueOf(random.nextInt(10000));
    }


    /**
     * 微信生成xml表单
     *
     * @param params
     * @return
     */
    private static String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");

            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        return sb.toString();
    }

    /**
     * 请求服务器数据
     */
    private static void requestHttp(final String url, final String entity, Context mContext, final FetchEntryListener mFetchDataListener) {
        if (TextUtils.isEmpty(url)) return;
        new Thread() {

            @Override
            public void run() {
                try {
                    String content = Util.httpPost(url, entity);
                    Log.e("微信支付宝验证", content);

                    Map<String, String> xml = decodeXml(content);
                    VerifyOrderResult result = receiveData(xml);

                    if (result != null) {
                        mFetchDataListener.setData(result);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    /**
     * 反解xml格式
     *
     * @param content
     * @return
     */
    public static Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if ("xml".equals(nodeName) == false) {
                            // 实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;

    }

    /**
     * 解析封装验证结果
     * <p>
     * <p>
     * 微信支付状态 trade_state ：
     * <p>
     * SUCCESS—支付成功
     * REFUND—转入退款
     * NOTPAY—未支付
     * CLOSED—已关闭
     * REVOKED—已撤销（刷卡支付）
     * USERPAYING--用户支付中
     * PAYERROR--支付失败(其他原因，如银行返回失败)
     *
     * @param mapXml
     * @return
     */
    private static VerifyOrderResult receiveData(Map<String, String> mapXml) {
        VerifyOrderResult result = new VerifyOrderResult();
        result.setWeixinOrAli(2);
        result.setResult(mapXml.get("trade_state"));
        result.setErrorMsg(mapXml.get("err_code_des"));
        result.setEndTime(mapXml.get("time_end"));
        result.setOut_no(mapXml.get("out_trade_no"));

        return result;
    }

    /**
     * 验证订单返回结果
     */
    public static class VerifyOrderResult extends Entry {
        private String result = "";
        private String endTime = "";// 交易完成时间，微信格式： 20160601191337
        private String payStatus = "";// 交易状态
        private int weixinOrAli = 0;// ali：1 weixin:2
        private String out_no = "";// 商户交易号
        private String errorMsg = "";

        public String getOut_no() {
            return out_no;
        }

        public void setOut_no(String out_no) {
            this.out_no = out_no;
        }

        public String getEndTime() {
            return endTime;
        }

        public String getPayStatus() {
            return payStatus;
        }

        public void setPayStatus(String payStatus) {
            if (!TextUtils.isEmpty(payStatus) && payStatus.equals("SUCCESS"))
                this.payStatus = PayHttpsOperate.SUCCESS;
            else this.payStatus = PayHttpsOperate.ERROR;
        }

        public int getWeixinOrAli() {
            return weixinOrAli;
        }

        public void setWeixinOrAli(int weixinOrAli) {
            this.weixinOrAli = weixinOrAli;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }


        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}
