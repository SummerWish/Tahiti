package octoteam.tahiti.client;

import com.google.common.eventbus.Subscribe;
import octoteam.tahiti.client.event.ChatMessageEvent;
import octoteam.tahiti.client.event.LoginAttemptEvent;
import octoteam.tahiti.client.event.SendMessageEvent;
import wheellllll.performance.LogUtils;
import wheellllll.performance.PerformanceManager;

import java.util.concurrent.TimeUnit;

class IndexLogger {

    private final PerformanceManager pm;

    IndexLogger(String filePattern, int periodSeconds) {
        LogUtils.setLogPrefix(filePattern);
        pm = new PerformanceManager();
        pm.addIndex("Successfully Login Times");
        pm.addIndex("Failed Login Times");
        pm.addIndex("Sent Messages");
        pm.addIndex("Received Messages");
        pm.setTimeUnit(TimeUnit.SECONDS);
        pm.setInitialDelay(1);
        pm.setPeriod(periodSeconds);
    }

    /**
     * 订阅用户尝试登录事件 LoginAttemptEvent
     * 监听用户尝试登录的行为, 事件中记录了该次登录是否成功,
     * 若登录成功则增加 VALID_LOGIN 的次数, 否则增加 INVALID_LOGIN 次数
     *
     * @param event 事件对象
     */
    @Subscribe
    public void onLogin(LoginAttemptEvent event) {
        if (event.isSuccess()) {
            pm.updateIndex("Successfully Login Times", 1);
        } else {
            pm.updateIndex("Failed Login Times", 1);
        }
    }

    /**
     * 订阅用户准备发送消息事件 SendMessageEvent
     * 监听该用户准备发送消息的行为,
     * 用户每发送一个消息, 增加一次 SENT_MESSAGE 的次数
     *
     * @param event 事件对象
     */
    @Subscribe
    public void onSendMessage(SendMessageEvent event) {
        pm.updateIndex("Sent Messages", 1);
    }

    /**
     * 订阅用户收到他人消息事件 ChatMessageEvent
     * 监听已登录用户接受来自服务器转发的消息的行为,
     * 用户每接受一次转发的消息, 增加 RECEIVED_MESSAGE 的次数
     *
     * @param event 事件对象
     */
    @Subscribe
    public void onReceiveChatMessage(ChatMessageEvent event) {
        pm.updateIndex("Received Messages", 1);
    }

}
