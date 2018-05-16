package tk.aakado.socketPrototype.server;

import tk.aakado.socketPrototype.shared.Action;
import tk.aakado.socketPrototype.shared.ActionType;

import java.awt.*;

public class ExampleActionHandler {

    @ActionHandler(actionType = ActionType.CLICK)
    public void handleClick(Message message) {
        System.out.println("Click handler executed");
        int x = message.getParams().get("x").getAsInt();
        int y = message.getParams().get("y").getAsInt();

        System.out.println("Click at " + x + " / " + y);

        Action action = new Action(ActionType.CLICK, new Point(x, y));
        message.getServer().broadcastExcept(action, message.getSender());
    }

    @ActionHandler(actionType = ActionType.SET_PASSWORD)
    public void handleSetPassword(Message message) {
        System.out.println("Set password action triggered!");
        String newPassword = message.getParams().get("newPassword").getAsString();
        System.out.println("New Password set: " + newPassword);
    }
}
