package sk.stuba.fei.uim.vsa.pr1.groupb;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;
import sk.stuba.fei.uim.vsa.pr1.TestData;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr1.TestData.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;

class CarTypeTest {

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
        clearCarTypeDB(mysql);
    }

    @Test
    void TYPE01_createCarType() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object type = carParkService.createCarType(CAR_TYPE_NAME);
        assertNotNull(type);
        testShouldHaveId(type);
        String[] nameField = findFieldByNameAndType(type, "name", String.class);
        if (nameField != null && nameField.length > 0) {
            assertEquals(CAR_TYPE_NAME, getFieldValue(type, "name", String.class));
        }
    }

    @Test
    void TYPE02_createAndGetCarType() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object type = carParkService.createCarType(CAR_TYPE_NAME);
        assertNotNull(type);
        testShouldHaveId(type);
        Object found = carParkService.getCarType(getFieldValue(type, "id", Long.class));
        assertNotNull(found);
        testShouldHaveId(found);
        assertEquals(getFieldValue(type, "id"), getFieldValue(found, "id"));
    }

    @Test
    void TYPE03_createAndGetAllCarTypes() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object type1 = carParkService.createCarType(CAR_TYPE_NAME);
        assertNotNull(type1);
        testShouldHaveId(type1);
        Object type2 = carParkService.createCarType(CAR_TYPE_NAME_ALT);
        assertNotNull(type2);
        testShouldHaveId(type2);
        List<Object> list = carParkService.getCarTypes();
        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(t -> {
            try {
                return Objects.equals(getFieldValue(t, "id"), getFieldValue(type1, "id"));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }));
        assertTrue(list.stream().anyMatch(t -> {
            try {
                return Objects.equals(getFieldValue(t, "id"), getFieldValue(type2, "id"));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }));
    }

    @Test
    void TYPE04_createAndGetCarTypeByName() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object type = carParkService.createCarType(CAR_TYPE_NAME);
        assertNotNull(type);
        testShouldHaveId(type);
        Object found = carParkService.getCarType(CAR_TYPE_NAME);
        assertNotNull(found);
        testShouldHaveId(found);
        assertEquals(getFieldValue(type, "id"), getFieldValue(found, "id"));
    }

    @Test
    void TYPE05_createAndGetAndDeleteCarType() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object type = carParkService.createCarType(CAR_TYPE_NAME);
        assertNotNull(type);
        testShouldHaveId(type);
        Object deleted = carParkService.deleteCarType(getFieldValue(type, "id", Long.class));
        assertNotNull(deleted);
        Object notFound = carParkService.getCarType(getFieldValue(type, "id", Long.class));
        assertNull(notFound);
    }

    @Test
    void TYPE06_createAndGetCarWithCarType() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object type = carParkService.createCarType(CAR_TYPE_NAME);
        assertNotNull(type);
        testShouldHaveId(type);
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        assertNotNull(user);
        Object car = carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv,
                getFieldValue(type, "id", Long.class));
        assertNotNull(car);
        testShouldHaveId(car);
        Object carType = null;
        if (hasField(car, "type")) {
            carType = getFieldValue(car, "type");
        } else if (hasField(car, "carType")) {
            carType = getFieldValue(car, "carType");
        }
        if (carType != null) {
            Object found = carParkService.getCarType(getFieldValue(carType, "id", Long.class));
            assertNotNull(found);
            assertEquals(getFieldValue(type, "id"), getFieldValue(found, "id"));
        } else {
            throw new RuntimeException("Could not test car type on car entity!");
        }
    }

    @Test
    void TYPE07_createAndGetParkingSpotWithCarType() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object type = carParkService.createCarType(CAR_TYPE_NAME);
        assertNotNull(type);
        testShouldHaveId(type);
        Long typeId = getFieldValue(type, "id", Long.class);
        Object carPark = carParkService.createCarPark(CarPark.name, CarPark.address, CarPark.price);
        assertNotNull(carPark);
        testShouldHaveId(carPark);
        Long carParkId = getFieldValue(carPark, "id", Long.class);
        Object carParkFloor = carParkService.createCarParkFloor(carParkId, CarPark.floor);
        assertNotNull(carParkFloor);
        Object spot = carParkService.createParkingSpot(carParkId, CarPark.floor, CarPark.spot, typeId);
        assertNotNull(spot);
        testShouldHaveId(spot);
        Object carType = null;
        if (hasField(spot, "type")) {
            carType = getFieldValue(spot, "type");
        } else if (hasField(spot, "carType")) {
            carType = getFieldValue(spot, "carType");
        }
        if (carType != null) {
            Object found = carParkService.getCarType(getFieldValue(carType, "id", Long.class));
            assertNotNull(found);
            assertEquals(typeId, getFieldValue(found, "id"));
        } else {
            throw new RuntimeException("Could not test car type on parking spot entity!");
        }
    }
}
