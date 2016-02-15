package cn.com.modernmedia.util;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AdvList.AdvSource;

public class AdvTest {
	private static String startTime = "1374728191";
	private static String endTime = "2422607825";

	public static void addRuBanIBBTest(AdvList advList) {
		AdvItem item = new AdvItem();
		AdvSource advSource = new AdvSource();
		advSource
				.setUrl("http://s1.cdn.imlady.bbwc.cn/issue_255/articles/22151/2013/0904/20130904032300880_640x0.jpg");
		item.getSourceList().add(advSource);
		item.setStartTime(startTime);
		item.setEndTime(endTime);
		item.setAdvType(1);
		item.setEffects(AdvList.IBB);
		item.setAutoClose(3);

		List<AdvItem> list = new ArrayList<AdvItem>();
		list.add(item);
		advList.getAdvMap().put(item.getAdvType(), list);
	}

	public static void addRuBanLohasTest(AdvList advList) {
		AdvItem item = new AdvItem();
		AdvSource advSource = new AdvSource();
		advSource
				.setUrl("http://s1.cdn.imlady.bbwc.cn/issue_255/articles/22248/2013/0829/20130829064015896_640x0.jpg");
		item.getSourceList().add(advSource);
		item.setStartTime(startTime);
		item.setEndTime(endTime);
		item.setAdvType(1);
		item.setEffects(AdvList.ILOHAS);
		AdvSource advSource2 = new AdvSource();
		advSource2
				.setUrl("http://s1.cdn.imlady.bbwc.cn/issue_255/articles/22151/2013/0904/20130904032300880_640x0.jpg");
		item.getSourceList().add(advSource2);

		List<AdvItem> list = new ArrayList<AdvItem>();
		list.add(item);
		advList.getAdvMap().put(item.getAdvType(), list);
	}

	public static void addRuBanWeeklyTest(AdvList advList) {
		AdvItem item = new AdvItem();
		item.setAdvType(1);
		item.setEffects(AdvList.IWEEKLY);
		item.setStartTime(startTime);
		item.setEndTime(endTime);
		AdvSource source = new AdvSource();
		source.setUrl("http://s3.cdn.bbwc.cn/issue_0/articles/0/2015/0123/54c20d7b793ee.jpg");
		item.getSourceList().add(source);
		source = new AdvSource();
		source.setUrl("http://s3.cdn.bbwc.cn/issue_0/articles/0/2014/1119/546c05c2208ff.jpg");
		item.getSourceList().add(source);
		source = new AdvSource();
		source.setUrl("http://s1.cdn.bbwc.cn/issue_0/category/2015/0127/54c79950ec350_640x0.jpg");
		item.getSourceList().add(source);
		item.setShowType(0);
		item.setAutoClose(2);
		List<AdvItem> list = new ArrayList<AdvItem>();
		list.add(item);
		advList.getAdvMap().put(item.getAdvType(), list);
	}

	public static void addRuBanZipTest(AdvList advList) {
		AdvItem item = new AdvItem();
		item.setAdvType(1);
		item.setEffects(AdvList.IWEEKLY);
		item.setStartTime(startTime);
		item.setEndTime(endTime);
		AdvSource source = new AdvSource();
		source.setUrl("http://s3.cdn.bbwc.cn/issue_0/articles/0/2015/0116/54b880ef09e3c.zip");
		item.getSourceList().add(source);
		item.setShowType(1);
		List<AdvItem> list = new ArrayList<AdvItem>();
		list.add(item);
		advList.getAdvMap().put(item.getAdvType(), list);
	}

	public static void addShiyeAdvTest(AdvList advList) {
		AdvSource source = new AdvSource();
		source.setTitle("客户端本地测试广告");
		source.setLink("http://www.bbwc.cn");
		source.setUrl("http://develop.bbwc.cn/dev/issue_434/articles/0/2014/0516/20140516163833825_2048x1536.jpg");
		source.setVideolink("http://www.ydtsystem.com/CardImage/21/video/20140305/20140305124807_37734.mp4");
		source.setWidth(2);
		source.setHeight(1);

		AdvItem item = new AdvItem();
		item.getSourceList().add(source);
		item.setStartTime(startTime);
		item.setEndTime(endTime);
		item.setAdvType(3);
		item.setTagname("cat_191");
		item.setSection(1);
		item.setPosId("2");
		item.setSort("1");
		List<AdvItem> list = new ArrayList<AdvItem>();
		list.add(item);
		advList.getAdvMap().get(3).addAll(list);
	}

