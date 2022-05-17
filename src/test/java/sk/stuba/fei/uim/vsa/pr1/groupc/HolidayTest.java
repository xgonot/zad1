/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr1.groupc;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr1.AbstractCarParkService;
import static sk.stuba.fei.uim.vsa.pr1.TestData.DB;
import static sk.stuba.fei.uim.vsa.pr1.TestData.PASSWORD;
import static sk.stuba.fei.uim.vsa.pr1.TestData.USERNAME;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.clearDB;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.getMySQL;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.getServiceClass;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr1.TestUtils.*;

/**
 *
 * @author sheax
 */
public class HolidayTest {
    
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
    public void HOL_01_createAndGetHoliday() throws ParseException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
    {
        String dateString = "2022-01-01";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date d = formatter.parse(dateString);
        Object holiday = carParkService.createHoliday("Novy rok", d);
        
        assertNotNull(holiday);
        
        Object h = carParkService.getHoliday(d);
        assertNotNull(holiday);
        String[] dateFields = findFieldByType(h, Date.class);
        if (! (dateFields.length > 0)){
            dateFields = findFieldByType(h, LocalDate.class);
            if (! (dateFields.length > 0)) {
                dateFields = findFieldByType(h, LocalDateTime.class);
                if (! (dateFields.length > 0)) {
                    dateFields = findFieldByType(h, Calendar.class); 
                    if (! (dateFields.length > 0)) {
                        dateFields = findFieldByType(h, GregorianCalendar.class);
                        if (! (dateFields.length > 0)) {
                             throw new RuntimeException("Field not found!");
                        }
                    }
                }
            }
        }
        for (String f: dateFields) {
            assertEquals(getFieldValue(h, f), getFieldValue(holiday, f));
        }
    }
    
    @Test
    public void HOL_02_getAllHolidaysTest()  throws ParseException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
    {
        String dateString = "2022-01-01";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date d = formatter.parse(dateString);
        Object holiday = carParkService.createHoliday("Novy rok", d);
        assertNotNull(holiday);
        
        String dateString2 = "2022-05-08";
        Date d2 = formatter.parse(dateString2);
        Object holiday2 = carParkService.createHoliday("Den matiek", d2);
        assertNotNull(holiday2);
        
        List<Object> holidays = carParkService.getHolidays();
        Object h = holidays.get(0);
        Object h2 = holidays.get(1);
        
        String[] dateFields = findFieldByType(h, Date.class);
        if (! (dateFields.length > 0)){
            dateFields = findFieldByType(h, LocalDate.class);
            if (! (dateFields.length > 0)) {
                dateFields = findFieldByType(h, LocalDateTime.class);
                if (! (dateFields.length > 0)) {
                    dateFields = findFieldByType(h, Calendar.class); 
                    if (! (dateFields.length > 0)) {
                        dateFields = findFieldByType(h, GregorianCalendar.class);
                        if (! (dateFields.length > 0)) {
                             throw new RuntimeException("Field not found!");
                        }
                    }
                }
            }
        }
        boolean wasMatch = false;
        for (String f: dateFields) {
            wasMatch = getFieldValue(h, f).equals(getFieldValue(holiday, f));
            if (! wasMatch) {
                break;
            }
        }
        if (wasMatch) {
            for (String f: dateFields) {
                assertEquals(getFieldValue(h2, f), getFieldValue(holiday2, f));
            }
        } else {
            for (String f: dateFields) {
                 assertEquals(getFieldValue(h2, f), getFieldValue(holiday, f));
                 assertEquals(getFieldValue(h, f), getFieldValue(holiday2, f));
            }
        }
        
        String pa = "2020-02-29";
        Date paDate = formatter.parse(pa);
        
        LocalDate l = paDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withYear(1);
        System.out.println(l.getDayOfYear());
    }
    
}
