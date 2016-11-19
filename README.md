# TrackReader

[![Travis CI](https://travis-ci.org/magdel/trackreader.svg?branch=master)](https://travis-ci.org/magdel/trackreader)

Reactive server to read GPS Trackers TCP streams and send data over HTTP further.

Now accepts data from MT90 GPSTracker and TK102-2.

## Request format

Each message sent by HTTP request with content-type: application/x-www-form-urlencoded
        
    eventId     - each request has it's own unique id, to exclude duplication 
    deviceId    - device id as it comes
    dttm        - event time, Unix milliseconds in UTC
    imei        - device IMEI, if present
    lat         - latitude, if present
    lon         - latitude, if present
    spd         - speed in m/s, if present
    crs         - course (bearing) to absolute north in degrees, if present

HTTP request body:

<pre>
eventId=799968846034384073&deviceId=Id-112233&dttm=1419420522000&imei=013226008424265&lat=60.500000&lon=-30.355715
</pre>

## Configuration

Setup properties in Spring Boot via application.properties or environment:

		#Your REST API
		notify.http.notifyUri=http://someyourserver.com/api/trackreaderwhatever/location
		notify.http.maxConnections=10
		#TCP port to listen
		server.tcp.port=7777

## Starting server

**requires Java 8 or + to run**.


		cd trackreader
		mvn spring-boot:run


_Pavel Raev, 2016_


