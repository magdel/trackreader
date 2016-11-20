package ru.ogpscenter.tracker.reader.consumer;

import org.apache.http.NameValuePair;
import org.joda.time.DateTime;
import org.testng.annotations.Test;
import ru.ogpscenter.tracker.reader.domain.LocationRecord;
import ru.ogpscenter.tracker.reader.domain.TrackerType;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;

/**
 * Created by rfk on 18.11.2016.
 */
public class HttpConsumerTest {
    @Test
    public void testCreateFullParametersArray() throws Exception {
        ArrayList<NameValuePair> array = HttpConsumer.createParametersArray(new LocationRecord(1L, "2", "ime", DateTime.parse("2010-06-30T01:20Z"),
                60.55, -120.1, 3.33, 155, true, true, TrackerType.mt90));
        assertEquals(array.toString(), "[eventId=1, deviceId=2, dttm=1277860800000, trackerType=mt90, imei=ime, lat=60.550000, lon=-120.100000, spd=3.3, crs=155]");
    }

    @Test
    public void testCreateMinParametersArray() throws Exception {
        ArrayList<NameValuePair> array = HttpConsumer.createParametersArray(new LocationRecord(1L, "2", "ime", DateTime.parse("2010-06-30T01:20Z"),
                60.55, -120.1, 3.33, 155, false, false, TrackerType.tk102));
        assertEquals(array.toString(), "[eventId=1, deviceId=2, dttm=1277860800000, trackerType=tk102, imei=ime]");
    }

    //dev integration test
   /* @Test(threadPoolSize = 8, invocationCount = 10)
    public void sendTCP() throws IOException, InterruptedException {
        String s = "STX,Id-112233,$GPRMC,112842.000,A,6030.0000,N,03021.3429,W,0.00,0.00,241214,,,A*6C,F,,imei:013226008424265,03,23.0,Battery=70%,,0,250,02,1E82,173D;36\n";

        TrackReaderApplicationTests.sendLocalhostTCPString(s, 10000);
    }*/

}