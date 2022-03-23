package sk.stuba.fei.uim.vsa.pr1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

public class TestUtils {

    static void log(String msg) {
        System.out.println("[TEST LOG] " + LocalDate.now() + " : " + msg);
    }

    static Object getFieldValue(Object obj, String field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getter = obj.getClass().getMethod("get" + capitalize(field));
        return getter.invoke(obj);
    }

    static <T> T getFieldValue(Object obj, String field, Class<T> fieldType) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return fieldType.cast(getFieldValue(obj, field));
    }

    static String capitalize(String str) {
        if (str.length() == 0) return "";
        if (str.length() == 1) return str.toUpperCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