	public static void addZuiXinTitleAdvTest(AdvList advList) {
		AdvSource source = new AdvSource();
		source.setTitle("客户端本地测试广告");
		source.setLink("http://www.bbwc.cn");
		source.setUrl("http://develop.bbwc.cn/dev/issue_434/articles/0/2014/0516/20140516163833825_2048x1536.jpg");
		source.setVideolink("http://www.ydtsystem.com/CardImage/21/video/20140305/20140305124807_37734.mp4");
		source.setWidth(2);
		source.setHeight(1);

		AdvItem item = new AdvItem();
		item.getSourceList().add(source);
		item.setStartTime(startTime);
		item.setEndTime(endTime);
		item.setAdvType(3);
		item.setTagname("cat_1375");
		item.setSection(1);
		item.setPosId("1");
		item.setSort("1");
		List<AdvItem> list = new ArrayList<AdvItem>();
		list.add(item);
		advList.getAdvMap().get(3).addAll(list);
	}

	public static void addXinWenListAdvTest(AdvList advList) {
		AdvSource source = new AdvSource();
		source.setTitle("客户端本地测试广告");
		source.setLink("http://www.bbwc.cn");
		source.setUrl("http://develop.bbwc.cn/dev/issue_434/articles/0/2014/0516/20140516163833825_2048x1536.jpg");
		source.setVideolink("http://www.ydtsystem.com/CardImage/21/video/20140305/20140305124807_37734.mp4");
		source.setWidth(2);
		source.setHeight(1);

		AdvItem item = new AdvItem();
		item.getSourceList().add(source);
		item.setStartTime(startTime);
		item.setEndTime(endTime);
		item.setAdvType(3);
		item.setTagname("cat_192");
		item.setSection(1);
		item.setPosId("2");
		item.setSort("2");
		List<AdvItem> list = new ArrayList<AdvItem>();
		list.add(item);
		advList.getAdvMap().get(3).addAll(list);
	}

	public static void addCatAdvTest(AdvList advList) {
		AdvSource source = new AdvSource();
		source.setTitle("dunhill");
		// pic.setLink("slate://adv/article/262/16/23351/0/0");
		source.setLink("http://www.bbwc.cn");
		source.setUrl("http://develop.bbwc.cn/dev/issue_434/articles/0/2014/0516/20140516163833825_2048x1536.jpg");
		source.setWidth(2);
		source.setHeight(1);

		AdvItem item = new AdvItem();
		item.getSourceList().add(source);
		item.setStartTime("1374728191");
		item.setEndTime("1424728191");
		// case 4-11
		item.setAdvType(3);
		// case 10
		// item.setTagname("*");
		// item.setPosId("2");
		// item.setSort("1");
		// case 11
		// item.setTagname("*/2");
		// item.setPosId("1");
		// item.setSort("1");
		// case 4,5,8,9
		// item.setTagname("cat_11"); //科技
		// case 6-7
		item.setTagname("cat_15"); // 今日焦点
		// case 4
		// item.setPosId("1");
		// item.setSort("1");
		// case 5
		// item.setPosId("2");
		// item.setSort("2");
		// case 6（case有误）
		item.setSection(2);
		item.setPosId("2");
		item.setSort("2");
		// case 7
		// item.setSort("2");
		// item.setPosId("3");
		// item.setSection(2);
		// case 8
		// item.setSort("*");
		// item.setPosId("2");
		// case 9
		// item.setSort("*/2");
		// item.setPosId("2");

		// pos = 3
		// AdvItem item3 = new AdvItem();
		// item3.setAdvType(3);
		// item3.setIssueId("*");
		// item3.setStartTime("1378200121");
		// item3.setEndTime("1398472945");
		// item3.setCatId("*");
		// AdvPic pic3 = new AdvPic();
		// pic3.setTitle("test adv pos = 1");
		// pic3.setLink("slate://adv/video/http://sadsadsa");
		// item3.getPicList().add(pic3);
		// item3.setPosId("2");
		// item3.setSort("1");

		// AdvItem item3_1 = new AdvItem();
		// item3_1.setAdvType(3);
		// item3_1.setIssueId("*");
		// item3_1.setStartTime("1378200121");
		// item3_1.setEndTime("1398472945");
		// item3_1.setCatId("*");
		// AdvPic pic_1 = new AdvPic();
		// pic_1.setTitle("test adv pos = *");
		// // pic_1.setLink("slate://adv/article/0/32/23486/0/0");
		// pic_1.setLink("slate://adv/101");
		// item3_1.getPicList().add(pic_1);
		// item3_1.setPosId("1");
		// item3_1.setSort("*");

		// AdvItem item3_2 = new AdvItem();
		// item3_2.setAdvType(3);
		// item3_2.setIssueId("*");
		// item3_2.setStartTime("1378200121");
		// item3_2.setEndTime("1398472945");
		// item3_2.setCatId("*");
		// AdvPic pic_2 = new AdvPic();
		// pic_2.setTitle("test adv pos = */2");
		// pic_2.setLink("slate://adv/100");
		// item3_2.getPicList().add(pic_2);
		// item3_2.setPosId("2");
		// item3_2.setSort("*/2");

		List<AdvItem> list = new ArrayList<AdvItem>();
		list.add(item);
		// // list.add(item3);
		// // list.add(item3_1);
		// // list.add(item3_2);
		advList.getAdvMap().put(3, list);
	}

