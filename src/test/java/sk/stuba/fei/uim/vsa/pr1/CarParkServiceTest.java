package sk.stuba.fei.uim.vsa.pr1;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.reflections.scanners.Scanners.SubTypes;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.getFieldValue;

class CarParkServiceTest {

    private static AbstractCarParkService carParkService;

    @BeforeAll
    static void setup() {
        Reflections reflections = new Reflections("sk.stuba.fei.uim.vsa");
        Set<Class<?>> cps = reflections.get(SubTypes.of(AbstractCarParkService.class).asClass());
        assertEquals(1, cps.size());
        cps.forEach(clazz -> {
            try {
                carParkService = (AbstractCarParkService) clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            assertNotNull(carParkService);
            System.out.println("car park class: " + carParkService.getClass().getName());
        });
    }

    @BeforeEach
    void beforeEach(){
        // Clean DB
    }

    @Test
    void shouldCreateUser() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object user = carParkService.createUser(TestData.User.firstName, TestData.User.lastNAme, TestData.User.email);
        assertNotNull(user);
        testId(user);
    }

    @Test
    void shouldCreateCar() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object user = carParkService.getUser(TestData.User.email);
        if (user == null)
            user = carParkService.createUser(TestData.User.firstName, TestData.User.lastNAme, TestData.User.email);

        Object car = carParkService.createCar(getFieldValue(user, "id", Long.class),
                TestData.Car.brand, TestData.Car.model, TestData.Car.colour, TestData.Car.ecv);
        assertNotNull(car);
        testId(car);
        Object carUser = getFieldValue(car, "user"); //TODO check name of user attribute in car
        assertNotNull(carUser);
        assertEquals(getFieldValue(user, "id", Long.class), getFieldValue(carUser, "id", Long.class));
    }

    void testId(Object obj) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Long id = getFieldValue(obj, "id", Long.class);
        assertEquals(Long.class, id.getClass());
        assertTrue(id > 0);
    }

}
