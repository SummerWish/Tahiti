package octoteam.tahiti.client;

import com.google.common.eventbus.Subscribe;
import octoteam.tahiti.client.event.ChatMessageEvent;
import octoteam.tahiti.client.event.LoginAttemptEvent;
import octoteam.tahiti.client.event.SendMessageEvent;
import wheellllll.performance.ArchiveManager;
import wheellllll.performance.IntervalLogger;

import java.util.concurrent.TimeUnit;

class IndexLogger {

    private IntervalLogger logger;

    IndexLogger(String logDir, String logFile, String archiveDir, String archiveFile) {
        logger = new IntervalLogger();
        logger.setLogDir(logDir);
        logger.setLogPrefix(logFile);
        logger.addIndex("Successfully Login Times");
        logger.addIndex("Failed Login Times");
        logger.addIndex("Sent Messages");
        logger.addIndex("Received Messages");
        logger.setInterval(60, TimeUnit.SECONDS);
        logger.setInitialDelay(60);

        ArchiveManager archiveManager = new ArchiveManager();
        archiveManager.setArchiveDir(archiveDir);
        archiveManager.setArchivePrefix(archiveFile);
        archiveManager.setInterval(86400, TimeUnit.SECONDS);
        archiveManager.addLogger(logger);
        archiveManager.setInitialDelay(120);
        archiveManager.start();
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
            logger.updateIndex("Successfully Login Times", 1);
        } else {
            logger.updateIndex("Failed Login Times", 1);
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
        logger.updateIndex("Sent Messages", 1);
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
        logger.updateIndex("Received Messages", 1);
    }

}
