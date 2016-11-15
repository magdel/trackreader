package ru.ogpscenter.tracker.reader.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Created by rfk on 15.11.2016.
 */
public class HttpConsumer implements Consumer<String> {
    private static final Logger logger = LoggerFactory.getLogger(HttpConsumer.class);

    public HttpConsumer() {
    }

    @Override
    public void accept(String s) {
        logger.info("Sent params to site..");
    }
}
