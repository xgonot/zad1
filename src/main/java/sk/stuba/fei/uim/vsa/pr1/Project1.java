package sk.stuba.fei.uim.vsa.pr1;

import sk.stuba.fei.uim.vsa.pr1.domain.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Project1 {

    public static void main(String[] args) {
        AbstractCarParkService c = new CarParkService();
        Object u = c.createUser("Thomas", "Gono", "gono@tssgroup.sk");
        User user = (User) u;
        Object carO = c.createCar(user.getId(), "HONDA", "CIVIC", "RED", "BA781AH");

        Car car = (Car) carO;
        SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
        try {
            c.createHoliday("Vianoce", formater.parse("27-04-2000"));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }


        Object p = c.createCarPark("A", "a", 10);
        CarPark carPark = (CarPark) p;

        CarPark p2 = (CarPark) c.createCarPark("AB", "C", 10);
        p2.setName("A");
        c.updateCarPark((Object) p2);

        CarParkFloor floor = (CarParkFloor) c.createCarParkFloor(carPark.getId(), "1st");

        ParkingSpot parkingSpot = (ParkingSpot) c.createParkingSpot(carPark.getId(), floor.getEmbeddedId().getIdentifier(), "1.001");
        ParkingSpot parkingSpot2 = (ParkingSpot) c.createParkingSpot(carPark.getId(), floor.getEmbeddedId().getIdentifier(), "1.002");

        Map<String, List<Object>> freeSpot = c.getAvailableParkingSpots(carPark.getName());
        for (Map.Entry<String, List<Object>> e : freeSpot.entrySet()) {
            System.out.println(e.getKey());
            for (Object obj : e.getValue()) {
                ParkingSpot spt = (ParkingSpot) obj;
                System.out.println(spt.getId() + " " + spt.getIdentifier());
            }
        }

        Reservation r = (Reservation) c.createReservation(parkingSpot.getId(), car.getId());

        LocalDateTime now = LocalDateTime.now().minusHours(10).minusMinutes(3);
        r.setStartsAt(now);
        c.updateReservation((Object) r);


        freeSpot = c.getAvailableParkingSpots(carPark.getName());
        for (Map.Entry<String, List<Object>> e : freeSpot.entrySet()) {
            System.out.println(e.getKey());
            for (Object obj : e.getValue()) {
                ParkingSpot spt = (ParkingSpot) obj;
                System.out.println(spt.getId() + " " + spt.getIdentifier());
            }
        }

        c.endReservation(r.getId());

        freeSpot = c.getAvailableParkingSpots(carPark.getName());
        for (Map.Entry<String, List<Object>> e : freeSpot.entrySet()) {
            System.out.println(e.getKey());
            for (Object obj : e.getValue()) {
                ParkingSpot spt = (ParkingSpot) obj;
                System.out.println(spt.getId() + " " + spt.getIdentifier());
            }
        }

        Reservation r2 = (Reservation) c.createReservation(parkingSpot2.getId(), car.getId());

        //c.deleteParkingSpot(parkingSpot2.getId());

        //c.deleteCarPark(carPark.getId());
       /* carPark = (CarPark) c.getCarPark(carPark.getId());
       for (CarParkFloor f: carPark.getCarParkFloorList())
       {
           c.deleteCarParkFloor(f.getEmbeddedId().getCarParkId(), f.getEmbeddedId().getIdentifier());
       }*/

        c.deleteUser(user.getId());

    }

}
