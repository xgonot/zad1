package sk.stuba.fei.uim.vsa.pr1;

import sk.stuba.fei.uim.vsa.pr1.domain.Car;
import sk.stuba.fei.uim.vsa.pr1.domain.CarPark;
import sk.stuba.fei.uim.vsa.pr1.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.function.Function;

public class CarParkService extends AbstractCarParkService {

    public CarParkService() {
        super();
    }

    @Override
    public Object createCarPark(String name, String address, Integer pricePerHour) {
        return runTransaction(em -> {
            CarPark park = new CarPark(name, address, 0.0);
            park.setPrice(pricePerHour);
            em.persist(park);
            return park;
        });
    }

    @Override
    public Object getCarPark(Long carParkId) {
        EntityManager em = emf.createEntityManager();
        CarPark park = em.find(CarPark.class, carParkId);
        em.close();
        return park;
    }

    @Override
    public Object getCarPark(String carParkName) {
        return findOne(em -> {
            TypedQuery<CarPark> query = em.createQuery("select c from CarPark c where c.name = '" + carParkName + "'", CarPark.class);
            return query.getSingleResult();
        });
    }

    @Override
    public List<Object> getCarParks() {
        return findAll(em -> {
            TypedQuery<CarPark> query = em.createQuery("select c from CarPark c", CarPark.class);
            return query.getResultList();
        });
    }

    @Override
    public Object updateCarPark(Object carPark) {
        if (carPark == null) return null;
        CarPark park = (CarPark) carPark;
        if (park.getId() == null) return null;
        return runTransaction(em -> em.merge(park));
    }

    @Override
    public Object deleteCarPark(Long carParkId) {
        if (carParkId == null) return null;
        return runTransaction(em -> {
            CarPark park = em.find(CarPark.class, carParkId);
            if (park == null) return null;
            em.remove(park);
            return park;
        });
    }

    @Override
    public Object createCarParkFloor(Long carParkId, String floorIdentifier) {
        return null;
    }

    @Override
    public Object getCarParkFloor(Long carParkId, String floorIdentifier) {
        return null;
    }

    @Override
    public Object getCarParkFloor(Long carParkFloorId) {
        return null;
    }

    @Override
    public List<Object> getCarParkFloors(Long carParkId) {
        return null;
    }

    @Override
    public Object updateCarParkFloor(Object carParkFloor) {
        return null;
    }

    @Override
    public Object deleteCarParkFloor(Long carParkId, String floorIdentifier) {
        return null;
    }

    @Override
    public Object deleteCarParkFloor(Long carParkFloorId) {
        return null;
    }

    @Override
    public Object createParkingSpot(Long carParkId, String floorIdentifier, String spotIdentifier) {
        return null;
    }

    @Override
    public Object getParkingSpot(Long parkingSpotId) {
        return null;
    }

    @Override
    public List<Object> getParkingSpots(Long carParkId, String floorIdentifier) {
        return null;
    }

    @Override
    public Map<String, List<Object>> getParkingSpots(Long carParkId) {
        return null;
    }

    @Override
    public Map<String, List<Object>> getAvailableParkingSpots(String carParkName) {
        return null;
    }

    @Override
    public Map<String, List<Object>> getOccupiedParkingSpots(String carParkName) {
        return null;
    }

    @Override
    public Object updateParkingSpot(Object parkingSpot) {
        return null;
    }

    @Override
    public Object deleteParkingSpot(Long parkingSpotId) {
        return null;
    }

