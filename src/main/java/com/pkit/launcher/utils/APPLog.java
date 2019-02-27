package com.pkit.launcher.utils;

import android.util.Log;

public class APPLog {
	public static final int INFO = 0x03;
	public static final int DEBUG = 0x02;
	public static final int WARN = 0x01;
	public static final int ERROR = 0x00;
	private static final String TAG = "PK_Launcher_Log";
	private static final String SEPARATE = " --- ";
	private static int currentLevel = INFO;

	public static void setLevel(int level) {
		currentLevel = level;
	}

	private static String getPrefix() {
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		long threadId = currentThread.getId();
		String prefix = threadName + "(" + threadId + "):";
		return prefix;
	}

	private static String getTrace() {
		try {
			Throwable t = new Throwable();
			StackTraceElement trace = t.getStackTrace()[2];
			String method = trace.getClassName() + "." + trace.getMethodName() + "[" + trace.getLineNumber() + "]";
			return method;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void printInfo(String msg) {
		try {
			if (currentLevel >= INFO) {
				String log = getPrefix() + getTrace() + SEPARATE + msg;
				Log.i(TAG, log);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printDebug(String msg) {
		try {
			if (currentLevel >= DEBUG) {
				String log = getPrefix() + getTrace() + SEPARATE + msg;
				Log.d(TAG, log);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printWarn(String msg) {
		try {
			if (currentLevel >= WARN) {
				String log = getPrefix() + getTrace() + SEPARATE + msg;
				Log.w(TAG, log);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printError(Exception e) {
		try {
			if (currentLevel >= ERROR) {
				StackTraceElement[] stackTraces = e.getStackTrace();
				String log = getPrefix() + e.toString();
				Log.e(TAG, log);
				for (StackTraceElement stackTrace : stackTraces) {
					log = getPrefix() + "		at " + stackTrace.getClassName() + "." + stackTrace.getMethodName() + "(" + stackTrace.getFileName() + ":"
							+ stackTrace.getLineNumber() + ")";
					Log.e(TAG, log);
				}
				Throwable t = e.getCause();
				if (t == null) {
					return;
				}
				StackTraceElement[] causes = t.getStackTrace();
				log = getPrefix() + t.toString();
				Log.e(TAG, log);
				for (StackTraceElement stackTrace : causes) {
					log = getPrefix() + "		at " + stackTrace.getClassName() + "." + stackTrace.getMethodName() + "(" + stackTrace.getFileName() + ":"
							+ stackTrace.getLineNumber() + ")";
					Log.e(TAG, log);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void printThrowable(Throwable throwable) {
		try {
			if (currentLevel >= ERROR) {
				StackTraceElement[] stackTraces = throwable.getStackTrace();
				String log = getPrefix() + throwable.toString();
				Log.e(TAG, log);
				for (StackTraceElement stackTrace : stackTraces) {
					log = getPrefix() + "		at " + stackTrace.getClassName() + "." + stackTrace.getMethodName() + "(" + stackTrace.getFileName() + ":"
							+ stackTrace.getLineNumber() + ")";
					Log.e(TAG, log);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
