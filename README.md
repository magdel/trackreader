# TrackReader

[![Travis CI](https://travis-ci.org/magdel/trackreader.svg?branch=master)](https://travis-ci.org/magdel/trackreader)

Reactive server to read GPS Trackers TCP streams and send data over HTTP further.

Now accepts data from MT90 GPSTracker.

## Starting server

**requires Java 8 or + to run**.


		cd trackreader
		mvn spring-boot:run

## Configuration

Setup properties in usual Spring Boot way:

		#Your REST API
		notify.http.notifyUri=http://someyourserver.com/api/trackreaderwhatever/location
		#TCP port to listen
		server.tcp.port=7777

That's


