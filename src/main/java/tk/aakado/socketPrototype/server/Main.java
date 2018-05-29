package tk.aakado.socketPrototype.server;

public class Main {
    public static void main(String[] args) {
        ServerConnector server = new ServerConnector(9988);
        server.addActionHandler(ExampleActionHandler.class);
        server.start();
    }
}
