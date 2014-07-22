package cn.com.modernmedia.api;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.model.LastestArticleId;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.util.PrintHelper;

/**
 * ��ȡ���µ�����id
 * 
 * @author ZhuQiao
 * 
 */
@SuppressLint("UseSparseArrays")
public class GetLastestArticleIdOperate extends BaseOperate {
	private static final String FILE_NAME = ConstData
			.getLastestArticleIdFileName();
	private String url;
	private ReadDb readDb;
	private LastestArticleId mLastestArticleId;

	protected GetLastestArticleIdOperate(Context context, int issueId) {
		url = UrlMaker.getLastestArticleId(issueId);
		readDb = ReadDb.getInstance(context);
		mLastestArticleId = new LastestArticleId();
	}

	public LastestArticleId getmLastestArticleId() {
		return mLastestArticleId;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handler(JSONObject jsonObject) {
		HashMap<Integer, HashMap<Integer, String>> mOldEntryIds = fecthLocalData();
		HashMap<Integer, HashMap<Integer, String>> mLastestEntryIds = handlerMap(
				jsonObject, true);
		if (mOldEntryIds == null) {
			return;
		}
		mLastestArticleId = new LastestArticleId();
		Iterator<Object> mk = jsonObject.keys();
		while (mk.hasNext()) {
			int key = ParseUtil.stoi(mk.next().toString(), -1);
			HashMap<Integer, String> entryInfo = new HashMap<Integer, String>();
			// int count = mLastestEntryIds.get(key).size();
			if (mOldEntryIds.containsKey(key)) {
				HashMap<Integer, String> mOldColumnMap = mOldEntryIds.get(key);
				HashMap<Integer, String> mLastestColumnMap = mLastestEntryIds
						.get(key);
				for (int articleKey : mLastestColumnMap.keySet()) {
					if (mOldColumnMap.containsKey(articleKey)) {
						// �����ǰ���µĸ���ʱ�����ϴλ�ȡ�ĸ���ʱ��һ��������id���Ѷ����ݿ��У�˵����ƪ�����Ѷ�
						if (mOldColumnMap.get(articleKey).equals(
								mLastestColumnMap.get(articleKey))
								&& readDb.isReaded(articleKey)) {
							// count--;
						} else {
							if (readDb.isReaded(articleKey))
								readDb.deleteArticle(articleKey);
							entryInfo.put(articleKey,
									mLastestColumnMap.get(articleKey));
							mLastestArticleId.getUnReadedId().put(articleKey,
									key);
						}
					} else {
						if (readDb.isReaded(articleKey))
							readDb.deleteArticle(articleKey);
						entryInfo.put(articleKey, mLastestColumnMap.get(articleKey));
						mLastestArticleId.getUnReadedId().put(articleKey, key);
					}
				}
				mLastestArticleId.getUnReadedEntryIds().put(key, entryInfo);
			} else {
				HashMap<Integer, String> mLastestColumnMap = mLastestEntryIds
						.get(key);
				mLastestArticleId.getUnReadedEntryIds().put(key,
						mLastestColumnMap);
				addUnReadedArticleId(key, mLastestColumnMap);
			}
			// mLastestArticleId.getCount()[i] = count;
		}
	}

	private void addUnReadedArticleId(int key,
			HashMap<Integer, String> mLastestColumnMap) {
		for (int articleKey : mLastestColumnMap.keySet()) {
			if (readDb.isReaded(articleKey))
				readDb.deleteArticle(articleKey);
			mLastestArticleId.getUnReadedId().put(articleKey, key);
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap<Integer, HashMap<Integer, String>> handlerMap(
			JSONObject jsonObject, boolean lastest) {
		HashMap<Integer, HashMap<Integer, String>> map = new HashMap<Integer, HashMap<Integer, String>>();
		Iterator<Object> k = jsonObject.keys();
		while (k.hasNext()) {
			String name = k.next().toString();
			JSONArray arr = jsonObject.optJSONArray(name);
			// key:articleid;value:time
			HashMap<Integer, String> entryInfo = new HashMap<Integer, String>();
			if (!isNull(arr)) {
				// mLastestArticleId.getCount()[k] = arr.length();
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.optJSONObject(i);
					if (!isNull(obj)) {
						for (Iterator<String> iter = obj.keys(); iter.hasNext();) {
							String key = iter.next();
							String value = arr.optJSONObject(i).optString(key,
									"");
							entryInfo.put(ParseUtil.stoi(key, -1), value);
							if (lastest)
								mLastestArticleId.getUnReadedId().put(
										ParseUtil.stoi(key, -1),
										ParseUtil.stoi(name, -1));
						}
					}
				}
				map.put(ParseUtil.stoi(name, -1), entryInfo);
			}
		}
		if (lastest)
			mLastestArticleId.setUnReadedEntryIds(map);
		return map;
	}

	/**
	 * ��ȡ��һ�θ��µ�������������
	 * 
	 * @return
	 */
	private HashMap<Integer, HashMap<Integer, String>> fecthLocalData() {
		String data = FileManager.getApiData(FILE_NAME);
		if (TextUtils.isEmpty(data))
			return null;
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
			PrintHelper.print(FILE_NAME + "�ļ����쳣�޸ģ��޷���װ��json���ݣ���");
			FileManager.deleteFile(FILE_NAME);
			return null;
		}
		return handlerMap(jsonObject, false);
	}

	@Override
	protected void saveData(String data) {
		FileManager.saveApiData(FILE_NAME, data);
	}

	@Override
	protected String getDefaultFileName() {
		return FILE_NAME;
	}
}
