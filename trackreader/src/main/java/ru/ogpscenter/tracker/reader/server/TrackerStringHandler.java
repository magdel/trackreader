package ru.ogpscenter.tracker.reader.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Created by rfk on 15.11.2016.
 */

@ChannelHandler.Sharable
public class TrackerStringHandler extends ChannelHandlerAdapter implements Consumer<FluxSink<String>> {
    private static final Logger logger = LoggerFactory.getLogger(TrackReaderServer.class);

    private final AtomicLong readCounter = new AtomicLong();
    private FluxSink<String> stringFluxSink;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String trackerMessage = (String) msg;
        if (stringFluxSink != null) {
            long count = readCounter.incrementAndGet();
            logger.info("Read: count={}, msg={}", count, trackerMessage);
            stringFluxSink.next(trackerMessage);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("Active: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info("Inactive: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("On handle", cause);
        ctx.close();
    }

    @Override
    public void accept(FluxSink<String> stringFluxSink) {
        if (this.stringFluxSink != null) {
            throw new IllegalArgumentException("Already set");
        }
        this.stringFluxSink = stringFluxSink;
    }

    public long getReadCount() {
        return readCounter.get();
    }
}
