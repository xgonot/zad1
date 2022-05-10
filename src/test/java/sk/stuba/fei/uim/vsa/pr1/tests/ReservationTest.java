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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void RESERVATION01_shouldCreateReservation() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
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
        String[] starTimeFields = findFieldByType(reservation, LocalDateTime.class);
        if (starTimeFields.length > 0) {
            LocalDateTime startTime = Arrays.stream(starTimeFields).filter(f -> {
                        try {
                            return Objects.nonNull(getFieldValue(reservation, f, LocalDateTime.class));
                        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }).map(f -> {
                        try {
                            return getFieldValue(reservation, f, LocalDateTime.class);
                        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .findFirst().orElse(null);
            assertNotNull(startTime);
            assertTrue(LocalDateTime.now().isAfter(startTime));
        } else {
            starTimeFields = findFieldByType(reservation, Date.class);
            if (starTimeFields.length > 0) {

            } else {
                log("Cannot test reservation for starting time. Field not found!");
            }
        }
    }
}