    @Override
    public Object createCar(Long userId, String brand, String model, String colour, String vehicleRegistrationPlate) {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, userId);
        if (user == null)
            return null;
        Car car = new Car(brand, model, colour, vehicleRegistrationPlate);
        car.setUser(user);
        em.getTransaction().begin();
        em.persist(car);
        em.getTransaction().commit();
        return car;
    }

    @Override
    public Object getCar(Long carId) {
        if (carId == null) return null;
        EntityManager em = emf.createEntityManager();
        Car car = em.find(Car.class, carId);
        em.close();
        return car;
    }

    @Override
    public Object getCar(String vehicleRegistrationPlate) {
        if (vehicleRegistrationPlate == null) return null;
        EntityManager em = emf.createEntityManager();
        Car car = null;
        try {
            TypedQuery<Car> query = em.createQuery("select c from Car c where c.vrp = '" + vehicleRegistrationPlate + "'", Car.class);
            car = query.getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        em.close();
        return car;
    }

    @Override
    public List<Object> getCars(Long userId) {
        if (userId == null) return new ArrayList<>();
        EntityManager em = emf.createEntityManager();
        List<Car> cars = new ArrayList<>();
        try {
            User user = em.find(User.class, userId);
            if (user == null) throw new IllegalStateException("Cannot find user with id: " + userId);
            TypedQuery<Car> query = em.createQuery("select c from Car c where c.user.id = " + userId, Car.class);
            cars = query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        em.close();
        return Arrays.asList(cars.toArray());
    }

    @Override
    public Object updateCar(Object car) {
        if (car == null) return null;
        Car c = (Car) car;
        if (c.getId() == null) return null;
        // TODO ošetriť existenciu
        return runTransaction(em -> em.merge(c));
    }

    @Override
    public Object deleteCar(Long carId) {
        if (carId == null) return null;
        return runTransaction(em -> {
            Car car = em.find(Car.class, carId);
            if (car == null) return null;
            em.remove(car);
            return car;
        });
    }

    @Override
    public Object createUser(String firstname, String lastname, String email) {
        return runTransaction(em -> {
            User user = new User(firstname, lastname, email);
            em.persist(user);
            return user;
        });
    }

    @Override
    public Object getUser(Long userId) {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, userId);
        em.close();
        return user;
    }

    @Override
    public Object getUser(String email) {
        EntityManager em = emf.createEntityManager();
        User user = null;
        try {
            TypedQuery<User> query = em.createQuery("select u from User u where u.email = '" + email + "'", User.class);
            user = query.getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        em.close();
        return user;
    }

    @Override
    public List<Object> getUsers() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<User> query = em.createQuery("select u from User u", User.class);
        List<User> users = query.getResultList();
        em.close();
        return Arrays.asList(users.toArray());
    }

    @Override
    public Object updateUser(Object user) {
        if (user == null) return null;
        User u = (User) user;
        if (u.getId() == null) return null;
        // TODO ošetriť či user vôbec existuje
        return runTransaction(em -> em.merge(u));
    }

    @Override
    public Object deleteUser(Long userId) {
        if (userId == null) return null;
        return runTransaction(em -> {
            User u = em.find(User.class, userId);
            if (u == null) return null;
            em.remove(u);
            return u;
        });
    }

    @Override
    public Object createReservation(Long parkingSpotId, Long cardId) {
        return null;
    }

    @Override
    public Object endReservation(Long reservationId) {
        return null;
    }

    @Override
    public List<Object> getReservations(Long parkingSpotId, Date date) {
        return null;
    }

    @Override
    public List<Object> getMyReservations(Long userId) {
        return null;
    }

    @Override
    public Object updateReservation(Object reservation) {
        return null;
    }

    @Override
    public Object createDiscountCoupon(String name, Integer discount) {
        return null;
    }

    @Override
    public void giveCouponToUser(Long couponId, Long userId) {

    }

    @Override
    public Object getCoupon(Long couponId) {
        return null;
    }

    @Override
    public List<Object> getCoupons(Long userId) {
        return null;
    }

    @Override
    public Object endReservation(Long reservationId, Long couponId) {
        return null;
    }

    @Override
    public Object deleteCoupon(Long couponId) {
        return null;
    }

    @Override
    public Object createCarType(String name) {
        return null;
    }

    @Override
    public List<Object> getCarTypes() {
        return null;
    }

    @Override
    public Object getCarType(Long carTypeId) {
        return null;
    }

    @Override
    public Object getCarType(String name) {
        return null;
    }

    @Override
    public Object deleteCarType(Long carTypeId) {
        return null;
    }

    @Override
    public Object createCar(Long userId, String brand, String model, String colour, String vehicleRegistrationPlate, Long carTypeId) {
        return null;
    }

    @Override
    public Object createParkingSpot(Long carParkId, String floorIdentifier, String spotIdentifier, Long carTypeId) {
        return null;
    }

    @Override
    public Object createHoliday(String name, Date date) {
        return null;
    }

    @Override
    public Object getHoliday(Date date) {
        return null;
    }

    @Override
    public List<Object> getHolidays() {
        return null;
    }

    @Override
    public Object deleteHoliday(Long holidayId) {
        return null;
    }

    private Object runTransaction(Function<EntityManager, Object> operation) {
        EntityManager em = emf.createEntityManager();
        Object obj = null;
        try {
            em.getTransaction().begin();
            obj = operation.apply(em);
            em.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
        em.close();
        return obj;
    }

    private Object findOne(Function<EntityManager, Object> queryFunction) {
        EntityManager em = emf.createEntityManager();
        Object obj = null;
        try {
            obj = queryFunction.apply(em);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        em.close();
        return obj;
    }

    private List<Object> findAll(Function<EntityManager, List<?>> queryFunction) {
        EntityManager em = emf.createEntityManager();
        List<?> list = new ArrayList<>();
        try {
            list = queryFunction.apply(em);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        em.close();
        return Arrays.asList(list.toArray());
    }
}
