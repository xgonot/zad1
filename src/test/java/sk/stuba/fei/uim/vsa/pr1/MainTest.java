package sk.stuba.fei.uim.vsa.pr1;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sheax
 */

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.BeforeAll;
import static org.reflections.scanners.Scanners.SubTypes;
import org.reflections.Reflections;

public class MainTest {
    
    private static AbstractCarParkService carService;
    
    @BeforeAll
    public static void before()
    {
        try {
            Reflections reflections = new Reflections("sk.stuba.fei.uim.vsa");
            Set<Class<?>> cps = reflections.get(SubTypes.of(AbstractCarParkService.class).asClass());
            cps.forEach(clazz -> {
                try {
                    MainTest.carService =(AbstractCarParkService) clazz.getDeclaredConstructor().newInstance();
                }  catch (Exception e) {
                } 
                
            });
        } catch (Exception e) {
            
        }
       
        assertNotNull( MainTest.carService);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vsa-project");
        EntityManager em = emf.createEntityManager();
        //.flush();
        em.close();
    }
    
    @Test
    public void getCreateAndGetCarParkTest()
    {
        //assertTrue(false);
        Object carPark = MainTest.carService.createCarPark("test1", "testtest", 12);
        assertNotNull(carPark);
        Class c = carPark.getClass();
        try {
            Method[] methods = c.getMethods();
            Method getId = null;
            List<Method> stringMethods = new ArrayList<>();
            for (Method m: methods) {
                if (m.getReturnType() == Long.class && m.getParameterCount() == 0) {
                   getId = m;
                }
                if (m.getReturnType() == String.class && ! m.getName().equals("toString") && m.getParameterCount() == 0) {
                    stringMethods.add(m);
                }
            }
             assertNotNull(getId);
             Object id = getId.invoke(carPark);
             Long carParkId = (Long) id;
             Object carPark2 = MainTest.carService.getCarPark(carParkId);
            assertNotNull(carPark2);
            Object id2 =getId.invoke(carPark2);
             assertEquals(id, id2);
             for (Method m: stringMethods) {
                 Object a1 = m.invoke(carPark);
                 Object a2 = m.invoke(carPark2);
                 assertEquals(a1, a2);
             }
        } catch (Exception ex ) {
            assertTrue(false);
        }
       
    }
    
    @Test
    public void uniqueCarParkNameTest()
    {
        Object carPark = MainTest.carService.createCarPark("test2", "testtest", 12);
        try {
            Object carPark2 = MainTest.carService.createCarPark("test2", "testtest", 12);
            assertNull(carPark2);
        } catch (Exception e) {
            assertTrue(true);
        }
        
    }
    
    @Test
    public void createCarParkFloorTest()
    {
        try {
            Object carPark = MainTest.carService.createCarPark("test3", "testtest", 12);
            assertNotNull(carPark);
            Class c = carPark.getClass();
                        Method[] methods = c.getMethods();
            Method getId = null;
            for (Method m: methods) {
                if (m.getReturnType() == Long.class && m.getParameterCount() == 0) {
                   getId = m;
                   break;
                }
            }
             assertNotNull(getId);
             Long id = (Long) getId.invoke(carPark);
            
            
            Object carParkFloor1 = MainTest.carService.createCarParkFloor(id, "Floor1");
            assertNotNull(carParkFloor1);
        
            Object carParkFloor2 = MainTest.carService.createCarParkFloor(id, "Floor2");
            
            assertNotNull(carParkFloor2);
        } catch (Exception e) {
            assertTrue(false);
        }
        
        
    }
    
    @Test
    public void getCarParkFloorTestEmbedded()
    {
        try {
            Object carPark = MainTest.carService.createCarPark("test4", "testtest", 12);
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
            
            
            Object carParkFloor1 = MainTest.carService.createCarParkFloor(id, "Floor1");
            assertNotNull(carParkFloor1);
            
            Object floor1 = MainTest.carService.getCarParkFloor(id, "Floor1");
            assertNotNull(floor1);
        
            Object carParkFloor2 = MainTest.carService.createCarParkFloor(id, "Floor2");
            assertNotNull(carParkFloor2);
            
            Object floor2 =  MainTest.carService.getCarParkFloor(id, "Floor2");
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
    
    public void getCarParkFloorsEmbedded2()
    {
        try {
            Object carPark = MainTest.carService.createCarPark("test5", "testtest", 12);
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
            
            
            Object carParkFloor1 = MainTest.carService.createCarParkFloor(id, "Floor1");
            assertNotNull(carParkFloor1);
            
            Object floor1 = MainTest.carService.getCarParkFloor(id, "Floor1");
            assertNotNull(floor1);
        
            Object carParkFloor2 = MainTest.carService.createCarParkFloor(id, "Floor2");
            assertNotNull(carParkFloor2);
            
            Object floor2 =  MainTest.carService.getCarParkFloor(id, "Floor2");
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
            
            List<Object> floors = MainTest.carService.getCarParkFloors(id);
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
                
            if (emfFlId.equals(f1)) {
                if (emfFlId2.equals(f2)) {
                    assertTrue(true);
                } else {
                    assertTrue(false);
                }
            } else if (emfFlId.equals(f2)) {
                 if (emfFlId2.equals(f1)) {
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
        /*List<Object> floors = MainTest.carService.getCarParkFloors(carParkId);
            assertEquals(floors.size(), 2);
            
            Object floor1 = floors.get(1);
            
            for (Object fl : floors) {
                
            }*/
    }
    
    //@Test
    public void getCarParkFloorTestId()
    {
        try {
            Object carPark = MainTest.carService.createCarPark("test6", "testtest", 12);
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
            
            Object carParkFloor1 = MainTest.carService.createCarParkFloor(id, "Floor1-1");
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
            
            Object floor1 = MainTest.carService.getCarParkFloor(id);
            assertNotNull(floor1);
            
            Long floor1Id = (Long) carParkFloorGetId.invoke(floor1);
            assertNotNull(floor1Id);
            assertEquals(carParkFloor1Id, floor1Id);
            String carParkFloor1Identifier = (String) carParkFloorGetIdentifier.invoke(carParkFloor1);
            String floor1Identifier = (String) carParkFloorGetIdentifier.invoke(floor1);
            assertNotNull(carParkFloor1Identifier);
            assertNotNull(floor1Identifier);
            assertEquals(carParkFloor1Identifier, floor1Identifier);
        
            Object carParkFloor2 = MainTest.carService.createCarParkFloor(id, "Floor1-2");
            assertNotNull(carParkFloor2);
            Long carParkFloor2Id = (Long) carParkFloorGetId.invoke(carParkFloor2);
            assertNotNull(carParkFloor2Id);
            Object floor2 =  MainTest.carService.getCarParkFloor(carParkFloor2Id);
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
    
    @Test
    public void getParkingFloorWithoutTypeTest()
    {
        try {
            Object carPark = MainTest.carService.createCarPark("test7", "testtest", 12);
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
            
            Object carParkFloor = MainTest.carService.createCarParkFloor(id, "Floor3-1");
            assertNotNull(carParkFloor);
            
            Object spot1 = MainTest.carService.createParkingSpot(id, "Floor3-1", "1.01");
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
            
            Object carParkSpot = MainTest.carService.getParkingSpot(spotId);
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

