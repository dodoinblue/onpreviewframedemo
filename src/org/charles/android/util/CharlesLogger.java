package org.charles.android.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import android.util.Log;

public class CharlesLogger {
	
	private static String TAG = "Charles_TAG";

    public static void log(String s) {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        StackTraceElement trace = traces[3]; /* 3 is the line in stack where exception took place */
        String tag = trace.getFileName() + " +" + trace.getLineNumber() + " :: " + trace.getMethodName();
        Log.i(TAG, tag + " :: " + s);
    }
    
    public static void logAllPublicFields(Object obj) {
        if (obj == null) {
            Log.i(TAG, "Object does not exist");
            return;
        }
        Log.i(TAG, "Logging for obj=" + obj.hashCode() + " of class: " + obj.getClass().getSimpleName());
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers())) {
                try {
                    Object value = field.get(obj);
                    if (value != null) {
                        Log.i(TAG, field.getName() + "=" + value);
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Failed to get value of " + field.getName());
                }
            }
        }
    }
}
