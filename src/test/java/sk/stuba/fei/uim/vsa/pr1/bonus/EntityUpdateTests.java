package sk.stuba.fei.uim.vsa.pr1.bonus;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;
import sk.stuba.fei.uim.vsa.pr1.TestData;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr1.TestData.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;

class EntityUpdateTests {

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
        clearHolidayDB(mysql);
    }

    @Test
    void BONUSU01_shouldUpdateCarBrandAndModel() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object car = carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv);

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
    void BONUSU02_shouldCreateAndUpdateUserByChangingEmail() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
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

}
