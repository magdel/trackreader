package ru.ogpscenter.tracker.reader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrackReaderApplication {

    public static void main(String[] args) {
        System.out.println("Starting..");
        SpringApplication.run(TrackReaderApplication.class, args);
        System.out.println("Started");
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
