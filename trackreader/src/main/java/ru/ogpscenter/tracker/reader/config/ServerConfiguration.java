package ru.ogpscenter.tracker.reader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ogpscenter.tracker.reader.config.properties.TCPServerProperties;
import ru.ogpscenter.tracker.reader.server.TrackReaderServer;
import ru.ogpscenter.tracker.reader.server.TrackerStringHandler;

import java.io.IOException;

@Configuration
public class ServerConfiguration {

    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public TrackReaderServer trackReaderServer(TCPServerProperties tcpServerProperties,
                                               TrackerStringHandler trackerStringHandler) throws IOException {
        return new TrackReaderServer(tcpServerProperties.getPort(),
                trackerStringHandler);
    }

    @Bean
    public TrackerStringHandler trackerStringHandler() throws IOException {
        return new TrackerStringHandler();
    }

}
