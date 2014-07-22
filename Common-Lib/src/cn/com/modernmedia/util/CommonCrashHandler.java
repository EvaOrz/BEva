package cn.com.modernmedia.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;

/**
 * 崩溃日志
 * 
 * @author ZhuQiao
 * 
 */
public class CommonCrashHandler implements UncaughtExceptionHandler {

	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static CommonCrashHandler INSTANCE;

	private CommonCrashHandler() {
	}

	public static CommonCrashHandler getInstance() {
		if (INSTANCE == null)
			INSTANCE = new CommonCrashHandler();
		return INSTANCE;
	}

	public void init(Context ctx) {
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		String msg = getCrashInfoToFile(ex);
		FileManager.saveApiData(ConstData.getCrashLogFilename(), msg);
		if (mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	private String getCrashInfoToFile(Throwable ex) {
		if (ex == null) {
			return null;
		}
		StringWriter info = null;
		PrintWriter printWriter = null;
		try {
			info = new StringWriter();
			printWriter = new PrintWriter(info);
			ex.printStackTrace(printWriter);
			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			return info.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (printWriter != null) {
					printWriter.close();
				}
				if (info != null) {
					info.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}