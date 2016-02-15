//package cn.com.modernmedia.api;
//
//import org.json.JSONObject;
//
//import android.text.TextUtils;
//import cn.com.modernmedia.model.TagInfoList;
//import cn.com.modernmedia.newtag.db.TagInfoListDb;
//import cn.com.modernmedia.util.PrintHelper;
//import cn.com.modernmediaslate.model.Entry;
//import cn.com.modernmediaslate.unit.ParseUtil;
//
///**
// * 获取tag子标签
// * 
// * @author jiancong
// * 
// */
//public class GetTagChildOperate extends GetTagInfoOperate {
//	private String url;
//	private String group = "";
//	private String parentTagName;
//
//	public GetTagChildOperate(String parentTagName, String tagName,
//			String group, String top) {
//		super(tagName);
//		this.parentTagName = parentTagName;
//		this.group = group;
//		url = UrlMaker.getTagChild(parentTagName, tagName, group, top);
//	}
//
//	@Override
//	protected String getUrl() {
//		return url;
//	}
//
//	@Override
//	protected void handler(JSONObject jsonObject) {
//		super.handler(jsonObject);
//		if (TextUtils.equals(group, "3")) {
//			TagInfoListDb.getInstance(getmContext()).clearTable(null);
//			TagInfoListDb.getInstance(getmContext()).addEntry(tagInfoList);
//		}
//	}
//
//	@Override
//	public boolean fecthLocalData(String fileName) {
//		Entry entry = TagInfoListDb.getInstance(getmContext()).getEntry(this,
//				parentTagName, tagName, group, "");
//		if (entry instanceof TagInfoList) {
//			TagInfoList list = (TagInfoList) entry;
//			if (ParseUtil.listNotNull(list.getList())) {
//				tagInfoList = (TagInfoList) entry;
//				if (callBack != null) {
//					PrintHelper.print("from db:tag/child?group=3" + "===="
//							+ url);
//					callBack.callback(true, false);
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//}
