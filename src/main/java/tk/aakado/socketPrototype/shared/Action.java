package tk.aakado.socketPrototype.shared;

public class Action {
    private ActionType actionType;
    private Object params;

    public Action(ActionType actionType, Object params) {
        this.actionType = actionType;
        this.params = params;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public Object getParams() {
        return params;
    }
}
