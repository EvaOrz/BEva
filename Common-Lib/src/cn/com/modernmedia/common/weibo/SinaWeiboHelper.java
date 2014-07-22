
package cn.com.modernmedia.common.weibo;

import java.io.IOException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;


public class SinaWeiboHelper {
	
	private static final String TAG = "OAuthForSinaWeibo";
	private static final String CONSUMER_KEY = "2115890114";
	private static final String REDIRECT_URL = "http://";
	
	private static SinaWeiboHelper instance;
	private Weibo mWeibo;
	private Oauth2AccessToken accessToken;
	
	/**
	 * @return the accessToken
	 */
	public Oauth2AccessToken getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(Oauth2AccessToken accessToken) {
		this.accessToken = accessToken;
	}

	public static SinaWeiboHelper getInstance() {
		if (instance == null) {
			instance = new SinaWeiboHelper();
		}
		return instance;
	}
	
	private SinaWeiboHelper() {
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
	}
	
	public void oAuth(Context context) {
		mWeibo.authorize(context, new AuthDialogListener(context));
	}
	
	public void oAuth(Context context, WeiboAuthListener listener) {
		mWeibo.authorize(context, listener);
	}
	
	public boolean checkIsOAuthed(Context context) {
		if (accessToken == null) {
			accessToken = AccessTokenKeeper.readAccessToken(context);
		}
		return accessToken != null && accessToken.isSessionValid();
	}
	
	public void unBundle(Context context) {
		AccessTokenKeeper.clear(context);
		accessToken = null;
		mWeibo = null;
		instance = null;
        Toast.makeText(context, "解除绑定成功", Toast.LENGTH_LONG).show();
	}
	
	public boolean sendText(String content, String lat, String lon) {
		StatusesAPI api = new StatusesAPI(accessToken);
		api.update(content, lat, lon, new RequestListener() {

			@Override
			public void onComplete(String arg0) {
				Log.d(TAG, "the result of uploading text is:" + arg0);		
			}

			@Override
			public void onError(WeiboException arg0) {
				Log.d(TAG, "sendText_weiboexception", arg0);
			}

			@Override
			public void onIOException(IOException arg0) {
				Log.d(TAG, "sendText_weiboexception", arg0);				
			}
			
		});
		return true;
	}
	
	
	public boolean sendImage(String content, String filePath, String lat, String lon) {
		StatusesAPI api = new StatusesAPI(accessToken);
		api.upload(content, filePath, lat, lon, new RequestListener() {
			
			@Override
			public void onIOException(IOException arg0) {
				Log.d(TAG, "sendImage_upload_ioexception", arg0);				
			}
			
			@Override
			public void onError(WeiboException arg0) {
				Log.d(TAG, "sendImage_upload_error", arg0);
			}
			
			@Override
			public void onComplete(String arg0) {
				Log.d(TAG, "the result of uploading image is:" + arg0);
			}
		});
		return true;
	}
	
	class AuthDialogListener implements WeiboAuthListener {
		
		private Context mContext;
		
		AuthDialogListener (Context context) {
			mContext = context;
		}
		
        @Override
        public void onComplete(Bundle values) {
            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            accessToken = new Oauth2AccessToken(token, expires_in);
            if (accessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new java.util.Date(accessToken
                                .getExpiresTime()));
                String msg = "认证成功: \r\n access_token: " + token + "\r\n"
                        + "expires_in: " + expires_in + "\r\n有效期：" + date;
                Log.d(TAG, msg);
                
                AccessTokenKeeper.keepAccessToken(mContext, accessToken);
                Toast.makeText(mContext, "认证成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(WeiboDialogError e) {
            Toast.makeText(mContext,
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(mContext, "Auth cancel", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(mContext,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }
}
