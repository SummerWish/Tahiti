package octoteam.tahiti.client.ui;

import com.google.common.eventbus.Subscribe;
import octoteam.tahiti.client.TahitiClient;
import octoteam.tahiti.client.event.ConnectErrorEvent;
import octoteam.tahiti.client.event.ConnectedEvent;
import octoteam.tahiti.client.event.UIOnLoginCommandEvent;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;

public class Reactor {

    private TahitiClient client;

    private Renderer renderer;

    private String loginUsername;
    private String loginPassword;

    public Reactor(TahitiClient client, Renderer renderer) {
        this.client = client;
        this.renderer = renderer;
    }

    void login() {
        client.login(loginUsername, loginPassword, msg -> {
            renderer.actionHideLoginStateDialog();
            if (msg.getStatus() == Message.StatusCode.PASSWORD_INCORRECT) {
                renderer.actionShowMessageDialog("Login failed", "Incorrect password");
            } else if (msg.getStatus() == Message.StatusCode.USERNAME_NOT_FOUND) {
                renderer.actionShowMessageDialog("Login failed", "Username not found");
            } else if (msg.getStatus() == Message.StatusCode.SUCCESS) {
                renderer.actionShowMessageDialog("Login success", "Success!");
                renderer.actionHideLoginDialog();
                renderer.actionShowMainWindow();
            }
            return null;
        });
    }

    @Subscribe
    public void onConnected(ConnectedEvent event) {
        try {
            renderer.actionShowLoginStateDialog("Log in...");
            login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onConnectError(ConnectErrorEvent event) {
        try {
            renderer.actionHideLoginStateDialog();
            renderer.actionShowMessageDialog("Login failed", "Cannot connect to server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onLoginCommand(UIOnLoginCommandEvent event) {
        try {
            loginUsername = event.getUsername();
            loginPassword = event.getPassword();
            if (client.isConnected()) {
                login();
            } else {
                client.connectAsync();
            }
            renderer.actionShowLoginStateDialog("Connecting to server...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