	public static void addArticleAdvTest(AdvList advList) {
		List<AdvItem> list = new ArrayList<AdvItem>();
		AdvItem item = new AdvItem();
		item.setAdvId(100);
		item.setAdvType(5);
		item.setStartTime("1374728191");
		item.setEndTime("1428472945");
		item.setTagname("*");
		AdvSource source = new AdvSource();
		// source.setUrl("http://develop.bbwc.cn/dev/issue_434/articles/0/2014/0516/20140516163833825_2048x1536.jpg");
		source.setUrl("http://www.baidu.com/"); // case 19用
		item.getSourceList().add(source);
		// case 12,15
		// item.setSort("2");
		// case 13,16
		// item.setSort("*");
		// case 14,17
		// item.setSort("*/2");

		// case 18
		// item.setSort("*");
		// item.setShowType(0);
		// AdvSource advSource2 = new AdvSource();
		// advSource2
		// .setUrl("http://s1.cdn.imlady.bbwc.cn/issue_255/articles/22151/2013/0904/20130904032300880_640x0.jpg");
		// item.getSourceList().add(advSource2);

		// case 19
		item.setSort("*");
		item.setShowType(1);

		list.add(item);

		// item = new AdvItem();
		// item.setAdvType(5);
		// item.setIssueId("*");
		// item.setStartTime("1378200121");
		// item.setEndTime("1398472945");
		// item.setCatId(AdvList.ARTICLE_NULL_CAT);
		// item.getPosInfo().setSort("*/2");
		// item.getPageInfo().setUrl("http://www.baidu.com/");
		// list.add(item);
		//
		// // atlas
		// item = new AdvItem();
		// item.setAdvType(5);
		// item.setIssueId("*");
		// item.setStartTime("1378200121");
		// item.setEndTime("1398472945");
		// item.setCatId("16");
		// item.getPosInfo().setSort("2");
		// item.setEffects(AdvList.ATLAS);
		// AdvPic pic = new AdvPic();
		// pic.setTitle("test altas title");
		// pic.setDesc("test altas desc");
		// pic.setUrl("http://s1.cdn.imlady.bbwc.cn/issue_255/articles/22248/2013/0829/20130829064015896_640x0.jpg");
		// item.getPicList().add(pic);
		// pic = new AdvPic();
		// pic.setTitle("test altas title");
		// pic.setDesc("test altas desc");
		// pic.setUrl("http://s1.cdn.imlady.bbwc.cn/issue_255/articles/22151/2013/0904/20130904032300880_640x0.jpg");
		// item.getPicList().add(pic);
		// list.add(item);

		advList.getAdvMap().put(5, list);
	}

	public static void addFuDongAdvTest(AdvList advList) {
		List<AdvItem> list = new ArrayList<AdvItem>();
		AdvItem item = new AdvItem();
		item.setAdvId(100);
		item.setAdvType(4);
		item.setStartTime("1374728191");
		item.setEndTime("1428472945");
		item.setTagname("*");
		AdvSource source = new AdvSource();
		source.setUrl("http://develop.bbwc.cn/dev/issue_434/articles/0/2014/0516/20140516163833825_2048x1536.jpg");
		item.getSourceList().add(source);

		list.add(item);
		advList.getAdvMap().put(4, list);
	}
}
