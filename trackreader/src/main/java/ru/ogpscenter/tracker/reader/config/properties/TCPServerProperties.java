package ru.ogpscenter.tracker.reader.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties("server.tcp")
public class TCPServerProperties {


    @NotNull
    private Integer port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}