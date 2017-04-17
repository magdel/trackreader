package ru.ogpscenter.tracker.reader.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by rfk on 15.11.2016.
 */
public class TrackReaderServer {
    private static final Logger logger = LoggerFactory.getLogger(TrackReaderServer.class);

    private final int port;
    private final ChannelHandlerAdapter channelHandlerAdapter;
    private ChannelFuture channelFuture;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public TrackReaderServer(int port, ChannelHandlerAdapter channelHandlerAdapter) {
        this.port = port;
        this.channelHandlerAdapter = channelHandlerAdapter;
    }

    public void init() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TrackerStringDecoder(), channelHandlerAdapter);
                        }
                    })
                    .option(ChannelOption.SO_LINGER, 0)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            channelFuture = serverBootstrap.bind(port);
            logger.info("Started TCP server: port={}", port);
        } catch (Exception e) {
            logger.error("Not started: port={}", port);
            workerGroup.shutdownGracefully();
            workerGroup = null;
            bossGroup.shutdownGracefully();
            bossGroup = null;
            throw new RuntimeException("TCP server failed to start", e);
        }
    }

    public void shutdown() {
        // Wait until the server socket is closed.
        // In this example, this does not happen, but you can do that to gracefully
        // shut down your server.
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        logger.info("Shutdowned TCP server: port={}", port);
    }

    private static class TrackerStringDecoder extends ByteToMessageDecoder { // (1)
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) { // (2)
            int bytesBefore = in.bytesBefore((byte) '\n');
            if (bytesBefore < 0) {
                return;
            }

            byte[] array = in.readBytes(bytesBefore + 1).array();
            out.add(new String(array)); // (4)
        }
    }


}
