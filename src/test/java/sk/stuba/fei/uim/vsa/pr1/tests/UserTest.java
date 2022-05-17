package sk.stuba.fei.uim.vsa.pr1.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;
import sk.stuba.fei.uim.vsa.pr1.TestData;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr1.TestData.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;

class UserTest {

    private static AbstractCarParkService carParkService;
    private static Connection mysql;

    @BeforeAll
    static void setup() throws SQLException, ClassNotFoundException {
        carParkService = getServiceClass();
        mysql = getMySQL(DB, USERNAME, PASSWORD);
    }

    @BeforeEach
    void beforeEach() {
        clearDB(mysql);
    }

    @Test
    void USER01_shouldCreateUser() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        assertNotNull(user);
        testShouldHaveId(user);
    }

    @Test
    void USER02_shouldCreateAndGetUserById() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object found = carParkService.getUser(getFieldValue(user, "id", Long.class));
        assertNotNull(found);
        assertEquals(getFieldValue(user, "id"), getFieldValue(found, "id"));
    }

    @Test
    void USER03_shouldCreateGetUserByEmail() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object found = carParkService.getUser(TestData.User.email);
        assertNotNull(found);
        assertEquals(getFieldValue(user, "id"), getFieldValue(found, "id"));
    }

    @Test
    void USER04_shouldCreateAndGetAllUsers() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        List<Object> users = carParkService.getUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getClass(), users.get(0).getClass());
        assertEquals(getFieldValue(user, "id"), getFieldValue(users.get(0), "id"));
    }

    @Test
    void USER06_shouldCreateAndDeleteUser() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        assertNotNull(user);
        Object deleted = carParkService.deleteUser(getFieldValue(user, "id", Long.class));
        assertNotNull(deleted);
        Object found = carParkService.getUser(TestData.User.email);
        assertNull(found);
    }


}
