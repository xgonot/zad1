package sk.stuba.fei.uim.vsa.pr1.groupb;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;
import sk.stuba.fei.uim.vsa.pr1.TestData;
import static sk.stuba.fei.uim.vsa.pr1.TestData.CAR_TYPE_NAME;
import static sk.stuba.fei.uim.vsa.pr1.TestData.CAR_TYPE_NAME_ALT;
import static sk.stuba.fei.uim.vsa.pr1.TestData.DB;
import static sk.stuba.fei.uim.vsa.pr1.TestData.PASSWORD;
import static sk.stuba.fei.uim.vsa.pr1.TestData.USERNAME;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.clearCarTypeDB;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.clearDB;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.getFieldValue;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.getMySQL;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.getServiceClass;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.testShouldHaveId;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sheax
 */
public class ReservationAndParkingSpotWithTypeTest {
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
        clearCarTypeDB(mysql);
    }
    
    @Test
    public void TYPE_RESERVATION_01_reserveOnlyForCarType() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object carPark = carParkService.createCarPark(TestData.CarPark.name, TestData.CarPark.address, TestData.CarPark.price);
        assertNotNull(carPark);
        testShouldHaveId(carPark);
        Long id = getFieldValue(carPark, "id", Long.class);
        assertNotNull(id);
        
        Object carParkFloor = carParkService.createCarParkFloor(id, TestData.CarPark.floor);
        
        assertNotNull(carParkFloor);
        
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastName, TestData.User.email);
        assertNotNull(user);
        testShouldHaveId(user);
        Long userId = getFieldValue(user, "id", Long.class);
        assertNotNull(userId);
        
        Object type = carParkService.createCarType(CAR_TYPE_NAME);
        Object altType = carParkService.createCarType(CAR_TYPE_NAME_ALT);
        
        assertNotNull(type);
        assertNotNull(altType);
        testShouldHaveId(type);
        testShouldHaveId(altType);
        
        Long typeId = getFieldValue(type, "id", Long.class);
        Long altTypeId = getFieldValue(altType, "id", Long.class);
        assertNotNull(typeId);
        assertNotNull(altTypeId);
        
        Object car = carParkService.createCar(userId, TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv, typeId);
        Object parkingSpot = carParkService.createParkingSpot(id, TestData.CarPark.floor, TestData.CarPark.spot, typeId);
        testShouldHaveId(car);
        testShouldHaveId(parkingSpot);
        Long carId = getFieldValue(car, "id", Long.class);
        Long parkingSpotId = getFieldValue(parkingSpot, "id", Long.class);
        assertNotNull(carId);
        assertNotNull(parkingSpotId);
        
        Object user2 = carParkService.createUser(TestData.User2.firstName, TestData.User2.lastName, TestData.User2.email);
        assertNotNull(user2);
        testShouldHaveId(user2);
        Long user2Id = getFieldValue(user2, "id", Long.class);
        assertNotNull(user2Id);
        
        Object altCar = carParkService.createCar(user2Id, TestData.Car2.brand, TestData.Car2.model, TestData.Car2.colour, TestData.Car2.ecv, altTypeId);
        Object altParkingSpot = carParkService.createParkingSpot(id, TestData.CarPark.floor, TestData.CarPark.spot2, altTypeId);
        assertNotNull(altCar);
        assertNotNull(altParkingSpot);
        testShouldHaveId(altCar);
        testShouldHaveId(altParkingSpot);
        Long altCarId = getFieldValue(altCar, "id", Long.class);
        Long altParkingSpotId = getFieldValue(altParkingSpot, "id", Long.class);
        assertNotNull(altCarId);
        assertNotNull(altParkingSpotId);
        
        Object reservation = carParkService.createReservation(parkingSpotId, carId);
        assertNotNull(reservation);
        testShouldHaveId(reservation);
        Long reservationId = getFieldValue(reservation, "id", Long.class);
        assertNotNull(reservationId);
        
        Object endedReservation = carParkService.endReservation(reservationId);
        
        Object altReservation = carParkService.createReservation(altParkingSpotId, altCarId);
        assertNotNull(altReservation);
        testShouldHaveId(altReservation);
        Long altReservationId = getFieldValue(altReservation, "id", Long.class);
        Object altEndedReservation = carParkService.endReservation(altReservationId);
        
        try {
            reservation = carParkService.createReservation(parkingSpotId, altCarId);
            assertNull(reservation);
        } catch (Exception e) {
            assertTrue(true);
        }
        
        try {
            reservation = carParkService.createReservation(altParkingSpotId, carId);
            assertNull(reservation);
        } catch (Exception e) {
            assertTrue(true);
        }
        
        
    }
}
