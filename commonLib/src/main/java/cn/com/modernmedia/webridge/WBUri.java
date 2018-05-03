package cn.com.modernmedia.webridge;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.util.List;

import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmediaslate.model.Entry;

/**
 * uri分发机制
 *
 * @author user
 */
public class WBUri {
    private Context mContext;
    private WebUriListener mListener;

    public WBUri(Context context, WebUriListener listener) {
        mContext = context;
        mListener = listener;
        if (mContext == null) {
            throw new NullPointerException("WBUri mContext is NULL!");
        }
        if (mListener == null) {
            throw new NullPointerException("WBUri UriListener is NULL!");
        }
    }

    /**
     * 是否可以用slate协议打开uri
     *
     * @param uriStr
     * @return
     */
    public boolean canOpenURI(String uriStr, View view,ArticleItem articleItem) {
        if (TextUtils.isEmpty(uriStr)) {
            return false;
        }

        Uri uri = Uri.parse(uriStr);
        String scheme = uri.getScheme();
        String path = uri.getPath();
        if (TextUtils.isEmpty(scheme) || TextUtils.isEmpty(path)) {
            return false;
        }

        //        if (scheme.equals("slate") || scheme.equals("http") || scheme.equals("https")) {
        if (scheme.equals("slate")) {
            UriParse.clickSlate(mContext, uriStr, new Entry[]{articleItem}, view, new Class<?>[0]);
            return true;
        }
        Log.e("未能识别的uri  --1 ", uriStr);
        return false;
    }

    //    /**
    //     * 打开uri
    //     *
    //     * @param uriStr
    //     */
    //    public boolean openURI(String uriStr) {
    //        if (canOpenURI(uriStr)) {
    //            return false;
    //        }
    //
    //        Uri uri = Uri.parse(uriStr);
    //        String scheme = uri.getScheme();
    //        String path = uri.getPath();
    //        String command = "";
    //        String params = "";
    //
    //        if (TextUtils.equals(scheme, mListener.scheme())) {
    //            // NOTE slate://article/4/12/238/
    //            command = uri.getHost();
    //            if (TextUtils.isEmpty(command)) {
    //                mListener.unknownURI(uriStr);
    //                Log.e("未能识别的uri  --2 ", uriStr);
    //                return false;
    //            }
    //            params = path;
    //        } else {
    //            // NOTE http://www.xxx.com/slate/article/4/12/238/
    //            String[] pathArr = path.split("/");
    //            // NOTE {"",slate,article,4,12,238};
    //            if (pathArr.length < 2) {
    //                mListener.unknownURI(uriStr);
    //                Log.e("未能识别的uri  --3 ", uriStr);
    //                return false;
    //            }
    //            command = pathArr[2];
    //            int start = mListener.scheme().length() + command.length() + 2;
    //            if (start >= 0 && start <= path.length()) {
    //                params = path.substring(start);
    //            } else {
    //                mListener.unknownURI(uriStr);
    //                Log.e("未能识别的uri  --4 ", uriStr);
    //                return false;
    //            }
    //        }
    //
    //        List<String> paramsList = new ArrayList<String>();
    //        if (!TextUtils.isEmpty(params)) {
    //            if (params.startsWith("/")) params = params.substring(1);
    //            String[] pArr = params.split("/");
    //            for (String p : pArr) {
    //                paramsList.add(p);
    //            }
    //        }
    //
    //        return handleURICommand(command, params, paramsList);
    //    }

    private boolean handleURICommand(String command, String params, List<String> paramsList) {
        String methodName = command + "Command";
        Object[] args = {command, params, paramsList};
        try {
            InvokeMethod.invokeMethod(mListener, methodName, args);
        } catch (Exception e) {
            e.printStackTrace();
            // 弹出dialog提示异常信息
            //            mListener.unknownCommand(command, params, paramsList);
            return false;
        }

        return true;
    }

}
