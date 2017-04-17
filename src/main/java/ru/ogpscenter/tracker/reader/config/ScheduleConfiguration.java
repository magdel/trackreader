package ru.ogpscenter.tracker.reader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.ogpscenter.tracker.reader.consumer.HttpConsumer;
import ru.ogpscenter.tracker.reader.scheduler.StatisticLogger;
import ru.ogpscenter.tracker.reader.server.TrackerStringHandler;


@Configuration
@EnableScheduling
public class ScheduleConfiguration {

    @Bean
    public StatisticLogger statisticLogger(HttpConsumer httpConsumer,
                                           TrackerStringHandler trackerStringHandler) {
        return new StatisticLogger(httpConsumer,
                trackerStringHandler);
    }
}
