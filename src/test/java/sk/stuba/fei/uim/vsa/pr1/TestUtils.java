package sk.stuba.fei.uim.vsa.pr1;

import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.reflections.scanners.Scanners.SubTypes;

public class TestUtils {

    public static void log(String msg) {
        System.out.println("[TEST LOG] " + LocalDate.now() + " : " + msg);
    }

    public static Object getFieldValue(Object obj, String field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getter = obj.getClass().getMethod("get" + capitalize(field));
        assertNotNull(getter);
        if (getter.getParameterCount() != 0)
            throw new NoSuchMethodException("Retrieved method 'get" + capitalize(field) + "' does not have 0 arguments so it cannot be safely invoked");
        return getter.invoke(obj);
    }

    public static <T> T getFieldValue(Object obj, String field, Class<T> fieldType) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Method getter = obj.getClass().getMethod("get" + capitalize(field));
        assertNotNull(getter);
        if (getter.getParameterCount() != 0)
            throw new NoSuchMethodException("Retrieved method 'get" + capitalize(field) + "' does not have 0 arguments so it cannot be safely invoked");
        if (getter.getReturnType() != fieldType)
            throw new NoSuchMethodException("Retrieved method 'get" + capitalize(field) + "' does not have provided return type " + fieldType.getName());
        return fieldType.cast(getFieldValue(obj, field));
    }

    public static Object setFieldValue(Object obj, String field, Object newValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method setter = obj.getClass().getMethod("set" + capitalize(field), newValue.getClass());
        assertNotNull(setter);
        if (setter.getParameterCount() != 1 || (setter.getParameterCount() >= 1 && setter.getParameterTypes()[0] != newValue.getClass()))
            throw new NoSuchMethodException("Retrieved method 'set" + capitalize(field) + "' does not have 1 argument of type '" + newValue.getClass() + "' so it cannot be safely invoked");
        setter.invoke(obj, newValue);
        return obj;
    }

    public static boolean hasField(Object obj, String field) {
        try {
            Method getter = obj.getClass().getMethod("get" + capitalize(field));
            assertNotNull(getter);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static <T> String[] findFieldByNameAndType(Object obj, String fieldContainingString, Class<T> type) {
        return Arrays.stream(obj.getClass().getMethods()).filter(method ->
                        method.getName().startsWith("get") &&
                                method.getName().toLowerCase().contains(fieldContainingString.toLowerCase()) &&
                                method.getParameterCount() == 0 &&
                                method.getReturnType() == type)
                .map(method -> camelCase(method.getName().substring(3)))
                .collect(Collectors.toList()).toArray(new String[]{});
    }

    public static <T> String[] findFieldByType(Object obj, Class<T> type) {
        return Arrays.stream(obj.getClass().getMethods()).filter(method ->
                        method.getName().startsWith("get") &&
                                method.getReturnType() == type &&
                                method.getParameterCount() == 0)
                .map(method -> camelCase(method.getName().substring(3)))
                .collect(Collectors.toList()).toArray(new String[]{});
    }

    public static <T> boolean isFieldNull(Object obj, String field, Class<T> type) {
        try {
            return Objects.isNull(getFieldValue(obj, field, type));
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static String capitalize(String str) {
        if (str.length() == 0) return "";
        if (str.length() == 1) return str.toUpperCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String camelCase(String str) {
        if (str.length() == 0) return "";
        if (str.length() == 1) return str.toLowerCase();
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static void testShouldHaveId(Object obj) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Long id = getFieldValue(obj, "id", Long.class);
        assertNotNull(id);
        assertEquals(Long.class, id.getClass());
        assertTrue(id > 0);
    }

    public static void runSQLStatement(Connection con, String sql) {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static AbstractCarParkService getServiceClass() {
        Reflections reflections = new Reflections("sk.stuba.fei.uim.vsa");
        Set<Class<?>> cps = reflections.get(SubTypes.of(AbstractCarParkService.class).asClass());
        assertEquals(1, cps.size());
        return cps.stream().map(clazz -> {
            AbstractCarParkService carParkService = null;
            try {
                carParkService = (AbstractCarParkService) clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
            assertNotNull(carParkService);
            System.out.println("car park class: " + carParkService.getClass().getName());
            return carParkService;
        }).findFirst().orElse(null);
    }

    public static Connection getMySQL(String db, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + db, username, password);
    }

    public static void clearDB(Connection mysql) {
        runSQLStatement(mysql, "SET FOREIGN_KEY_CHECKS=0");
        runSQLStatement(mysql, "TRUNCATE TABLE CAR");
        runSQLStatement(mysql, "TRUNCATE TABLE USER");
        runSQLStatement(mysql, "TRUNCATE TABLE CARPARK");
        runSQLStatement(mysql, "TRUNCATE TABLE CARPARKFLOOR");
        runSQLStatement(mysql, "TRUNCATE TABLE PARKINGSPOT");
        runSQLStatement(mysql, "TRUNCATE TABLE RESERVATION");
        runSQLStatement(mysql, "SET FOREIGN_KEY_CHECKS=1");
    }
    
    public static void clearHolidayDB(Connection mysql) {
        runSQLStatement(mysql, "SET FOREIGN_KEY_CHECKS=0");
        runSQLStatement(mysql, "TRUNCATE TABLE HOLIDAY");
        runSQLStatement(mysql, "SET FOREIGN_KEY_CHECKS=1");
         
    }

}
