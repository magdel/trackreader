package ru.ogpscenter.tracker.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrackReaderApplication {
    private static final Logger logger = LoggerFactory.getLogger(TrackReaderApplication.class);

    public static void main(String[] args) {
        logger.info("Entering..");
        System.out.println("Starting..");
        SpringApplication.run(TrackReaderApplication.class, args);
        System.out.println("Started");
    }
}
