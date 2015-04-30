package cn.com.modernmediaslate.unit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

import android.os.Environment;
import android.text.TextUtils;
import cn.com.modernmediaslate.SlateApplication;

/**
 * 收集页面访问时间以及接口访问时间
 * 
 * @author jiancong
 * 
 */
public class TimeCollectUtil {
	public static final String EVENT_LAUNCH_APP = "LaunchApp";
	public static final String EVENT_SPLASH = "Splash";
	public static final String EVENT_HTML_ADV = "HTMLAdv";
	public static final String EVENT_OPEN_VISION = "OpenVision";
	public static final String EVENT_OPEN_COLUMN = "OpenColumn_";
	public static final String EVENT_OPEN_PUSH = "OpenPush";

	private final static String FILE_NAME = "time_collect.txt";
	private String filePath = "";
	private static TimeCollectUtil instance;
	private HashMap<String, String> timeMap = new HashMap<String, String>();

	public static TimeCollectUtil getInstance() {
		if (instance == null) {
			instance = new TimeCollectUtil();
		}
		instance.createFile();
		return instance;
	}

	private TimeCollectUtil() {
	}

	/**
	 * 只统计iweekly
	 * 
	 * @return
	 */
	private boolean needLog() {
		return SlateApplication.APP_ID == 20;
	}

	private void createFile() {
		if (!needLog())
			return;
		if (TextUtils.isEmpty(filePath)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator
					+ SlateApplication.mConfig.getCache_file_name()
					+ File.separator + FILE_NAME;
		}
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				SlatePrintHelper.print("create time collect file failed!");
			}
		}
	}

	/**
	 * 存入页面进入时间和结束时间
	 * 
	 * @param event
	 * @param isStart
	 */
	public void savePageTime(String event, boolean isStart) {
		if (!needLog())
			return;
		String state = isStart ? "start" : "end";
		saveTime(1, event, state, "");
	}

	/**
	 * 存入接口请求时间和结束时间
	 * 
	 * @param url
	 *            接口地址
	 * @param isStart
	 *            是否开始
	 * @param resultCode
	 *            接口响应码(没有拿到的时候异常传-1)
	 * @param isCache
	 *            是否缓存 (如果为false并且resultCode!=200，说明接口请求错误，并且没有拿到缓存)
	 */
	public void saveRequestTime(String url, boolean isStart, int resultCode,
			boolean isCache) {
		if (!needLog())
			return;
		// 如果是请求开始，那么传"";如果是请求结束，那么传resultCode
		String state = isStart ? "" : resultCode + "";

		String cache = "";
		if (!isStart) {
			cache = isCache ? "1" : "0";
		}
		saveTime(0, url, state, cache);
	}

	/**
	 * 存储记录，格式:id,type,name,timestamp,state,isCache,timespan
	 * 
	 * @param type
	 *            分辨是UI统计还是接口统计。值为0，1。0为接口统计，1为UI统计
	 * @param name
	 *            eventName或者url
	 * @param state
	 *            "start:事件开始,"end":时间结束
	 * @param isCache
	 *            接口请求到的数据是否为缓存数据("0":不是；"1":是)，若果不是接口请求(type为1时)，则为空字符串
	 */
	private void saveTime(int type, String name, String state, String isCache) {
		String id = "";// 事件起始时间
		String timespan = "";
		String suffix = "\r\n";
		double time = System.currentTimeMillis() / 1000.0;
		DecimalFormat format = new DecimalFormat("0.000");
		String fTime = format.format(time);
		if (TextUtils.equals(state, "start") || TextUtils.isEmpty(state)) {
			id = fTime;
			timeMap.put(name, id);
		} else if (timeMap.containsKey(name)) {
			id = timeMap.get(name);
			timespan = format.format(time - ParseUtil.stod(id)); // 时间差
		}
		String data = id + "," + type + "," + name + "," + fTime + "," + state
				+ "," + isCache + "," + timespan + suffix;
		write(data);
	}

	private synchronized void write(String data) {
		final String str = data;
		new Thread(new Runnable() {

			@Override
			public void run() {
				FileWriter writer = null;
				try {
					writer = new FileWriter(new File(filePath), true);
					writer.write(str);
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (writer != null) {
						try {
							writer.close();
						} catch (IOException e1) {
							writer = null;
						}
					}
				}
			}
		}).start();
	}

	public void deleteFile() {
		timeMap.clear();
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}

}
