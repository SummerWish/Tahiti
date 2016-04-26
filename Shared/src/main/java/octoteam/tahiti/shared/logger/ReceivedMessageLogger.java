package octoteam.tahiti.shared.logger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import octoteam.tahiti.shared.event.MessageReceivedEvent;

public class ReceivedMessageLogger {

    /**
     * 日志生成器
     */
    private Logger logger;

    private final static String fileNamePattern = "received_messages_%d{yyyy_MM_dd}.log";

    private final static String pattern = "%d - Received Message:%n%msg%n%n";

    public ReceivedMessageLogger() {
        this(fileNamePattern);
    }

    public ReceivedMessageLogger(String fileNamePattern) {
        this(fileNamePattern, pattern);
    }

    public ReceivedMessageLogger(String fileNamePattern, String pattern) {

        LoggerContext context = new LoggerContext();

        // init encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setImmediateFlush(true);
        encoder.setPattern(pattern);
        encoder.start();

        // init fileAppender
        RollingFileAppender fileAppender = new RollingFileAppender();
        fileAppender.setContext(context);
        fileAppender.setEncoder(encoder);

        // init policy
        TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
        policy.setContext(context);
        policy.setParent(fileAppender);
        policy.setFileNamePattern(fileNamePattern);

        fileAppender.setRollingPolicy(policy);

        policy.start();
        fileAppender.start();

        logger = context.getLogger(ReceivedMessageLogger.class);
        logger.setAdditive(false);
        logger.addAppender(fileAppender);
    }

    public void log(MessageReceivedEvent event) {
            logger.info(event.toString());
    }
}
