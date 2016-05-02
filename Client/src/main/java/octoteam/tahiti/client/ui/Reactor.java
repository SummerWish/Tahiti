package octoteam.tahiti.client.ui;

import com.google.common.eventbus.Subscribe;
import octoteam.tahiti.client.TahitiClient;
import octoteam.tahiti.client.event.*;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.protocol.SocketMessageProtos.SessionExpiredPushBody;
import org.apache.commons.lang3.StringUtils;

public class Reactor {

    private TahitiClient client;

    private Renderer renderer;

    private String loginUsername;
    private String loginPassword;
    private int loginGroupNumber;

    public Reactor(TahitiClient client, Renderer renderer) {
        this.client = client;
        this.renderer = renderer;
    }

    void login() {
        client.login(loginUsername, loginPassword, loginGroupNumber, msg -> {
            renderer.actionHideLoginStateDialog();
            if (msg.getStatus() == Message.StatusCode.PASSWORD_INCORRECT) {
                renderer.actionShowMessageDialog("Login failed", "Incorrect password");
            } else if (msg.getStatus() == Message.StatusCode.USERNAME_NOT_FOUND) {
                renderer.actionShowMessageDialog("Login failed", "Username not found");
            } else if (msg.getStatus() == Message.StatusCode.SUCCESS) {
                renderer.actionHideLoginDialog();
                renderer.actionShowMainWindow();
            }
            return null;
        });
    }

    /**
     * 处理会话过期事件: 对于类型为 EXPIRED 的会话过期, 发起重新登录请求
     *
     * @param event 事件对象
     */
    @Subscribe
    public void onSessionExpired(SessionExpiredEvent event) {
        renderer.actionAppendNotice("Session expired. You are logged out.");
        if (event.getReason() == SessionExpiredPushBody.Reason.EXPIRED) {
            client.login(loginUsername, loginPassword, loginGroupNumber);
        }
    }

    /**
     * 处理已连接事件: 在界面上显示正在登录
     *
     * @param event 事件对象
     */
    @Subscribe
    public void onConnected(ConnectedEvent event) {
        try {
            renderer.actionShowLoginStateDialog("Log in...");
            login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理连接失败事件: 在界面上显示无法连接到服务器
     *
     * @param event 事件对象
     */
    @Subscribe
    public void onConnectError(ConnectErrorEvent event) {
        try {
            renderer.actionHideLoginStateDialog();
            renderer.actionShowMessageDialog("Login failed", "Cannot connect to server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理登录响应: 在界面上显示您已经登录
     *
     * @param event 事件对象
     */
    @Subscribe
    public void onLoginResponse(LoginAttemptEvent event) {
        if (event.isSuccess()) {
            renderer.actionAppendNotice("You are logged in.");
        }
    }

    /**
     * 处理登录按钮事件: 显示正在连接到服务器并发起登录请求
     *
     * @param event 事件对象
     */
    @Subscribe
    public void onLoginCommand(UIOnLoginCommandEvent event) {
        try {
            loginUsername = event.getUsername();
            loginPassword = event.getPassword();
            loginGroupNumber = event.getGroupNumber();

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

    /**
     * 处理发送按钮事件: 发送消息给服务端
     * 若发送失败, 则在界面上显示失败信息
     *
     * @param event 事件对象
     */
    @Subscribe
    public void onClickSend(UIOnSendCommandEvent event) {
        client.sendMessage(event.getPayload(), msg -> {
            if (msg.getStatus() != Message.StatusCode.SUCCESS) {
                renderer.actionAppendNotice(String.format(
                        "Failed to deliver \"%s\"\nReason: %s",
                        StringUtils.abbreviate(event.getPayload(), 20),
                        msg.getStatus().toString()
                ));
            }
            return null;
        });
    }

    /**
     * 处理发送消息事件: 显示在界面上
     *
     * @param event 事件对象
     */
    @Subscribe
    public void onSendMessage(SendMessageEvent event) {
        renderer.actionAppendChatMessage("Me", event.getTimestamp(), event.getPayload());
    }

    /**
     * 处理收到广播消息事件: 显示在界面上
     *
     * @param event 事件对象
     */
    @Subscribe
    public void onReceiveChatMessage(ChatMessageEvent event) {
        renderer.actionAppendChatMessage(event.getUsername(), event.getTimestamp(), event.getPayload());
    }

}


