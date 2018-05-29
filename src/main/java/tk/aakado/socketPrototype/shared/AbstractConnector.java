package tk.aakado.socketPrototype.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract public class AbstractConnector implements Connector {
    private List<Class> actionHandlers = new ArrayList<>();

    @Override
    public void addActionHandler(Class actionHandler) {
        actionHandlers.add(actionHandler);
    }

    @Override
    public void addAllActionHandlers(Collection<Class> actionsHandlers) {
        this.actionHandlers.addAll(actionsHandlers);
    }

}
