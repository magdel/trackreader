package ru.ogpscenter.tracker.reader.domain;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by rfk on 18.04.2017.
 */
public class LocationRecordTest {
    @Test
    public void testToString() throws Exception {
        LocationRecord locationRecord = new LocationRecord(1L, "2", "ime", DateTime.parse("2010-06-30T01:20Z"),
                60.55, -120.1, 3.33, 155, false, false, TrackerType.tk102);

        Assert.assertTrue(locationRecord.toString().contains("120"));
    }

}