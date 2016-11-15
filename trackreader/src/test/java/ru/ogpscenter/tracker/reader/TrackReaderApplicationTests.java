package ru.ogpscenter.tracker.reader;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest(classes = {TrackReaderApplication.class})
public class TrackReaderApplicationTests extends AbstractTestNGSpringContextTests {
    private static final Logger logger = LoggerFactory.getLogger(TrackReaderApplicationTests.class);

    @Autowired
    Environment environment;

    //@Autowired
    //DeviceStorage deviceStorage;


    @BeforeClass
    public void setupd() {
        System.out.println("tcp.port=" + environment.getProperty("server.tcp.port"));
    }

    @Test
    public void testSome() throws Exception {
        //deviceStorage.getDevices();
    }

}
