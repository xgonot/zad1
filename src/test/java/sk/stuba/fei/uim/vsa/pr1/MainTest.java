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
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
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
    public void createAndGetCarParkTest()
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
    public void createAndGetCarParkByName()
    {
        Object carPark = MainTest.carService.createCarPark("CAR-PARK-NAME", "testtest", 12);
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
             Object carPark2 = MainTest.carService.getCarPark("CAR-PARK-NAME");
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
    @Order(1)
    public void getCarParksTest()
    {
        Object carPark = MainTest.carService.createCarPark("CAR-PARK-1-1", "test11", 10);
        assertNotNull(carPark);
        Class c = carPark.getClass();
        Method getId = null;
        List<Method> stringMethods = new ArrayList<>();
        for (Method m: c.getMethods()) {
            if (m.getReturnType() == Long.class && m.getParameterCount() == 0) {
               getId = m;
            }
            if (m.getReturnType() == String.class && ! m.getName().equals("toString") && m.getParameterCount() == 0) {
                stringMethods.add(m);
            }
        }
         assertNotNull(getId);
         assertFalse(stringMethods.isEmpty());
         
         Object carPark2 = MainTest.carService.createCarPark("CAR-PARK-1-2", "test12", 20);
         assertNotNull(carPark2);
         
         List<Object> parks = MainTest.carService.getCarParks();
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
                      for (Method m: stringMethods) {
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
                       for (Method m: stringMethods) {
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
    public void updateCarParkTest()
    {
        Object carPark = MainTest.carService.createCarPark("UPDATE-CAR-PARK-1", "test11", 10);
        assertNotNull(carPark);
        Class c = carPark.getClass();
        Method getId = null;
        List<Method> stringMethods = new ArrayList<>();
        List<Method> stringSetterMethods = new ArrayList<>();
        Method getPriceMethod = null;
        Method setPriceMethod = null;
        Object modifiedPrice = null;
        for (Method m: c.getMethods()) {
            if (m.getReturnType() == Long.class && m.getParameterCount() == 0) {
               getId = m;
            }
            else if (m.getReturnType() == String.class && ! m.getName().equals("toString") && m.getParameterCount() == 0) {
                stringMethods.add(m);
            }
            else if (m.getParameterCount() == 0 && !m.getName().equals("hashCode") && (m.getReturnType() == Integer.class || m.getReturnType() == int.class || m.getReturnType() == Float.class || m.getReturnType() == float.class || m.getReturnType() == Double.class || m.getReturnType() == double.class)) {
                getPriceMethod = m;
            }
            else if(m.getParameterCount() == 1) {
                Class[] paramClasses = m.getParameterTypes();
                if (paramClasses[0] == String.class ) {
                    stringSetterMethods.add(m);
                } else if (paramClasses[0] == Integer.class || paramClasses[0] == int.class ) {
                    setPriceMethod = m;
                    modifiedPrice = 12;
                } else if (paramClasses[0] == Float.class || paramClasses[0] == float.class) {
                    modifiedPrice = 12.0f;
                } else if (paramClasses[0] == Double.class || paramClasses[0] == double.class) {
                    modifiedPrice = 12.0;
                }
            }
        }
         assertNotNull(getId);
         assertFalse(stringMethods.isEmpty());
         assertNotNull(getPriceMethod);
         assertNotNull(modifiedPrice);
         assertNotNull(setPriceMethod);
        try {
            Object id = getId.invoke(carPark);
            Long carParkId = (Long) id;
            assertNotNull(carParkId);
            
            Object park = MainTest.carService.getCarPark(carParkId);
            assertNotNull(park);
            Long parkId = (Long) getId.invoke(park);
            assertNotNull(parkId);
            assertEquals(id, parkId);
            for (Method m: stringMethods) {
                String carParkS = (String) m.invoke(carPark);
                String parkS = (String) m.invoke(park);
                assertNotNull(carParkS);
                assertNotNull(parkS);
                assertEquals(carParkS, parkS);
            }
            
            Object carParkPrice = getPriceMethod.invoke(carPark);
            Object parkPrice = getPriceMethod.invoke(park);
            assertNotNull(carParkPrice);
            assertNotNull(parkPrice);
            assertEquals(carParkPrice, parkPrice);
            setPriceMethod.invoke(carPark, modifiedPrice);
            for (Method m: stringSetterMethods) {
                m.invoke(carPark, "MODIFIED-CAR-PARK");
            }
            
            Object modifiedPark = MainTest.carService.updateCarPark(carPark);
            
            park = MainTest.carService.getCarPark(carParkId);
            
            assertNotNull(park);
            
            Object parkModifiedPrice = getPriceMethod.invoke(park);
            assertNotNull(parkModifiedPrice);
            assertEquals(modifiedPrice, parkModifiedPrice);
            
            for (Method m: stringMethods) {
                String x = (String) m.invoke(park);
                assertNotNull(x);
                assertEquals(x, "MODIFIED-CAR-PARK");
            }
            
        } catch (Exception e) {
            assertTrue(false);
        }
       
         
         
    }
    /*
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
        
        
    }*/
    
    @Test
    public void deleteCarParkTest()
    {
        Object carPark = MainTest.carService.createCarPark("DELETE-CAR-PARK-1", "test11", 10);
        assertNotNull(carPark);
        Class c = carPark.getClass();
        Method getId = null;
        
        for (Method m: c.getMethods()) {
            if (m.getParameterCount() == 0 && m.getReturnType() == Long.class) {
                getId = m;
                break;
            }
        }
        assertNotNull(getId);
        try {
            Long id = (Long) getId.invoke(carPark);
            Object park = MainTest.carService.getCarPark(id);
            assertNotNull(park);
            
            MainTest.carService.deleteCarPark(id);
            try {
                park = MainTest.carService.getCarPark(id);
                assertNull(park);
            } catch (Exception e) {
                assertTrue(true);
            }
        } catch (Exception e) {
            assertTrue(false);
        }
        
    }
    
    @Test
    public void createAndGetCarParkFloorTestEmbedded()
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
    
    //@Test
    public void updateCarParkFloorTest()
    {
        assertTrue(false);
    }
    
    @Test
    public void deleteCarParkFloorTest()
    {
        Object carPark = MainTest.carService.createCarPark("FLOOR-DELETE", "testtest", 12);
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


            Object carParkFloor1 = MainTest.carService.createCarParkFloor(id, "Floor1");
            assertNotNull(carParkFloor1);

            Object floor1 = MainTest.carService.getCarParkFloor(id, "Floor1");
            Long carParkFloorId = null;
            if (floor1 == null) {
                for (Method m: floor1.getClass().getMethods()) {
                    if (m.getParameterCount() == 0 && m.getReturnType() == Long.class) {
                        carParkFloorId = (Long) m.invoke(floor1);
                        assertNotNull(carParkFloorId);
                        floor1 = MainTest.carService.getCarParkFloor(carParkFloorId);
                        assertNotNull(floor1);
                    }
                }
            }
            List<Object> floors = MainTest.carService.getCarParkFloors(id);
            assertNotNull(floors);
            assertEquals(floors.size(), 1);
            
            Object a = MainTest.carService.deleteCarParkFloor(id, "Floor1");
            if (a == null) {
                assertNotNull(carParkFloorId);
                a = MainTest.carService.deleteCarParkFloor(carParkFloorId);
                assertNotNull(a);
            }
            
           
            if (carParkFloorId == null) {
                try {
                    floor1 = MainTest.carService.getCarParkFloor(carParkFloorId, "Floor1");
                    assertNull(floor1);
                } catch (Exception e) {
                    assertTrue(true);
                } 
            } else {
                try {
                    floor1 = MainTest.carService.getCarParkFloor(carParkFloorId);
                    assertNull(floor1);
                } catch (Exception e) {
                    assertTrue(true);
                }
            }
            try {
                floors = MainTest.carService.getCarParkFloors(id);
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
    
    @Test
    public void createAndGetParkingFloorWithoutTypeTest()
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
    
    public void getAllParkingSlotsWithoutType()
    {
        try {
            Object carPark = MainTest.carService.createCarPark("test8", "testtest", 12);
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
            
            Object parkingSpot2 = MainTest.carService.createParkingSpot(carParkSpotId, "Floor3-1", "1.02");
             assertNotNull(parkingSpot2);
            Long parkingSpot2Id = (Long) getParkingSpotId.invoke(parkingSpot2);
            assertNotNull(parkingSpot2Id);
            Object spot2 = MainTest.carService.getParkingSpot(parkingSpot2Id);
            assertNotNull(spot2);
            Long spot2Id = (Long) getParkingSpotId.invoke(spot2);
            assertNotNull(spot2Id);
            
            assertEquals(parkingSpot2Id, spot2Id);
            
            Object carParkFloor2 = MainTest.carService.createCarParkFloor(id, "Floor3-2");
            assertNotNull(carParkFloor2);
            Object parkingSpot3 = MainTest.carService.createParkingSpot(carParkSpotId, "Floor3-2", "2.01");
            assertNotNull(parkingSpot3);
            
            Long parkingSpot3Id = (Long) getParkingSpotId.invoke(parkingSpot3);
            assertNotNull(parkingSpot3Id);
            
            Map<String, List<Object>> parkFloors = MainTest.carService.getParkingSpots(id);
            assertNotNull(parkFloors);
            Set<Map.Entry<String, List<Object>>> parkFloorsEntrySet = parkFloors.entrySet();
            assertEquals(parkFloorsEntrySet.size(), 2);
            
            List<Object> firstFloorSpots = parkFloors.get("Floor3-1");
            assertNotNull(firstFloorSpots);
            assertEquals(firstFloorSpots.size(), 2);
            
            Long firstFloorFirstSpotId = (Long) getParkingSpotId.invoke(firstFloorSpots.get(0));
            Long firstFloorSecondSpotId = (Long) getParkingSpotId.invoke(firstFloorSpots.get(1));
            assertNotNull(firstFloorFirstSpotId);
            assertNotNull(firstFloorSecondSpotId);
            
            if (firstFloorFirstSpotId.equals(carParkSpotId)) {
                if (! firstFloorSecondSpotId.equals(spot2Id)) {
                    assertTrue(false);
                }
            } else if (firstFloorFirstSpotId.equals(spot2Id)) {
                if (! firstFloorSecondSpotId.equals(carParkSpotId)) {
                    assertTrue(false);
                }
            } else {
                assertTrue(false);
            }
            
            List<Object> secondFloorSpots = parkFloors.get("Floor3-2");
            assertNotNull(secondFloorSpots);
            assertEquals(secondFloorSpots.size(), 1);
            
            Object secondFloorSpot = secondFloorSpots.get(0);
            Long secondFloorSpotId = (Long) getParkingSpotId.invoke(secondFloorSpot);
            assertNotNull(secondFloorSpotId);
            
            assertEquals(secondFloorSpotId, parkingSpot3Id);
            
            
        } catch (Exception e) {
            assertTrue(false);
        }
    }
    
    @Test
    public void creareUserTest()
    {
        try {
            Object user = MainTest.carService.createUser("TestUser", "UserTest", "user-test@test.com");
            assertNotNull(user);
            Class c = user.getClass();
            List<Method> stringMethods = new ArrayList<>();
            Method getUserId = null;
            for (Method m : c.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        getUserId = m;
                    } else if (m.getReturnType() == String.class && !m.getName().equals("toString")) {
                     stringMethods.add(m);
                    }
                }
            }
            assertNotNull(getUserId);
            assertFalse(stringMethods.isEmpty());
            
            Long userId = (Long) getUserId.invoke(user);
            assertNotNull(userId);
            
            Object usr = MainTest.carService.getUser(userId);
            assertNotNull(usr);
            Long usrId = (Long) getUserId.invoke(usr);
            assertNotNull(usrId);
            
            assertEquals(userId, usrId);
            
            for (Method m: stringMethods) {
                String s1 =(String) m.invoke(user);
                String s2 = (String) m.invoke(usr);
                assertNotNull(s1);
                assertNotNull(s2);
                assertEquals(s1,s2);
            }
            
            
        } catch (Exception e) {
            assertTrue(false);
        }
    }
    
    @Test
    public void getUserByEmailTest()
    {
        try {
            Object user = MainTest.carService.createUser("TestUser2", "UserTest2", "user-test2@test.com");
            assertNotNull(user);
            Class c = user.getClass();
            List<Method> stringMethods = new ArrayList<>();
            Method getUserId = null;
            for (Method m : c.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        getUserId = m;
                    } else if (m.getReturnType() == String.class && !m.getName().equals("toString")) {
                     stringMethods.add(m);
                    }
                }
            }
            assertNotNull(getUserId);
            assertFalse(stringMethods.isEmpty());
            
            Long userId = (Long) getUserId.invoke(user);
            assertNotNull(userId);
            
            Object usr = MainTest.carService.getUser("user-test2@test.com");
            assertNotNull(usr);
            Long usrId = (Long) getUserId.invoke(usr);
            assertNotNull(usrId);
            
            assertEquals(userId, usrId);
            
            for (Method m: stringMethods) {
                String s1 =(String) m.invoke(user);
                String s2 = (String) m.invoke(usr);
                assertNotNull(s1);
                assertNotNull(s2);
                assertEquals(s1,s2);
            }
            
            Object user2 = MainTest.carService.createUser("TestUser3", "UserTest3", "user-test3@test.com");
            assertNotNull(user2);
            Long user2Id =(Long) getUserId.invoke(user2);
            assertNotNull(user2Id);
            
            Object usr2 = MainTest.carService.getUser("user-test3@test.com");
            assertNotNull(usr2);
            Long usr2Id = (Long) getUserId.invoke(usr2);
            assertNotNull(usr2Id);
            
            assertEquals(user2Id, usr2Id);
            
            for (Method m: stringMethods) {
                String s1 = (String) m.invoke(user2);
                String s2 = (String) m.invoke(usr2);
                assertNotNull(s1);
                assertNotNull(s2);
                assertEquals(s1, s2);
            }
            
            
        } catch (Exception e) {
            assertTrue(false);
        }
    }
    
    public void getAllUsersTest()
    {
        
    }
    
    @Test
    public void updateUserTest()
    {
        try {
            Object user = MainTest.carService.createUser("TestUser4", "UserTest4", "user-test4@test.com");
            assertNotNull(user);
            Class c = user.getClass();
            List<Method> stringMethods = new ArrayList<>();
            List<Method> stringSetterMethods = new ArrayList<>();
            Method getUserId = null;
            for (Method m : c.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        getUserId = m;
                    } else if (m.getReturnType() == String.class && !m.getName().equals("toString")) {
                     stringMethods.add(m);
                    }
                } else if (m.getParameterCount() == 1) {
                    Class[] params = m.getParameterTypes();
                    if (params[0] == String.class) {
                        stringSetterMethods.add(m);
                    }
                }
            }
            assertNotNull(getUserId);
            assertFalse(stringMethods.isEmpty());
            
            Long userId = (Long) getUserId.invoke(user);
            assertNotNull(userId);
            
            Object usr = MainTest.carService.getUser("user-test4@test.com");
            assertNotNull(usr);
            Long usrId = (Long) getUserId.invoke(usr);
            assertNotNull(usrId);
            
            assertEquals(userId, usrId);
            
            for (Method m: stringMethods) {
                String s1 =(String) m.invoke(user);
                String s2 = (String) m.invoke(usr);
                assertNotNull(s1);
                assertNotNull(s2);
                assertEquals(s1,s2);
            }
            
            for (Method m: stringSetterMethods) {
                m.invoke(user, "user-test5@test.com");
            }
            
            usr = MainTest.carService.updateUser(user);
            assertNotNull(usr);
            for (Method m: stringMethods) {
                String s1 =(String) m.invoke(usr);
                assertNotNull(s1);
                assertEquals(s1, "user-test5@test.com");
            }
            
            usr = MainTest.carService.getUser(userId);
            assertNotNull(usr);
            for (Method m: stringMethods) {
                String s1 =(String) m.invoke(usr);
                assertNotNull(s1);
                assertEquals(s1, "user-test5@test.com");
            }
            
            
            
        } catch (Exception e) {
            assertTrue(false);
            
        }
    }
    
    public void deleteUserTest()
    {
        try {
            
        
            Object user = MainTest.carService.createUser("TestUser6", "UserTest6", "user-test6@test.com");
            assertNotNull(user);
            Class c = user.getClass();
            //List<Method> stringMethods = new ArrayList<>();
            Method getUserId = null;
            for (Method m : c.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        getUserId = m;
                    } /*else if (m.getReturnType() == String.class && !m.getName().equals("toString")) {
                     stringMethods.add(m);
                    }*/
                }
            }
            assertNotNull(getUserId);
            //assertFalse(stringMethods.isEmpty());
           
            Long userId = (Long) getUserId.invoke(user);
            assertNotNull(userId);
            Object usr = MainTest.carService.getUser(userId);
            
            assertNotNull(usr);
            
            MainTest.carService.deleteUser(userId);
            
            usr = MainTest.carService.getUser(userId);
            assertNull(usr);
            
            
            
        } catch (Exception e) {
            assertTrue(false);
        }
    }
    
    @Test
    public void createCarWithoutTypeTest()
    {
         try {
            Object user = MainTest.carService.createUser("TestUser7", "UserTest7", "user-test7@test.com");
            assertNotNull(user);
            Class c = user.getClass();
            //List<Method> stringMethods = new ArrayList<>();
            Method getUserId = null;
            for (Method m : c.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        getUserId = m;
                    } /*else if (m.getReturnType() == String.class && !m.getName().equals("toString")) {
                     stringMethods.add(m);
                    }*/
                }
            }
            assertNotNull(getUserId);
            //assertFalse(stringMethods.isEmpty());
           
            Long userId = (Long) getUserId.invoke(user);
            assertNotNull(userId);
            
            Object car = MainTest.carService.createCar(userId, "Skoda", "Rapid", "RED", "BA-111AA");
            assertNotNull(car);
            
            Class carClass = car.getClass();
            Method getCarId = null;
            List<Method> stringMethods = new ArrayList<>();
            for (Method m: carClass.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        getCarId = m;
                    } else if (m.getReturnType() == String.class && ! m.getName().equals("toString")) {
                        stringMethods.add(m);
                    }
                }
            }
            assertNotNull(getCarId);
            assertFalse(stringMethods.isEmpty());
            
            Long carId = (Long) getCarId.invoke(car);
            assertNotNull(carId);
            
            Object ca = MainTest.carService.getCar(carId);
            assertNotNull(ca);
            
            Long caId = (Long) getCarId.invoke(ca);
            assertNotNull(caId);
            
            assertEquals(carId, caId);
            for (Method m: stringMethods) {
                String s1 = (String) m.invoke(car);
                String s2 = (String) m.invoke(ca);
                assertNotNull(s1);
                assertNotNull(s2);
                assertEquals(s1,s2);
            }
            
         } catch (Exception e) {
            assertTrue(false);
        }     
    }
    
    @Test
    public void createAndGetCarWithoutType()
    {
        try {
            Object user = MainTest.carService.createUser("TestUser8", "UserTest8", "user-test8@test.com");
            assertNotNull(user);
            Class c = user.getClass();
            //List<Method> stringMethods = new ArrayList<>();
            Method getUserId = null;
            for (Method m : c.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        getUserId = m;
                    } /*else if (m.getReturnType() == String.class && !m.getName().equals("toString")) {
                     stringMethods.add(m);
                    }*/
                }
            }
            assertNotNull(getUserId);
            //assertFalse(stringMethods.isEmpty());
           
            Long userId = (Long) getUserId.invoke(user);
            assertNotNull(userId);
            
            Object car = MainTest.carService.createCar(userId, "Skoda", "Rapid", "RED", "BA-112AA");
            assertNotNull(car);
            
            Class carClass = car.getClass();
            Method getCarId = null;
            List<Method> stringMethods = new ArrayList<>();
            for (Method m: carClass.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        getCarId = m;
                    } else if (m.getReturnType() == String.class && ! m.getName().equals("toString")) {
                        stringMethods.add(m);
                    }
                }
            }
            assertNotNull(getCarId);
            assertFalse(stringMethods.isEmpty());
            
            Long carId = (Long) getCarId.invoke(car);
            assertNotNull(carId);
            
            Object ca = MainTest.carService.getCar("BA-112AA");
            assertNotNull(ca);
            
            Long caId = (Long) getCarId.invoke(ca);
            assertNotNull(caId);
            
            assertEquals(carId, caId);
            for (Method m: stringMethods) {
                String s1 = (String) m.invoke(car);
                String s2 = (String) m.invoke(ca);
                assertNotNull(s1);
                assertNotNull(s2);
                assertEquals(s1,s2);
            }
            
         } catch (Exception e) {
            assertTrue(false);
        }     
    }
    
    public void deleteCarTest()
    {
        try {
            Object user = MainTest.carService.createUser("TestUser9", "UserTest9", "user-test9@test.com");
            assertNotNull(user);
            Class c = user.getClass();
            //List<Method> stringMethods = new ArrayList<>();
            Method getUserId = null;
            for (Method m : c.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        getUserId = m;
                    } /*else if (m.getReturnType() == String.class && !m.getName().equals("toString")) {
                     stringMethods.add(m);
                    }*/
                }
            }
            assertNotNull(getUserId);
            //assertFalse(stringMethods.isEmpty());
           
            Long userId = (Long) getUserId.invoke(user);
            assertNotNull(userId);
            
            Object car = MainTest.carService.createCar(userId, "Skoda", "Rapid", "RED", "BA-113AA");
            assertNotNull(car);
            
            Class carClass = car.getClass();
            Method getCarId = null;
            List<Method> stringMethods = new ArrayList<>();
            for (Method m: carClass.getMethods()) {
                if (m.getParameterCount() == 0) {
                    if (m.getReturnType() == Long.class) {
                        getCarId = m;
                    } else if (m.getReturnType() == String.class && ! m.getName().equals("toString")) {
                        stringMethods.add(m);
                    }
                }
            }
            assertNotNull(getCarId);
            assertFalse(stringMethods.isEmpty());
            
            Long carId = (Long) getCarId.invoke(car);
            assertNotNull(carId);
            
            Object ca = MainTest.carService.getCar(carId);
            assertNotNull(ca);
            
            Long caId = (Long) getCarId.invoke(ca);
            assertNotNull(caId);
            
            assertEquals(carId, caId);
            for (Method m: stringMethods) {
                String s1 = (String) m.invoke(car);
                String s2 = (String) m.invoke(ca);
                assertNotNull(s1);
                assertNotNull(s2);
                assertEquals(s1,s2);
            }
            
            MainTest.carService.deleteCar(carId);
            try {
                ca = MainTest.carService.getCar(carId);
                assertNull(ca);
            } catch (Exception e) {
                assertTrue(true);
            }
            
         } catch (Exception e) {
            assertTrue(false);
        }     
    }
}

