package ru.ogpscenter.tracker.reader.consumer;

import org.apache.http.NameValuePair;
import org.joda.time.DateTime;
import org.testng.annotations.Test;
import ru.ogpscenter.tracker.reader.domain.LocationRecord;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;

/**
 * Created by rfk on 18.11.2016.
 */
public class HttpConsumerTest {
    @Test
    public void testCreateFullParametersArray() throws Exception {
        ArrayList<NameValuePair> array = HttpConsumer.createParametersArray(new LocationRecord(1L, "2", "ime", DateTime.parse("2010-06-30T01:20Z"),
                60.55, -120.1, 3.33, 155, true, true));
        assertEquals(array.toString(), "[eventId=1, deviceId=2, dttm=1277860800000, imei=ime, lat=60.550000, lon=-120.100000, spd=3.3, crs=155]");
    }

    @Test
    public void testCreateMinParametersArray() throws Exception {
        ArrayList<NameValuePair> array = HttpConsumer.createParametersArray(new LocationRecord(1L, "2", "ime", DateTime.parse("2010-06-30T01:20Z"),
                60.55, -120.1, 3.33, 155, false, false));
        assertEquals(array.toString(), "[eventId=1, deviceId=2, dttm=1277860800000, imei=ime]");
    }

}