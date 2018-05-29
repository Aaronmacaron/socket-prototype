package tk.aakado.socketPrototype.shared;

import java.util.Collection;

public interface Connector {
    void addActionHandler(Class clazz);

    void addAllActionHandlers(Collection<Class> classes);

    void send(Action action);

    void start();
}
