package tk.aakado.socketPrototype.client;

import com.google.gson.Gson;
import tk.aakado.socketPrototype.shared.Action;
import tk.aakado.socketPrototype.shared.ActionType;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        try {
            Socket connection = new Socket("localhost", 9988);
            System.out.println("Connected to MultiSweeper Server");
            PrintWriter output = new PrintWriter(connection.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            TimeUnit.SECONDS.sleep(3);

            Action action = new Action(ActionType.CLICK, new Point(123, 19));
            output.println(action.toJson());

            String line;
            while((line = input.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
