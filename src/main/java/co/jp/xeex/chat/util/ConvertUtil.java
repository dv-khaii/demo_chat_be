package co.jp.xeex.chat.util;

import com.google.gson.Gson;

/**
 * Utility class for converting something...
 * 
 * @author v_long
 */
public class ConvertUtil {
    private ConvertUtil() {
        // just to prevent instantiation
    }

    /**
     * Convert object to json string
     * 
     * @param obj Object to convert
     * @return JSON string representation. If object is null, return null
     */
    public static String toJson(Object obj) {
        try {
            if (obj != null) {
                return new Gson().toJson(obj);
            }
            return null;
        } catch (Exception e) {
            return "Unknown object";
        }
    }

    /**
     * Get the simple name of the object. If object is null, return "NoName"
     * 
     * @param obj Object to get simple name
     * @return Simple name of the object
     * 
     */
    public static String toSimpleName(Object obj) {
        try {
            if (obj != null) {
                return obj.getClass().getSimpleName();
            }
            return "NoName";
        } catch (Exception e) {
            return "Unknown object";
        }
    }
}
