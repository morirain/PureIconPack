package com.by_syk.lib.nanoiconpack.util;


import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by morirain on 2018/4/30.
 * E-Mail Address：morirain.dev@outlook.com
 */


public class LogUtil {
    private static boolean mIsLoggingEnabled = false;
    private static String mLoggingTag = "AndroidHelpers";

    public LogUtil() {
    }

    public static void setLoggingEnabled(boolean enabled) {
        mIsLoggingEnabled = enabled;
    }

    public static void setLoggingTag(@NonNull String tag) {
        mLoggingTag = tag;
    }

    public static void d(String message) {
        if (mIsLoggingEnabled) {
            Log.d(mLoggingTag, "" + message);
        }

    }

    public static void e(String message) {
        if (mIsLoggingEnabled) {
            Log.e(mLoggingTag, "" + message);
        }

    }
}