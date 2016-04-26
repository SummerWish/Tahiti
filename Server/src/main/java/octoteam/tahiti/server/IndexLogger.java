package octoteam.tahiti.server;

import com.google.common.eventbus.Subscribe;
import octoteam.tahiti.server.event.LoginAttemptEvent;
import octoteam.tahiti.server.event.MessageEvent;
import octoteam.tahiti.server.event.MessageForwardEvent;
import octoteam.tahiti.shared.event.MessageReceivedEvent;
import octoteam.tahiti.shared.logger.ReceivedMessageLogger;
import wheellllll.performance.ArchiveManager;
import wheellllll.performance.IntervalLogger;

import java.util.concurrent.TimeUnit;

class IndexLogger {

    private final IntervalLogger logger;
    private final ReceivedMessageLogger rLogger;

    IndexLogger(
            String logDir,
            String logFile,
            String archiveDir,
            String archiveFile,
            String messageFile
    ) {
        logger = new IntervalLogger();
        logger.setLogDir(logDir);
        logger.setLogPrefix(logFile);
        logger.addIndex("Valid Login Times");
        logger.addIndex("Invalid Login Times");
        logger.addIndex("Received Messages");
        logger.addIndex("Ignored Messages");
        logger.addIndex("Forwarded messages");
        logger.setInterval(60, TimeUnit.SECONDS);
        logger.setInitialDelay(60);

        ArchiveManager archiveManager = new ArchiveManager();
        archiveManager.setArchiveDir(archiveDir);
        archiveManager.setArchivePrefix(archiveFile);
        archiveManager.setInterval(86400, TimeUnit.SECONDS);
        archiveManager.addLogger(logger);
        archiveManager.setInitialDelay(120);
        logger.start();
        archiveManager.start();

        rLogger = new ReceivedMessageLogger(logDir + "/" + messageFile);
    }

    /**
     * 订阅用户尝试登陆 LoginAttemptEvent 事件, 该事件的类型为该函数的参数类型.
     * 监听用户尝试登录的行为, 事件中记录了该登陆是否成功,
     * 若登陆成功则增加 VALID_LOGIN 的次数, 否则增加 INVALID_LOGIN 次数.
     *
     * @param event 监听的事件类型
     */
    @Subscribe
    public void onLoginAttempt(LoginAttemptEvent event) {
        if (event.getSuccess()) {
            logger.updateIndex("Valid Login Times", 1);
        } else {
            logger.updateIndex("Invalid Login Times", 1);
        }
    }

    /**
     * 订阅用户发送消息 MessageEvent 事件, 该事件的类型为该函数的参数类型.
     * 监听用户发送消息的行为, 事件中记录了该消息是否通过服务器认证, 认证成功表示消息被服务器接受, 否则被忽略.
     * 若成功则增加 RECEIVED_MESSAGE 的次数, 否则增加 IGNORED_MESSAGE 次数.
     *
     * @param event 监听的事件类型
     */
    @Subscribe
    public void onMessage(MessageEvent event) {
        if (event.isAuthenticated()) {
            logger.updateIndex("Received Messages", 1);
        } else {
            logger.updateIndex("Ignored Messages", 1);
        }
    }

    /**
     * 订阅消息转发 MessageForwardEvent 事件, 该事件的类型为该函数的参数类型.
     * 监听群发消息的行为,
     * 每有一个已登录的客户端(除发送方), 都增加一次 FORWARDED_MESSAGE 的次数, .
     *
     * @param event 监听的事件类型
     */
    @Subscribe
    public void onForwardedMessage(MessageForwardEvent event) {
        logger.updateIndex("Forwarded messages", 1);
    }

    /**
     * 订阅消息接收 MEssageReceivedEvent 时间，该事件的类型为该函数的参数类型.
     * 每接收到一个消息，就将其写入文件中保存
     */
    @Subscribe
    public void onReceivedMessage(MessageReceivedEvent event) {
        rLogger.log(event);
    }

}
