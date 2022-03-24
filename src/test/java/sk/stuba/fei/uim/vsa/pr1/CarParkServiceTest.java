package sk.stuba.fei.uim.vsa.pr1;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.reflections.scanners.Scanners.SubTypes;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;

class CarParkServiceTest {

    public static final String DB = "VSA_PR1";
    public static final String USERNAME = "vsa";
    public static final String PASSWORD = "vsa";

    private static AbstractCarParkService carParkService;
    private static Connection mysql;

    @BeforeAll
    static void setup() throws ClassNotFoundException, SQLException {
        Reflections reflections = new Reflections("sk.stuba.fei.uim.vsa");
        Set<Class<?>> cps = reflections.get(SubTypes.of(AbstractCarParkService.class).asClass());
        assertEquals(1, cps.size());
        cps.forEach(clazz -> {
            try {
                carParkService = (AbstractCarParkService) clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            assertNotNull(carParkService);
            System.out.println("car park class: " + carParkService.getClass().getName());
        });

        Class.forName("com.mysql.jdbc.Driver");
        mysql = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DB, USERNAME, PASSWORD);
    }

    @BeforeEach
    void beforeEach() {
        runSQLStatement(mysql, "SET FOREIGN_KEY_CHECKS=0");
        runSQLStatement(mysql, "TRUNCATE TABLE CAR");
        runSQLStatement(mysql, "TRUNCATE TABLE USER");
        runSQLStatement(mysql, "SET FOREIGN_KEY_CHECKS=1");
    }

    @Test
    void shouldCreateUser() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        assertNotNull(user);
        testId(user);
    }

    @Test
    void shouldGetUserById() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object found = carParkService.getCarPark(getFieldValue(user, "id", Long.class));
        assertNotNull(found);
        assertEquals(getFieldValue(user, "id"), getFieldValue(found, "id"));
    }

    @Test
    void shouldGetUserByEmail() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object found = carParkService.getCarPark(TestData.User.email);
        assertNotNull(found);
        assertEquals(getFieldValue(user, "id"), getFieldValue(found, "id"));
    }

    @Test
    void shouldGetAllUsers() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        List<Object> users = carParkService.getUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getClass(), users.get(0).getClass());
        assertEquals(getFieldValue(user, "id"), getFieldValue(users.get(0), "id"));
    }

    @Test
    void shouldUpdateUserByChangingEmail() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        if (!hasField(user, "email")) {
            String newEmail = "jozko.je.super@example.com";
            setFieldValue(user, "email", newEmail);
            Object updated = carParkService.updateUser(user);
            assertNotNull(updated);
            assertEquals(newEmail, getFieldValue(updated, "email", String.class));
            assertEquals(getFieldValue(user, "id"), getFieldValue(updated, "id"));
        } else {
            fail("User object does not have email property. So this test cannot be performed!");
        }
    }

    @Test
    void shouldDeleteUser() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        assertNotNull(user);
        Object deleted = carParkService.deleteUser(getFieldValue(user, "id", Long.class));
        assertNotNull(deleted);
        Object found = carParkService.getUser(TestData.User.email);
        assertNull(found);
    }

    @Test
    void shouldCreateCar() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);

        Object car = carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv);
        assertNotNull(car);
        testId(car);
        if (hasField(car, "user")) {
            Object carUser = getFieldValue(car, "user");
            assertNotNull(carUser);
            assertEquals(getFieldValue(user, "id", Long.class), getFieldValue(carUser, "id", Long.class));
        }
    }

    void testId(Object obj) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Long id = getFieldValue(obj, "id", Long.class);
        assertEquals(Long.class, id.getClass());
        assertTrue(id > 0);
    }

    private static void runSQLStatement(Connection con, String sql) {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
