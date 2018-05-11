package tk.aakado.socketPrototype.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        try {

            // Start Server
            ServerSocket serverSocket = new ServerSocket(9988);
            System.out.println("Server Started!");

            final List<PrintWriter> outputs = new ArrayList<>();
            final List<BufferedReader> inputs = new ArrayList<>();

            // Accept new connections
            ScheduledExecutorService listenService = Executors.newScheduledThreadPool(1);
            listenService.scheduleWithFixedDelay(
                    () -> {
                        try {
                            Socket connection = serverSocket.accept();
                            System.out.println("Client connected: " + connection.getRemoteSocketAddress());

                            outputs.add(new PrintWriter(connection.getOutputStream(), true));
                            inputs.add(new BufferedReader(new InputStreamReader(connection.getInputStream())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }, 1,
                    1,
                    TimeUnit.SECONDS
            );

            // Periodic output
            ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
            timer.scheduleAtFixedRate(
                    () -> outputs.forEach(output -> output.println(LocalDateTime.now())),
                    0,
                    1,
                    TimeUnit.SECONDS
            );

            // Print inputs
            inputs.forEach(input -> Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    String line;
                    while ((line = input.readLine()) != null) {
                        System.out.print(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
