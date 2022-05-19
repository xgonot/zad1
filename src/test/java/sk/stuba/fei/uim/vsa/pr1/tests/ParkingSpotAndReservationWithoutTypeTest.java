/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr1.TestData.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;

/**
 * @author sheax
 */
class ParkingSpotAndReservationWithoutTypeTest {

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
    void SPOT_RES_01_getAvailableSpotsTest() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object carPark = carParkService.createCarPark("test7", "testtest", 12);
        assertNotNull(carPark);

        Long carParkId = getFieldValue(carPark, "id", Long.class);
        assertNotNull(carParkId);

        Object floor1 = carParkService.createCarParkFloor(carParkId, "Floor1");
        Object floor2 = carParkService.createCarParkFloor(carParkId, "Floor2");
        assertNotNull(floor1);
        assertNotNull(floor2);

        Object floor1Spot1 = carParkService.createParkingSpot(carParkId, "Floor1", "1.1");
        Object floor1Spot2 = carParkService.createParkingSpot(carParkId, "Floor1", "1.2");

        Object floor2Spot1 = carParkService.createParkingSpot(carParkId, "Floor2", "2.1");
        Object floor2Spot2 = carParkService.createParkingSpot(carParkId, "Floor2", "2.2");

        assertNotNull(floor1Spot1);
        assertNotNull(floor1Spot2);
        assertNotNull(floor2Spot1);
        assertNotNull(floor2Spot2);

        Map<String, List<Object>> map = carParkService.getAvailableParkingSpots("test7");
        assertNotNull(map);
        assertEquals(map.keySet().size(), 2);
        assertTrue(map.keySet().contains("Floor1"));
        assertTrue(map.keySet().contains("Floor2"));

        List<Object> floor1Slots = map.get("Floor1");
        List<Object> floor2Slots = map.get("Floor2");
        assertNotNull(floor1Slots);
        assertNotNull(floor2Slots);

        assertEquals(floor1Slots.size(), 2);
        assertEquals(floor2Slots.size(), 2);

        Long floor1Spot1Id = getFieldValue(floor1Spot1, "id", Long.class);
        Long floor1Spot2Id = getFieldValue(floor1Spot2, "id", Long.class);
        Long floor2Spot1Id = getFieldValue(floor2Spot1, "id", Long.class);
        Long floor2Spot2Id = getFieldValue(floor2Spot2, "id", Long.class);

        assertNotNull(floor1Spot1Id);
        assertNotNull(floor1Spot2Id);
        assertNotNull(floor2Spot1Id);
        assertNotNull(floor2Spot2Id);

        Object fl1Spot1 = floor1Slots.get(0);
        Object fl1Spot2 = floor1Slots.get(1);

        Object fl2Spot1 = floor2Slots.get(0);
        Object fl2Spot2 = floor2Slots.get(1);

        assertNotNull(fl1Spot1);
        assertNotNull(fl1Spot2);
        assertNotNull(fl2Spot1);
        assertNotNull(fl2Spot2);

        String[] spotFields = findFieldByType(floor1Spot1, String.class);
        assertNotNull(spotFields);

        Long fl1Spot1Id = getFieldValue(fl1Spot1, "id", Long.class);
        Long fl1Spot2Id = getFieldValue(fl1Spot2, "id", Long.class);
        Long flr2Spot1Id = getFieldValue(fl2Spot1, "id", Long.class);
        Long flr2Spot2Id = getFieldValue(fl2Spot2, "id", Long.class);

        assertNotNull(fl1Spot1Id);
        assertNotNull(fl1Spot2Id);
        assertNotNull(flr2Spot1Id);
        assertNotNull(flr2Spot2Id);

        if (floor1Spot1Id.equals(fl1Spot1Id)) {
            if (floor1Spot2Id.equals(fl1Spot2Id)) {
                for (String f : spotFields) {
                    assertEquals(getFieldValue(floor1Spot1, f, String.class), getFieldValue(fl1Spot1, f, String.class));
                    assertEquals(getFieldValue(floor1Spot2, f, String.class), getFieldValue(fl1Spot2, f, String.class));

                }
            } else {
                assertTrue(false);
            }

        } else if (floor1Spot2Id.equals(fl1Spot2Id)) {
            for (String f : spotFields) {
                assertEquals(getFieldValue(floor1Spot2, f, String.class), getFieldValue(fl1Spot1, f, String.class));
                assertEquals(getFieldValue(floor1Spot1, f, String.class), getFieldValue(fl1Spot2, f, String.class));
            }

        } else {
            assertTrue(false);
        }

