package com.avit.xhttp.utils;

import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 *
 */
final public class AvitLog {

    private static final int VERBOSE = 4;
    private static final int INFO = 3;
    private static final int DEBUG = 2;
    private static final int WARN = 1;
    private static final int ERROR = 0;

    private static String LOG_TAG = "XHttp";
    public static final int DEBUG_LOG = 4;

    private static boolean isLoggable;

    public static void init(String tag, boolean loggable) {
        LOG_TAG = tag;
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(2)         // (Optional) How many method line to show. Default 2
                .methodOffset(2)        // (Optional) Hides internal method calls up to offset. Default 5
//                .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag(tag+"Logger")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        isLoggable = loggable;
    }

    public static void init(String tag){
        init(tag,false);
    }

    /**
     *
     * @param tag tag
     * @param args string
     */
    public static void v(String tag, Object... args) {
        if (DEBUG_LOG > 3) {
            Log.v(LOG_TAG, genMSG(tag, args));
        }
    }

    public static void i(String tag, Object... args) {
        if (DEBUG_LOG > 2) {
            Log.i(LOG_TAG, genMSG(tag, args));
        }
    }

    public static void d(String tag, Object... args) {
        if (DEBUG_LOG > 1) {
            Log.d(LOG_TAG, genMSG(tag, args));
        }
    }

    public static void w(String tag, Object... args) {
        if (DEBUG_LOG > 0) {
            Log.w(LOG_TAG, genMSG(tag, args));
        }
    }

    public static void e(String tag, Object... args) {
        Log.e(LOG_TAG, genMSG(tag, args));
    }

    public static void json(String json) {
        if (isLoggable) {
            Logger.json(json);
        }
    }

    public static void object(Object obj){
        Logger.d(obj);
    }

    public static void xml(String xml){
        if (isLoggable) {
            Logger.d(xml);
        }
    }



    private static String genMSG(String tag, Object... args) {
        String msg = tag + "=>";
        for (Object arg : args) {
            msg += arg + " | ";
        }

        return msg;
    }


}
