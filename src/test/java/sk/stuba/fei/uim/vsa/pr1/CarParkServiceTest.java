package sk.stuba.fei.uim.vsa.pr1;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    void PARK01_shouldCreateCarPark(){

    }

    void PARK02_shouldFindAllCarParks() {

    }

    void PARK03_shouldFindCarParkById() {

    }

    void PARK04_shouldFindCarParkByName() {

    }

    void PARK05_shouldUpdateCarPark() {

    }

    void PARK06_shouldRemoveCarPark() {

    }

    @Test
    void USER01_shouldCreateUser() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        assertNotNull(user);
        testId(user);
    }

    @Test
    void USER02_shouldGetUserById() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object found = carParkService.getUser(getFieldValue(user, "id", Long.class));
        assertNotNull(found);
        assertEquals(getFieldValue(user, "id"), getFieldValue(found, "id"));
    }

    @Test
    void USER03_shouldGetUserByEmail() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object found = carParkService.getUser(TestData.User.email);
        assertNotNull(found);
        assertEquals(getFieldValue(user, "id"), getFieldValue(found, "id"));
    }

    @Test
    void USER04_shouldGetAllUsers() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        List<Object> users = carParkService.getUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getClass(), users.get(0).getClass());
        assertEquals(getFieldValue(user, "id"), getFieldValue(users.get(0), "id"));
    }

    @Test
    void USER05_shouldUpdateUserByChangingEmail() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        if (hasField(user, "email")) {
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
    void USER06_shouldDeleteUser() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        assertNotNull(user);
        Object deleted = carParkService.deleteUser(getFieldValue(user, "id", Long.class));
        assertNotNull(deleted);
        Object found = carParkService.getUser(TestData.User.email);
        assertNull(found);
    }

    @Test
    void CAR01_shouldCreateCar() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object car = createNewCar();
        assertNotNull(car);
        testId(car);
        if (hasField(car, "user")) {
            Object carUser = getFieldValue(car, "user");
            assertNotNull(carUser);
            Object foundUser = carParkService.getUser(getFieldValue(carUser, "id", Long.class));
            assertNotNull(foundUser);
        }
    }

    private Object createNewCar() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        return carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv);
    }

    @Test
    void CAR02_shouldGetCarById() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object car = createNewCar();
        Object found = carParkService.getCar(getFieldValue(car, "id", Long.class));
        assertNotNull(found);
        assertEquals(getFieldValue(car, "id"), getFieldValue(found, "id"));
    }

    @Test
    void CAR02_shouldGetCarByECV() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object car = createNewCar();
        Object found = carParkService.getCar(TestData.Car.ecv);
        assertNotNull(found);
        assertEquals(getFieldValue(car, "id"), getFieldValue(found, "id"));
    }

    @Test
    void CAR04_shouldGetCarsByUser() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object car = createNewCar();
        List<Object> users = carParkService.getUsers();
        assertEquals(1, users.size());
        List<Object> cars = carParkService.getCars(getFieldValue(users.get(0), "id", Long.class));
        assertNotNull(cars);
        assertEquals(1, cars.size());
        assertEquals(car.getClass(), cars.get(0).getClass());
        assertEquals(getFieldValue(car, "id"), getFieldValue(cars.get(0), "id"));
    }

    @Test
    void CAR05_shouldUpdateCarBrandAndModel() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object car = createNewCar();
        if (hasField(car, "brand") && hasField(car, "model")) {
            String newBrand = "Porsche";
            String newModel = "911 GTS";

            setFieldValue(car, "brand", newBrand);
            setFieldValue(car, "model", newModel);
            Object updated = carParkService.updateCar(car);
            assertNotNull(updated);
            assertEquals(newBrand, getFieldValue(updated, "brand", String.class));
            assertEquals(newModel, getFieldValue(updated, "model", String.class));
            assertEquals(getFieldValue(car, "id"), getFieldValue(updated, "id"));
        } else {
            fail("Car object does not have a brand and a model property. So this test cannot be performed!");
        }
    }

    @Test
    void CAR06_shouldDeleteCar() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object car = createNewCar();
        Object deleted = carParkService.deleteCar(getFieldValue(car, "id", Long.class));
        assertNotNull(deleted);
        Object notFound = carParkService.getCar(TestData.Car.ecv);
        assertNull(notFound);
    }

}
