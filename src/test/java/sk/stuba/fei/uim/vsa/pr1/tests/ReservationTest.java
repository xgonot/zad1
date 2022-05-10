package sk.stuba.fei.uim.vsa.pr1.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;
import sk.stuba.fei.uim.vsa.pr1.TestData;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr1.TestData.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;

class ReservationTest {

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
    void RES01_shouldCreateReservation() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object carPark = carParkService.createCarPark("test8", "testtest", 12);
        assertNotNull(carPark);
        testShouldHaveId(carPark);
        Long carParkId = getFieldValue(carPark, "id", Long.class);
        Object floor1 = carParkService.createCarParkFloor(carParkId, "Floor3-1");
        assertNotNull(floor1);
        Object spot1 = carParkService.createParkingSpot(carParkId, "Floor3-1", "1.01");
        assertNotNull(spot1);
        testShouldHaveId(spot1);
        Long spot1Id = getFieldValue(spot1, "id", Long.class);

        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object car = carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv);
        Long carId = getFieldValue(car, "id", Long.class);

        Object reservation = carParkService.createReservation(spot1Id, carId);
        assertNotNull(reservation);
        testShouldHaveId(reservation);

        LocalDateTime startLocalTime = getStartDateField(reservation, LocalDateTime.class);
        if (startLocalTime != null) {
            assertNotNull(startLocalTime);
            assertTrue(LocalDateTime.now().isAfter(startLocalTime));
        } else {
            Date startTime = getStartDateField(reservation, Date.class);
            if (startTime != null) {
                assertNotNull(startTime);
                assertTrue(new Date().after(startTime));
            } else {
                throw new RuntimeException("Cannot test reservation for starting time. Field not found!");
            }
        }
    }

    @Test
    void RES02_shouldCreateAndEndReservation() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InterruptedException {
        Object carPark = carParkService.createCarPark("test8", "testtest", 12);
        assertNotNull(carPark);
        testShouldHaveId(carPark);
        Long carParkId = getFieldValue(carPark, "id", Long.class);
        Object floor1 = carParkService.createCarParkFloor(carParkId, "Floor3-1");
        assertNotNull(floor1);
        Object spot1 = carParkService.createParkingSpot(carParkId, "Floor3-1", "1.01");
        assertNotNull(spot1);
        testShouldHaveId(spot1);
        Long spot1Id = getFieldValue(spot1, "id", Long.class);

        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object car = carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv);
        Long carId = getFieldValue(car, "id", Long.class);

        Object reservation = carParkService.createReservation(spot1Id, carId);
        assertNotNull(reservation);
        testShouldHaveId(reservation);

        log("Waiting for simulating parking clock");
        Thread.sleep(60500);

        Object ended = carParkService.endReservation(getFieldValue(reservation, "id", Long.class));
        assertNotNull(ended);
        assertEquals(getFieldValue(reservation, "id", Long.class), getFieldValue(ended, "id", Long.class));

        String[] localDateFields = findFieldByType(ended, LocalDateTime.class);
        if (localDateFields.length > 0) {
            assertTrue(Arrays.stream(localDateFields).noneMatch(f -> isFieldNull(ended, f, LocalDateTime.class)));
        } else {
            String[] dateFields = findFieldByType(ended, Date.class);
            if (dateFields.length > 0) {
                assertTrue(Arrays.stream(dateFields).noneMatch(f -> isFieldNull(ended, f, Date.class)));
            } else {
                throw new RuntimeException("Cannot test reservation for start time and end time. Field not found!");
            }
        }

        String[] doublePrice = findFieldByType(ended, Double.class);
        if (doublePrice.length > 0) {
            assertTrue(Arrays.stream(doublePrice).noneMatch(f -> {
                try {
                    return isFieldNull(ended, f, Double.class) ||
                            getFieldValue(ended, f, Double.class) == 0.0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }));
        } else {
            String[] intPrice = findFieldByType(ended, Integer.class);
            if (intPrice.length > 0) {
                assertTrue(Arrays.stream(intPrice).noneMatch(f -> {
                    try {
                        return isFieldNull(ended, f, Integer.class) ||
                                getFieldValue(ended, f, Integer.class) == 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }));
            } else {
                throw new RuntimeException("Cannot test reservation for price. Field not found!");
            }
        }
    }

    @Test
    void RES03_shouldCreateAndGetReservations() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object carPark = carParkService.createCarPark("test8", "testtest", 12);
        assertNotNull(carPark);
        testShouldHaveId(carPark);
        Long carParkId = getFieldValue(carPark, "id", Long.class);
        Object floor1 = carParkService.createCarParkFloor(carParkId, "Floor3-1");
        assertNotNull(floor1);
        Object spot1 = carParkService.createParkingSpot(carParkId, "Floor3-1", "1.01");
        assertNotNull(spot1);
        testShouldHaveId(spot1);
        Long spot1Id = getFieldValue(spot1, "id", Long.class);

        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object car = carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv);
        Long carId = getFieldValue(car, "id", Long.class);

        Object reservation = carParkService.createReservation(spot1Id, carId);
        assertNotNull(reservation);
        testShouldHaveId(reservation);

        List<Object> reservations = carParkService.getReservations(spot1Id, new Date());
        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        assertEquals(getFieldValue(reservation, "id", Long.class), getFieldValue(reservations.get(0), "id", Long.class));
    }

    @Test
    void RES04_shouldCreateAndGetReservationByUser() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object carPark = carParkService.createCarPark("test8", "testtest", 12);
        assertNotNull(carPark);
        testShouldHaveId(carPark);
        Long carParkId = getFieldValue(carPark, "id", Long.class);
        Object floor1 = carParkService.createCarParkFloor(carParkId, "Floor3-1");
        assertNotNull(floor1);
        Object spot1 = carParkService.createParkingSpot(carParkId, "Floor3-1", "1.01");
        assertNotNull(spot1);
        testShouldHaveId(spot1);
        Long spot1Id = getFieldValue(spot1, "id", Long.class);

        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object car = carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv);
        Long carId = getFieldValue(car, "id", Long.class);

        Object reservation = carParkService.createReservation(spot1Id, carId);
        assertNotNull(reservation);
        testShouldHaveId(reservation);

        List<Object> usersReservations = carParkService.getMyReservations(getFieldValue(user, "id", Long.class));
        assertNotNull(usersReservations);
        assertEquals(1, usersReservations.size());
        assertEquals(getFieldValue(reservation, "id", Long.class), getFieldValue(usersReservations.get(0), "id", Long.class));
    }

    @Test
    void RES05_shouldCreateAndUpdateReservation() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object carPark = carParkService.createCarPark("test8", "testtest", 12);
        assertNotNull(carPark);
        testShouldHaveId(carPark);
        Long carParkId = getFieldValue(carPark, "id", Long.class);
        Object floor1 = carParkService.createCarParkFloor(carParkId, "Floor3-1");
        assertNotNull(floor1);
        Object spot1 = carParkService.createParkingSpot(carParkId, "Floor3-1", "1.01");
        assertNotNull(spot1);
        testShouldHaveId(spot1);
        Long spot1Id = getFieldValue(spot1, "id", Long.class);

        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object car = carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv);
        Long carId = getFieldValue(car, "id", Long.class);

        Object reservation = carParkService.createReservation(spot1Id, carId);
        assertNotNull(reservation);
        testShouldHaveId(reservation);

        String[] localDateFields = findFieldByType(reservation, LocalDateTime.class);
        if (localDateFields.length > 0) {
            LocalDateTime controlTime = LocalDateTime.now();
            for (String localDateField : localDateFields) {
                setFieldValue(reservation, localDateField, controlTime);
            }
            Object updatedRes = carParkService.updateReservation(reservation);
            assertTrue(Arrays.stream(localDateFields).allMatch(f -> {
                try {
                    return Objects.equals(controlTime, getFieldValue(updatedRes, f, LocalDateTime.class));
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                    e.printStackTrace();
                    return false;
                }
            }));
        } else {
            String[] dateFields = findFieldByType(reservation, Date.class);
            if (dateFields.length > 0) {
                Date controlTime2 = new Date();
                for (String dateField : dateFields) {
                    setFieldValue(reservation, dateField, controlTime2);
                }
                Object updatedRes = carParkService.updateReservation(reservation);
                assertTrue(Arrays.stream(dateFields).allMatch(f -> {
                    try {
                        return Objects.equals(controlTime2, getFieldValue(updatedRes, f, Date.class));
                    } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                        e.printStackTrace();
                        return false;
                    }
                }));
            } else {
                throw new RuntimeException("Cannot test reservation for update. Field not found!");
            }
        }
    }

    private <T> T getStartDateField(Object reservation, Class<T> dateClass) {
        String[] starTimeFields = findFieldByType(reservation, LocalDateTime.class);
        if (starTimeFields.length > 0) {
            return Arrays.stream(starTimeFields).filter(f ->
                            !isFieldNull(reservation, f, dateClass)
                    ).map(f -> {
                        try {
                            return getFieldValue(reservation, f, dateClass);
                        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .findFirst().orElse(null);
        }
        return null;
    }
}
