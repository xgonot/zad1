package sk.stuba.fei.uim.vsa.pr1.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr1.TestData.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;

class CarParkTest {

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
    void PARK01_createAndGetCarParkTest() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object carPark = carParkService.createCarPark("test1", "testtest", 12);
        assertNotNull(carPark);
        testShouldHaveId(carPark);
        Long id = getFieldValue(carPark, "id", Long.class);
        assertNotNull(id);
        Object carPark2 = carParkService.getCarPark(id);
        assertNotNull(carPark2);
        assertEquals(id, getFieldValue(carPark2, "id", Long.class));
    }

    @Test
    void PARK02_createAndGetCarParkByName() {
        Object carPark = carParkService.createCarPark("CAR-PARK-NAME", "testtest", 12);
        assertNotNull(carPark);
        Class c = carPark.getClass();
        try {
            Method[] methods = c.getMethods();
            Method getId = null;
            List<Method> stringMethods = new ArrayList<>();
            for (Method m : methods) {
                if (m.getReturnType() == Long.class && m.getParameterCount() == 0) {
                    getId = m;
                }
                if (m.getReturnType() == String.class && !m.getName().equals("toString") && m.getParameterCount() == 0) {
                    stringMethods.add(m);
                }
            }
            assertNotNull(getId);
            Object id = getId.invoke(carPark);
            Long carParkId = (Long) id;
            Object carPark2 = carParkService.getCarPark("CAR-PARK-NAME");
            assertNotNull(carPark2);
            Object id2 = getId.invoke(carPark2);
            assertEquals(id, id2);
            for (Method m : stringMethods) {
                Object a1 = m.invoke(carPark);
                Object a2 = m.invoke(carPark2);
                assertEquals(a1, a2);
            }
        } catch (Exception ex) {
            assertTrue(false);
        }
    }

    @Test
    void PARK03_uniqueCarParkNameTest() {
        Object carPark = carParkService.createCarPark("test2", "testtest", 12);
        try {
            Object carPark2 = carParkService.createCarPark("test2", "testtest", 12);
            assertNull(carPark2);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    @Order(1)
    void PARK04_getCarParksTest() {
        Object carPark = carParkService.createCarPark("CAR-PARK-1-1", "test11", 10);
        assertNotNull(carPark);
        Class c = carPark.getClass();
        Method getId = null;
        List<Method> stringMethods = new ArrayList<>();
        for (Method m : c.getMethods()) {
            if (m.getReturnType() == Long.class && m.getParameterCount() == 0) {
                getId = m;
            }
            if (m.getReturnType() == String.class && !m.getName().equals("toString") && m.getParameterCount() == 0) {
                stringMethods.add(m);
            }
        }
        assertNotNull(getId);
        assertFalse(stringMethods.isEmpty());

        Object carPark2 = carParkService.createCarPark("CAR-PARK-1-2", "test12", 20);
        assertNotNull(carPark2);

        List<Object> parks = carParkService.getCarParks();
        assertNotNull(parks);
        assertFalse(parks.isEmpty());
        assertEquals(parks.size(), 2);
        try {
            Long carPark1Id = (Long) getId.invoke(carPark);
            Long carPark2Id = (Long) getId.invoke(carPark2);
            Long park1Id = (Long) getId.invoke(parks.get(0));
            Long park2Id = (Long) getId.invoke(parks.get(1));
            assertNotNull(carPark1Id);
            assertNotNull(carPark2Id);
            assertNotNull(park1Id);
            assertNotNull(park2Id);

            if (carPark1Id.equals(park1Id)) {
                if (carPark2Id.equals(park2Id)) {
                    for (Method m : stringMethods) {
                        String carPark1S = (String) m.invoke(carPark);
                        String carPark2S = (String) m.invoke(carPark2);
                        String park1S = (String) m.invoke(parks.get(0));
                        String park2S = (String) m.invoke(parks.get(1));
                        assertNotNull(carPark1S);
                        assertNotNull(carPark2S);
                        assertNotNull(park1S);
                        assertNotNull(park2S);

                        assertEquals(carPark1S, park1S);
                        assertEquals(carPark2S, park2S);
                    }
                } else {
                    assertTrue(false);
                }
            } else if (carPark1Id.equals(park2Id)) {
                if (carPark2Id.equals(park1Id)) {
                    for (Method m : stringMethods) {
                        String carPark1S = (String) m.invoke(carPark);
                        String carPark2S = (String) m.invoke(carPark2);
                        String park1S = (String) m.invoke(parks.get(0));
                        String park2S = (String) m.invoke(parks.get(1));
                        assertNotNull(carPark1S);
                        assertNotNull(carPark2S);
                        assertNotNull(park1S);
                        assertNotNull(park2S);

                        assertEquals(carPark1S, park2S);
                        assertEquals(carPark2S, park1S);
                    }
                } else {
                    assertTrue(false);
                }
            } else {
                assertTrue(false);
            }

        } catch (Exception e) {
            assertTrue(false);
        }


    }

    @Test
    void PARK06_deleteCarParkTest() {
        Object carPark = carParkService.createCarPark("DELETE-CAR-PARK-1", "test11", 10);
        assertNotNull(carPark);
        Class c = carPark.getClass();
        Method getId = null;

        for (Method m : c.getMethods()) {
            if (m.getParameterCount() == 0 && m.getReturnType() == Long.class) {
                getId = m;
                break;
            }
        }
        assertNotNull(getId);
        try {
            Long id = (Long) getId.invoke(carPark);
            Object park = carParkService.getCarPark(id);
            assertNotNull(park);

            carParkService.deleteCarPark(id);
            try {
                park = carParkService.getCarPark(id);
                assertNull(park);
            } catch (Exception e) {
                assertTrue(true);
            }
        } catch (Exception e) {
            assertTrue(false);
        }

    }
}
