package ru.ogpscenter.tracker.reader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.FluxSink;
import ru.ogpscenter.tracker.reader.consumer.HttpConsumer;
import ru.ogpscenter.tracker.reader.consumer.TrackConsumer;

import java.io.IOException;
import java.util.function.Consumer;

@Configuration
public class ConsumerConfiguration {

    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public TrackConsumer trackConsumer(Consumer<FluxSink<String>> trackerStringHandler,
                                       HttpConsumer httpConsumer) throws IOException {
        return new TrackConsumer(trackerStringHandler,
                httpConsumer);
    }

    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public HttpConsumer httpConsumer() throws IOException {
        return new HttpConsumer();
    }

}
