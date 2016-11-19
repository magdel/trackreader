package ru.ogpscenter.tracker.reader.mt90;

import org.testng.annotations.Test;
import ru.ogpscenter.tracker.reader.domain.LocationRecord;
import ru.ogpscenter.tracker.reader.domain.TrackerType;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

/**
 * Created by rfk on 18.11.2016.
 */
public class StringLocationRecordMapperTest {

    @Test
    public void shouldIgnoreTrash() throws Exception {
        StringLocationRecordMapper mapper = new StringLocationRecordMapper(() -> 1L);
        Optional<LocationRecord> recordOptional = mapper.apply("STX,Id-112233,$GPRMC,,A,,,,,0.00,,,,,A*5B,L,,imei:013226008424265,,,Battery=100%,,0,250,02,1E82,173A;E0\n");
        assertFalse(recordOptional.isPresent());
        recordOptional = mapper.apply("STX,Id-112233,.00,,,,,A*5B,L,,imei:013226008424265,,,Battery=100%,,0,250,02,1E82,173A;E0\n");
        assertFalse(recordOptional.isPresent());
        recordOptional = mapper.apply("\n");
        assertFalse(recordOptional.isPresent());
    }

    @Test
    public void shouldAcceptFullLocation() throws Exception {
        StringLocationRecordMapper mapper = new StringLocationRecordMapper(() -> 1L);
        Optional<LocationRecord> recordOptional = mapper.apply("STX,Id-112233,$GPRMC,112842.000,A,6030.0000,N,03021.3429,W,0.00,0.00,241214,,,A*6C,F,,imei:013226008424265,03,23.0,Battery=70%,,0,250,02,1E82,173D;36\n");
        assertTrue(recordOptional.isPresent());
        LocationRecord record = recordOptional.get();
        assertEquals(record.getEventId(), new Long(1));
        assertEquals(record.getDeviceId(), "Id-112233");
        assertEquals(record.getDttm().getDayOfMonth(), 24);
        assertEquals(record.getDttm().getYear(), 2014);
        assertEquals(record.getDttm().getMonthOfYear(), 12);
        assertEquals(record.getDttm().getMinuteOfHour(), 28);
        assertEquals(record.getLat(), 60.5, 0.00005);
        assertEquals(record.getLon(), -30.355, 0.005);
        assertEquals(record.getSpd(), 0, 0.005);
        assertEquals(record.getCrs(), 0);
        assertEquals(record.getImei(), "013226008424265");
        assertTrue(record.isValidLocation());
        assertFalse(record.isValidSpdCrs());
    }

    @Test
    public void shouldValidSpdCrs() throws Exception {
        StringLocationRecordMapper mapper = new StringLocationRecordMapper(() -> 1L);
        Optional<LocationRecord> recordOptional = mapper.apply("STX,Id-112233,$GPRMC,112842.000,A,6030.0000,N,03021.3429,W,0.00,0.00,241214,3.0,180.0,A*6C,F,,imei:013226008424265,03,23.0,Battery=70%,,0,250,02,1E82,173D;36\n");
        assertTrue(recordOptional.isPresent());
        LocationRecord record = recordOptional.get();
        assertEquals(record.getSpd(), 1.542, 0.005);
        assertEquals(record.getCrs(), 180);
        assertEquals(record.getImei(), "013226008424265");
        assertTrue(record.isValidLocation());
        assertTrue(record.isValidSpdCrs());
    }

    @Test
    public void shouldValidDttmOnly() throws Exception {
        StringLocationRecordMapper mapper = new StringLocationRecordMapper(() -> 1L);
        Optional<LocationRecord> recordOptional = mapper.apply("STX,Id-112233,$GPRMC,001521.000,A,,,,,0.00,0.00,010109,,,A*5B,L,,imei:013226008424265,,,Battery=100%,,0,250,02,1E82,173A;E0\n");
        assertTrue(recordOptional.isPresent());
        LocationRecord record = recordOptional.get();
        assertFalse(record.isValidLocation());
        assertFalse(record.isValidSpdCrs());
        assertEquals(record.getDeviceId(), "Id-112233");
        assertEquals(record.getImei(), "013226008424265");
        assertEquals(record.getDttm().getYear(), 2009);
        assertEquals(record.getDttm().getMonthOfYear(), 1);
        assertEquals(record.getDttm().getMinuteOfHour(), 15);
        assertEquals(record.getTrackerType(), TrackerType.mt90);
    }

    @Test
    public void shouldValidLocationTK102() throws Exception {
        StringLocationRecordMapper mapper = new StringLocationRecordMapper(() -> 1L);
        Optional<LocationRecord> recordOptional = mapper.apply("090723164830,+13616959853,GPRMC," +
                "214830.000,A,3017.2558,N,09749.4888,W,26.9,108.8,230709,10,200,A*61,F, Help me,imei: 359587013388627," +
                "05,264.5,F:3.79V,0,122,13990,310,01,0AB0,345A");
        assertTrue(recordOptional.isPresent());
        LocationRecord record = recordOptional.get();
        assertEquals(record.getEventId(), new Long(1));
        assertEquals(record.getDeviceId(), "090723164830");
        assertEquals(record.getDttm().getDayOfMonth(), 24);
        assertEquals(record.getDttm().getYear(), 2009);
        assertEquals(record.getDttm().getMonthOfYear(), 7);
        assertEquals(record.getDttm().getMinuteOfHour(), 48);
        assertEquals(record.getLat(), 30.2875966, 0.00005);
        assertEquals(record.getLon(), -97.82481, 0.005);
        assertEquals(record.getSpd(), 5.14, 0.005);
        assertEquals(record.getCrs(), 200);
        assertEquals(record.getImei(), "359587013388627");
        assertEquals(record.getTrackerType(), TrackerType.tk102);
        assertTrue(record.isValidLocation());
        assertTrue(record.isValidSpdCrs());
    }

    @Test
    public void shouldValidDttmTK102() throws Exception {
        StringLocationRecordMapper mapper = new StringLocationRecordMapper(() -> 1L);
        Optional<LocationRecord> recordOptional = mapper.apply("090723164830,+13616959853,GPRMC," +
                "214830.000,A,,N,,W,26.9,108.8,230709,,,A*61,F, Help me,imei: 359587013388627," +
                "05,264.5,F:3.79V,0,122,13990,310,01,0AB0,345A");
        assertTrue(recordOptional.isPresent());
        LocationRecord record = recordOptional.get();
        assertFalse(record.isValidLocation());
        assertFalse(record.isValidSpdCrs());
        assertEquals(record.getDeviceId(), "090723164830");
        assertEquals(record.getImei(), "359587013388627");
        assertEquals(record.getDttm().getDayOfMonth(), 24);
        assertEquals(record.getDttm().getYear(), 2009);
        assertEquals(record.getDttm().getMonthOfYear(), 7);
        assertEquals(record.getDttm().getMinuteOfHour(), 48);
    }

}