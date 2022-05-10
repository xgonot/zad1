package sk.stuba.fei.uim.vsa.pr1.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;
import sk.stuba.fei.uim.vsa.pr1.MainTest;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr1.TestData.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;

class CarParkFloorTest {

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
    public void createAndGetCarParkFloorTestEmbedded()
    {
        try {
            Object carPark = carParkService.createCarPark("test4", "testtest", 12);
            assertNotNull(carPark);
            Class c = carPark.getClass();
            Method[] methods = c.getMethods();
            Method getId = null;
            for (Method m: methods) {
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

            Object floor2 =  carParkService.getCarParkFloor(id, "Floor2");
            assertNotNull(floor2);

            Class floorClass = floor1.getClass();
            Class embeddedKeyClass = null;
            Method getEmbeddedKeyMethod = null;
            for (Method m: floorClass.getMethods()) {
                if (! Collection.class.isAssignableFrom(m.getReturnType())
                        && m.getReturnType() != String.class
                        && m.getReturnType() != Long.class
                        && m.getReturnType() != c
                        && m.getParameterCount() == 0)
                {
                    embeddedKeyClass = m.getReturnType();
                    getEmbeddedKeyMethod = m;
                    break;
                }
            }

            assertNotNull(embeddedKeyClass);
            assertNotNull(getEmbeddedKeyMethod);

            Method carParkFloorEmbeddedStringMethod = null;
            Method carParkFloorEmbeddedIdMethod = null;

            for (Method m: embeddedKeyClass.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        carParkFloorEmbeddedIdMethod = m;
                    } else if (m.getReturnType() == String.class && !m.getName().equals("toString") ) {
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


            String cF1 = (String)carParkFloorEmbeddedStringMethod.invoke(carParkFloor1EmbeddedKey);
            String f1 = (String)carParkFloorEmbeddedStringMethod.invoke(floor1EmbeddedKey);
            String cF2 = (String)carParkFloorEmbeddedStringMethod.invoke(carParkFloor2EmbeddedKey);
            String f2 = (String)carParkFloorEmbeddedStringMethod.invoke(floor2EmbeddedKey);

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
    public void getAllCarParkFloorsEmbedded()
    {
        try {
            Object carPark = carParkService.createCarPark("test5", "testtest", 12);
            assertNotNull(carPark);
            Class c = carPark.getClass();
            Method[] methods = c.getMethods();
            Method getId = null;
            for (Method m: methods) {
                if (m.getReturnType() == Long.class) {
                    getId = m;
                    break;
                }
            }
            assertNotNull(getId);
            Long id = (Long) getId.invoke(carPark);


            Object carParkFloor1 = carParkService.createCarParkFloor(id, "Floor1");
            assertNotNull(carParkFloor1);

            /*Object floor1 = carParkService.getCarParkFloor(id, "Floor1");
            assertNotNull(floor1);*/

            Object carParkFloor2 = carParkService.createCarParkFloor(id, "Floor2");
            assertNotNull(carParkFloor2);

            /*Object floor2 =  carParkService.getCarParkFloor(id, "Floor2");
            assertNotNull(floor2);*/

            Class floorClass = carParkFloor1.getClass();
            Class embeddedKeyClass = null;
            Method getEmbeddedKeyMethod = null;
            for (Method m: floorClass.getMethods()) {
                if (! Collection.class.isAssignableFrom(m.getReturnType())
                        && m.getReturnType() != String.class
                        && m.getReturnType() != Long.class
                        && m.getReturnType() != c
                        && m.getParameterCount() == 0)
                {
                    embeddedKeyClass = m.getReturnType();
                    getEmbeddedKeyMethod = m;
                    break;
                }
            }

            assertNotNull(embeddedKeyClass);
            assertNotNull(getEmbeddedKeyMethod);

            Method carParkFloorEmbeddedStringMethod = null;
            Method carParkFloorEmbeddedIdMethod = null;

            for (Method m: embeddedKeyClass.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        carParkFloorEmbeddedIdMethod = m;
                    } else if (m.getReturnType() == String.class && !m.getName().equals("toString") ) {
                        carParkFloorEmbeddedStringMethod = m;
                    }
                }

            }

            assertNotNull(carParkFloorEmbeddedIdMethod);
            assertNotNull(carParkFloorEmbeddedStringMethod);

            Object carParkFloor1EmbeddedKey = getEmbeddedKeyMethod.invoke(carParkFloor1);
            //Object floor1EmbeddedKey = getEmbeddedKeyMethod.invoke(floor1);
            assertNotNull(carParkFloor1EmbeddedKey);
            //assertNotNull(floor1EmbeddedKey);

            /*assertEquals(
                    carParkFloorEmbeddedIdMethod.invoke(carParkFloor1EmbeddedKey),
                    carParkFloorEmbeddedIdMethod.invoke(floor1EmbeddedKey)
            );*/

            Object carParkFloor2EmbeddedKey = getEmbeddedKeyMethod.invoke(carParkFloor2);
            //Object floor2EmbeddedKey = getEmbeddedKeyMethod.invoke(floor2);

            assertNotNull(carParkFloor2EmbeddedKey);
            //assertNotNull(floor2EmbeddedKey);

            /*assertEquals(
                    carParkFloorEmbeddedIdMethod.invoke(carParkFloor2EmbeddedKey),
                    carParkFloorEmbeddedIdMethod.invoke(floor2EmbeddedKey)
            );*/


            String cF1 = (String)carParkFloorEmbeddedStringMethod.invoke(carParkFloor1EmbeddedKey);
            //String f1 = (String)carParkFloorEmbeddedStringMethod.invoke(floor1EmbeddedKey);
            String cF2 = (String)carParkFloorEmbeddedStringMethod.invoke(carParkFloor2EmbeddedKey);
            //String f2 = (String)carParkFloorEmbeddedStringMethod.invoke(floor2EmbeddedKey);

            assertNotNull(cF1);
            //assertNotNull(f1);
            assertNotNull(cF2);
            //assertNotNull(f2);

            /*assertEquals(cF1, f1);
            assertEquals(cF2, f2);*/

            List<Object> floors = carParkService.getCarParkFloors(id);
            assertEquals(floors.size(), 2);
            Object fl = floors.get(0);
            // gotta find which one it is, floor1 or floor2
            Object embFl = getEmbeddedKeyMethod.invoke(fl);
            assertNotNull(embFl);
            String emfFlId =(String) carParkFloorEmbeddedStringMethod.invoke(embFl);
            assertNotNull(emfFlId);

            Object fl2 = floors.get(1);
            // gotta find which one it is, floor1 or floor2
            Object embFl2 = getEmbeddedKeyMethod.invoke(fl2);
            assertNotNull(embFl2);
            String emfFlId2 =(String) carParkFloorEmbeddedStringMethod.invoke(embFl2);
            assertNotNull(emfFlId2);

            if (emfFlId.equals(cF1)) {
                if (emfFlId2.equals(cF2)) {
                    assertTrue(true);
                } else {
                    assertTrue(false);
                }
            } else if (emfFlId.equals(cF2)) {
                if (emfFlId2.equals(cF1)) {
                    assertTrue(true);
                } else {
                    assertTrue(false);
                }
            } else {
                assertTrue(false);
            }
        } catch (Exception e) {
            assertTrue(false);
        }
        /*List<Object> floors = carParkService.getCarParkFloors(carParkId);
            assertEquals(floors.size(), 2);
            
            Object floor1 = floors.get(1);
            
            for (Object fl : floors) {
                
            }*/
    }

    //@Test
    public void getCarParkFloorIdTest()
    {
        try {
            Object carPark = carParkService.createCarPark("test6", "testtest", 12);
            assertNotNull(carPark);
            Class c = carPark.getClass();
            Method[] methods = c.getMethods();
            Method getId = null;
            for (Method m: methods) {
                if (m.getReturnType() == Long.class) {
                    getId = m;
                    break;
                }
            }
            assertNotNull(getId);
            Long id = (Long) getId.invoke(carPark);
            assertNotNull(id);

            Object carParkFloor1 = carParkService.createCarParkFloor(id, "Floor1-1");
            assertNotNull(carParkFloor1);

            Class carParkFloorClass = carParkFloor1.getClass();
            Method carParkFloorGetId = null;
            Method carParkFloorGetIdentifier = null;

            for (Method m: carParkFloorClass.getMethods()) {
                if (m.getParameterCount() == 0 && m.getReturnType() == Long.class) {
                    carParkFloorGetId = m;
                } else if (m.getParameterCount() == 0 && m.getReturnType() == String.class && ! m.getName().equals("toString")) {
                    carParkFloorGetIdentifier = m;
                }
                if (carParkFloorGetId != null && carParkFloorGetIdentifier != null) {
                    break;
                }
            }
            assertNotNull(carParkFloorGetId);

            Long carParkFloor1Id = (Long) carParkFloorGetId.invoke(carParkFloor1);
            assertNotNull(carParkFloor1Id);

            Object floor1 = carParkService.getCarParkFloor(id);
            assertNotNull(floor1);

            Long floor1Id = (Long) carParkFloorGetId.invoke(floor1);
            assertNotNull(floor1Id);
            assertEquals(carParkFloor1Id, floor1Id);
            String carParkFloor1Identifier = (String) carParkFloorGetIdentifier.invoke(carParkFloor1);
            String floor1Identifier = (String) carParkFloorGetIdentifier.invoke(floor1);
            assertNotNull(carParkFloor1Identifier);
            assertNotNull(floor1Identifier);
            assertEquals(carParkFloor1Identifier, floor1Identifier);

            Object carParkFloor2 = carParkService.createCarParkFloor(id, "Floor1-2");
            assertNotNull(carParkFloor2);
            Long carParkFloor2Id = (Long) carParkFloorGetId.invoke(carParkFloor2);
            assertNotNull(carParkFloor2Id);
            Object floor2 =  carParkService.getCarParkFloor(carParkFloor2Id);
            assertNotNull(floor2);
            Long floor2Id = (Long) carParkFloorGetId.invoke(floor2);
            assertNotNull(floor2Id);

            String carParkFloor2Identifier = (String) carParkFloorGetIdentifier.invoke(carParkFloor2);
            String floor2Identifier = (String) carParkFloorGetIdentifier.invoke(floor2);
            assertNotNull(carParkFloor2Identifier);
            assertNotNull(floor2Identifier);
            assertEquals(carParkFloor2Identifier, floor2Identifier);

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    //@Test
    public void updateCarParkFloorTest()
    {
        assertTrue(false);
    }

    @Test
    public void deleteCarParkFloorTest()
    {
        Object carPark = carParkService.createCarPark("FLOOR-DELETE", "testtest", 12);
        try {
            assertNotNull(carPark);
            Class c = carPark.getClass();
            Method[] methods = c.getMethods();
            Method getId = null;
            for (Method m: methods) {
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
                for (Method m: floor1.getClass().getMethods()) {
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
    
    // @Test
    public void getAllCarParkFloorsId()
    {
        try {
            Object carPark = carParkService.createCarPark("test7", "testtest", 12);
            Long carParkId = getFieldValue(carPark, "id", Long.class);
            assertNotNull(carParkId);
            Object floor1 = carParkService.createCarParkFloor(carParkId, "Floor1");
            Object floor2 = carParkService.createCarParkFloor(carParkId, "Floor2");
            assertNotNull(floor1);
            assertNotNull(floor2);
            Method carParkFloorGetIdentifier = null;
            for (Method m: floor1.getClass().getMethods()) {
                if (m.getParameterCount() == 0 && m.getReturnType() == String.class && ! m.getName().equals("toString")) {
                    carParkFloorGetIdentifier = m;
                    break;
                }
            }
            assertNotNull(carParkFloorGetIdentifier);
            
            List<Object> floors = carParkService.getCarParkFloors(carParkId);
            assertEquals(floors.size(), 2);
            Object fl1 = floors.get(0);
            Object fl2 = floors.get(1);
            assertNotNull(fl1);
            assertNotNull(fl2);
            
            Long floor1Id = getFieldValue(floor1, "id", Long.class);
            Long floor2Id = getFieldValue(floor2, "id", Long.class);
            Long fl1Id = getFieldValue(fl1, "id", Long.class);
            Long fl2Id = getFieldValue(fl2, "id", Long.class);
            assertNotNull(floor1Id);
            assertNotNull(floor2Id);
            assertNotNull(fl1Id);
            assertNotNull(fl2Id);
            
            if (floor1Id.equals(fl1)) {
                if (floor2Id.equals(fl2)) {
                    assertEquals(carParkFloorGetIdentifier.invoke(floor1), carParkFloorGetIdentifier.invoke(fl1));
                    assertEquals(carParkFloorGetIdentifier.invoke(floor2), carParkFloorGetIdentifier.invoke(fl2));
                } else {
                    assertTrue(false);
                }
            } else if (floor2Id.equals(fl1)) {
                if (floor1Id.equals(fl1)) {
                    assertEquals(carParkFloorGetIdentifier.invoke(floor2), carParkFloorGetIdentifier.invoke(fl1));
                    assertEquals(carParkFloorGetIdentifier.invoke(floor1), carParkFloorGetIdentifier.invoke(fl2));
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

    //@Test
    public void createAndGetParkingFloorWithoutTypeTest()
    {
        try {
            Object carPark = carParkService.createCarPark("test7", "testtest", 12);
            assertNotNull(carPark);
            Class c = carPark.getClass();
            Method[] methods = c.getMethods();
            Method getId = null;
            for (Method m: methods) {
                if (m.getReturnType() == Long.class) {
                    getId = m;
                    break;
                }
            }
            assertNotNull(getId);
            Long id = (Long) getId.invoke(carPark);
            assertNotNull(id);

            Object carParkFloor = carParkService.createCarParkFloor(id, "Floor3-1");
            assertNotNull(carParkFloor);

            Object spot1 = carParkService.createParkingSpot(id, "Floor3-1", "1.01");
            assertNotNull(spot1);

            Method getParkingSpotId = null;
            Method getParkingSpotIdentifier = null;
            Class parkingSpotClass = spot1.getClass();

            for (Method m: parkingSpotClass.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        getParkingSpotId = m;
                    } else if (m.getReturnType() == String.class && ! m.getName().equals("toString")) {
                        getParkingSpotIdentifier = m;
                    }
                }
            }

            assertNotNull(getParkingSpotId);
            assertNotNull(getParkingSpotIdentifier);

            Long spotId = (Long) getParkingSpotId.invoke(spot1);
            assertNotNull(spotId);

            Object carParkSpot = carParkService.getParkingSpot(spotId);
            assertNotNull(carParkSpot);

            Long carParkSpotId = (Long) getParkingSpotId.invoke(carParkSpot);
            assertNotNull(carParkSpotId);

            String spotIdentifier = (String) getParkingSpotIdentifier.invoke(spot1);
            String parkingSpotIdentifier = (String) getParkingSpotIdentifier.invoke(carParkSpot);

            assertNotNull(spotIdentifier);
            assertNotNull(parkingSpotIdentifier);

            assertEquals(spotIdentifier, parkingSpotIdentifier);


        } catch (Exception e) {
            assertTrue(false);
        }
    }    
    
}
