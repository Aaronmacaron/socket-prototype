package tk.aakado.socketPrototype.server;

import com.google.gson.*;
import tk.aakado.socketPrototype.shared.Action;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private ServerSocket server;
    private List<Connection> connections = new ArrayList<>();
    private List<Class> actionHandlers = new ArrayList<>();
    private ExecutorService queue = Executors.newFixedThreadPool(20);

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started! Listening for incoming connections on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        executeRepeatidly(this::listen);
    }

    public void addActionHandler(Class actionHandler) {
        actionHandlers.add(actionHandler);
    }

    public void addAllActionHandlers(Collection<Class> actionsHandlers) {
        this.actionHandlers.addAll(actionsHandlers);
    }

    private void executeRepeatidly(Runnable r) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(r, 1, 1, TimeUnit.MICROSECONDS);
    }

    private void listen() {
        try {
            Socket socket = server.accept();
            System.out.println("Client connected: " + socket.getRemoteSocketAddress());

            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Connection connection = new Connection(socket, output, input);
            connections.add(connection);
            executeRepeatidly(() -> handleInput(connection));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void handleInput(Connection connection) {
        try {
            String line;
            while((line = connection.getInput().readLine()) != null) {
                JsonParser parser = new JsonParser();
                JsonObject json = parser.parse(line).getAsJsonObject();
                JsonObject params = json.getAsJsonObject("params");
                ActionType actionType = ActionType.valueOf(json.get("actionType").getAsString());
                queue.submit(() -> executeAllMatchingActionHandlers(actionType, params, connection));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void executeAllMatchingActionHandlers(ActionType actionType, JsonObject json, Connection connection) {
        actionHandlers.forEach(actionHandler -> {
            final List<Method> allMethods = new ArrayList<>(Arrays.asList(actionHandler.getDeclaredMethods()));
            for (Method method : allMethods) {
                if (method.isAnnotationPresent(ActionHandler.class)) {
                    ActionHandler annotation = method.getAnnotation(ActionHandler.class);
                    if (annotation.actionType() == actionType) {
                        try {
                            Message message = new Message(this, connection, json);
                            method.invoke(actionHandler.newInstance(), message);
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

    public void broadcast(Action action) {
        for (Connection connection : connections) {
            connection.getOutput().println(action.toJson());
        }
    }

    public void broadcastExcept(Action action, Connection except) {
        for (Connection connection : connections) {
            if (!connection.equals(except)) {
                connection.getOutput().println(action.toJson());
            }
        }
    }
}
