package ru.ogpscenter.tracker.reader.consumer;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ogpscenter.tracker.reader.domain.LocationRecord;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Created by rfk on 15.11.2016.
 */
public class HttpConsumer implements Consumer<Optional<LocationRecord>> {
    private static final Logger logger = LoggerFactory.getLogger(HttpConsumer.class);

    private final PoolingNHttpClientConnectionManager connectionManager;
    private final CloseableHttpAsyncClient client;
    private final URI notifyUri;
    private final AtomicLong acceptedCounter = new AtomicLong();
    private final AtomicLong sentCounter = new AtomicLong();

    public HttpConsumer(URI notifyUri, int maxConnections) throws IOReactorException {
        this.notifyUri = notifyUri;
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(1)
                .build();
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);

        this.connectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
        this.connectionManager.setMaxTotal(maxConnections);
        this.connectionManager.setDefaultMaxPerRoute(maxConnections);
        this.client = HttpAsyncClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(5000)
                        .setConnectionRequestTimeout(500)
                        .build()
                )
                .setConnectionManager(connectionManager).build();
        logger.info("Sending: uri={}, maxConnections={}", notifyUri, maxConnections);
    }

    public void init() {
        client.start();
    }

    void shutdown() {
        try {
            client.close();
        } catch (IOException e) {
            logger.error("On close", e);
        }
        try {
            connectionManager.shutdown();
        } catch (IOException e) {
            logger.error("On shutdown", e);
        }
    }

    @Override
    public void accept(Optional<LocationRecord> optinalLocationRecord) {
        if (!optinalLocationRecord.isPresent()) {
            logger.info("Empty record");
            return;
        }
        long acceptNumber = acceptedCounter.incrementAndGet();
        LocationRecord locationRecord = optinalLocationRecord.get();
        logger.info("Request to send: eventId={}, accepted={}", locationRecord.getEventId(), acceptNumber);
        HttpPost httpget = createHttpPost(locationRecord);
        long started = System.currentTimeMillis();
        client.execute(httpget, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                long sentCount = sentCounter.incrementAndGet();
                logger.info("Request sent: eventId={}, code={}, time={}, count={}",
                        locationRecord.getEventId(),
                        httpResponse.getStatusLine().getStatusCode(),
                        System.currentTimeMillis() - started,
                        sentCount);
            }

            @Override
            public void failed(Exception e) {
                logger.error("Request failed: eventId={}, msg={}", locationRecord.getEventId(), e.getMessage());
            }

            @Override
            public void cancelled() {
                logger.warn("Request cancelled: eventId={}", locationRecord.getEventId());
            }
        });
    }

    private HttpPost createHttpPost(LocationRecord locationRecord) {
        HttpPost httpget = new HttpPost(notifyUri);
        httpget.setProtocolVersion(HttpVersion.HTTP_1_1);
        ArrayList<NameValuePair> parameters = createParametersArray(locationRecord);
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8);
        httpget.setEntity(entity);
        return httpget;
    }

    public long getAcceptCount() {
        return acceptedCounter.get();
    }

    public long getSentCount() {
        return sentCounter.get();
    }


    static ArrayList<NameValuePair> createParametersArray(LocationRecord locationRecord) {
        ArrayList<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("eventId", locationRecord.getEventId().toString()));
        parameters.add(new BasicNameValuePair("deviceId", locationRecord.getDeviceId()));
        parameters.add(new BasicNameValuePair("dttm", Long.toString(locationRecord.getDttm().getMillis())));
        parameters.add(new BasicNameValuePair("trackerType", locationRecord.getTrackerType().name()));

        //optional parameters
        if (locationRecord.getImei() != null) {
            parameters.add(new BasicNameValuePair("imei", locationRecord.getImei()));
        }
        DecimalFormat dfCoord = new DecimalFormat("0.000000");
        DecimalFormatSymbols instance = DecimalFormatSymbols.getInstance();
        instance.setDecimalSeparator('.');
        dfCoord.setDecimalFormatSymbols(instance);
        if (locationRecord.isValidLocation()) {
            parameters.add(new BasicNameValuePair("lat", dfCoord.format(locationRecord.getLat())));
            parameters.add(new BasicNameValuePair("lon", dfCoord.format(locationRecord.getLon())));
        }
        DecimalFormat dfSpd = new DecimalFormat("0.0");
        dfSpd.setDecimalFormatSymbols(instance);
        if (locationRecord.isValidSpdCrs()) {
            parameters.add(new BasicNameValuePair("spd", dfSpd.format(locationRecord.getSpd())));
            parameters.add(new BasicNameValuePair("crs", Integer.toString(locationRecord.getCrs())));
        }
        return parameters;
    }
}
