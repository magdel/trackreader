package ru.ogpscenter.tracker.reader.consumer;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Created by rfk on 15.11.2016.
 */
public class HttpConsumer implements Consumer<String> {
    private static final Logger logger = LoggerFactory.getLogger(HttpConsumer.class);

    public HttpConsumer() throws IOReactorException {

        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
        PoolingNHttpClientConnectionManager cm =
                new PoolingNHttpClientConnectionManager(ioReactor);
        CloseableHttpAsyncClient client =
                HttpAsyncClients.custom().setConnectionManager(cm).build();
        client.start();
    }

    @Override
    public void accept(String s) {
        logger.info("Sent params to site..");
    }
}
