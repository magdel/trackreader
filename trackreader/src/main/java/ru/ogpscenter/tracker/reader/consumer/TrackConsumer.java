package ru.ogpscenter.tracker.reader.consumer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

/**
 * Created by rfk on 15.11.2016.
 */
public class TrackConsumer {
    private Consumer<FluxSink<String>> trackerStringHandler;
    private HttpConsumer httpConsumer;

    public TrackConsumer(Consumer<FluxSink<String>> trackerStringHandler, HttpConsumer httpConsumer) {
        this.trackerStringHandler = trackerStringHandler;
        this.httpConsumer = httpConsumer;
    }

    public void init() {
        Flux.create(trackerStringHandler, FluxSink.OverflowStrategy.LATEST)
                .onBackpressureBuffer(16)
                .subscribe(httpConsumer);

        //Processor<Integer, Integer> p = WorkQueueProcessor.share("TK",32, true);// RingBufferProcessor.create("test", 32);

    }

    public void shutdown() {

    }

}
