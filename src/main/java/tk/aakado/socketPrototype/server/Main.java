package tk.aakado.socketPrototype.server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(9988);
        server.addActionHandler(ExampleActionHandler.class);
    }
}
