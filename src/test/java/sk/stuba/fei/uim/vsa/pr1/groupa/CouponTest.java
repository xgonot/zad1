package sk.stuba.fei.uim.vsa.pr1.groupa;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;
import sk.stuba.fei.uim.vsa.pr1.TestData;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr1.TestData.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;

class CouponTest {

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
        clearCouponDB(mysql);
    }

    @Test
    void COUPON01_createCoupon() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object coupon = carParkService.createDiscountCoupon(Coupon.name, Coupon.discount);
        assertNotNull(coupon);
        testShouldHaveId(coupon);
        String[] nameField = findFieldByType(coupon, String.class);
        if (nameField != null && nameField.length > 0) {
            assertEquals(Coupon.name, getFieldValue(coupon, nameField[0]));
        }
    }

    @Test
    void COUPON02_createCouponAndGiveItToUser() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object coupon = carParkService.createDiscountCoupon(Coupon.name, Coupon.discount);
        assertNotNull(coupon);
        testShouldHaveId(coupon);
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        assertNotNull(user);
        testShouldHaveId(user);
        carParkService.giveCouponToUser(getFieldValue(coupon, "id", Long.class), getFieldValue(user, "id", Long.class));
        // TODO test this somehow
    }

    @Test
    void COUPON03_createAndGetCoupon() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object coupon = carParkService.createDiscountCoupon(Coupon.name, Coupon.discount);
        assertNotNull(coupon);
        testShouldHaveId(coupon);
        Object found = carParkService.getCoupon(getFieldValue(coupon, "id", Long.class));
        assertNotNull(found);
        assertEquals(getFieldValue(coupon, "id"), getFieldValue(found, "id"));
    }

    @Test
    void COUPON04_createAndGetCouponsByUser() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object coupon = carParkService.createDiscountCoupon(Coupon.name, Coupon.discount);
        assertNotNull(coupon);
        testShouldHaveId(coupon);
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        assertNotNull(user);
        testShouldHaveId(user);
        carParkService.giveCouponToUser(getFieldValue(coupon, "id", Long.class), getFieldValue(user, "id", Long.class));
        List<Object> cps = carParkService.getCoupons(getFieldValue(user, "id", Long.class));
        assertNotNull(cps);
        assertEquals(1, cps.size());
        assertTrue(cps.stream().anyMatch(c -> {
            try {
                return Objects.equals(getFieldValue(c, "id"), getFieldValue(coupon, "id"));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }));
    }

    @Test
    void COUPON05_createAndDeleteCoupon() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object coupon = carParkService.createDiscountCoupon(Coupon.name, Coupon.discount);
        assertNotNull(coupon);
        testShouldHaveId(coupon);
        Long id = getFieldValue(coupon, "id", Long.class);
        Object deleted = carParkService.deleteCoupon(id);
        assertNotNull(deleted);
        Object notFound = carParkService.getCoupon(id);
        assertNull(notFound);
    }

    @Test
    void COUPON06_createCouponAndCreateReservationAndEndReservationWithCoupon() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InterruptedException {
        Object coupon = carParkService.createDiscountCoupon(Coupon.name, Coupon.discount);
        assertNotNull(coupon);
        testShouldHaveId(coupon);
        Object carPark = carParkService.createCarPark(CarPark.name, CarPark.address, CarPark.price);
        assertNotNull(carPark);
        testShouldHaveId(carPark);
        Long carParkId = getFieldValue(carPark, "id", Long.class);
        Object floor = carParkService.createCarParkFloor(carParkId, CarPark.floor);
        assertNotNull(floor);
        Object spot = carParkService.createParkingSpot(carParkId, CarPark.floor, CarPark.spot);
        assertNotNull(spot);
        testShouldHaveId(spot);
        Long spotId = getFieldValue(spot, "id", Long.class);

        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object car = carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv);
        Long carId = getFieldValue(car, "id", Long.class);

        Object reservation = carParkService.createReservation(spotId, carId);
        assertNotNull(reservation);
        testShouldHaveId(reservation);
        Long reservationId = getFieldValue(reservation, "id", Long.class);

        log("Waiting for simulating parking clock");
        Thread.sleep(60500);

        Object ended = carParkService.endReservation(reservationId, getFieldValue(coupon, "id", Long.class));
        assertNotNull(ended);
        assertEquals(reservationId, getFieldValue(ended, "id", Long.class));

        String[] localDateFields = findFieldByType(ended, LocalDateTime.class);
        if (localDateFields.length > 0) {
            assertTrue(Arrays.stream(localDateFields).noneMatch(f -> isFieldNull(ended, f, LocalDateTime.class)));
        } else {
            String[] dateFields = findFieldByType(ended, Date.class);
            if (dateFields.length > 0) {
                assertTrue(Arrays.stream(dateFields).noneMatch(f -> isFieldNull(ended, f, Date.class)));
            } else {
                String[] calendarDateFields = findFieldByType(ended, Calendar.class);
                if (calendarDateFields.length > 0) {
                    assertTrue(Arrays.stream(calendarDateFields).noneMatch(f -> isFieldNull(ended, f, Calendar.class)));
                } else {
                    String[] gregorianDateFields = findFieldByType(ended, GregorianCalendar.class);
                    if (gregorianDateFields.length > 0) {
                        assertTrue(Arrays.stream(gregorianDateFields).noneMatch(f -> isFieldNull(ended, f, GregorianCalendar.class)));
                    } else {
                        throw new RuntimeException("Cannot test reservation for start time and end time. Field not found!");
                    }
                }

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

}
