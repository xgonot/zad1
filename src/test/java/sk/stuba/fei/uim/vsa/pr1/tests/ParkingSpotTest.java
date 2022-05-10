package sk.stuba.fei.uim.vsa.pr1.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;

import java.lang.reflect.InvocationTargetException;
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

}
