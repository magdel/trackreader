import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by senin on 21.12.2014.
 * Multithreaded server side for messages from MiniTracker
 */

public class MiniTrackerServer implements Runnable {
    public static final int DEFAULT_PORT = 12000;
    Socket clientSocket;
    MiniTrackerServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public static void main(String args[]) throws Exception {
        ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
        System.out.println("Listening");
        while (true) {
            Socket sock = serverSocket.accept();
            new Thread(new MiniTrackerServer(sock)).start();
        }
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + ": connection opened");
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String inputData;
            while ((inputData = input.readLine()) != null) {
                System.out.println(inputData);
            }
            clientSocket.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(Thread.currentThread().getName() + ": connection closed");
    }
}