        if (floor2Spot1Id.equals(flr2Spot1Id)) {
            if (floor2Spot2Id.equals(flr2Spot2Id)) {
                for (String f : spotFields) {
                    assertEquals(getFieldValue(floor2Spot1, f, String.class), getFieldValue(fl2Spot1, f, String.class));
                    assertEquals(getFieldValue(floor2Spot2, f, String.class), getFieldValue(fl2Spot2, f, String.class));
                }
            } else {
                assertTrue(false);
            }
        } else if (floor2Spot2Id.equals(flr2Spot1Id)) {
            if (floor2Spot1Id.equals(flr2Spot2Id)) {
                for (String f : spotFields) {
                    assertEquals(getFieldValue(floor2Spot2, f, String.class), getFieldValue(fl2Spot1, f, String.class));
                    assertEquals(getFieldValue(floor2Spot1, f, String.class), getFieldValue(fl2Spot2, f, String.class));
                }
            } else {
                assertTrue(false);
            }
        } else {
            assertTrue(false);
        }

        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object car = carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv);
        Long carId = getFieldValue(car, "id", Long.class);

        Object reservation = carParkService.createReservation(floor1Spot1Id, carId);
        map = carParkService.getAvailableParkingSpots("test7");
        assertEquals(map.keySet().size(), 2);
        assertTrue(map.keySet().contains("Floor1"));
        assertTrue(map.keySet().contains("Floor2"));

        floor1Slots = map.get("Floor1");
        floor2Slots = map.get("Floor2");
        assertNotNull(floor1Slots);
        assertNotNull(floor2Slots);

        assertEquals(floor1Slots.size(), 1);
        assertEquals(floor2Slots.size(), 2);

        fl1Spot1 = floor1Slots.get(0);

        fl2Spot1 = floor2Slots.get(0);
        fl2Spot2 = floor2Slots.get(1);

        assertNotNull(fl1Spot1);

        assertNotNull(fl2Spot1);
        assertNotNull(fl2Spot2);

        fl1Spot1Id = getFieldValue(fl1Spot1, "id", Long.class);
        flr2Spot1Id = getFieldValue(fl2Spot1, "id", Long.class);
        flr2Spot2Id = getFieldValue(fl2Spot2, "id", Long.class);

        assertNotNull(fl1Spot1Id);
        assertNotNull(flr2Spot1Id);
        assertNotNull(flr2Spot2Id);

        if (floor2Spot1Id.equals(flr2Spot1Id)) {
            if (floor2Spot2Id.equals(flr2Spot2Id)) {
                for (String f : spotFields) {
                    assertEquals(getFieldValue(floor2Spot1, f, String.class), getFieldValue(fl2Spot1, f, String.class));
                    assertEquals(getFieldValue(floor2Spot2, f, String.class), getFieldValue(fl2Spot2, f, String.class));
                }
            } else {
                assertTrue(false);
            }
        } else if (floor2Spot2Id.equals(flr2Spot1Id)) {
            if (floor2Spot1Id.equals(flr2Spot2Id)) {
                for (String f : spotFields) {
                    assertEquals(getFieldValue(floor2Spot2, f, String.class), getFieldValue(fl2Spot1, f, String.class));
                    assertEquals(getFieldValue(floor2Spot1, f, String.class), getFieldValue(fl2Spot2, f, String.class));
                }
            } else {
                assertTrue(false);
            }
        } else {
            assertTrue(false);
        }

        assertEquals(fl1Spot1Id, floor1Spot2Id);
        for (String f : spotFields) {
            assertEquals(getFieldValue(floor1Spot2, f, String.class), getFieldValue(fl1Spot1, f, String.class));
        }

        Object user2 = carParkService.createUser(TestData.User2.firstName, TestData.User2.lastName, TestData.User2.email);
        Object car2 = carParkService.createCar(getFieldValue(user2, "id", Long.class),
                TestData.Car2.brand, TestData.Car2.model, TestData.Car2.colour, TestData.Car2.ecv);
        Long car2Id = getFieldValue(car2, "id", Long.class);

        Object reservation2 = carParkService.createReservation(floor2Spot2Id, car2Id);

        map = carParkService.getAvailableParkingSpots("test7");
        assertEquals(map.keySet().size(), 2);
        assertTrue(map.keySet().contains("Floor1"));
        assertTrue(map.keySet().contains("Floor2"));

        floor1Slots = map.get("Floor1");
        floor2Slots = map.get("Floor2");
        assertNotNull(floor1Slots);
        assertNotNull(floor2Slots);

        assertEquals(floor1Slots.size(), 1);
        assertEquals(floor2Slots.size(), 1);

        fl1Spot1 = floor1Slots.get(0);

        fl2Spot1 = floor2Slots.get(0);

        assertNotNull(fl1Spot1);

        assertNotNull(fl2Spot1);

        fl1Spot1Id = getFieldValue(fl1Spot1, "id", Long.class);
        flr2Spot1Id = getFieldValue(fl2Spot1, "id", Long.class);

        assertNotNull(fl1Spot1Id);
        assertNotNull(flr2Spot1Id);

        assertEquals(floor2Spot1Id, flr2Spot1Id);

        for (String f : spotFields) {
            assertEquals(getFieldValue(floor2Spot1, f, String.class), getFieldValue(fl2Spot1, f, String.class));
        }

        assertEquals(fl1Spot1Id, floor1Spot2Id);
        for (String f : spotFields) {
            assertEquals(getFieldValue(floor1Spot2, f, String.class), getFieldValue(fl1Spot1, f, String.class));
        }

        Object user3 = carParkService.createUser(TestData.User3.firstName, TestData.User3.lastName, TestData.User3.email);
        Object car3 = carParkService.createCar(getFieldValue(user3, "id", Long.class),
                TestData.Car3.brand, TestData.Car3.model, TestData.Car3.colour, TestData.Car3.ecv);
        Long car3Id = getFieldValue(car3, "id", Long.class);

        Object reservation3 = carParkService.createReservation(floor1Spot2Id, car3Id);

        map = carParkService.getAvailableParkingSpots("test7");
        if (map.keySet().size() == 2) {
            List<Object> o = map.get("Floor1");
            if (o != null && !o.isEmpty()) {
                assertTrue(false);
            }
        }
        assertTrue(map.keySet().contains("Floor2"));
        floor2Slots = map.get("Floor2");
        assertNotNull(floor2Slots);
        assertEquals(floor2Slots.size(), 1);
        fl2Spot1 = floor2Slots.get(0);
        flr2Spot1Id = getFieldValue(fl2Spot1, "id", Long.class);
        assertEquals(floor2Spot1Id, flr2Spot1Id);
        for (String f : spotFields) {
            assertEquals(getFieldValue(floor2Spot1, f, String.class), getFieldValue(fl2Spot1, f, String.class));
        }

    }

    @Test
    void SPOT_RES_02_getOccupiedParkingSpotsWithoutTypeTest() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object carPark = carParkService.createCarPark("test7", "testtest", 12);
        assertNotNull(carPark);

        Long carParkId = getFieldValue(carPark, "id", Long.class);
        assertNotNull(carParkId);

        Object floor1 = carParkService.createCarParkFloor(carParkId, "Floor1");
        Object floor2 = carParkService.createCarParkFloor(carParkId, "Floor2");
        assertNotNull(floor1);
        assertNotNull(floor2);

        Object floor1Spot1 = carParkService.createParkingSpot(carParkId, "Floor1", "1.1");
        Object floor1Spot2 = carParkService.createParkingSpot(carParkId, "Floor1", "1.2");

        Object floor2Spot1 = carParkService.createParkingSpot(carParkId, "Floor2", "2.1");
        Object floor2Spot2 = carParkService.createParkingSpot(carParkId, "Floor2", "2.2");

        assertNotNull(floor1Spot1);
        assertNotNull(floor1Spot2);
        assertNotNull(floor2Spot1);
        assertNotNull(floor2Spot2);

        Map<String, List<Object>> map = null;
        try {
            map = carParkService.getOccupiedParkingSpots("test7");
            if (map == null || map.isEmpty()) {
                assertTrue(true);
            } else {
                if (map.keySet().size() == 1 || map.keySet().size() == 2) {
                    for (Map.Entry<String, List<Object>> entry : map.entrySet()) {
                        if (entry.getValue() == null || entry.getValue().isEmpty()) {
                            assertTrue(true);
                        } else {
                            assertTrue(false);
                        }
                    }
                } else {
                    assertTrue(false);
                }
            }

        } catch (Exception e) {
            assertTrue(true);
        }

        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        Object car = carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv);
        Long carId = getFieldValue(car, "id", Long.class);
        assertNotNull(carId);


        Long floor1Spot1Id = getFieldValue(floor1Spot1, "id", Long.class);
        Long floor1Spot2Id = getFieldValue(floor1Spot2, "id", Long.class);
        ;
        Long floor2Spot1Id = getFieldValue(floor2Spot1, "id", Long.class);
        ;
        Long floor2Spot2Id = getFieldValue(floor2Spot2, "id", Long.class);
        ;

        assertNotNull(floor1Spot1Id);
        assertNotNull(floor1Spot2Id);
        assertNotNull(floor2Spot1Id);
        assertNotNull(floor2Spot2Id);

        Object reservation = carParkService.createReservation(floor1Spot1Id, carId);
        map = carParkService.getOccupiedParkingSpots("test7");
        assertNotNull(map);
        if (map.keySet().size() == 1) {
            assertTrue(true);
        } else if (map.keySet().size() == 2) {
            if (map.get("Floo2") == null || map.get("Floor2").isEmpty()) {
                assertTrue(true);
            } else {
                assertTrue(false);
            }
        } else {
            assertTrue(false);
        }

        List<Object> floor1Slots = map.get("Floor1");
        List<Object> floor2Slots = null;
        assertNotNull(floor1Slots);

        assertEquals(floor1Slots.size(), 1);


        assertNotNull(floor1Spot1Id);

        Object fl1Spot1 = floor1Slots.get(0);
        Object fl1Spot2 = null;

        Object fl2Spot1 = null;
        Object fl2Spot2 = null;

        assertNotNull(fl1Spot1);

        String[] spotFields = findFieldByType(floor1Spot1, String.class);
        assertNotNull(spotFields);

        Long fl1Spot1Id = getFieldValue(fl1Spot1, "id", Long.class);
        Long fl1Spot2Id = null;
        Long flr2Spot1Id = null;
        Long flr2Spot2Id = null;

        assertNotNull(fl1Spot1Id);

        for (String f : spotFields) {
            assertEquals(getFieldValue(floor1Spot1, f, String.class), getFieldValue(fl1Spot1, f, String.class));
        }

        Object user2 = carParkService.createUser(TestData.User2.firstName, TestData.User2.lastName, TestData.User2.email);
        Object car2 = carParkService.createCar(getFieldValue(user2, "id", Long.class),
                TestData.Car2.brand, TestData.Car2.model, TestData.Car2.colour, TestData.Car2.ecv);
        Long car2Id = getFieldValue(car2, "id", Long.class);

        Object reservation2 = carParkService.createReservation(floor1Spot2Id, car2Id);

        map = carParkService.getOccupiedParkingSpots("test7");
        if (map.keySet().size() == 1) {
            assertTrue(true);
        } else if (map.keySet().size() == 2) {
            if (map.get("Floo2") == null || map.get("Floor2").isEmpty()) {
                assertTrue(true);
            } else {
                assertTrue(false);
            }
        } else {
            assertTrue(false);
        }
        assertTrue(map.keySet().contains("Floor1"));

        floor1Slots = map.get("Floor1");
        assertNotNull(floor1Slots);


        assertEquals(floor1Slots.size(), 2);

        fl1Spot1 = floor1Slots.get(0);
        fl1Spot2 = floor1Slots.get(1);

        assertNotNull(fl1Spot1);
        assertNotNull(fl1Spot2);

        fl1Spot1Id = getFieldValue(fl1Spot1, "id", Long.class);
        fl1Spot2Id = getFieldValue(fl1Spot2, "id", Long.class);

        assertNotNull(fl1Spot1Id);
        assertNotNull(fl1Spot2Id);

        if (floor1Spot1Id.equals(fl1Spot1Id)) {
            if (floor1Spot2Id.equals(fl1Spot2Id)) {
                for (String f : spotFields) {
                    assertEquals(getFieldValue(floor1Spot1, f, String.class), getFieldValue(fl1Spot1, f, String.class));
                    assertEquals(getFieldValue(floor1Spot2, f, String.class), getFieldValue(fl1Spot2, f, String.class));

                }
            } else {
                assertTrue(false);
            }

        } else if (floor1Spot2Id.equals(fl1Spot2Id)) {
            for (String f : spotFields) {
                assertEquals(getFieldValue(floor1Spot2, f, String.class), getFieldValue(fl1Spot1, f, String.class));
                assertEquals(getFieldValue(floor1Spot1, f, String.class), getFieldValue(fl1Spot2, f, String.class));
            }

        } else {
            assertTrue(false);
        }


        Object user3 = carParkService.createUser(TestData.User3.firstName, TestData.User3.lastName, TestData.User3.email);
        Object car3 = carParkService.createCar(getFieldValue(user3, "id", Long.class),
                TestData.Car3.brand, TestData.Car3.model, TestData.Car3.colour, TestData.Car3.ecv);
        Long car3Id = getFieldValue(car3, "id", Long.class);

        Object reservation3 = carParkService.createReservation(floor2Spot1Id, car3Id);

        map = carParkService.getOccupiedParkingSpots("test7");
        assertNotNull(map);
        assertEquals(map.keySet().size(), 2);

        assertTrue(map.keySet().contains("Floor2"));
        assertTrue(map.keySet().contains("Floor1"));

        floor1Slots = map.get("Floor1");
        assertNotNull(floor1Slots);


        assertEquals(floor1Slots.size(), 2);

        fl1Spot1 = floor1Slots.get(0);
        fl1Spot2 = floor1Slots.get(1);

        assertNotNull(fl1Spot1);
        assertNotNull(fl1Spot2);

        fl1Spot1Id = getFieldValue(fl1Spot1, "id", Long.class);
        fl1Spot2Id = getFieldValue(fl1Spot2, "id", Long.class);

        assertNotNull(fl1Spot1Id);
        assertNotNull(fl1Spot2Id);

        if (floor1Spot1Id.equals(fl1Spot1Id)) {
            if (floor1Spot2Id.equals(fl1Spot2Id)) {
                for (String f : spotFields) {
                    assertEquals(getFieldValue(floor1Spot1, f, String.class), getFieldValue(fl1Spot1, f, String.class));
                    assertEquals(getFieldValue(floor1Spot2, f, String.class), getFieldValue(fl1Spot2, f, String.class));

                }
            } else {
                assertTrue(false);
            }

        } else if (floor1Spot2Id.equals(fl1Spot2Id)) {
            for (String f : spotFields) {
                assertEquals(getFieldValue(floor1Spot2, f, String.class), getFieldValue(fl1Spot1, f, String.class));
                assertEquals(getFieldValue(floor1Spot1, f, String.class), getFieldValue(fl1Spot2, f, String.class));
            }

        } else {
            assertTrue(false);
        }

        floor2Slots = map.get("Floor2");
        assertNotNull(floor2Slots);
        assertEquals(floor2Slots.size(), 1);
        fl2Spot1 = floor2Slots.get(0);
        flr2Spot1Id = getFieldValue(fl2Spot1, "id", Long.class);
        assertEquals(floor2Spot1Id, flr2Spot1Id);
        for (String f : spotFields) {
            assertEquals(getFieldValue(floor2Spot1, f, String.class), getFieldValue(fl2Spot1, f, String.class));
        }
    }
}
