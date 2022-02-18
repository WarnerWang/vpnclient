package com.buzz.vpn.utils;

import android.os.SystemClock;
import android.util.Log;

import java.util.Locale;

/**
 */
public class Logger {
    private static final String TAG = Logger.class.getSimpleName();
    private static final String space = "----------------------------------------------------------------------------------------------------";
    private static int tagLength = 28;
//    private static boolean enable = Api.ins().isDebug();
    private static boolean enable = true;
    private static String preTag = "^_^";
    private static boolean LOG_V = true;
    private static boolean LOG_D = true;
    private static boolean LOG_I = true;
    private static boolean LOG_W = true;
    private static boolean LOG_E = true;

    public enum LogLevel {
        V, D, I, W, E
    }

    private Logger() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void setTagLength(int tagLength) {
        Logger.tagLength = tagLength;
    }

    public static void setPreTag(String preTag) {
        Logger.preTag = preTag;
    }

    public static void setLogLevel(LogLevel logLevel) {
        LOG_V = true;
        LOG_D = true;
        LOG_I = true;
        LOG_W = true;
        LOG_E = true;
        switch (logLevel) {
            case E:
                LOG_W = false;
            case W:
                LOG_I = false;
            case I:
                LOG_D = false;
            case D:
                LOG_V = false;
        }
    }

    public static void setEnable(boolean enable) {
        Logger.enable = enable;
    }

    public static void v(String tag, String format, Object... args) {
        if (enable && LOG_V) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);

            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.v(tag, message);
        }
    }

    public static void v(Throwable throwable, String tag, String format, Object... args) {
        if (enable && LOG_V) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.v(tag, message, throwable);
        }
    }

    public static void d(String tag, String format, Object... args) {
        if (enable && LOG_D) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.d(tag, message);
        }
    }

    public static void d(String format, Object... args) {
        String tag = getTag();
        if (enable && LOG_D) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.d(tag, message);
        }
    }

    public static void d(Throwable throwable, String tag, String format, Object... args) {
        if (enable && LOG_D) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.d(tag, message, throwable);
        }
    }

    public static void i(String tag, String format, Object... args) {
        if (enable && LOG_I) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.i(tag, message);
        }
    }

    public static void i(String format, Object... args) {
        String tag = getTag();
        if (enable && LOG_I) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.i(tag, message);
        }
    }

    public static void i(Throwable throwable, String tag, String format, Object... args) {
        if (enable && LOG_I) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.i(tag, message, throwable);
        }
    }

    public static void w(String format, Object... args) {
        String tag = getTag();
        if (enable && LOG_W) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.w(tag, message);
        }
    }

    public static void w(String tag, String format, Object... args) {
        if (enable && LOG_W) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.w(tag, message);
        }
    }

    public static void w(Throwable throwable, String tag, String format, Object... args) {
        if (enable && LOG_W) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.w(tag, message, throwable);
        }
    }

    public static void e(String format, Object... args) {
        String tag = getTag();
        if (enable && LOG_E) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.e(tag, message);
        }
    }

    public static void e(String tag, String format, Object... args) {
        if (enable && LOG_E) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.e(tag, message);
        }
    }

    public static void e(Throwable throwable, String tag, String format, Object... args) {
        if (enable && LOG_E) {
            String message = buildMessage(format, args);
            tag = formatLength(preTag + tag, tagLength);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.e(tag, message, throwable);
        }
    }

    /**
     * print exception info
     *
     * @param throwable
     */
    public static void printException(Throwable throwable) {
        if (enable) {
            String message = buildMessage("", new Object[]{});
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.e(formatLength(preTag + TAG, tagLength), message, throwable);
        }
    }

    /**
     * print exception info
     */
    public static void printException(String tag, Throwable throwable) {
        if (enable) {
            String message = buildMessage("", new Object[]{});
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.e(formatLength(preTag + tag, tagLength), message, throwable);
        }
    }

    /**
     * print exception info
     */
    public static void printException(String tag, String message, Throwable throwable) {
        if (enable) {
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Log.e(tag, message, throwable);
        }
    }

    /**
     * 打印调用者栈信息
     * <p>
     * print use Android Log
     *
     * @see Log#i(String, String)
     */
    public static void printCaller(String tag) {
        if (!enable) {
            return;
        }

        try {
            String caller, callingClass, callFile;
            int lineNumber;
            StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
            StringBuilder infoBuffer = new StringBuilder();
            infoBuffer.append("==========BEGIN OF CALLER INFO============\n");
            for (int i = 1; i < trace.length; i++) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                caller = trace[i].getMethodName();
                callFile = trace[i].getFileName();
                lineNumber = trace[i].getLineNumber();
                String method = String.format(Locale.US, "[%03d] %s.%s(%s:%d)"
                        , Thread.currentThread().getId(), callingClass, caller, callFile, lineNumber);
                infoBuffer.append(method);
                infoBuffer.append("\n");
            }
            infoBuffer.append("==========END OF CALLER INFO============");
            Log.i(tag, infoBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String buildMessage(String format, Object[] args) {
        try {
            String msg = (args == null || args.length == 0) ? format : String.format(Locale.US, format, args);
            if (!enable) {
                return msg;
            }
            StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
            String caller = "";
            String callingClass = "";
            String callFile = "";
            int lineNumber = 0;
            for (int i = 2; i < trace.length; i++) {
                Class<?> clazz = trace[i].getClass();
                if (!clazz.equals(Logger.class)) {
                    callingClass = trace[i].getClassName();
                    callingClass = callingClass.substring(callingClass
                            .lastIndexOf('.') + 1);
                    caller = trace[i].getMethodName();
                    callFile = trace[i].getFileName();
                    lineNumber = trace[i].getLineNumber();
                    break;
                }
            }

            String method = String.format(Locale.US, "[%03d] %s.%s(%s:%d)"
                    , Thread.currentThread().getId(), callingClass, caller, callFile, lineNumber);

            return String.format(Locale.US, "%s> %s", formatLength(method, 93), msg);
        } catch (Exception e) {
            Logger.e(e, TAG, e.getMessage());
        }
        return "----->ERROR LOG STRING<------";
    }

    private static String getTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String callingClass = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(Logger.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass
                        .lastIndexOf('.') + 1);
                break;
            }
        }
        return callingClass;
    }

    private static String formatLength(String src, int len) {
        StringBuilder sb = new StringBuilder();
        if (src.length() >= len) {
            sb.append(src);
        } else {
            sb.append(src);
            sb.append(space.substring(0, len - src.length()));
        }
        return sb.toString();
    }

    /**
     * 耗时统计
     */
    public static class TimeCut {
        private long start;

        public TimeCut() {
            start = SystemClock.elapsedRealtime();
        }

        /**
         * 结束统计
         */
        public long end() {
            return SystemClock.elapsedRealtime() - start;
        }

        /**
         * 获取当前耗时，并继续统计
         */
        public long goOn() {
            long l = SystemClock.elapsedRealtime() - start;
            start = SystemClock.elapsedRealtime();
            return l;
        }

    }
}