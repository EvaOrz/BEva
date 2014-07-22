package cn.com.modernmediasolo.api;

import android.content.Context;
import android.os.Handler;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.SoloColumn;
import cn.com.modernmediaslate.listener.DataCallBack;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

public class SoloOperateController {
	private static SoloOperateController instance;
	private Context mContext;

	private Handler mHandler = new Handler();

	private SoloOperateController(Context mContext) {
		this.mContext = mContext;
	}

	public static synchronized SoloOperateController getInstance(Context context) {
		if (instance == null)
			instance = new SoloOperateController(context);
		return instance;
	}

	private void sendMessage(final Entry entry,
			final FetchEntryListener listener) {
		synchronized (mHandler) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					listener.setData(entry);
				}
			});
		}
	}

	/**
	 * 独立栏目index
	 * 
	 * @param catId
	 * @param fromOffset
	 *            (from_0)取from前的所有数据(最新数据)
	 * @param toOffset
	 *            (0_to)取to后的所有数据(旧数据) ......0_0代表取全部数据
	 * @param soloColumn
	 * @param fecthNew
	 *            是否获取新数据
	 * @param listener
	 * @return
	 */
	public void getSoloCatIndex(String catId, String fromOffset,
			String toOffset, SoloColumn soloColumn, boolean fecthNew,
			int position, final FetchEntryListener listener) {
		final GetSoloCatIndexOperate operate = new GetSoloCatIndexOperate(
				mContext, catId, fromOffset, toOffset, soloColumn, position);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getCatIndexArticle() : null,
						listener);
			}
		});
	}

	/**
	 * 独立栏目文章列表
	 * 
	 * @param catId
	 * @param issue
	 * @param listener
	 */
	public void getSoloArticleList(String catId, String fromOffset,
			String toOffset, boolean fetchNew, final FetchEntryListener listener) {
		final GetSoloArticleListOperate operate = new GetSoloArticleListOperate(
				mContext, catId, fromOffset, toOffset, true, fetchNew);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getArticleList() : null, listener);
			}
		});
	}

	/**
	 * 获取文章详情
	 * 
	 * @param issue
	 * @param columnId
	 * @param articleId
	 * @param listener
	 */
	public void getSoloArticleById(FavoriteItem detail,
			final FetchEntryListener listener) {
		if (detail == null) {
			sendMessage(null, listener);
			return;
		}
		final GetSoloArticleOperate operate = new GetSoloArticleOperate(detail);
		operate.asyncRequest(mContext, true, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getAtlas() : null, listener);
			}
		});
	}

	/**
	 * 获取独立栏目列表
	 * 
	 * @param listener
	 */
	public void getSoloColumn(boolean useCache,
			final FetchEntryListener listener) {
		final GetSoloColumnOperate operate = new GetSoloColumnOperate();
		operate.asyncRequest(mContext, useCache, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getColumn() : null, listener);
			}
		});
	}
}
