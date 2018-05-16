package tk.aakado.socketPrototype.client;

public class SetPasswordAction {
    private String newPassword;

    public SetPasswordAction(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
