package tk.aakado.socketPrototype.server;

import com.google.gson.JsonObject;
import tk.aakado.socketPrototype.shared.ActionType;

public class ExampleActionHandler {

    @ActionHandler(actionType = ActionType.CLICK)
    public void handleClick(JsonObject params) {
        System.out.println("Click handler executed");
        int x = params.get("x").getAsInt();
        int y = params.get("y").getAsInt();

        System.out.println("Click at " + x + " / " + y);
    }
}
