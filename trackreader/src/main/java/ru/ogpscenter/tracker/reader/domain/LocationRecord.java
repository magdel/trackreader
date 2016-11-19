package ru.ogpscenter.tracker.reader.domain;

import org.joda.time.DateTime;

/**
 * Created by rfk on 17.11.2016.
 */
public class LocationRecord {
    private final Long eventId;
    private final String deviceId;
    private final String imei;
    private final DateTime dttm;
    private final double lat;
    private final double lon;
    private final double spd;
    private final int crs;
    private final boolean validLocation;
    private final boolean validSpdCrs;

    public LocationRecord(Long eventId, String deviceId, String imei, DateTime dttm,
                          double lat, double lon, double spd, int crs,
                          boolean validLocation, boolean validSpdCrs) {
        this.eventId = eventId;
        this.deviceId = deviceId;
        this.imei = imei;
        this.dttm = dttm;
        this.lat = lat;
        this.lon = lon;
        this.spd = spd;
        this.crs = crs;
        this.validLocation = validLocation;
        this.validSpdCrs = validSpdCrs;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getImei() {
        return imei;
    }

    public DateTime getDttm() {
        return dttm;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getSpd() {
        return spd;
    }

    public int getCrs() {
        return crs;
    }


    public boolean isValidLocation() {
        return validLocation;
    }


    public boolean isValidSpdCrs() {
        return validSpdCrs;
    }

    @Override
    public String toString() {
        return "LocationRecord{" +
                "eventId=" + eventId +
                ", deviceId='" + deviceId + '\'' +
                ", imei='" + imei + '\'' +
                ", dttm=" + dttm +
                ", lat=" + lat +
                ", lon=" + lon +
                ", spd=" + spd +
                ", crs=" + crs +
                ", validLocation=" + validLocation +
                ", validSpdCrs=" + validSpdCrs +
                '}';
    }
}
