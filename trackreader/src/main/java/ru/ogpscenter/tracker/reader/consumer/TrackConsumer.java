package ru.ogpscenter.tracker.reader.consumer;

import reactor.core.Cancellation;
import reactor.core.publisher.*;

import java.util.function.Consumer;

/**
 * Created by rfk on 15.11.2016.
 */
public class TrackConsumer {
    private final Consumer<FluxSink<String>> trackerStringHandler;
    private final Consumer<String> httpConsumer;
    private Cancellation cancellation;

    public TrackConsumer(Consumer<FluxSink<String>> trackerStringHandler,
                         Consumer<String> httpConsumer) {
        this.trackerStringHandler = trackerStringHandler;
        this.httpConsumer = httpConsumer;
    }

    public void init() {
        cancellation = Flux.create(trackerStringHandler, FluxSink.OverflowStrategy.LATEST)
                .onBackpressureBuffer(4)
                .subscribe(httpConsumer);
    }

    public void shutdown() {
        cancellation.dispose();
    }

}
