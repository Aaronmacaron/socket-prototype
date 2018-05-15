package tk.aakado.socketPrototype.server;

import tk.aakado.socketPrototype.shared.ActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionHandler {
    ActionType actionType();
}
