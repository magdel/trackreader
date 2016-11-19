package ru.ogpscenter.tracker.reader.consumer;

import reactor.core.Cancellation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import ru.ogpscenter.tracker.reader.domain.LocationRecord;
import ru.ogpscenter.tracker.reader.mt90.StringLocationRecordMapper;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by rfk on 15.11.2016.
 */
public class TrackConsumer {
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
        cancellation = Flux.create(trackerStringHandler, FluxSink.OverflowStrategy.LATEST)
                .onBackpressureBuffer(4)
                .map(mapper)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribe(httpConsumer);
    }

    public void shutdown() {
        cancellation.dispose();
    }

}
