package sk.stuba.fei.uim.vsa.pr1.bonus;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;
import sk.stuba.fei.uim.vsa.pr1.TestData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr1.TestData.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;

class EntityUpdateTest {

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

    @Test
    void BONUSU03_updateCarParkTest() {
        Object carPark = carParkService.createCarPark("UPDATE-CAR-PARK-1", "test11", 10);
        assertNotNull(carPark);
        Class c = carPark.getClass();
        Method getId = null;
        List<Method> stringMethods = new ArrayList<>();
        List<Method> stringSetterMethods = new ArrayList<>();
        Method getPriceMethod = null;
        Method setPriceMethod = null;
        Object modifiedPrice = null;
        for (Method m : c.getMethods()) {
            if (m.getReturnType() == Long.class && m.getParameterCount() == 0) {
                getId = m;
            } else if (m.getReturnType() == String.class && !m.getName().equals("toString") && m.getParameterCount() == 0) {
                stringMethods.add(m);
            } else if (m.getParameterCount() == 0 && !m.getName().equals("hashCode") && (m.getReturnType() == Integer.class || m.getReturnType() == int.class || m.getReturnType() == Float.class || m.getReturnType() == float.class || m.getReturnType() == Double.class || m.getReturnType() == double.class)) {
                getPriceMethod = m;
            } else if (m.getParameterCount() == 1) {
                Class[] paramClasses = m.getParameterTypes();
                if (paramClasses[0] == String.class) {
                    stringSetterMethods.add(m);
                } else if (paramClasses[0] == Integer.class || paramClasses[0] == int.class) {
                    setPriceMethod = m;
                    modifiedPrice = 12;
                } else if (paramClasses[0] == Float.class || paramClasses[0] == float.class) {
                    modifiedPrice = 12.0f;
                } else if (paramClasses[0] == Double.class || paramClasses[0] == double.class) {
                    modifiedPrice = 12.0;
                }
            }
        }
        assertNotNull(getId);
        assertFalse(stringMethods.isEmpty());
        assertNotNull(getPriceMethod);
        assertNotNull(modifiedPrice);
        assertNotNull(setPriceMethod);
        try {
            Object id = getId.invoke(carPark);
            Long carParkId = (Long) id;
            assertNotNull(carParkId);

            Object park = carParkService.getCarPark(carParkId);
            assertNotNull(park);
            Long parkId = (Long) getId.invoke(park);
            assertNotNull(parkId);
            assertEquals(id, parkId);
            for (Method m : stringMethods) {
                String carParkS = (String) m.invoke(carPark);
                String parkS = (String) m.invoke(park);
                assertNotNull(carParkS);
                assertNotNull(parkS);
                assertEquals(carParkS, parkS);
            }

            Object carParkPrice = getPriceMethod.invoke(carPark);
            Object parkPrice = getPriceMethod.invoke(park);
            assertNotNull(carParkPrice);
            assertNotNull(parkPrice);
            assertEquals(carParkPrice, parkPrice);
            setPriceMethod.invoke(carPark, modifiedPrice);
            for (Method m : stringSetterMethods) {
                m.invoke(carPark, "MODIFIED-CAR-PARK");
            }

            Object modifiedPark = carParkService.updateCarPark(carPark);

            park = carParkService.getCarPark(carParkId);

            assertNotNull(park);

            Object parkModifiedPrice = getPriceMethod.invoke(park);
            assertNotNull(parkModifiedPrice);
            assertEquals(modifiedPrice, parkModifiedPrice);

            for (Method m : stringMethods) {
                String x = (String) m.invoke(park);
                assertNotNull(x);
                assertEquals(x, "MODIFIED-CAR-PARK");
            }

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void BONUSU04_updateParkingSpotWithoutType() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object carPark = carParkService.createCarPark("test7", "testtest", 12);
        assertNotNull(carPark);

        Long carParkId = getFieldValue(carPark, "id", Long.class);
        assertNotNull(carParkId);

        Object floor1 = carParkService.createCarParkFloor(carParkId, "Floor1");
        assertNotNull(floor1);

        Object floor1Spot1 = carParkService.createParkingSpot(carParkId, "Floor1", "1.1");
        assertNotNull(floor1Spot1);

        String[] spotFields = findFieldByType(floor1Spot1, String.class);
        String baseString = "11-";
        int i = 1;
        for (String f : spotFields) {
            StringBuilder b = new StringBuilder();
            b.append(baseString);
            b.append(String.valueOf(i));
            setFieldValue(floor1Spot1, f, b.toString());
            i++;
        }

        Object fl1Spot1 = carParkService.updateParkingSpot(floor1Spot1);
        assertNotNull(fl1Spot1);
        Long floor1Spot1Id = getFieldValue(floor1Spot1, "id", Long.class);
        Long fl1Spot1Id = getFieldValue(fl1Spot1, "id", Long.class);
        assertNotNull(floor1Spot1Id);
        assertNotNull(fl1Spot1Id);
        assertEquals(floor1Spot1Id, fl1Spot1Id);
        for (String f : spotFields) {
            assertEquals(getFieldValue(floor1Spot1, f, String.class), getFieldValue(fl1Spot1, f, String.class));
        }

        Object f1 = carParkService.getParkingSpot(floor1Spot1Id);
        assertNotNull(f1);
        Long f1Id = getFieldValue(f1, "id", Long.class);
        assertNotNull(f1Id);
        assertEquals(f1Id, floor1Spot1Id);
        for (String f : spotFields) {
            assertEquals(getFieldValue(f1, f, String.class), getFieldValue(fl1Spot1, f, String.class));
        }
    }

    @Test
    void BONUSU05_shouldCreateAndUpdateReservation() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
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
                String[] calendarDateFields = findFieldByType(reservation, Calendar.class);
                if (calendarDateFields.length > 0) {
                    Calendar controlTime3 = Calendar.getInstance();
                    for (String dateField : calendarDateFields) {
                        setFieldValue(reservation, dateField, controlTime3);
                    }
                    Object updatedRes = carParkService.updateReservation(reservation);
                    assertTrue(Arrays.stream(dateFields).allMatch(f -> {
                        try {
                            return Objects.equals(controlTime3, getFieldValue(updatedRes, f, Calendar.class));
                        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }));

                } else {
                    String[] gregorianDateFields = findFieldByType(reservation, GregorianCalendar.class);
                    if (gregorianDateFields.length > 0) {
                        GregorianCalendar controlTime4 = (GregorianCalendar) Calendar.getInstance();
                        for (String dateField : calendarDateFields) {
                            setFieldValue(reservation, dateField, controlTime4);
                        }
                        Object updatedRes = carParkService.updateReservation(reservation);
                        assertTrue(Arrays.stream(dateFields).allMatch(f -> {
                            try {
                                return Objects.equals(controlTime4, getFieldValue(updatedRes, f, GregorianCalendar.class));
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
        }
    }


}
