package cn.nlifew.linovel.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public final class ReflectUtils {
    private static final String TAG = "ReflectUtils";

    public static Field getDeclaredField(Class<?> cls, String name) {
        if (cls == null || name == null || name.length() == 0) {
            return null;
        }

        try {
            Field field = cls.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "getDeclaredField: " + cls + " [" + name + "]", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldObject(Field field, Object obj) {
        if (field == null) {
            return null;
        }
        try {
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "getFieldObject: " + field + " " + obj, e);
        }
        return null;
    }
}
