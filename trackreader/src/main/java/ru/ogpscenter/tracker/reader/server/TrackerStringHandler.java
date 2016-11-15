package ru.ogpscenter.tracker.reader.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

/**
 * Created by rfk on 15.11.2016.
 */
public class TrackerStringHandler extends ChannelHandlerAdapter implements Consumer<FluxSink<String>> {
    private static final Logger logger = LoggerFactory.getLogger(TrackReaderServer.class);

    private FluxSink<String> stringFluxSink;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String trackerMessage = (String) msg;
        //System.out.print(trackerMessage);
        if (stringFluxSink != null) {
            logger.info("Got: msg={}", trackerMessage);
            stringFluxSink.next(trackerMessage);
        }
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
}
