package ru.ogpscenter.tracker.reader.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Cancellation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import ru.ogpscenter.tracker.reader.domain.LocationRecord;
import ru.ogpscenter.tracker.reader.mt90.StringLocationRecordMapper;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by rfk on 15.11.2016.
 */
public class TrackConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TrackConsumer.class);
    private final Consumer<FluxSink<String>> trackerStringHandler;
    private final Consumer<LocationRecord> httpConsumer;
    private final StringLocationRecordMapper mapper;
    private Cancellation cancellation;

    public TrackConsumer(Consumer<FluxSink<String>> trackerStringHandler,
                         Consumer<LocationRecord> httpConsumer,
                         StringLocationRecordMapper mapper) {
        this.trackerStringHandler = trackerStringHandler;
        this.httpConsumer = httpConsumer;
        this.mapper = mapper;
    }

    public void init() {
        Consumer<FluxSink<String>> trackerStringHandler = this.trackerStringHandler;
        cancellation = Flux.create(trackerStringHandler, FluxSink.OverflowStrategy.LATEST)
                //.log()
                //.onBackpressureBuffer(32)
                .publishOn(Schedulers.newSingle("mapper"))
                //.flatMap(str -> Mono.just(mapper.apply(str)))
                .map(mapper)
                .filter(Optional::isPresent)
                .map(Optional::get)
                //.log()
                .doOnComplete(() -> logger.warn("Stream completed"))
                .doOnError(throwable -> logger.error("Stream convert error", throwable))
                .subscribe(httpConsumer);
    }

    public void shutdown() {
        cancellation.dispose();
    }

}
