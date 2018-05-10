package tk.aakado.socketPrototype.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9988);
            System.out.println("Server Started!");

            Socket connection = serverSocket.accept();
            System.out.println("Client connected: " + connection.getRemoteSocketAddress());


            PrintWriter output = new PrintWriter(connection.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
            timer.scheduleAtFixedRate(
                    () -> output.println(LocalDateTime.now()),
                    0,
                    1,
                    TimeUnit.SECONDS
            );

            String line;
            while ((line = input.readLine()) != null) {
                System.out.print(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
