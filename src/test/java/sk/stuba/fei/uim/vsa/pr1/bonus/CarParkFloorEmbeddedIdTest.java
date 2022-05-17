package sk.stuba.fei.uim.vsa.pr1.bonus;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr1.TestData.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.clearHolidayDB;

class CarParkFloorEmbeddedIdTest {

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
    void BONUSE01_createAndGetCarParkFloorTestEmbedded() {
        try {
            Object carPark = carParkService.createCarPark("test4", "testtest", 12);
            assertNotNull(carPark);
            Class c = carPark.getClass();
            Method[] methods = c.getMethods();
            Method getId = null;
            for (Method m : methods) {
                if (m.getReturnType() == Long.class) {
                    getId = m;
                    break;
                }
            }
            assertNotNull(getId);
            Long id = (Long) getId.invoke(carPark);


            Object carParkFloor1 = carParkService.createCarParkFloor(id, "Floor1");
            assertNotNull(carParkFloor1);

            Object floor1 = carParkService.getCarParkFloor(id, "Floor1");
            assertNotNull(floor1);

            Object carParkFloor2 = carParkService.createCarParkFloor(id, "Floor2");
            assertNotNull(carParkFloor2);

            Object floor2 = carParkService.getCarParkFloor(id, "Floor2");
            assertNotNull(floor2);

            Class floorClass = floor1.getClass();
            Class embeddedKeyClass = null;
            Method getEmbeddedKeyMethod = null;
            for (Method m : floorClass.getMethods()) {
                if (!Collection.class.isAssignableFrom(m.getReturnType())
                        && m.getReturnType() != String.class
                        && m.getReturnType() != Long.class
                        && m.getReturnType() != c
                        && m.getParameterCount() == 0) {
                    embeddedKeyClass = m.getReturnType();
                    getEmbeddedKeyMethod = m;
                    break;
                }
            }

            assertNotNull(embeddedKeyClass);
            assertNotNull(getEmbeddedKeyMethod);

            Method carParkFloorEmbeddedStringMethod = null;
            Method carParkFloorEmbeddedIdMethod = null;

            for (Method m : embeddedKeyClass.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        carParkFloorEmbeddedIdMethod = m;
                    } else if (m.getReturnType() == String.class && !m.getName().equals("toString")) {
                        carParkFloorEmbeddedStringMethod = m;
                    }
                }

            }

            assertNotNull(carParkFloorEmbeddedIdMethod);
            assertNotNull(carParkFloorEmbeddedStringMethod);

            Object carParkFloor1EmbeddedKey = getEmbeddedKeyMethod.invoke(carParkFloor1);
            Object floor1EmbeddedKey = getEmbeddedKeyMethod.invoke(floor1);
            assertNotNull(carParkFloor1EmbeddedKey);
            assertNotNull(floor1EmbeddedKey);

            assertEquals(
                    carParkFloorEmbeddedIdMethod.invoke(carParkFloor1EmbeddedKey),
                    carParkFloorEmbeddedIdMethod.invoke(floor1EmbeddedKey)
            );

            Object carParkFloor2EmbeddedKey = getEmbeddedKeyMethod.invoke(carParkFloor2);
            Object floor2EmbeddedKey = getEmbeddedKeyMethod.invoke(floor2);

            assertNotNull(carParkFloor2EmbeddedKey);
            assertNotNull(floor2EmbeddedKey);

            assertEquals(
                    carParkFloorEmbeddedIdMethod.invoke(carParkFloor2EmbeddedKey),
                    carParkFloorEmbeddedIdMethod.invoke(floor2EmbeddedKey)
            );


            String cF1 = (String) carParkFloorEmbeddedStringMethod.invoke(carParkFloor1EmbeddedKey);
            String f1 = (String) carParkFloorEmbeddedStringMethod.invoke(floor1EmbeddedKey);
            String cF2 = (String) carParkFloorEmbeddedStringMethod.invoke(carParkFloor2EmbeddedKey);
            String f2 = (String) carParkFloorEmbeddedStringMethod.invoke(floor2EmbeddedKey);

            assertNotNull(cF1);
            assertNotNull(f1);
            assertNotNull(cF2);
            assertNotNull(f2);

            assertEquals(cF1, f1);
            assertEquals(cF2, f2);


        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void BONUSE02_deleteCarParkFloorEmbeddedTest() {
        Object carPark = carParkService.createCarPark("FLOOR-DELETE", "testtest", 12);
        try {
            assertNotNull(carPark);
            Class c = carPark.getClass();
            Method[] methods = c.getMethods();
            Method getId = null;
            for (Method m : methods) {
                if (m.getReturnType() == Long.class) {
                    getId = m;
                    break;
                }
            }
            assertNotNull(getId);
            Long id = (Long) getId.invoke(carPark);


            Object carParkFloor1 = carParkService.createCarParkFloor(id, "Floor1");
            assertNotNull(carParkFloor1);

            Object floor1 = carParkService.getCarParkFloor(id, "Floor1");
            Long carParkFloorId = null;
            if (floor1 == null) {
                for (Method m : floor1.getClass().getMethods()) {
                    if (m.getParameterCount() == 0 && m.getReturnType() == Long.class) {
                        carParkFloorId = (Long) m.invoke(floor1);
                        assertNotNull(carParkFloorId);
                        floor1 = carParkService.getCarParkFloor(carParkFloorId);
                        assertNotNull(floor1);
                    }
                }
            }
            List<Object> floors = carParkService.getCarParkFloors(id);
            assertNotNull(floors);
            assertEquals(floors.size(), 1);

            Object a = carParkService.deleteCarParkFloor(id, "Floor1");
            if (a == null) {
                assertNotNull(carParkFloorId);
                a = carParkService.deleteCarParkFloor(carParkFloorId);
                assertNotNull(a);
            }


            if (carParkFloorId == null) {
                try {
                    floor1 = carParkService.getCarParkFloor(carParkFloorId, "Floor1");
                    assertNull(floor1);
                } catch (Exception e) {
                    assertTrue(true);
                }
            } else {
                try {
                    floor1 = carParkService.getCarParkFloor(carParkFloorId);
                    assertNull(floor1);
                } catch (Exception e) {
                    assertTrue(true);
                }
            }
            try {
                floors = carParkService.getCarParkFloors(id);
                if (floors == null || floors.isEmpty()) {
                    assertTrue(true);
                } else {
                    assertTrue(false);
                }
            } catch (Exception e) {
                assertTrue(true);
            }


        } catch (Exception e) {
            assertTrue(false);
        }

    }


}
