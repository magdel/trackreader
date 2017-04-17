package ru.ogpscenter.tracker.reader.scheduler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import ru.ogpscenter.tracker.reader.consumer.HttpConsumer;
import ru.ogpscenter.tracker.reader.server.TrackerStringHandler;

public class StatisticLogger {
    private static final Logger logger = LoggerFactory.getLogger(StatisticLogger.class);
    private static final long MIN_MILLIS = 1000L * 60;
    private final HttpConsumer httpConsumer;
    private final TrackerStringHandler trackerStringHandler;

    public StatisticLogger(HttpConsumer httpConsumer,
                           TrackerStringHandler trackerStringHandler) {
        this.httpConsumer = httpConsumer;
        this.trackerStringHandler = trackerStringHandler;
    }

    @Scheduled(fixedDelay = MIN_MILLIS)
    public void checkStatistic() {
        logger.info("Processing stat: read={}, accepted={}, sent={}",
                trackerStringHandler.getReadCount(),
                httpConsumer.getAcceptCount(),
                httpConsumer.getSentCount());

    }

}
