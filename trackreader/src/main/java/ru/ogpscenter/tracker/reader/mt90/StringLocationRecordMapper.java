package ru.ogpscenter.tracker.reader.mt90;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ogpscenter.tracker.reader.domain.LocationRecord;
import ru.ogpscenter.tracker.reader.generator.IdGenerator;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by rfk on 18.11.2016.
 */
public class StringLocationRecordMapper implements Function<String, Optional<LocationRecord>> {
    private static final Logger logger = LoggerFactory.getLogger(StringLocationRecordMapper.class);
    private final IdGenerator idGenerator;

    public StringLocationRecordMapper(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Optional<LocationRecord> apply(String trackString) {
        try {
            return convert(trackString, idGenerator);
        } catch (Exception ex) {
            logger.error("Convert failed: dataString={}", trackString, ex);
            return Optional.empty();
        }
    }

    static Optional<LocationRecord> convert(String trackString, IdGenerator idGenerator) {
        //STX,Id-112233,$GPRMC,112842.000,A,6000.5274,N,03021.3429,E,0.00,0.00,241214,,,A*6C,F,,imei:013226008424265,03,23.0,Battery=70%,,0,250,02,1E82,173D;36

        String[] split = trackString.split(",");
        if (split.length < 18) {
            return Optional.empty();
        }
        String sDeviceId = split[1];
        if (sDeviceId == null || !"$GPRMC".equals(split[2])) {
            return Optional.empty();
        }
        String sTime = split[3];
        String sDate = split[11];
        if (sTime == null || sDate == null) {
            return Optional.empty();
        }
        if (sDate.length() != 6 || sTime.length() != 10) {
            return Optional.empty();
        }

        String sDateTimeIso = "20" + sDate.substring(4) + sDate.substring(2, 4) + sDate.substring(0, 2) + 'T' + sTime + 'Z';
        DateTime dttm = ISODateTimeFormat.basicDateTime().parseDateTime(sDateTimeIso);

        double lat = 0;
        double lon = 0;
        double spd = 0;
        int crs = 0;
        boolean validLocation = false;
        String sLat = split[5];
        String sLon = split[7];
        try {
            lat = Double.parseDouble(sLat.substring(0, 2)) + Double.parseDouble(sLat.substring(2)) / 60d;
            if (!"N".equals(split[6])) {
                lat = -lat;
            }
            lon = Double.parseDouble(sLon.substring(0, 3)) + Double.parseDouble(sLon.substring(3)) / 60d;
            if (!"E".equals(split[8])) {
                lon = -lon;
            }
            validLocation = true;
        } catch (Exception e) {
            //not valid data
        }

        boolean validSpdCrs = true;
        String sSpd = split[12];
        String sCrs = split[13];
        try {
            spd = Double.parseDouble(sSpd) * 0.514;
        } catch (Exception e) {
            validSpdCrs = false;
        }
        if (validSpdCrs) {
            try {
                crs = (int) Double.parseDouble(sCrs);
            } catch (Exception e) {
                validSpdCrs = false;
            }
        }

        String sImei = split[17];
        if (sImei != null) {
            if (sImei.startsWith("imei:")) {
                sImei = sImei.substring(5);
            } else {
                sImei = null;
            }
        }

        return Optional.of(new LocationRecord(idGenerator.generate(),
                sDeviceId, sImei, dttm, lat, lon, spd, crs, validLocation, validSpdCrs));
    }
}
