package ru.ogpscenter.tracker.reader;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.ogpscenter.tracker.reader.consumer.HttpConsumer;
import ru.ogpscenter.tracker.reader.server.TrackerStringHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

@SpringBootTest(classes = {TrackReaderApplication.class})
public class TrackReaderApplicationTests extends AbstractTestNGSpringContextTests {

    @Autowired
    Environment environment;

    @Autowired
    HttpConsumer httpConsumer;

    @Autowired
    TrackerStringHandler trackerStringHandler;

    @BeforeClass
    public void setupd() {
        System.out.println("tcp.port=" + environment.getProperty("server.tcp.port"));
    }

    @Test
    public void testProcessMt90Message() throws Exception {
        Assert.assertEquals(httpConsumer.getAcceptCount(), 0);
        String s = "STX,Id-112233,$GPRMC,112842.000,A,6030.0000,N,03021.3429,W,0.00,0.00,241214,,,A*6C,F,,imei:013226008424265,03,23.0,Battery=70%,,0,250,02,1E82,173D;36\n";
        sendTCPString(s);
        Thread.sleep(1000);
        Assert.assertEquals(httpConsumer.getAcceptCount(), 1);
        Assert.assertEquals(httpConsumer.getSentCount(), 1);
        Assert.assertEquals(trackerStringHandler.getReadCount(), 1);
    }

    @Test(dependsOnMethods = "testProcessMt90Message")
    public void testProcessMt90SouthMessage() throws Exception {
        Assert.assertEquals(httpConsumer.getAcceptCount(), 1);
        String s = "STX,Id-112233,$GPRMC,112842.000,A,6030.0000,S,03021.3429,W,0.00,0.00,241214,,,A*6C,F,,imei:013226008424265,03,23.0,Battery=70%,,0,250,02,1E82,173D;36\n";
        sendTCPString(s);
        Thread.sleep(1000);
        Assert.assertEquals(httpConsumer.getAcceptCount(), 2);
        Assert.assertEquals(httpConsumer.getSentCount(), 2);
        Assert.assertEquals(trackerStringHandler.getReadCount(), 2);
    }

    @Test(dependsOnMethods = "testProcessMt90SouthMessage")
    public void testProcessMt90noTimeMessage() throws Exception {
        Assert.assertEquals(httpConsumer.getAcceptCount(), 2);
        String s = "STX,Id-112233,$GPRMC,,A,6030.0000,S,03021.3429,W,0.00,0.00,241214,,,A*6C,F,,imei:013226008424265,03,23.0,Battery=70%,,0,250,02,1E82,173D;36\n";
        sendTCPString(s);
        Thread.sleep(1000);
        Assert.assertEquals(httpConsumer.getAcceptCount(), 2);
        Assert.assertEquals(httpConsumer.getSentCount(), 2);
        Assert.assertEquals(trackerStringHandler.getReadCount(), 3);
    }

    @Test(dependsOnMethods = "testProcessMt90noTimeMessage")
    public void testIgnoreTrashMessage() throws Exception {
        Assert.assertEquals(httpConsumer.getAcceptCount(), 2);
        String s = "STX,,,,,,\n";
        sendTCPString(s);
        Thread.sleep(1000);
        Assert.assertEquals(httpConsumer.getAcceptCount(), 2);
        Assert.assertEquals(trackerStringHandler.getReadCount(), 4);
    }

    @Test(dependsOnMethods = "testIgnoreTrashMessage")
    public void testProcessTk102Message() throws Exception {
        Assert.assertEquals(httpConsumer.getAcceptCount(),2);
        String s = "090723164830,+13616959853,GPRMC," +
                "214830.000,A,3017.2558,N,09749.4888,W,26.9,108.8,230709,10,200,A*61,F, Help me,imei: 359587013388627," +
                "05,264.5,F:3.79V,0,122,13990,310,01,0AB0,345A\n";
        sendTCPString(s);
        Thread.sleep(1000);
        Assert.assertEquals(httpConsumer.getAcceptCount(), 3);
        Assert.assertEquals(trackerStringHandler.getReadCount(), 5);
    }

    private void sendTCPString(String s) throws IOException, InterruptedException {
        int port = Integer.parseInt(environment.getProperty("server.tcp.port"));
        sendLocalhostTCPString(s, port);
    }

    public static void sendLocalhostTCPString(String s, int port) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", port);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(s.getBytes());
        outputStream.flush();
        outputStream.close();
        socket.close();
    }

}
