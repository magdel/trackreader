package ru.ogpscenter.tracker.reader.consumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by rfk on 15.11.2016.
 */
public class HttpConsumer implements Consumer<String> {
    private static final Logger logger = LoggerFactory.getLogger(HttpConsumer.class);

    private final PoolingNHttpClientConnectionManager connectionManager;
    private final CloseableHttpAsyncClient client;

    public HttpConsumer() throws IOReactorException {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(1)
                .build();
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);

        this.connectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
        this.connectionManager.setMaxTotal(20);
        this.connectionManager.setDefaultMaxPerRoute(10);
        this.client = HttpAsyncClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(5000)
                        .setConnectionRequestTimeout(500)
                        .build()
                )
                .setConnectionManager(connectionManager).build();
    }

    public void init() {
        client.start();
    }

    @Override
    public void accept(String s) {
        logger.info("Request to send");
        HttpGet httpget = new HttpGet("http://ya.ru");
        long started = System.currentTimeMillis();
        client.execute(httpget, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                logger.info("Request sent: time={}", System.currentTimeMillis() - started);
                //logger.info("C: {}", connectionManager.getTotalStats().getLeased());
            }

            @Override
            public void failed(Exception e) {
                logger.error("Request failed");
            }

            @Override
            public void cancelled() {
                logger.warn("Request cancelled");
            }
        });
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
}
