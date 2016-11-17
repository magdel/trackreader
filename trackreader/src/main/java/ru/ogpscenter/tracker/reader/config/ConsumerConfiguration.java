package ru.ogpscenter.tracker.reader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.FluxSink;
import ru.ogpscenter.tracker.reader.config.properties.NotificationDestination;
import ru.ogpscenter.tracker.reader.consumer.HttpConsumer;
import ru.ogpscenter.tracker.reader.consumer.TrackConsumer;
import ru.ogpscenter.tracker.reader.generator.SimpleFlakeIdGenerator;
import ru.ogpscenter.tracker.reader.mt90.StringLocationRecordMapper;

import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

@Configuration
public class ConsumerConfiguration {

    private final SimpleFlakeIdGenerator idGenerator = new SimpleFlakeIdGenerator();

    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public TrackConsumer trackConsumer(Consumer<FluxSink<String>> trackerStringHandler,
                                       HttpConsumer httpConsumer,
                                       StringLocationRecordMapper mapper) throws IOException {
        return new TrackConsumer(trackerStringHandler,
                httpConsumer,
                mapper);
    }

    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public HttpConsumer httpConsumer(NotificationDestination notificationDestination) throws IOException {
        return new HttpConsumer(URI.create(notificationDestination.getNotifyUri()));
    }

    @Bean
    public StringLocationRecordMapper recordMapper() throws IOException {
        return new StringLocationRecordMapper(idGenerator);
    }

}
