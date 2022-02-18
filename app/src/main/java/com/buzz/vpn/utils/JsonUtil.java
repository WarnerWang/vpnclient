package com.buzz.vpn.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixinyuan on 16/6/16.
 */
public final class JsonUtil {


    private static final String TAG = "JsonUtil";

    public static final JSONObject toJSONObject(Object object) {
        if (null != object) {
            try {
                return JSON.parseObject(JSON.toJSONString(object));
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(e, TAG, "json parse error");
            }
        }
        return null;
    }

    public static final String toJSONString(Object object) {
        if (null != object) {
            try {
                return JSON.toJSONString(object, SerializerFeature.WriteNullStringAsEmpty,
                        SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullBooleanAsFalse
                        , SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteMapNullValue
                        , SerializerFeature.WriteEnumUsingToString);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(e, TAG, "json parse error");
            }
        }
        return null;
    }


    public static final JSONObject parseObject(String text) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return JSON.parseObject(text);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(e, TAG, "json parse error");
            }
        }
        return null;
    }

    public static final <T> T parseObject(String text, Class<T> clazz) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return JSON.parseObject(text, clazz);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(e, TAG, "json parse error");
            }
        }
        return null;
    }


    public static final <T> T parseObject(String text, TypeReference<T> type) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return JSON.parseObject(text, type);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(e, TAG, "json parse error");
            }
        }
        return null;
    }

    public static final JSONArray parseArray(String text) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return JSON.parseArray(text);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(e, TAG, "json parse error");
            }
        }
        return null;
    }

    public static final <T> List<T> parseArray(String text, Class<T> clazz) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return JSON.parseArray(text, clazz);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(e, TAG, "json parse error");
            }
        }
        return null;
    }

    //===========================================================

    public static final int getIntValue(JSONObject object, String key) {
        if (null != object && !TextUtils.isEmpty(key)) {
            try {
                return object.getIntValue(key);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(e, TAG, "json parse error");
            }
        }
        return 0;
    }

    public static final long getLongValue(JSONObject object, String key) {
        if (null != object && !TextUtils.isEmpty(key)) {
            try {
                return object.getLongValue(key);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(e, TAG, "json parse error");
            }
        }
        return 0L;
    }

    public static final boolean getBooleanValue(JSONObject object, String key) {
        if (null != object && !TextUtils.isEmpty(key)) {
            try {
                return object.getBooleanValue(key);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(e, TAG, "json parse error");
            }
        }
        return false;
    }


    public static final String getStringValue(JSONObject object, String key) {
        if (null != object && !TextUtils.isEmpty(key)) {
            try {
                return object.getString(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static final JSONObject getJSONObject(JSONObject object, String key) {
        if (null != object && !TextUtils.isEmpty(key)) {
            try {
                return object.getJSONObject(key);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(e, TAG, "json parse error");
            }
        }
        return null;
    }

    public static final JSONArray getJSONArray(JSONObject object, String key) {
        if (null != object && !TextUtils.isEmpty(key)) {
            try {
                return object.getJSONArray(key);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(e, TAG, "json parse error");
            }
        }
        return null;
    }

    public static <E> List<E> deepCopy(List<E> src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            List<E> dest = (List<E>) in.readObject();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<E>();
        }

    }

}
