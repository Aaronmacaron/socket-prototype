package tk.aakado.socketPrototype.server;

import com.google.gson.*;
import tk.aakado.socketPrototype.shared.ActionType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private ServerSocket server;
    private List<Connection> connections = new ArrayList<>();
    private List<Class> actionHandlers = new ArrayList<>();

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started! Listening for incoming connections on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        listen();
    }

    public void addActionHandler(Class actionHandler) {
        actionHandlers.add(actionHandler);
    }

    public void addAllActionHandlers(Collection<Class> actionsHandlers) {
        this.actionHandlers.addAll(actionsHandlers);
    }

    private void listen() {
        ScheduledExecutorService listenService = Executors.newScheduledThreadPool(1);
        listenService.scheduleWithFixedDelay(
                () -> {
                    try {
                        Socket socket = server.accept();
                        System.out.println("Client connected: " + socket.getRemoteSocketAddress());

                        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        Connection connection = new Connection(socket, output, input);
                        connections.add(connection);
                        handleInput(connection);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }, 1,
                1,
                TimeUnit.MICROSECONDS
        );
    }

    private void handleInput(Connection connection) {
        ScheduledExecutorService inputService = Executors.newScheduledThreadPool(1);
        inputService.scheduleWithFixedDelay(() -> {
            try {
                String line;
                while((line = connection.getInput().readLine()) != null) {
                    JsonParser parser = new JsonParser();
                    JsonObject json = parser.parse(line).getAsJsonObject();
                    JsonObject params = json.getAsJsonObject("params");
                    ActionType actionType = ActionType.valueOf(json.get("actionType").getAsString());
                    executeAllMatchingActionHandlers(actionType, params);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }, 1, 1, TimeUnit.MICROSECONDS);
    }

    private void executeAllMatchingActionHandlers(ActionType actionType, JsonObject json) {
        actionHandlers.forEach(actionHandler -> {
            final List<Method> allMethods = new ArrayList<>(Arrays.asList(actionHandler.getDeclaredMethods()));
            for (Method method : allMethods) {
                if (method.isAnnotationPresent(ActionHandler.class)) {
                    ActionHandler annotation = method.getAnnotation(ActionHandler.class);
                    if (annotation.actionType() == actionType) {
                        try {
                            method.invoke(actionHandler.newInstance(), json);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            // Do nothing if method hasn't got the right parameters.
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
