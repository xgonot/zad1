package sk.stuba.fei.uim.vsa.pr1.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr1.TestData.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;

class ParkingSpotTest {

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
    void SPOT01_getAllParkingSlotsWithoutType() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
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
        Object spot1Loaded = carParkService.getParkingSpot(spot1Id);
        assertNotNull(spot1Loaded);
        Long spot1LoadedId = getFieldValue(spot1Loaded, "id", Long.class);
        String[] fields = findFieldByNameAndType(spot1, "identifier", String.class);
        assertNotNull(fields);
        assertTrue(fields.length > 0);
        String identifierField = fields[0];
        String spot1Identifier = getFieldValue(spot1, identifierField, String.class);
        String spot1LoadedIdentifier = getFieldValue(spot1Loaded, identifierField, String.class);
        assertEquals(spot1Identifier, spot1LoadedIdentifier);
        assertEquals(spot1Id, spot1LoadedId);

        Object spot2 = carParkService.createParkingSpot(carParkId, "Floor3-1", "1.02");
        assertNotNull(spot2);
        testShouldHaveId(spot2);
        Long spot2Id = getFieldValue(spot2, "id", Long.class);
        Object spot2Loaded = carParkService.getParkingSpot(spot2Id);
        assertNotNull(spot2Loaded);
        assertEquals(spot2Id, getFieldValue(spot2Loaded, "id", Long.class));

        Object floor2 = carParkService.createCarParkFloor(carParkId, "Floor3-2");
        assertNotNull(floor2);
        Object spot21 = carParkService.createParkingSpot(carParkId, "Floor3-2", "2.01");
        assertNotNull(spot21);
        testShouldHaveId(spot21);
        Long spot21Id = getFieldValue(spot21, "id", Long.class);
        Object spot21Loaded = carParkService.getParkingSpot(spot21Id);
        assertNotNull(spot21Loaded);
        assertEquals(spot21Id, getFieldValue(spot21Loaded, "id", Long.class));


        Map<String, List<Object>> floors = carParkService.getParkingSpots(carParkId);
        assertNotNull(floors);
        assertEquals(2, floors.size());
        List<Object> firstFloorSpots = floors.get("Floor3-1");
        assertNotNull(firstFloorSpots);
        assertEquals(2, firstFloorSpots.size());
        testShouldHaveId(firstFloorSpots.get(0));
        testShouldHaveId(firstFloorSpots.get(1));
        assertNotEquals(getFieldValue(firstFloorSpots.get(0), "id", Long.class), getFieldValue(firstFloorSpots.get(1), "id", Long.class));
        assertTrue(firstFloorSpots.stream().anyMatch(s -> {
            try {
                return Objects.equals(getFieldValue(firstFloorSpots.get(0), "id", Long.class), spot1Id);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }));
        List<Object> secondFloorSpots = floors.get("Floor3-2");
        assertNotNull(secondFloorSpots);
        assertEquals(1, secondFloorSpots.size());
        Object secondFloorSpot = secondFloorSpots.get(0);
        testShouldHaveId(secondFloorSpot);
        assertEquals(spot21Id, getFieldValue(secondFloorSpot, "id", Long.class));
    }
    

    @Test
    public void SPOT02_createAndGetParkingSpotWithoutType()
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
    
    @Test
    public void SPOT03_getAllParkingSpotsForCarParkWithoutType() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException
    {
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
        
        Map<String, List<Object>> map = carParkService.getParkingSpots(carParkId);
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
                for (String f: spotFields) {
                    assertEquals(getFieldValue(floor1Spot1, f, String.class), getFieldValue(fl1Spot1, f, String.class));
                    assertEquals(getFieldValue(floor1Spot2, f, String.class), getFieldValue(fl1Spot2, f, String.class));
                    
                }
            } else {
                assertTrue(false);
            }
            
        } else if (floor1Spot2Id.equals(fl1Spot2Id)) {
            for (String f: spotFields) {
                    assertEquals(getFieldValue(floor1Spot2, f, String.class), getFieldValue(fl1Spot1, f, String.class));
                    assertEquals(getFieldValue(floor1Spot1, f, String.class), getFieldValue(fl1Spot2, f, String.class));                    
                }
            
        } else {
            assertTrue(false);
        }
        
        if (floor2Spot1Id.equals(flr2Spot1Id)) {
            if (floor2Spot2Id.equals(flr2Spot2Id)) {
                for (String f: spotFields) {
                    assertEquals(getFieldValue(floor2Spot1, f, String.class), getFieldValue(fl2Spot1, f, String.class));
                    assertEquals(getFieldValue(floor2Spot2, f, String.class), getFieldValue(fl2Spot2, f, String.class));
                }
            } else {
                assertTrue(false);
            }
        } else if (floor2Spot2Id.equals(flr2Spot1Id)) {
            if (floor2Spot1Id.equals(flr2Spot2Id)) {
                for (String f: spotFields) {
                    assertEquals(getFieldValue(floor2Spot2, f, String.class), getFieldValue(fl2Spot1, f, String.class));
                    assertEquals(getFieldValue(floor2Spot1, f, String.class), getFieldValue(fl2Spot2, f, String.class));
                }
            } else {
                assertTrue(false);
            }
        } else {
            assertTrue(false);
        }
    }

}
